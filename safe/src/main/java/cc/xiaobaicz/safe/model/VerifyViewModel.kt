package cc.xiaobaicz.safe.model

import android.os.SystemClock
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.bean.InputStatus
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.global.Constant
import cc.xiaobaicz.safe.util.LockHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyViewModel : ViewModel() {

    //锁定时间
    private var lockTime = 0L

    /**
     * 输入状态
     */
    val inputStatus by lazy {
        MutableLiveData<InputStatus>()
    }

    /**
     * 是否显示指纹
     */
    val fingerprint by lazy {
        MutableLiveData<Boolean>()
    }

    /**
     * 是否显示指纹
     */
    fun isShowFingerprint() {
        viewModelScope.launch {
            val kv = DB.app.getStorageDao().query(Constant.KEY_FINGERPRINT)
            fingerprint.postValue(kv?.value == "1")
        }
    }

    /**
     * 获取输入状态
     */
    fun getInputStatus() {
        viewModelScope.launch {

            //是否永久锁定
            if (LockHelper.isForeverLock()) {
                inputStatus.postValue(InputStatus("永久锁定", false))
                return@launch
            }

            //输入提示
            val tips = DB.app.getStorageDao().query(Constant.KEY_TIPS)?.value ?: "输入密码"

            if (LockHelper.isLock()) {
                //锁定倒计时
                lockTime = SystemClock.elapsedRealtime()
                lockCountdown(tips)
            } else {
                inputStatus.postValue(InputStatus(tips, true))
            }
        }
    }

    //锁定倒计时
    private suspend fun lockCountdown(unLockTips: String) {
        val time = SystemClock.elapsedRealtime() - lockTime
        if (time > LockHelper.LOCK_TIME) {
            //解锁
            LockHelper.unlock()
            inputStatus.postValue(InputStatus(unLockTips, true))
            return
        }
        inputStatus.postValue(InputStatus("${(LockHelper.LOCK_TIME - time) / 1000}秒后重试", false))
        delay(1000L)
        lockCountdown(unLockTips)
    }

}