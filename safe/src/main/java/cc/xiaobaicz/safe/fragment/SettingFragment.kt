package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import kotlinx.android.synthetic.main.fragment_setting.*
import kotlinx.coroutines.launch

/**
 * 设置
 * 安全/备份/关于
 */
class SettingFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onConfigView(view: View) {
        lifecycleScope.launch {
            //设置安全区域
            val size = systemUiSize()
            toolbarSafeRegion(toolbar, size[1])
        }
    }

    override fun onSetListener() {
        //返回键
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // f 函数，重置该单次事件
        //安全设置
        btn_safe.setOnOnceClickListener { _, f ->
            f()
        }

        //备份设置
        btn_backup.setOnOnceClickListener { _, f ->
            f()
        }

        //关于
        btn_about.setOnOnceClickListener { _, f ->
            f()
        }
    }
}