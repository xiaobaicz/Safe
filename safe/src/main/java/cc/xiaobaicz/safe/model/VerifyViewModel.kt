package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.global.Constant
import kotlinx.coroutines.launch

class VerifyViewModel : ViewModel() {

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

}