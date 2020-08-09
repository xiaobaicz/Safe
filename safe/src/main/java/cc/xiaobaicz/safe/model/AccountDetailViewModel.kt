package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Account
import kotlinx.coroutines.launch

class AccountDetailViewModel : ViewModel() {

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
    fun target(account: Account?) {
        if (account == null) {
            //新建账户，可编辑
            isEdit = true
            isCreate = true
        } else {
            viewModelScope.launch {
                //查看详情，热度+1
                account.hot += 1
                DB.safe.accountDao().update(account)
            }
        }
        this.account.postValue(account ?: Account("", ""))
    }

    /**
     * 保存数据
     */
    fun save(domain: String, name: String, password: String) {
        val account = this.account.value ?: throw NullPointerException("account is null")
        account.domain = domain
        account.account = name
        account.password = password
        account.lastTime = System.currentTimeMillis()
        viewModelScope.launch {
            val dao = DB.safe.accountDao()
            val res = if (isCreate) {
                dao.insert(account)
            } else {
                dao.update(account).toLong()
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
     * 更新域
     */
    fun setDomain(domain: String) {
        account.value?.domain = domain
    }

    /**
     * 更新账户名
     */
    fun setAccount(account: String) {
        this.account.value?.account = account
    }

    /**
     * 更新密码
     */
    fun setPassword(password: String) {
        account.value?.password = password
    }

}