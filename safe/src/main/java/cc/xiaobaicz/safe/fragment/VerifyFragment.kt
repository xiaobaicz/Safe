package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.VerifyViewModel
import cc.xiaobaicz.safe.util.getText
import kotlinx.android.synthetic.main.fragment_verify.*

class VerifyFragment : BaseFragment() {

    private val vm by viewModels<VerifyViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }

    override fun onConfigView(view: View) {
        //指纹，文本框 切换
        vm.fingerprint.observe(viewLifecycleOwner, Observer {
            //指纹是否可用
            val isAvailable = if (it) vm.canAuthenticate(requireContext(), container) else false

            isShowFingerprint(it && isAvailable)

            if (isAvailable) {
                //开启指纹验证
                openAuthenticate()
            }
        })

        //是否显示指纹
        vm.isShowFingerprint()

        //登陆结果
        vmGlobal.verify.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                findNavController().navigate(R.id.action_verifyFragment_to_mainFragment)
            } else {
                showSnackbar(container, it.message ?: "")
                vm.getInputStatus()
            }
        })

        //文本提示
        vm.inputStatus.observe(viewLifecycleOwner, Observer {
            et_verify.setText("")
            et_verify.hint = it.hint
            et_verify.isEnabled = it.isEnable
        })

        vm.getInputStatus()
    }

    override fun onSetListener() {
        //键盘ime完成事件
        et_verify.setOnEditorActionListener { _, actionId, _ ->
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                vmGlobal.checkPassword(et_verify.getText)
            }
            return@setOnEditorActionListener false
        }

        //拦截回退
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

    //设置显示指纹
    private fun isShowFingerprint(isShow: Boolean) {
        iv_fingerprint.isVisible = isShow
        layer_input.isVisible = !isShow
    }

    //开启指纹
    private fun openAuthenticate() {
        val excutor = ContextCompat.getMainExecutor(requireContext())
        val biometricPrompt = BiometricPrompt(this, excutor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                //取消or异常退出
                requireActivity().finish()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                //开启密码框
                isShowFingerprint(false)
            }

            override fun onAuthenticationFailed() {
                //校验失败
                showSnackbar(container, "验证失败")
            }
        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("验证身份")
            .setSubtitle("验证后开启密码输入")
            .setNegativeButtonText("退出")
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

}