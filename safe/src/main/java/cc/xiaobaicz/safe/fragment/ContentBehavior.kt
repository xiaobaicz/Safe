package cc.xiaobaicz.safe.fragment

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePaddingRelative

/**
 * 内容区域 协调规则
 */
class ContentBehavior : CoordinatorLayout.Behavior<View> {
    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    /**
     * 协调Toolbar
     */
    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Toolbar
    }

    /**
     * 内容区域连接于Toolbar底部
     */
    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        child.updatePaddingRelative(child.paddingLeft, child.paddingTop + dependency.height, child.paddingRight, child.paddingBottom)
        return true
    }
}