package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.xiaobaicz.safe.bean.SafeSize
import cc.xiaobaicz.utils.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.utils.statusbar.SystemUiHelper

/**
 * 单页面全局变量
 */
class MainGlobalViewModel : ViewModel() {

    /**
     * 窗口安全区域大小
     */
    val systemUiSize by lazy {
        MutableLiveData<SafeSize>()
    }

    /**
     * 获取窗口安全区域大小
     */
    fun getSystemUiSize(helper: SystemUiHelper) {
        helper.systemUiHeight(object : SystemUiAttrCallback() {
            override fun windowPaddingSize(left: Int, top: Int, right: Int, bottom: Int) {
                systemUiSize.postValue(SafeSize(left, top, right, bottom))
            }
        })
    }

}