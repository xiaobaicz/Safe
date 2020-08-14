package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.bean.SafeSize
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.global.Constant
import cc.xiaobaicz.safe.util.hmacMD5
import cc.xiaobaicz.safe.util.localHmacMD5
import cc.xiaobaicz.utils.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.coroutines.launch

/**
 * 单页面全局变量
 */
class MainGlobalViewModel : ViewModel() {

    //密码
    var password = ""
    private set

    /**
     * 校验结果
     */
    val verify by lazy {
        MutableLiveData<Exception?>()
    }

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

    /**
     * 校验密码
     */
    fun checkPassword(pw: String) {
        //size 小于 6
        if (pw.length < 6) {
            verify.postValue(NullPointerException("密码最少6位"))
            return
        }
        viewModelScope.launch {
            if (hasPassword()) {
                val kv = DB.app.getStorageDao().query(Constant.KEY_PASSWORD)
                if (localHmacMD5(pw) == kv?.value) {
                    verify.postValue(null)
                } else {
                    verify.postValue(Exception("密码错误"))
                }
            } else {
                verify.postValue(Exception("密码未设置"))
            }
        }
    }

    /**
     * 是否有默认密码
     */
    suspend fun hasPassword(): Boolean {
        val kv = DB.app.getStorageDao().query(Constant.KEY_PASSWORD)
        return kv != null
    }

}