package cc.xiaobaicz.safe.model

import androidx.lifecycle.MutableLiveData
import cc.xiaobaicz.safe.BR
import cc.xiaobaicz.safe.BuildConfig

class SplashViewModel : BaseObservableViewModel() {

    val versionName by lazy {
        MutableLiveData<String>()
    }

    fun getVersionName() {
        versionName.value = "版本号: ${BuildConfig.VERSION_NAME}"
    }

}