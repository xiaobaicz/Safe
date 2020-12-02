package cc.xiaobaicz.safe.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.databinding.FragmentAccountDetailBinding
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.model.AccountDetailViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * 账户详情
 * 创建 / 修改 / 查看
 */
class AccountDetailFragment : BaseFragment() {

    private val vm by viewModels<AccountDetailViewModel>()

    private val bind by lazy {
        FragmentAccountDetailBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //设定目标账户
        vm.target(arguments?.getParcelable<Account?>("account"), vmGlobal.password)

        //查看状态可删除
        bind.btnDelete.isGone = vm.isCreate
        bind.layerLastTime.isGone = vm.isCreate
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)

        //目标账户绑定视图
        vm.account.observe(viewLifecycleOwner, Observer {
            bind.etDomain.setText(it.domain)
            bind.etAccount.setText(it.account)
            bind.etPassword.setText(it.password)
            bind.etLastTime.setText(SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it.lastTime)))
        })

        //编辑状态监听
        vm.edit.observe(viewLifecycleOwner, Observer {
            bind.btnEdit.isSelected = it
            bind.etDomain.isEnabled = it
            bind.etAccount.isEnabled = it
            bind.etPassword.isEnabled = it
            if (mAction.isShowEditAction(vm)) {
                showSnackbar(bind.container, "可编辑状态")
            }
        })

        //操作结果
        vm.result.observe(viewLifecycleOwner, Observer {
            showSnackbar(bind.container, if (it) "保存成功" else "保存失败") { bar ->
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
            showSnackbar(bind.container, "解密失败")
        })

        //加密异常
        vm.encodeError.observe(viewLifecycleOwner, Observer {
            showSnackbar(bind.container, "加密失败")
        })
    }

    override fun onSetListener() {
        //返回上一层
        bind.toolbar.setNavigationOnClickListener {
            onBack()
        }

        //编辑 or 保存
        bind.btnEdit.setOnIntervalClickListener {
            if (vm.isEdit) {
                showSnackbar(bind.container, "是否保存数据？", Snackbar.LENGTH_INDEFINITE) {
                    it.setAction("保存") {
                        //可编辑状态保存数据
                        vm.save(bind.etDomain.getText, bind.etAccount.getText, bind.etPassword.getText, vmGlobal.password)
                        vm.isEdit = false
                    }
                }
            } else {
                //不可编辑状态转换可编辑
                vm.isEdit = true
            }
        }

        //删除
        bind.btnDelete.setOnIntervalClickListener {
            showSnackbar(bind.container, "是否删除该账户？") {
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
        if (mAction.isBackInterceptor(vm)) {
            //编辑状态询问是否取消保存
            showSnackbar(bind.container, "是否取消修改？") {
                it.setAction("确定") {
                    findNavController().popBackStack()
                }
            }
        } else {
            findNavController().popBackStack()
        }
    }

    //差异操作 新建 or 编辑
    private val mAction by lazy {
        if (vm.isCreate) {
            Action.CreateAction
        } else {
            Action.DetailAction
        }
    }

    /**
     * 差异操作接口
     */
    internal sealed class Action {
        /**
         * 是否拦截返回
         */
        abstract fun isBackInterceptor(vm: AccountDetailViewModel): Boolean

        /**
         * 是否显示编辑状态
         */
        abstract fun isShowEditAction(vm: AccountDetailViewModel): Boolean

        /**
         * 新建操作
         */
        object CreateAction : Action() {
            override fun isBackInterceptor(vm: AccountDetailViewModel): Boolean {
                return false
            }

            override fun isShowEditAction(vm: AccountDetailViewModel): Boolean {
                return false
            }
        }

        /**
         * 详情操作
         */
        object DetailAction : Action() {
            override fun isBackInterceptor(vm: AccountDetailViewModel): Boolean {
                return vm.isEdit
            }

            override fun isShowEditAction(vm: AccountDetailViewModel): Boolean {
                return vm.isEdit
            }
        }
    }

}