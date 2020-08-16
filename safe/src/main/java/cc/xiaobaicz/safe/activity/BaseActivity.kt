package cc.xiaobaicz.safe.activity

import android.os.Bundle
import cc.xiaobaicz.safe.util.ActivityCounter
import cc.xiaobaicz.utils.statusbar.SystemUiHelper

/**
 * 基础活动类
 * @author BC
 * @date 2020/3/10
 */
abstract class BaseActivity : CoroutineActivity() {

    init {
        //添加程序计数器监听
        lifecycle.addObserver(ActivityCounter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUiHelper.get(this)
                .setStatusBarColor(0x33000000)
                .setNavigationBarColor(0x33000000)
                .transparentScreen()
    }

}