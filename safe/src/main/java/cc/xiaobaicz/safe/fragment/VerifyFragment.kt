package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.VerifyViewModel
import kotlinx.android.synthetic.main.fragment_verify.*

class VerifyFragment : BaseFragment() {

    private val vm by viewModels<VerifyViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify, container, false)
    }

    override fun onConfigView(view: View) {
        //指纹，文本框 切换
        vm.fingerprint.observe(viewLifecycleOwner, Observer {
            iv_fingerprint.isVisible = it
            layer_input.isVisible = !it
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
                vmGlobal.checkPassword(et_verify.text.toString())
            }
            return@setOnEditorActionListener false
        }

        //拦截回退
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireActivity().finish()
        }
    }

}