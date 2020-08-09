package cc.xiaobaicz.safe.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cc.xiaobaicz.safe.BuildConfig
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.SplashViewModel
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 闪屏页
 * @author BC
 * @date 2020/3/10
 */
class SplashActivity : BaseActivity() {

    private val vm by viewModels<SplashViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        SystemUiHelper.get(this)
                .fullScreen()

        vm.versionName.observe(this, Observer {
            tv_version.text = it
        })

        vm.getVersionName()

        goMain()
    }

    //3秒后跳转到主页
    private fun goMain() {
        launch {
            delay(if (BuildConfig.DEBUG) 1000L else 3000L)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

}