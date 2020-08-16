package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.ResetTipsViewModel
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import kotlinx.android.synthetic.main.fragment_setting_safe_reset_tips.*

class ResetTipsFragment : BaseFragment() {

    private val vm by viewModels<ResetTipsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_safe_reset_tips, container, false)
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)

        //保存结果
        vm.result.observe(viewLifecycleOwner, Observer {
            showSnackbar(container, if (it) "修改成功" else "修改失败")
        })
    }

    override fun onSetListener() {
        //返回
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //保存
        btn_save.setOnOnceClickListener { _, function ->
            vm.save(et_tips.text.toString(), function)
        }
    }
}