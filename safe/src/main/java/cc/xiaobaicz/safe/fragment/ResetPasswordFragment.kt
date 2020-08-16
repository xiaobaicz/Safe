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
import cc.xiaobaicz.safe.model.ResetPasswordViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import kotlinx.android.synthetic.main.fragment_setting_safe_reset_password.*

class ResetPasswordFragment : BaseFragment() {

    private val vm by viewModels<ResetPasswordViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_safe_reset_password, container, false)
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)

        //保存结果
        vm.result.observe(viewLifecycleOwner, Observer {
            showSnackbar(container, if (it == null) "修改成功" else it.message ?: "修改失败")
            if (it == null) {
                vmGlobal.resetPassword()
                findNavController().navigate(R.id.action_global_verifyFragment)
            }
        })

        //保存状态变更
        vm.saveStatus.observe(viewLifecycleOwner, Observer {
            layer_await.isVisible = it
        })

        //保存进度
        vm.saveProgress.observe(viewLifecycleOwner, Observer {
            pb_progress.progress = it
        })
    }

    override fun onSetListener() {
        //返回
        toolbar.setNavigationOnClickListener {
            back()
        }

        //返回
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            back()
        }

        //保存
        btn_save.setOnOnceClickListener { _, function ->
            vm.save(et_password.getText, et_password1.getText, et_password2.getText, function)
        }

        //防止点击
        layer_await.setOnClickListener {  }
    }

    //返回
    private fun back() {
        if (!vm.isSave) {
            findNavController().popBackStack()
        }
    }
}