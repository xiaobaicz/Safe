package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.VerifyViewModel
import kotlinx.android.synthetic.main.fragment_verify.*
import kotlinx.coroutines.launch

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
            }
        })

        //判断是否设置密码
        lifecycleScope.launch {
            if (!vmGlobal.hasPassword()) {
                //去设置密码
                findNavController().navigate(R.id.action_verifyFragment_to_infoConfigFragment)
            }
        }
    }

    override fun onSetListener() {
        //键盘ime完成事件
        et_verify.setOnEditorActionListener { _, actionId, _ ->
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                vmGlobal.checkPassword(et_verify.text.toString())
            }
            return@setOnEditorActionListener false
        }
    }

}