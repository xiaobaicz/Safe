package cc.xiaobaicz.safe.util

import android.content.res.Resources
import android.util.TypedValue

/**
 * dp数值转px
 */
val Number.dp get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics)

/**
 * sp数值转px
 */
val Number.sp get() = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics)