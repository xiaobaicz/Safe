package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.fragment.MainFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : BaseObservableViewModel() {

    //关键字
    private var mKeyWork = ""

    /**
     * 账户
     */
    val accounts by lazy {
        MutableLiveData<List<Account>>()
    }

    /**
     * 排序tab选中状态
     */
    val tabStatus by lazy {
        MutableLiveData<TabStatus>().also {
            selectTab(tabStatus = it)
        }
    }
    private val mTargetTabStatus = TabStatus()

    /**
     * 查询所有账户
     */
    fun selectAccountAll(accounts: MutableLiveData<List<Account>> = this.accounts) {
        mKeyWork = ""
        viewModelScope.launch(Dispatchers.IO) {
            accounts.postValue(sort(DB.safe.accountDao().selectAll()))
        }
    }

    /**
     * 模糊查询
     * @param text 关键字
     */
    fun selectAccountForKeyword(text: CharSequence?) {
        mKeyWork = text?.toString() ?: ""
        viewModelScope.launch {
            accounts.postValue(sort(DB.safe.accountDao().select("%$mKeyWork%")))
        }
    }

    //排序
    private fun sort(data: List<Account>) = mTargetTabStatus.sortType.sort(data, mTargetTabStatus.isAsc)

    /**
     * 切换排序方式
     */
    fun selectTab(sortType: MainFragment.SortType = MainFragment.SortType.HOT, tabStatus: MutableLiveData<TabStatus> = this.tabStatus) {
        if (mTargetTabStatus.isInit && mTargetTabStatus.sortType == sortType) {
            mTargetTabStatus.isAsc = !mTargetTabStatus.isAsc
        } else {
            mTargetTabStatus.isAsc = false
        }
        mTargetTabStatus.sortType = sortType
        mTargetTabStatus.isInit = true
        tabStatus.postValue(mTargetTabStatus)
        if (mKeyWork.isEmpty()) {
            selectAccountAll()
        } else {
            selectAccountForKeyword(mKeyWork)
        }
    }

    /**
     * Tab选择状态
     * @see MainFragment.SortType
     * @param sortType 排序依据 默认： {@see MainFragment.SortType.HOT}
     * @param isDesc 排序方式 默认： true 倒序
     */
    class TabStatus internal constructor(var sortType: MainFragment.SortType = MainFragment.SortType.HOT, var isAsc: Boolean = false, internal var isInit: Boolean = false)

}