package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.helper.statusbar.SystemUiHelper
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.databinding.FragmentSplashBinding
import cc.xiaobaicz.safe.model.SplashViewModel
import cc.xiaobaicz.safe.util.SafeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : BaseFragment() {

    private val vm by viewModels<SplashViewModel>()

    private val bind by lazy {
        FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onConfigView(view: View) {
        SystemUiHelper.get(requireActivity()).fullScreen()

        vm.versionName()
    }

    override fun onSetListener() {
        go()
    }

    //3秒后跳转
    private fun go() {
        lifecycleScope.launch {
            delay(resources.getInteger(R.integer.init_duration).toLong())
            SystemUiHelper.get(requireActivity()).transparentScreen()
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