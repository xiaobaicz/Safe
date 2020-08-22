package cc.xiaobaicz.safe.model

import android.content.Context
import android.os.SystemClock
import androidx.biometric.BiometricManager
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.bean.InputStatus
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.global.Constant
import cc.xiaobaicz.safe.util.LockHelper
import cc.xiaobaicz.safe.util.TipsHelper
import com.google.android.material.snackbar.Snackbar
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

            //失败次数
            val count = LockHelper.lockCount()

            //输入提示
            val tips = if (count >= LockHelper.MAX_ERROR_ROUTINE) {
                "输入密码"
            } else {
                TipsHelper.getTips()
            }

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

    /**
     * 生物识别是否可用
     */
    fun canAuthenticate(context: Context, container: CoordinatorLayout): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate()) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Snackbar.make(container, "该设备不支持指纹功能", Snackbar.LENGTH_SHORT).show()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Snackbar.make(container, "指纹暂不可用", Snackbar.LENGTH_SHORT).show()
                false
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Snackbar.make(container, "尚未开启指纹识别", Snackbar.LENGTH_SHORT).show()
                false
            }
            else -> false
        }
    }

}