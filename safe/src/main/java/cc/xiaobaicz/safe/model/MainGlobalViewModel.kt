package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.helper.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.helper.statusbar.SystemUiHelper
import cc.xiaobaicz.safe.bean.SafeSize
import cc.xiaobaicz.safe.util.LockHelper
import cc.xiaobaicz.safe.util.SafeHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

/**
 * 单页面全局变量
 */
class MainGlobalViewModel : BaseObservableViewModel() {

    //是否可关闭
    var isClose = true
    private set

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
            if (SafeHelper.hasPassword()) {
                if (SafeHelper.checkPassword(pw)) {
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

    //密码过期任务
    private var timeoutJob: Job? = null

    //缓存密码&设置超时
    private fun saveAndTimeout(pw: String) {
        password = pw
//        timeoutJob?.cancel()
//        timeoutJob = viewModelScope.launch {
//            delay(Constant.PASSWORD_TIME_OUT)
//            //清除密码
//            password = ""
//            //验证提示
//            verify.postValue(Exception("密码超时"))
//            timeout.postValue(TimeoutException("password timeout"))
//        }
    }

    /**
     * 密码变更
     */
    fun resetPassword() {
        password = ""
        verify.postValue(Exception("密码更新，请重新登陆"))
    }

    /**
     * 不可见时是否可关闭
     */
    fun isClose(close: Boolean) {
        isClose = close
    }

}