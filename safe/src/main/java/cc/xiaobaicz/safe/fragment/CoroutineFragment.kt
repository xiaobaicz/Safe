package cc.xiaobaicz.safe.fragment

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * 页面碎片协程基类
 * @author BC
 * @date 2020/3/12
 */
abstract class CoroutineFragment : Fragment(), CoroutineScope by MainScope()