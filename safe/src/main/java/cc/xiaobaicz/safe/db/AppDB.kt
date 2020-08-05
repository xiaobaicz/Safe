package cc.xiaobaicz.safe.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cc.xiaobaicz.safe.db.dao.StorageDao
import cc.xiaobaicz.safe.db.entity.Storage

/**
 * 应用数据库
 * @author LaoBoCheng
 * @date 2020/03/19
 */
@Database(entities = [Storage::class], version = 1)
abstract class AppDB : RoomDatabase() {

    /**
     * Key - Value 接口
     */
    abstract fun getStorageDao(): StorageDao

}