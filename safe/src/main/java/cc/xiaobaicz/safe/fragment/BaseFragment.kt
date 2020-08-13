package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updatePadding
import androidx.core.view.updatePaddingRelative
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cc.xiaobaicz.safe.bean.SafeSize
import cc.xiaobaicz.safe.model.MainGlobalViewModel
import cc.xiaobaicz.safe.util.dp
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 页面碎片基类
 * @author BC
 * @date 2020/3/12
 */
abstract class BaseFragment : CoroutineFragment() {

    /**
     * 全局变量
     */
    protected val vmGlobal by lazy {
        ViewModelProvider(requireActivity()).get(MainGlobalViewModel::class.java)
    }

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
    protected suspend fun safeRegion() = suspendCancellableCoroutine<SafeSize> {
        vmGlobal.systemUiSize.observe(viewLifecycleOwner, Observer { size ->
            it.resume(size)
        })
    }

    /**
     * 安全区域
     */
    protected fun safeRegion(toolbar: Toolbar, content: ViewGroup?) {
        vmGlobal.systemUiSize.observe(viewLifecycleOwner, Observer { size ->
            toolbar.also {
                it.layoutParams.height += size.t
                it.updatePadding(top = it.paddingTop + size.t)
            }
            if (content != null) {
                content.updatePaddingRelative(content.paddingLeft, content.paddingTop + 56.dp.toInt() + size.t, content.paddingRight, content.paddingBottom + size.b)
                content.clipToPadding = false
            }
        })
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