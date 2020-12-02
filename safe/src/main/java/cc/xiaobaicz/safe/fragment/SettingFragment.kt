package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.databinding.FragmentSettingBinding
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import com.google.android.material.snackbar.Snackbar

/**
 * 设置
 * 安全/备份/关于
 */
class SettingFragment : BaseFragment() {

    private val bind by lazy {
        FragmentSettingBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return bind.root
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)
    }

    override fun onSetListener() {
        //返回键
        bind.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        // f 函数，重置该单次事件
        //安全设置
        bind.btnSafe.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_safeFragment)
        }
        //备份设置
        bind.btnBackup.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_settingFragment_to_backupFragment)
        }
        //关于
        bind.btnAbout.setOnIntervalClickListener {
            showSnackbar(bind.container, "问题反馈：xiaojinjincz@outlook.com", Snackbar.LENGTH_INDEFINITE)
        }
    }
}