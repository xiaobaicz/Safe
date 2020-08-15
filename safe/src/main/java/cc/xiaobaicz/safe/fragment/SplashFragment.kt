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

        vm.getVersionName()
    }

    override fun onSetListener() {
        goMain()
    }

    //3秒后跳转到主页
    private fun goMain() {
        lifecycleScope.launch {
            delay(3000)
            findNavController().navigate(R.id.action_splashFragment_to_verifyFragment)
        }
    }
}