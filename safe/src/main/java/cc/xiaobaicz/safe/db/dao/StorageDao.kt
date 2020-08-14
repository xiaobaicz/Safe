package cc.xiaobaicz.safe.db.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import cc.xiaobaicz.safe.db.entity.Storage

/**
 * Key - Value 缓存表接口
 * @author LaoBoCheng
 * @date 2020/03/19
 */
@Dao
interface StorageDao {

    @Insert
    suspend fun inserts(vararg item: Storage): Array<Long>

    @Delete
    suspend fun deletes(vararg item: Storage): Int

    @Update
    suspend fun updates(vararg item: Storage): Int

    @Query("select * from Storage where `key` = :key limit 1")
    suspend fun query(key: String): Storage?

}