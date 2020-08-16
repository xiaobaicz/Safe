package cc.xiaobaicz.safe.util

import cc.xiaobaicz.safe.db.DB
import cc.xiaobaicz.safe.db.entity.Storage
import cc.xiaobaicz.safe.global.Constant

/**
 * 密码提示助手
 */
object TipsHelper {

    /**
     * 获取密码提示
     */
    suspend fun getTips(): String {
        return DB.app.getStorageDao().query(Constant.KEY_TIPS)?.value ?: "输入密码"
    }

    /**
     * 设置密码提示
     */
    suspend fun setTips(tips: String) {
        val dao = DB.app.getStorageDao()
        val kv = Storage(Constant.KEY_TIPS, if (tips.isEmpty()) "输入密码" else tips)
        if (dao.query(Constant.KEY_TIPS) == null) {
            dao.inserts(kv)
        } else {
            dao.updates(kv)
        }
    }

}