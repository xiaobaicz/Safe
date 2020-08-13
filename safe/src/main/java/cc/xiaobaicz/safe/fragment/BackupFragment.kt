package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import kotlinx.android.synthetic.main.fragment_setting_backup.*

class BackupFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_backup, container, false)
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
    }
}