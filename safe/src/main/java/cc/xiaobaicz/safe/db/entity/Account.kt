package cc.xiaobaicz.safe.db.entity

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
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

    /**
     * 热度
     */
    @NonNull
    @ColumnInfo(name = "hot")
    var hot: Long = 0

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        password = parcel.readString() ?: ""
        lastTime = parcel.readLong()
        hot = parcel.readLong()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(domain)
        parcel.writeString(account)
        parcel.writeValue(id)
        parcel.writeString(password)
        parcel.writeLong(lastTime)
        parcel.writeLong(hot)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Account> {
        override fun createFromParcel(parcel: Parcel): Account {
            return Account(parcel)
        }

        override fun newArray(size: Int): Array<Account?> {
            return arrayOfNulls(size)
        }
    }

}