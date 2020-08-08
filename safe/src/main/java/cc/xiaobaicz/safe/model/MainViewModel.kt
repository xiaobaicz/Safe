package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Account
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    /**
     * 账户
     */
    val accounts by lazy {
        MutableLiveData<List<Account>>()
    }

    /**
     * 查询所有账户
     */
    fun selectAccountAll() {
        viewModelScope.launch {
            accounts.postValue(DB.safe.accountDao().selectAll())
        }
    }

}