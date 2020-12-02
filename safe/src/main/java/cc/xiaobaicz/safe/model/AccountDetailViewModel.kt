package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.util.AccountHelper
import cc.xiaobaicz.safe.util.CipherHelper
import kotlinx.coroutines.launch

class AccountDetailViewModel : BaseObservableViewModel() {

    /**
     * 删除结果
     */
    val delete by lazy {
        MutableLiveData<Boolean>()
    }

    /**
     * 解密异常
     */
    val decodeError by lazy {
        MutableLiveData<Throwable>()
    }

    /**
     * 加密异常
     */
    val encodeError by lazy {
        MutableLiveData<Throwable>()
    }

    /**
     * 是否可编辑状态
     */
    val edit by lazy {
        MutableLiveData<Boolean>()
    }

    /**
     * 是否新建
     */
    var isCreate = false
    private set

    /**
     * 账户
     */
    val account by lazy {
        MutableLiveData<Account>()
    }

    /**
     * 操作结果
     */
    val result by lazy {
        MutableLiveData<Boolean>()
    }

    /**
     * 是否可编辑
     */
    var isEdit: Boolean = false
    set(value) {
        field = value
        edit.postValue(value)
    }

    /**
     * 目标账户
     */
    fun target(account: Account?, accountPassword: String) {
        viewModelScope.launch {
            if (account == null) {
                //新建账户，可编辑
                isEdit = true
                isCreate = true
            } else {
                //查看详情，热度+1
                account.hot += 1
                AccountHelper.updateAccount(account)
                //解密
                try {
                    account.password = CipherHelper.aesDecipher(account.password, accountPassword)
                } catch (t: Throwable) {
                    decodeError.postValue(t)
                }
            }
            //赋值目标账户
            this@AccountDetailViewModel.account.postValue(account ?: Account("", ""))
        }
    }

    /**
     * 保存数据
     */
    fun save(domain: String, name: String, password: String, accountPassword: String) {
        val account = this.account.value ?: throw NullPointerException("account is null")
        account.domain = domain
        account.account = name
        account.password = password
        account.lastTime = System.currentTimeMillis()
        viewModelScope.launch {
            //加密存储
            try {
                account.password = CipherHelper.aesEncipher(account.password, accountPassword)
            } catch (t: Throwable) {
                encodeError.postValue(t)
                return@launch
            }

            val res = if (isCreate) {
                AccountHelper.saveAccount(account)
            } else {
                AccountHelper.updateAccount(account)
            }

            //结果
            if (res > 0) {
                result.postValue(true)
            } else {
                result.postValue(false)
            }
        }
    }

    /**
     * 删除账户
     */
    fun delete() {
        val account = this.account.value ?: throw NullPointerException("account is null")
        viewModelScope.launch {
            AccountHelper.delete(account)
            delete.postValue(true)
        }
    }

}