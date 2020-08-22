package cc.xiaobaicz.safe.db.dao

import androidx.room.*
import cc.xiaobaicz.safe.db.entity.Account

/**
 * 账户表Dao
 */
@Dao
interface AccountDao {

    /**
     * 查询所有账户
     */
    @Ignore
    @Query("select * from account;")
    suspend fun selectAll(): List<Account>

    /**
     * 条件查询
     * 模糊查询 账户域 & 账户
     */
    @Query("select * from account where domain like :key union select * from account where account like :key;")
    suspend fun select(key: String): List<Account>

    /**
     * 添加账户
     */
    @Insert
    suspend fun insert(obj: Account): Long

    /**
     * 批量添加账户
     */
    @Transaction
    suspend fun insert(obj: List<Account>) {
        obj.forEach {
            insert(it)
        }
    }

    /**
     * 更新账户
     */
    @Update
    suspend fun update(obj: Account): Int

    /**
     * 批量更新账户
     */
    @Transaction
    suspend fun update(obj: List<Account>) {
        obj.forEach {
            update(it)
        }
    }

    /**
     * 删除账户
     */
    @Delete
    suspend fun delete(obj: Account): Int

}