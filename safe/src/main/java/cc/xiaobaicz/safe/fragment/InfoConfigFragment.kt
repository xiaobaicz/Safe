package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.InfoConfigViewModel
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import kotlinx.android.synthetic.main.fragment_info_config.*

class InfoConfigFragment : BaseFragment() {

    private val vm by viewModels<InfoConfigViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_info_config, container, false)
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)

        //登陆结果
        vmGlobal.verify.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                findNavController().navigate(R.id.action_infoConfigFragment_to_mainFragment)
            } else {
                showSnackbar(container, it.message ?: "")
            }
        })

        //保存结果
        vm.save.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                vmGlobal.checkPassword(et_password1.text.toString())
            } else {
                showSnackbar(container, it.message ?: "")
            }
        })
    }

    override fun onSetListener() {
        //保存数据
        btn_save.setOnOnceClickListener { _, function ->
            vm.save(et_password1.text.toString(), et_password2.text.toString(), et_tips.text.toString(), function)
        }

        //退出提示
        var lastTime = 0L
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (SystemClock.elapsedRealtime() - lastTime < 1000) {
                //退出
                requireActivity().finish()
            } else {
                lastTime = SystemClock.elapsedRealtime()
                showSnackbar(container, "双击退出")
            }
        }
    }
}