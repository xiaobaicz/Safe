package cc.xiaobaicz.safe.util

import android.os.SystemClock
import android.view.View
import android.widget.EditText

/**
 * 恢复函数
 * @see View.setOnOnceClickListener
 */
typealias Restore = ()->Unit

/**
 * 单次点击监听器
 * @see Restore
 * @see View.setOnOnceClickListener
 */
typealias OnOnceClickListener = (View, Restore)->Unit

/**
 * View单次点击事件
 * 去重点击
 * 可通过restore函数进行恢复
 * @see Restore
 * @see OnOnceClickListener
 * @param block 点击事件回调
 */
fun View.setOnOnceClickListener(block: OnOnceClickListener?) {
    var isAvailable = true  //是否可用
    val restore = {
        isAvailable = true  //调用恢复后重置为可用
    }
    setOnClickListener {
        if (isAvailable) {
            isAvailable = false //触发后不可用
            block?.invoke(it, restore)
        }
    }
}

/**
 * View间隔点击事件
 * 防重复点击
 * @param interval 可点击间隔 毫秒
 */
fun View.setOnIntervalClickListener(interval: Long = 1000L, block: ((View)->Unit)?) {
    var downTime = 0L
    setOnClickListener {
        if (SystemClock.elapsedRealtime() - downTime > interval) {
            downTime = SystemClock.elapsedRealtime()
            block?.invoke(this)
        }
    }
}

/**
 * 获取输入框文本
 */
val EditText.getText get() = this.text.toString()