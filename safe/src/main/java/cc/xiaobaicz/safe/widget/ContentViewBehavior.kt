package cc.xiaobaicz.safe.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * 主体内容关于标题栏的协调器
 */
open class ContentViewBehavior : CoordinatorLayout.Behavior<View> {

    protected var toolbarHeight = 0
    private set

    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return dependency is Toolbar
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        toolbarHeight = dependency.height
        child.translationY = toolbarHeight.toFloat()
        return true
    }

}