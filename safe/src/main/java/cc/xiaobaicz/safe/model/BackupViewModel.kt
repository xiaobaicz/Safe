package cc.xiaobaicz.safe.model

import android.content.Context
import android.net.Uri
import android.os.CancellationSignal
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cc.xiaobaicz.safe.db.entity.Account
import cc.xiaobaicz.safe.util.AccountHelper
import cc.xiaobaicz.safe.util.CipherHelper
import cc.xiaobaicz.safe.util.GSON
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume

class BackupViewModel : ViewModel() {

    /**
     * 密码初始化时需要赋值
     */
    var password = ""

    /**
     * 密码文件导入时的密码
     */
    var filePassword = ""

    /**
     * 可用状态
     */
    val busy by lazy {
        MutableLiveData<Boolean>(false)
    }

    //可用状态
    val isBusy get() = busy.value ?: false

    /**
     * 导入结果
     */
    val import by lazy {
        MutableLiveData<Exception?>()
    }

    /**
     * 导出结果
     */
    val export by lazy {
        MutableLiveData<Exception?>()
    }

    /**
     * 导入CSV
     */
    fun importCSV(context: Context, data: Uri?) {
        viewModelScope.launch {
            try {
                //繁忙
                busy.postValue(true)
                val fd = getFD(context, data)
                //解析CSV
                val accounts = analyzeCSV(fd)
                //保存数据
                save(accounts)
                import.postValue(null)
            } catch (e: Exception) {
                import.postValue(e)
            } finally {
                //空闲
                busy.postValue(false)
            }
        }
    }

    /**
     * 导入PW
     */
    fun importPW(context: Context, data: Uri?) {
        viewModelScope.launch {
            try {
                //繁忙
                busy.postValue(true)
                val fd = getFD(context, data)
                //读取文件内容
                val content = readAll(fd)
                //解析密文文件
                val accounts = analyzePW(content)
                try {
                    //通过文件密码解密
                    accounts.forEach {
                        it.id = 0 //重置id 防止冲突
                        it.domain = CipherHelper.aesDecipher(it.domain, filePassword)
                        it.account = CipherHelper.aesDecipher(it.account, filePassword)
                        it.password = CipherHelper.aesDecipher(it.password, filePassword)
                    }
                } catch (e: Exception) {
                    //解密异常，密码错误
                    import.postValue(FilePasswordException())
                    return@launch
                }
                //保存数据
                save(accounts)
                import.postValue(null)
            } catch (e: Exception) {
                import.postValue(e)
            } finally {
                //空闲
                busy.postValue(false)
            }
        }
    }

    //读取文件全部内容
    private suspend fun readAll(fd: FileDescriptor) = suspendCancellableCoroutine<String> {
        viewModelScope.launch(Dispatchers.IO) {
            val byteBuff = ByteArrayOutputStream()
            val buff = ByteArray(1024) //缓冲区
            var size: Int //缓冲区可用大小
            FileInputStream(fd).use { fin ->
                BufferedInputStream(fin).use { bin ->
                    size = bin.read(buff)
                    while (size > 0) {
                        byteBuff.write(buff, 0, size)
                        size = bin.read(buff)
                    }
                }
            }
            it.resume(byteBuff.toString(Charsets.UTF_8.name()))
        }
    }

    //校验输入
    private fun check(uri: Uri?): Boolean {
        if (uri == null) {
            return false
        }
        return true
    }

    //获取文件描述符 默认只读模式
    private fun getFD(context: Context, uri: Uri?, mode: String = "r"): FileDescriptor {
        if (!check(uri)) {
            throw FileNotFoundException("找不到文件")
        }
        return context.contentResolver.openFileDescriptor(uri!!, mode, CancellationSignal())?.fileDescriptor ?: throw FileNotFoundException("文件打开失败")
    }

    //CSV析构
    private suspend fun analyzeCSV(fd: FileDescriptor) = suspendCancellableCoroutine<List<Account>> {
        viewModelScope.launch(Dispatchers.IO) {
            val accounts = ArrayList<Account>()
            try {
                FileReader(fd).use { fread ->
                    CSVFormat.EXCEL.parse(fread).use { csv ->
                        csv.forEach { v ->
                            if (v.count() != 3) {
                                throw CSVFormatException()
                            }
                            val account = Account(v[0], v[1])
                            account.password = v[2]
                            accounts.add(account)
                        }
                    }
                }
            } catch (e: Exception) {
                //文件格式错误
                import.postValue(CSVFormatException())
                busy.postValue(false)
                return@launch
            }
            it.resume(accounts)
        }
    }

    //PW析构
    private suspend fun analyzePW(content: String) = suspendCancellableCoroutine<List<Account>> {
        viewModelScope.launch(Dispatchers.IO) {
            it.resume(GSON.fromJson(content, object : TypeToken<List<Account>>(){}.type))
        }
    }

    //批量保存
    private suspend fun save(accounts: List<Account>) {
        try {
            accounts.forEach {
                //保存前先加密
                it.password = CipherHelper.aesEncipher(it.password, password)
            }
            AccountHelper.saveAccount(accounts)
        } catch (t: Throwable) {
            throw Exception("保存失败")
        }
    }

    /**
     * 导出CSV
     */
    fun exportCSVDoc(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //繁忙
                busy.postValue(true)
                val time = SimpleDateFormat("yyMMdd_hhmmss", Locale.getDefault()).format(Date())
                //文件名 backup + 时间
                val file = createFile(context.getExternalFilesDir("backup")!!, "backup_${time}.csv")
                FileWriter(file).use { fileWriter ->
                    CSVPrinter(fileWriter, CSVFormat.EXCEL).use { print ->
                        AccountHelper.selectAll().forEach {
                            print.printRecord(it.domain, it.account, CipherHelper.aesDecipher(it.password, password))
                        }
                    }
                }
                export.postValue(null)
            } catch (e: Exception) {
                export.postValue(Exception("导出失败"))
            } finally {
                //空闲
                busy.postValue(false)
            }
        }
    }

    /**
     * 导出PW
     */
    fun exportPWDoc(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                //繁忙
                busy.postValue(true)
                val time = SimpleDateFormat("yyMMdd_hhmmss", Locale.getDefault()).format(Date())
                //文件名 backup + 时间
                val file = createFile(context.getExternalFilesDir("backup")!!, "backup_${time}.pw")
                //全字段加密
                val accounts = AccountHelper.selectAll().apply {
                    forEach {
                        it.domain = CipherHelper.aesEncipher(it.domain, password)
                        it.account = CipherHelper.aesEncipher(it.account, password)
                    }
                }
                val content = GSON.toJson(accounts)
                FileWriter(file).use { fileWriter ->
                    fileWriter.write(content)
                }
                export.postValue(null)
            } catch (e: Exception) {
                export.postValue(Exception("导出失败"))
            } finally {
                //空闲
                busy.postValue(false)
            }
        }
    }

    //生成输出文件
    private fun createFile(path: File, name: String): File {
        if (!path.exists()) {
            path.mkdirs()
        }
        val file = File(path, name)
        if (file.exists()) {
            file.createNewFile()
        }
        return file
    }

    /**
     * 校验密码
     */
    fun checkPassword(text: String): Boolean {
        return text.length >= 6
    }

    /**
     * CSV格式错误
     */
    class CSVFormatException(msg: String = "CSV 格式错误") : Exception(msg)

    /**
     * 文件密码错误
     */
    class FilePasswordException(msg: String = "文件密码错误") : Exception(msg)

}