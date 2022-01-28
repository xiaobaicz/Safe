package cc.xiaobaicz.safe.util

import androidx.annotation.NonNull
import androidx.lifecycle.*
import java.lang.ref.Reference
import java.lang.ref.WeakReference

typealias Observer = (Boolean)->Unit

/**
 * 页面计数器
 * @author BoCheng
 * @date 2019/11/27
 */
object ActivityCounter : DefaultLifecycleObserver {

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
    override fun onCreate(@NonNull owner: LifecycleOwner) {
        sum++
        dormancy++
    }

    /**
     * 页面恢复
     */
    override fun onStart(@NonNull owner: LifecycleOwner) {
        dormancy--
    }

    /**
     * 页面休眠
     */
    override fun onStop(@NonNull owner: LifecycleOwner) {
        dormancy++
        mActiveObservables.forEach {
            it.get()?.invoke(isActive)
        }
    }

    /**
     * 页面销毁
     */
    override fun onDestroy(@NonNull owner: LifecycleOwner) {
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