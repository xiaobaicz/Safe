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
import cc.xiaobaicz.safe.databinding.FragmentSettingSafeBinding
import cc.xiaobaicz.safe.model.SafeViewModel
import cc.xiaobaicz.safe.util.setOnIntervalClickListener

/**
 * 安全设置页
 */
class SafeFragment : BaseFragment() {

    private val vm by viewModels<SafeViewModel>()

    private val bind by lazy {
        FragmentSettingSafeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)

        //指纹校验
        vm.fingerprint.observe(viewLifecycleOwner, Observer {
            bind.switchFingerprint.isChecked = it
        })

        //自动超时时间
        vm.timeout.observe(viewLifecycleOwner, Observer {
            bind.etLockTime.setText("$it")
        })

        vm.getFingerprint()
        vm.getTimeout()
    }

    override fun onSetListener() {
        //返回键
        bind.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //时间变更
        bind.etLockTime.addTextChangedListener(onTextChanged = { t, _, _, _ ->
            val time = try {
                (t?.toString() ?: "0").toLong()
            } catch (e: Exception) {
                0L
            }
            vm.setTimeout(time)
        })

        //指纹校验变更
        bind.switchFingerprint.setOnCheckedChangeListener { _, isChecked ->
            vm.setFingerprint(isChecked)
        }

        //修改密码提示
        bind.btnPasswordTips.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_safeFragment_to_resetTipsFragment)
        }

        //修改密码
        bind.btnPassword.setOnIntervalClickListener {
            findNavController().navigate(R.id.action_safeFragment_to_resetPasswordFragment)
        }
    }
}