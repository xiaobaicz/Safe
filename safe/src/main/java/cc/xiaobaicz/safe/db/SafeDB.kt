package cc.xiaobaicz.safe.db

import androidx.room.Database
import androidx.room.RoomDatabase
import cc.xiaobaicz.safe.db.dao.AccountDao
import cc.xiaobaicz.safe.db.entity.Account

/**
 * 主库
 */
@Database(entities = [Account::class], version = 1)
abstract class SafeDB : RoomDatabase() {

    abstract fun accountDao(): AccountDao

}