package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Account

/**
 * 账户助手
 */
object AccountHelper {

    /**
     * 账户保存
     */
    suspend fun saveAccount(account: Account): Long {
        val dao = DB.safe.accountDao()
        return dao.insert(account)
    }

    /**
     * 批量账户保存
     */
    suspend fun saveAccount(account: List<Account>) {
        val dao = DB.safe.accountDao()
        dao.insert(account)
    }

    /**
     * 账户更新
     */
    suspend fun updateAccount(account: Account): Long {
        val dao = DB.safe.accountDao()
        return dao.update(account).toLong()
    }

    /**
     * 重新加密数据，更换密码
     */
    suspend fun reEncipher(pw: String, pw1: String) {
        val dao = DB.safe.accountDao()
        val accounts = ArrayList<Account>()
        dao.selectAll().forEach {
            it.password = CipherHelper.aesDecipher(it.password, pw)
            it.password = CipherHelper.aesEncipher(it.password, pw1)
            accounts.add(it)
        }
        dao.update(accounts)
    }

    /**
     * 查询所有账户
     */
    suspend fun selectAll(): List<Account> {
        val dao = DB.safe.accountDao()
        return dao.selectAll()
    }

    /**
     * 删除账户
     */
    suspend fun delete(account: Account): Int {
        return DB.safe.accountDao().delete(account)
    }

}