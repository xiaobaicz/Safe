package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.bean.SafeSize
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.global.Constant
import cc.xiaobaicz.safe.util.LockHelper
import cc.xiaobaicz.safe.util.localHmacMD5
import cc.xiaobaicz.utils.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

/**
 * 单页面全局变量
 */
class MainGlobalViewModel : ViewModel() {

    //密码
    var password = ""
    private set

    /**
     * 密码超时通知
     */
    val timeout by lazy {
        MutableLiveData<TimeoutException>()
    }

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
                    //校验成功 解锁 & 重置 锁定信息
                    LockHelper.unlockAndReset()
                    saveAndTimeout(pw)
                    verify.postValue(null)
                } else {
                    //失败锁定
                    LockHelper.lock()
                    verify.postValue(Exception("密码错误"))
                }
            } else {
                verify.postValue(Exception("密码未设置"))
            }
        }
    }

    //缓存密码&设置超时
    private fun saveAndTimeout(pw: String) {
        viewModelScope.launch {
            password = pw
            delay(Constant.TIME_OUT)
            //清除密码
            password = ""
            //验证提示
            verify.postValue(Exception("密码超时"))
            timeout.postValue(TimeoutException("password timeout"))
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