package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.model.AccountDetailViewModel
import cc.xiaobaicz.safe.util.dp
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import kotlinx.android.synthetic.main.fragment_account_detail.*
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 账户详情
 * 创建 / 修改 / 查看
 */
class AccountDetailFragment : BaseFragment() {

    private val vm by viewModels<AccountDetailViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_account_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //设定目标账户
        vm.target(arguments?.getParcelable<Account?>("account"))
    }

    override fun onConfigView() {
        lifecycleScope.launch {
            //设置安全区域
            val size = systemUiSize()
            toolbarSafeRegion(toolbar, size[1])
            content.translationY = 56.dp + size[1]
        }

        //目标账户绑定视图
        vm.account.observe(viewLifecycleOwner, Observer {
            et_domain.setText(it.domain)
            et_account.setText(it.account)
            et_password.setText(it.password)
            et_last_time.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it.lastTime)))
        })

        //编辑状态监听
        vm.edit.observe(viewLifecycleOwner, Observer {
            btn_edit.isSelected = it
            et_domain.isEnabled = it
            et_account.isEnabled = it
            et_password.isEnabled = it
        })

        //操作结果
        vm.result.observe(viewLifecycleOwner, Observer {
            showSnackbar(container, if (it) "保存成功" else "保存失败") { bar ->
                if (it) {
                    //操作成功 提供返回操作
                    bar.setAction("返回") {
                        findNavController().popBackStack()
                    }
                }
            }
        })
    }

    override fun onSetListener() {
        //返回上一层
        toolbar.setNavigationOnClickListener {
            onBack()
        }

        //编辑 or 保存
        btn_edit.setOnOnceClickListener { _, function ->
            if (vm.isEdit) {
                //可编辑状态保存数据
                vm.save(et_domain.text.toString(), et_account.text.toString(), et_password.text.toString())
            } else {
                //不可编辑状态转换可编辑
                vm.isEdit = true
            }
            function()
        }

        //退出询问操作
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBack()
        }
    }

    //退出操作
    private fun onBack() {
        if (vm.isEdit) {
            //编辑状态询问是否取消保存
            showSnackbar(container, "是否取消修改？") {
                it.setAction("确定") {
                    findNavController().popBackStack()
                }
            }
        } else {
            findNavController().popBackStack()
        }
    }

}