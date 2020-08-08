package cc.xiaobaicz.safe.db.entity

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 账户表
 */
@Entity(tableName = "account")
data class Account(
    /**
     * 账户域
     */
    @NonNull
    @ColumnInfo(name = "domain", index = true)
    var domain: String,

    /**
     * 账户
     */
    @NonNull
    @ColumnInfo(name = "account", index = true)
    var account: String
) {
    /**
     * id
     */
    @Nullable
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int? = null

    /**
     * 密码
     */
    @Nullable
    @ColumnInfo(name = "password")
    var password: String = ""

    /**
     * 最后修改时间
     */
    @NonNull
    @ColumnInfo(name = "last_time")
    var lastTime: Long = System.currentTimeMillis()
}