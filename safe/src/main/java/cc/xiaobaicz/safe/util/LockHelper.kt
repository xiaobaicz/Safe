package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage
import kotlinx.coroutines.flow.flowOf

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
     * 常规错误最大次数
     */
    const val MAX_ERROR_ROUTINE = 3

    /**
     * 错误最大次数
     */
    const val MAX_ERROR = 6

    /**
     * 锁定
     */
    suspend fun lock() {
        val dao = DB.app.getStorageDao()

        val number = dao.query(KEY_FAILURE_NUMBER)
        //失败次数+1
        if (number == null) {
            dao.inserts(Storage(KEY_FAILURE_NUMBER, "1"))
        } else {
            val count = number.value!!.toInt() + 1
            dao.updates(Storage(KEY_FAILURE_NUMBER, "$count"))

            //失败超过3次锁定
            if (count >= MAX_ERROR_ROUTINE ) {
                //锁定
                val status = Storage(KEY_LOCK_STATUS, LOCK)
                if (dao.query(KEY_LOCK_STATUS) == null) {
                    dao.inserts(status)
                } else {
                    dao.updates(status)
                }
            }
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
        return lockCount() >= MAX_ERROR //失败次数超过6次，永久锁定
    }

    /**
     * 是否永久锁定
     */
    suspend fun lockCount(): Int {
        val dao = DB.app.getStorageDao()
        val number = (dao.query(KEY_FAILURE_NUMBER)?.value ?: "0").toInt() //默认0次
        return number
    }

    /**
     * 是否锁定
     */
    suspend fun isLock() = DB.app.getStorageDao().query(KEY_LOCK_STATUS)?.value == LOCK

}