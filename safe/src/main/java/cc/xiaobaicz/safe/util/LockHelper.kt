package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage

/**
 * App锁定助手
 */
object LockHelper {

    /**
     * 错误次数 0 1 2 3 4 5
     */
    private const val KEY_FAILURE_NUMBER = "key_failure_number"

    /**
     * 锁定状态 0解锁 / 1锁定
     * @see LOCK
     * @see UNLOCK
     */
    private const val KEY_LOCK_STATUS = "key_lock_status"

    /**
     * 锁定时间
     */
    const val LOCK_TIME = 2 * 60 * 1000L

    /**
     * 锁定状态
     */
    private const val LOCK = "1"

    /**
     * 解锁状态
     */
    private const val UNLOCK = "0"

    /**
     * 锁定
     */
    suspend fun lock() {
        val dao = DB.app.getStorageDao()

        //锁定
        val status = Storage(KEY_LOCK_STATUS, LOCK)
        if (dao.query(KEY_LOCK_STATUS) == null) {
            dao.inserts(status)
        } else {
            dao.updates(status)
        }

        val number = dao.query(KEY_FAILURE_NUMBER)
        //失败次数+1
        if (number == null) {
            dao.inserts(Storage(KEY_FAILURE_NUMBER, "1"))
        } else {
            dao.updates(Storage(KEY_FAILURE_NUMBER, "${number.value!!.toInt() + 1}"))
        }
    }

    /**
     * 解锁
     */
    suspend fun unlock() {
        val dao = DB.app.getStorageDao()

        //解锁
        val status = Storage(KEY_LOCK_STATUS, UNLOCK)
        if (dao.query(KEY_LOCK_STATUS) == null) {
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
        unlock()

        val number = dao.query(KEY_FAILURE_NUMBER)
        //失败次数重置 0 次
        if (number == null) {
            dao.inserts(Storage(KEY_FAILURE_NUMBER, "0"))
        } else {
            dao.updates(Storage(KEY_FAILURE_NUMBER, "0"))
        }
    }

    /**
     * 是否永久锁定
     */
    suspend fun isForeverLock(): Boolean {
        val dao = DB.app.getStorageDao()
        val number = (dao.query(KEY_FAILURE_NUMBER)?.value ?: "0").toInt() //默认0次
        return number >= 5 //失败次数超过5次，永久锁定
    }

    /**
     * 是否锁定
     */
    suspend fun isLock() = DB.app.getStorageDao().query(KEY_LOCK_STATUS)?.value == LOCK

}