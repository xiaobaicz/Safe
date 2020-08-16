package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage
import cc.xiaobaicz.safe.global.Constant

/**
 * 安全助手
 */
object SafeHelper {

    /**
     * 是否已设置密码
     */
    suspend fun hasPassword(): Boolean {
        val kv = DB.app.getStorageDao().query(Constant.KEY_PASSWORD)
        return (kv?.value ?: "").isNotEmpty()
    }

    /**
     * 校验密码
     */
    suspend fun checkPassword(pw: String): Boolean {
        val kv = DB.app.getStorageDao().query(Constant.KEY_PASSWORD) //本地密码
        val now = localHmacMD5(pw) //输入密码
        return now == kv?.value //密码是否一致
    }

    /**
     * 设置密码
     */
    suspend fun setPassword(pw: String) {
        val dao = DB.app.getStorageDao()
        val kv = Storage(Constant.KEY_PASSWORD, localHmacMD5(pw))
        if (hasPassword()) {
            dao.updates(kv)
        } else {
            dao.inserts(kv)
        }
    }

}