package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.databinding.FragmentSettingSafeResetTipsBinding
import cc.xiaobaicz.safe.model.ResetTipsViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnOnceClickListener

class ResetTipsFragment : BaseFragment() {

    private val vm by viewModels<ResetTipsViewModel>()

    private val bind by lazy {
        FragmentSettingSafeResetTipsBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)

        //保存结果
        vm.result.observe(viewLifecycleOwner, Observer {
            showSnackbar(bind.container, if (it) getString(R.string.snackbar_update_success) else getString(R.string.snackbar_update_faild))
        })
    }

    override fun onSetListener() {
        //返回
        bind.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        //保存
        bind.btnSave.setOnOnceClickListener { _, function ->
            vm.save(requireContext(), bind.etTips.getText, function)
        }
    }
}