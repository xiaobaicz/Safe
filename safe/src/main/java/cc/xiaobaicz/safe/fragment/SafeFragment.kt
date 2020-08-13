package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import kotlinx.android.synthetic.main.fragment_setting_safe.*
import kotlinx.coroutines.launch

/**
 * 安全设置页
 */
class SafeFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_safe, container, false)
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
    }
}