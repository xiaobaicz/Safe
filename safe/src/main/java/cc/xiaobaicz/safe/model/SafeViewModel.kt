package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage
import cc.xiaobaicz.safe.global.Constant
import kotlinx.coroutines.launch

class SafeViewModel : ViewModel() {

    //指纹校验
    val fingerprint by lazy {
        MutableLiveData<Boolean>()
    }

    //超时时间
    val timeout by lazy {
        MutableLiveData<Long>()
    }

    //获取超时时间
    fun getTimeout() {
        viewModelScope.launch {
            val dao = DB.app.getStorageDao()
            val timeout = dao.query(Constant.KEY_TIME_OUT) ?: Storage(Constant.KEY_TIME_OUT, "${Constant.CHECK_TIME_OUT}")
            this@SafeViewModel.timeout.postValue(timeout.value!!.toLong())
        }
    }

    //设置超时时间
    fun setTimeout(time: Long) {
        viewModelScope.launch {
            if (time < 60) {
                return@launch
            }
            val dao = DB.app.getStorageDao()
            val timeout = Storage(Constant.KEY_TIME_OUT, "$time")
            if (dao.query(Constant.KEY_TIME_OUT) == null) {
                dao.inserts(timeout)
            } else {
                dao.updates(timeout)
            }
        }
    }

    //获取指纹校验
    fun getFingerprint() {
        viewModelScope.launch {
            val dao = DB.app.getStorageDao()
            val fingerprint = dao.query(Constant.KEY_FINGERPRINT) ?: Storage(Constant.KEY_FINGERPRINT, "0")
            this@SafeViewModel.fingerprint.postValue(fingerprint.value == "1")
        }
    }

    //设置指纹校验
    fun setFingerprint(check: Boolean) {
        viewModelScope.launch {
            val dao = DB.app.getStorageDao()
            val fingerprint = Storage(Constant.KEY_FINGERPRINT, if (check) "1" else "0")
            if (dao.query(Constant.KEY_FINGERPRINT) == null) {
                dao.inserts(fingerprint)
            } else {
                dao.updates(fingerprint)
            }
        }
    }

}