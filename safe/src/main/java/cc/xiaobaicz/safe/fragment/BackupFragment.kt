package cc.xiaobaicz.safe.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import cc.xiaobaicz.safe.databinding.FragmentSettingBackupBinding
import cc.xiaobaicz.safe.model.BackupViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import java.text.SimpleDateFormat
import java.util.*

class BackupFragment : BaseFragment() {

    //打开CSV文件
    private val OPEN_CSV = 0x0001

    //打开PW文件
    private val OPEN_PW = 0x0002

    //新建CSV文件
    private val CREATE_CSV = 0x0004

    //新建PW文件
    private val CREATE_PW = 0x0008

    //CSV后缀
    private val SUFFIX_CSV = "csv"
    //pw后缀
    private val SUFFIX_PW = "pw"

    private val vm by viewModels<BackupViewModel>()

    private val bind by lazy {
        FragmentSettingBackupBinding.inflate(layoutInflater)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind.model = vm
        vm.password = vmGlobal.password //用于导入时加密数据
        return bind.root
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(bind.toolbar)

        //可用状态
        vm.busy.observe(viewLifecycleOwner, Observer {
            bind.layerAwait.isVisible = it
        })

        //导入结果
        vm.import.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                showSnackbar(bind.container, "导入完成")
            } else {
                showSnackbar(bind.container, it.message ?: "导入失败")
            }
        })

        //导出结果
        vm.export.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                showSnackbar(bind.container, "导出完成")
            } else {
                showSnackbar(bind.container, it.message ?: "导出失败")
            }
        })
    }

    override fun onSetListener() {
        //返回键
        bind.toolbar.setNavigationOnClickListener {
            back()
        }

        //返回
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            back()
        }

        //明文导入 .csv
        bind.btnImportCsv.setOnIntervalClickListener {
            openDoc(OPEN_CSV)
        }

        //密文导入 *.pw
        bind.etPassword.setOnEditorActionListener { _, actionId, _ ->
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                //密码合法性校验
                if (vm.checkPassword(bind.etPassword.getText)) {
                    vm.filePassword = bind.etPassword.getText
                    openDoc(OPEN_PW)
                } else {
                    showSnackbar(bind.container, "密码至少6位")
                }
            }
            return@setOnEditorActionListener false
        }

        //明文导出 *.csv
        bind.btnExportCsv.setOnIntervalClickListener {
            createDoc(CREATE_CSV, SUFFIX_CSV)
        }

        //密文导出 *.pw
        bind.btnExportPw.setOnIntervalClickListener {
            createDoc(CREATE_PW, SUFFIX_PW)
        }

        //拦截事件
        bind.layerAwait.setOnClickListener {  }
    }

    //打开文档
    private fun openDoc(req: Int) {
        //权限请求
        vmGlobal.isClose(false)//期间应用不关闭
        Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            startActivityForResult(this, req)
        }
    }

    //新建文档
    private fun createDoc(req: Int, suffix: String) {
        //权限请求
        vmGlobal.isClose(false)//期间应用不关闭
        Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            val time = SimpleDateFormat("YYYYMMdd_HHmmss", Locale.getDefault()).format(Date())
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_TITLE, "backup_${time}.$suffix")
            startActivityForResult(this, req)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //处理完成
            vmGlobal.isClose(true)//获取文档结束，不可见时关闭
            when (requestCode) {
                OPEN_CSV -> {   //CSV文档
                    vm.importCSV(requireContext(), data?.data)
                }
                OPEN_PW -> {    //PW文档
                    vm.importPW(requireContext(), data?.data)
                }
                CREATE_CSV -> {   //CSV文档
                    vm.exportCSVDoc(requireContext(), data?.data)
                }
                CREATE_PW -> {    //PW文档
                    vm.exportPWDoc(requireContext(), data?.data)
                }
            }
        }
    }

    //返回
    private fun back() {
        if (!vm.isBusy) {
            findNavController().popBackStack()
        }
    }

}