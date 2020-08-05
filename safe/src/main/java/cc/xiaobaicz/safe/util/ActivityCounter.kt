package cc.xiaobaicz.safe.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.lang.ref.Reference
import java.lang.ref.WeakReference

typealias Observer = (Boolean)->Unit

/**
 * 页面计数器
 * @author BoCheng
 * @date 2019/11/27
 */
object ActivityCounter : LifecycleObserver {

    /**
     * 页面活动观察对象
     */
    private val mActiveObservables = ArrayList<Reference<Observer>>()

    /**
     * 总页面数
     */
    var sum = 0
        private set

    /**
     * 休眠页面数
     */
    var dormancy = 0
        private set

    /**
     * 新增页面
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        sum++
        dormancy++
    }

    /**
     * 页面恢复
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        dormancy--
    }

    /**
     * 页面休眠
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        dormancy++
        mActiveObservables.forEach {
            it.get()?.invoke(isActive)
        }
    }

    /**
     * 页面销毁
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        sum--
        dormancy--
    }

    /**
     * 程序是否在活动
     */
    val isActive
        get() = sum > dormancy

    /**
     * 添加活动观察者
     */
    fun addActiveObserver(observer: Observer) {
        mActiveObservables.add(WeakReference(observer))
    }

}