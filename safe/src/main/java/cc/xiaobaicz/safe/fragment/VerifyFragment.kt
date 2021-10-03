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
import cc.xiaobaicz.safe.databinding.FragmentVerifyBinding
import cc.xiaobaicz.safe.model.VerifyViewModel
import cc.xiaobaicz.safe.util.getText

class VerifyFragment : BaseFragment() {

    private val vm by viewModels<VerifyViewModel>()

    private val bind by lazy {
        FragmentVerifyBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onConfigView(view: View) {
        //指纹，文本框 切换
        vm.fingerprint.observe(viewLifecycleOwner, Observer {
            //指纹是否可用
            val isAvailable = if (it) vm.canAuthenticate(requireContext(), bind.container) else false

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
                showSnackbar(bind.container, it.message ?: "")
                vm.getInputStatus(requireContext())
            }
        })

        //文本提示
        vm.inputStatus.observe(viewLifecycleOwner, Observer {
            bind.etVerify.setText("")
            bind.etVerify.hint = it.hint
            bind.etVerify.isEnabled = it.isEnable
        })

        vm.getInputStatus(requireContext())
    }

    override fun onSetListener() {
        //键盘ime完成事件
        bind.etVerify.setOnEditorActionListener { _, actionId, _ ->
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                vmGlobal.checkPassword(requireContext(), bind.etVerify.getText)
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
        bind.ivFingerprint.isVisible = isShow
        bind.layerInput.isVisible = !isShow
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
                showSnackbar(bind.container, getString(R.string.snackbar_verify_faild))
            }
        })
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.dialog_title_verify))
            .setSubtitle(getString(R.string.dialog_subtitle_verify))
            .setNegativeButtonText(getString(R.string.dialog_action_exit))
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

}