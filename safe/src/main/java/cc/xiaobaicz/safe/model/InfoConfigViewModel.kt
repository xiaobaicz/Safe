package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage
import cc.xiaobaicz.safe.global.Constant
import cc.xiaobaicz.safe.util.Restore
import cc.xiaobaicz.safe.util.TipsHelper
import cc.xiaobaicz.safe.util.localHmacMD5
import kotlinx.coroutines.launch

class InfoConfigViewModel : ViewModel() {

    /**
     * 保存结果
     */
    val save by lazy {
        MutableLiveData<Exception>()
    }

    /**
     * 保存信息
     */
    fun save(pw1: String, pw2: String, tips: String, function: Restore) {
        if (check(pw1, pw2, tips)) {
            viewModelScope.launch {
                val dao = DB.app.getStorageDao()
                dao.inserts(Storage(Constant.KEY_PASSWORD, localHmacMD5(pw1)))
                TipsHelper.setTips(tips)
                save.postValue(null)
                function()
            }
        } else {
            function()
        }
    }

    //内容校验
    private fun check(pw1: String, pw2: String, tips: String): Boolean {
        if (pw1 != pw2) {
            save.postValue(Exception("密码不一致"))
            return false
        }
        if (pw1.length < 6 || pw2.length < 6) {
            save.postValue(Exception("密码长度最少6位"))
            return false
        }
        return true
    }

}