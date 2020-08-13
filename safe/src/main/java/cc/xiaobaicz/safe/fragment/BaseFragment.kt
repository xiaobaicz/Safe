package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import cc.xiaobaicz.safe.util.dp
import cc.xiaobaicz.utils.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 页面碎片基类
 * @author BC
 * @date 2020/3/12
 */
abstract class BaseFragment : CoroutineFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onConfigView(view)
        onSetListener()
    }

    /**
     * 配置View
     */
    protected abstract fun onConfigView(view: View)

    /**
     * 配置监听器
     */
    protected abstract fun onSetListener()

    /**
     * 获取系统窗口内边框大小
     */
    protected suspend fun systemUiSize() = suspendCancellableCoroutine<Array<Int>> {
        SystemUiHelper.get(activity).systemUiHeight(object : SystemUiAttrCallback() {
            override fun windowPaddingSize(left: Int, top: Int, right: Int, bottom: Int) {
                it.resume(arrayOf(left, top, right, bottom))
            }
        })
    }

    /**
     * 工具栏安全区域
     */
    protected fun toolbarSafeRegion(toolbar: Toolbar, top: Int) {
        toolbar.also {
            it.layoutParams.height += top
            it.updatePadding(top = it.paddingTop + top)
        }
    }

    /**
     * 显示提示
     */
    protected fun showSnackbar(view: CoordinatorLayout, msg: String, time: Int = Snackbar.LENGTH_SHORT, block: ((Snackbar)->Unit)? = null) {
        val bar = Snackbar.make(view, msg, time)
        block?.invoke(bar)
        bar.show()
    }

}