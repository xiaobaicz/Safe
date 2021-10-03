package cc.xiaobaicz.safe.util

import android.content.Context
import cc.xiaobaicz.safe.R
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
    suspend fun getTips(context: Context): String {
        return DB.app.getStorageDao().query(Constant.KEY_TIPS)?.value ?: context.getString(R.string.key_input_passwrod)
    }

    /**
     * 设置密码提示
     */
    suspend fun setTips(context: Context, tips: String) {
        val dao = DB.app.getStorageDao()
        val kv = Storage(Constant.KEY_TIPS, if (tips.isEmpty()) context.getString(R.string.key_input_passwrod) else tips)
        if (dao.query(Constant.KEY_TIPS) == null) {
            dao.inserts(kv)
        } else {
            dao.updates(kv)
        }
    }

}