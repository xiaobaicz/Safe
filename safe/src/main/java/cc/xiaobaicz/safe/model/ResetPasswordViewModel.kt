package cc.xiaobaicz.safe.model

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.util.AccountHelper
import cc.xiaobaicz.safe.util.Restore
import cc.xiaobaicz.safe.util.SafeHelper
import kotlinx.coroutines.launch

class ResetPasswordViewModel : BaseObservableViewModel() {

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
     * 是否保存中
     */
    val isSave get() = saveStatus.value ?: false

    /**
     * 保存密码提示
     */
    fun save(context: Context, pw: String, pw1: String, pw2: String, function: Restore) {
        viewModelScope.launch {
            if (check(context, pw, pw1, pw2)) {
                saveStatus.postValue(true)
                try {
                    AccountHelper.reEncipher(pw, pw1)
                    SafeHelper.setPassword(pw1)
                    result.postValue(null)
                } catch (t: Throwable) {
                    result.postValue(Exception(context.getString(R.string.snackbar_update_faild)))
                }
                saveStatus.postValue(false)
                function()
            } else {
                function()
            }
        }
    }

    //校验输入
    private suspend fun check(context: Context, pw: String, pw1: String, pw2: String): Boolean {
        if (!SafeHelper.checkPassword(pw)) {
            result.postValue(Exception(context.getString(R.string.exception_password_error)))
            return false
        }
        if (pw1 != pw2) {
            result.postValue(Exception(context.getString(R.string.exception_passwords_match)))
            return false
        }
        if (pw1.length < 6) {
            result.postValue(Exception(context.getString(R.string.hint_password)))
            return false
        }
        return true
    }

}