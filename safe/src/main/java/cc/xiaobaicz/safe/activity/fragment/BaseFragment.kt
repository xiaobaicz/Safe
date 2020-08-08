package cc.xiaobaicz.safe.activity.fragment

import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePadding
import cc.xiaobaicz.utils.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * 页面碎片基类
 * @author BC
 * @date 2020/3/12
 */
abstract class BaseFragment : CoroutineFragment() {

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

}