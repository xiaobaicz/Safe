package cc.xiaobaicz.safe.fragment

import android.Manifest
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
import cc.xiaobaicz.permissions.apply
import cc.xiaobaicz.permissions.openAppSettings
import cc.xiaobaicz.safe.R
import cc.xiaobaicz.safe.model.BackupViewModel
import cc.xiaobaicz.safe.util.getText
import cc.xiaobaicz.safe.util.setOnIntervalClickListener
import kotlinx.android.synthetic.main.fragment_setting_backup.*
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        vm.password = vmGlobal.password //用于导入时加密数据
        return inflater.inflate(R.layout.fragment_setting_backup, container, false)
    }

    override fun onConfigView(view: View) {
        //设置安全区域
        safeRegion(toolbar, content)

        //可用状态
        vm.busy.observe(viewLifecycleOwner, Observer {
            layer_await.isVisible = it
        })

        //导入结果
        vm.import.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                showSnackbar(container, "导入完成")
            } else {
                showSnackbar(container, it.message ?: "导入失败")
            }
        })

        //导出结果
        vm.export.observe(viewLifecycleOwner, Observer {
            if (it == null) {
                showSnackbar(container, "导出完成")
            } else {
                showSnackbar(container, it.message ?: "导出失败")
            }
        })
    }

    override fun onSetListener() {
        //返回键
        toolbar.setNavigationOnClickListener {
            back()
        }

        //返回
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            back()
        }

        //明文导入 .csv
        btn_import_csv.setOnIntervalClickListener {
            openDoc(OPEN_CSV)
        }

        //密文导入 *.pw
        et_password.setOnEditorActionListener { _, actionId, _ ->
            if (EditorInfo.IME_ACTION_DONE == actionId) {
                //密码合法性校验
                if (vm.checkPassword(et_password.getText)) {
                    vm.filePassword = et_password.getText
                    openDoc(OPEN_PW)
                } else {
                    showSnackbar(container, "密码至少6位")
                }
            }
            return@setOnEditorActionListener false
        }

        //明文导出 *.csv
        btn_export_csv.setOnIntervalClickListener {
            createDoc(CREATE_CSV, SUFFIX_CSV)
        }

        //密文导出 *.pw
        btn_export_pw.setOnIntervalClickListener {
            createDoc(CREATE_PW, SUFFIX_PW)
        }

        //拦截事件
        layer_await.setOnClickListener {  }
    }

    //打开文档
    private fun openDoc(req: Int) {
        //权限请求
        vmGlobal.isClose(false)//期间应用不关闭
        requireActivity().apply(Manifest.permission.READ_EXTERNAL_STORAGE) { r, f ->
            if (r.isEmpty()) {
                //获取权限
                Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    startActivityForResult(this, req)
                }
                return@apply
            }
            if (f.isNotEmpty()) {
                //永久关闭，提示用户打开
                showSnackbar(container, "导入需文件读取权限，请打开") {
                    it.setAction("设置") {
                        //前往设置
                        requireActivity().openAppSettings()
                    }
                }
                return@apply
            }
            //提示用户需要权限
            showSnackbar(container, "导入需文件读取权限")
            vmGlobal.isClose(true)//应用关闭
        }
    }

    //新建文档
    private fun createDoc(req: Int, suffix: String) {
        //权限请求
        vmGlobal.isClose(false)//期间应用不关闭
        requireActivity().apply(Manifest.permission.WRITE_EXTERNAL_STORAGE) { r, f ->
            if (r.isEmpty()) {
                //获取权限
                Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                    val time = SimpleDateFormat("yyMMdd_hhmmss", Locale.getDefault()).format(Date())
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "*/*"
                    putExtra(Intent.EXTRA_TITLE, "backup_${time}.$suffix")
                    startActivityForResult(this, req)
                }
                return@apply
            }
            if (f.isNotEmpty()) {
                //永久关闭，提示用户打开
                showSnackbar(container, "导出需文件写入权限，请打开") {
                    it.setAction("设置") {
                        //前往设置
                        requireActivity().openAppSettings()
                    }
                }
                return@apply
            }
            //提示用户需要权限
            showSnackbar(container, "导出需文件写入权限")
            vmGlobal.isClose(true)//应用关闭
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