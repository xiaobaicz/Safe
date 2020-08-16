package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_setting.*

/**
 * 设置
 * 安全/备份/关于
 */
class SettingFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)
    }

    override fun onSetListener() {
        //返回键
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // f 函数，重置该单次事件
        //安全设置
        btn_safe.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_safeFragment)
        }
        //备份设置
        btn_backup.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_backupFragment)
        }
        //关于
        btn_about.setOnIntervalClickListener {
            showSnackbar(container, "问题反馈：xiaojinjincz@outlook.com", Snackbar.LENGTH_INDEFINITE)
        }
    }
}