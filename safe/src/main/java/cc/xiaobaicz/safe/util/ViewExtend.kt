package cc.xiaobaicz.safe.util

import android.view.View

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