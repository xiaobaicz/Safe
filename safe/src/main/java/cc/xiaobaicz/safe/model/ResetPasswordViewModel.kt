package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.util.AccountHelper
import cc.xiaobaicz.safe.util.Restore
import cc.xiaobaicz.safe.util.SafeHelper
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    /**
     * 保存结果
     */
    val result by lazy {
        MutableLiveData<Exception?>()
    }

    /**
     * 保存状态
     */
    val saveStatus by lazy {
        MutableLiveData<Boolean>(false)
    }

    /**
     * 保存进度
     */
    val saveProgress by lazy {
        MutableLiveData<Int>()
    }

    /**
     * 是否保存中
     */
    val isSave get() = saveStatus.value ?: false

    /**
     * 保存密码提示
     */
    fun save(pw: String, pw1: String, pw2: String, function: Restore) {
        viewModelScope.launch {
            if (check(pw, pw1, pw2)) {
                saveStatus.postValue(true)
                try {
                    AccountHelper.reEncipher(pw, pw1)
                    SafeHelper.setPassword(pw1)
                    result.postValue(null)
                } catch (t: Throwable) {
                    result.postValue(Exception("修改失败"))
                }
                saveStatus.postValue(false)
                function()
            } else {
                function()
            }
        }
    }

    //校验输入
    private suspend fun check(pw: String, pw1: String, pw2: String): Boolean {
        if (!SafeHelper.checkPassword(pw)) {
            result.postValue(Exception("密码错误"))
            return false
        }
        if (pw1 != pw2) {
            result.postValue(Exception("新密码不一致"))
            return false
        }
        if (pw1.length < 6) {
            result.postValue(Exception("密码最少6位"))
            return false
        }
        return true
    }

}