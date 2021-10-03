package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.databinding.FragmentSettingSafeResetPasswordBinding
import cc.xiaobaicz.safe.model.ResetPasswordViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnOnceClickListener

class ResetPasswordFragment : BaseFragment() {

    private val vm by viewModels<ResetPasswordViewModel>()

    private val bind by lazy {
        FragmentSettingSafeResetPasswordBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)

        //保存结果
        vm.result.observe(viewLifecycleOwner, Observer {
            showSnackbar(bind.container, if (it == null) getString(R.string.snackbar_update_success) else it.message ?: getString(R.string.snackbar_update_faild))
            if (it == null) {
                vmGlobal.resetPassword(requireContext())
                findNavController().navigate(R.id.action_global_verifyFragment)
            }
        })

        //保存状态变更
        vm.saveStatus.observe(viewLifecycleOwner, Observer {
            bind.layerAwait.isVisible = it
        })
    }

    override fun onSetListener() {
        //返回
        bind.toolbar.setNavigationOnClickListener {
            back()
        }

        //返回
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            back()
        }

        //保存
        bind.btnSave.setOnOnceClickListener { _, function ->
            vm.save(requireContext(), bind.etPassword.getText, bind.etPassword1.getText, bind.etPassword2.getText, function)
        }

        //防止点击
        bind.layerAwait.setOnClickListener {  }
    }

    //返回
    private fun back() {
        if (!vm.isSave) {
            findNavController().popBackStack()
        }
    }
}