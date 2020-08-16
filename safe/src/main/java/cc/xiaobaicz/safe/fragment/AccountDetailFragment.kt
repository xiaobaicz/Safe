package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.model.AccountDetailViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import cc.xiaobaicz.safe.util.setOnOnceClickListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_account_detail.*
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
        vm.target(arguments?.getParcelable<Account?>("account"), vmGlobal.password)

        //查看状态可删除
        btn_delete.isGone = vm.isCreate
        layer_last_time.isGone = vm.isCreate
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)

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
            if (it) {
                showSnackbar(container, "可编辑状态")
            }
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

        //删除
        vm.delete.observe(viewLifecycleOwner, Observer {
            if (it) {
                findNavController().popBackStack()
            }
        })

        //解密异常
        vm.decodeError.observe(viewLifecycleOwner, Observer {
            showSnackbar(container, "解密失败")
        })

        //加密异常
        vm.encodeError.observe(viewLifecycleOwner, Observer {
            showSnackbar(container, "加密失败")
        })
    }

    override fun onSetListener() {
        //返回上一层
        toolbar.setNavigationOnClickListener {
            onBack()
        }

        //编辑 or 保存
        btn_edit.setOnIntervalClickListener {
            if (vm.isEdit) {
                showSnackbar(container, "是否保存数据？", Snackbar.LENGTH_INDEFINITE) {
                    it.setAction("保存") {
                        //可编辑状态保存数据
                        vm.save(et_domain.getText, et_account.getText, et_password.getText, vmGlobal.password)
                        vm.isEdit = false
                    }
                }
            } else {
                //不可编辑状态转换可编辑
                vm.isEdit = true
            }
        }

        //删除
        btn_delete.setOnOnceClickListener { _, function ->
            showSnackbar(container, "是否删除该账户？") {
                it.setAction("删除") {
                    vm.delete()
                }
            }
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