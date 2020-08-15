package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage
import cc.xiaobaicz.safe.global.Constant

/**
 * App锁定助手
 */
object LockHelper {

    /**
     * 锁定
     */
    suspend fun lock() {
        val dao = DB.app.getStorageDao()

        //锁定
        val status = Storage(Constant.KEY_LOCK_STATUS, "1")
        if (dao.query(Constant.KEY_LOCK_STATUS) == null) {
            dao.inserts(status)
        } else {
            dao.updates(status)
        }

        val number = dao.query(Constant.KEY_FAILURE_NUMBER)
        //失败次数+1
        if (number == null) {
            dao.inserts(Storage(Constant.KEY_FAILURE_NUMBER, "1"))
        } else {
            dao.updates(Storage(Constant.KEY_FAILURE_NUMBER, "${number.value!!.toInt() + 1}"))
        }
    }

    /**
     * 解锁
     */
    suspend fun unlock() {
        val dao = DB.app.getStorageDao()

        //解锁
        val status = Storage(Constant.KEY_LOCK_STATUS, "0")
        if (dao.query(Constant.KEY_LOCK_STATUS) == null) {
            dao.inserts(status)
        } else {
            dao.updates(status)
        }
    }

    /**
     * 解锁
     */
    suspend fun unlockAndReset() {
        val dao = DB.app.getStorageDao()

        //解锁
        val status = Storage(Constant.KEY_LOCK_STATUS, "0")
        if (dao.query(Constant.KEY_LOCK_STATUS) == null) {
            dao.inserts(status)
        } else {
            dao.updates(status)
        }

        val number = dao.query(Constant.KEY_FAILURE_NUMBER)
        //失败次数重置
        if (number == null) {
            dao.inserts(Storage(Constant.KEY_FAILURE_NUMBER, "0"))
        } else {
            dao.updates(Storage(Constant.KEY_FAILURE_NUMBER, "0"))
        }
    }

    /**
     * 是否永久锁定
     */
    suspend fun isForeverLock(): Boolean {
        val dao = DB.app.getStorageDao()
        val number = (dao.query(Constant.KEY_FAILURE_NUMBER)?.value ?: "0").toInt()
        return number >= 5 //失败次数超过5次，永久锁定
    }

    /**
     * 是否锁定
     */
    suspend fun isLock() = DB.app.getStorageDao().query(Constant.KEY_LOCK_STATUS)?.value == "1"

}