package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.util.Restore
import cc.xiaobaicz.safe.util.TipsHelper
import kotlinx.coroutines.launch

class ResetTipsViewModel : BaseObservableViewModel() {

    /**
     * 保存结果
     */
    val result by lazy {
        MutableLiveData<Boolean>()
    }

    /**
     * 保存密码提示
     */
    fun save(tips: String, function: Restore) {
        viewModelScope.launch {
            TipsHelper.setTips(tips)
            function()
            result.postValue(true)
        }
    }

}