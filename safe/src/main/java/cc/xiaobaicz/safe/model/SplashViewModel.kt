package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cc.xiaobaicz.safe.BuildConfig

class SplashViewModel : ViewModel() {

    val versionName by lazy {
        MutableLiveData<String>()
    }

    fun getVersionName() {
        versionName.value = "版本号: ${BuildConfig.VERSION_NAME}"
    }

}