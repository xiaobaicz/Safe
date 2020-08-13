package cc.xiaobaicz.safe.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.MainGlobalViewModel
import cc.xiaobaicz.utils.statusbar.SystemUiAttrCallback
import cc.xiaobaicz.utils.statusbar.SystemUiHelper
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {

    private val vm by viewModels<MainGlobalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm.systemUiSize.observe(this, Observer {
            //获取窗口安全区域大小后再加载导航组件
            setContentView(R.layout.activity_main)
        })

        vm.getSystemUiSize(SystemUiHelper.get(this@MainActivity))
    }

}