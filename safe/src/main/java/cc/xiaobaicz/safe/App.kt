package cc.xiaobaicz.safe

import android.app.Application
import cc.xiaobaicz.safe.util.ActivityCounter

/**
 * 程序入口
 * @author BC
 * @date 2020/3/10
 */
class App : Application() {

    companion object {
        var app: App? = null
            private set
    }

    override fun onCreate() {
        super.onCreate()
        //程序活动监测
        ActivityCounter.addActiveObserver {
//            if (!it) {
//                //程序活动状态提醒
//                ToastCompat.show("")
//            }
        }
    }

}