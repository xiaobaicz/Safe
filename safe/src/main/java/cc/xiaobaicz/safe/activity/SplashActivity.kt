package cc.xiaobaicz.safe.activity

import android.content.Intent
import android.os.Bundle
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 闪屏页
 * @author BC
 * @date 2020/3/10
 */
class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        SystemUiHelper.get(this)
                .fullScreen()

        goMain()
    }

    private fun goMain() {
        launch {
            delay(3000)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }

}