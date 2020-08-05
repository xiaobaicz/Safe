package cc.xiaobaicz.safe.activity

import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

/**
 * 活动协程基类
 * @author BC
 * @date 2020/3/10
 */
abstract class CoroutineActivity : AppCompatActivity(), CoroutineScope by MainScope()