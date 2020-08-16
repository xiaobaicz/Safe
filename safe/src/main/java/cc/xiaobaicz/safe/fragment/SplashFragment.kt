package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.SplashViewModel
import cc.xiaobaicz.safe.util.SafeHelper
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.android.synthetic.main.fragment_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment() {

    private val vm by viewModels<SplashViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onConfigView(view: View) {
        SystemUiHelper.get(requireActivity()).fullScreen()

        vm.versionName.observe(this, Observer {
            tv_version.text = it
        })

        lifecycleScope.launch {

        }

        vm.getVersionName()
    }

    override fun onSetListener() {
        go()
    }

    //3秒后跳转
    private fun go() {
        lifecycleScope.launch {
            delay(3000)
            //判断是否设置密码
            if (SafeHelper.hasPassword()) {
                findNavController().navigate(R.id.action_splashFragment_to_verifyFragment)
            } else {
                //去设置密码
                findNavController().navigate(R.id.action_splashFragment_to_infoConfigFragment)
            }
        }
    }
}