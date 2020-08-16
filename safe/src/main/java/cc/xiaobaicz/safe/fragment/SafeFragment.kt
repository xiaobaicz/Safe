package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.SafeViewModel
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import kotlinx.android.synthetic.main.fragment_setting_safe.*

/**
 * 安全设置页
 */
class SafeFragment : BaseFragment() {

    private val vm by viewModels<SafeViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setting_safe, container, false)
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)

        //指纹校验
        vm.fingerprint.observe(viewLifecycleOwner, Observer {
            switch_fingerprint.isChecked = it
        })

        //自动超时时间
        vm.timeout.observe(viewLifecycleOwner, Observer {
            et_lock_time.setText("$it")
        })

        vm.getFingerprint()
        vm.getTimeout()
    }

    override fun onSetListener() {
        //返回键
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //时间变更
        et_lock_time.addTextChangedListener(onTextChanged = { t, _, _, _ ->
            val time = try {
                (t?.toString() ?: "0").toLong()
            } catch (e: Exception) {
                0L
            }
            vm.setTimeout(time)
        })

        //指纹校验变更
        switch_fingerprint.setOnCheckedChangeListener { _, isChecked ->
            vm.setFingerprint(isChecked)
        }

        //修改密码提示
        btn_password_tips.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_safeFragment_to_resetTipsFragment)
        }
    }
}