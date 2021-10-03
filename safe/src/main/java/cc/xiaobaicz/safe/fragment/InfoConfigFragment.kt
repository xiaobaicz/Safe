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
import cc.xiaobaicz.safe.databinding.FragmentInfoConfigBinding
import cc.xiaobaicz.safe.model.InfoConfigViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnOnceClickListener

class InfoConfigFragment : BaseFragment() {

    private val vm by viewModels<InfoConfigViewModel>()

    private val bind by lazy {
        FragmentInfoConfigBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)

        //登陆结果
        vmGlobal.verify.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                findNavController().navigate(R.id.action_infoConfigFragment_to_mainFragment)
            } else {
                showSnackbar(bind.container, it.message ?: "")
            }
        })

        //保存结果
        vm.save.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                vmGlobal.checkPassword(requireContext(), bind.etPassword1.getText)
            } else {
                showSnackbar(bind.container, it.message ?: "")
            }
        })
    }

    override fun onSetListener() {
        //保存数据
        bind.btnSave.setOnOnceClickListener { _, function ->
            vm.save(requireContext(), function)
        }

        //退出提示
        var lastTime = 0L
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (SystemClock.elapsedRealtime() - lastTime < 1000) {
                //退出
                requireActivity().finish()
            } else {
                lastTime = SystemClock.elapsedRealtime()
                showSnackbar(bind.container, getString(R.string.snackbar_double_click))
            }
        }
    }
}