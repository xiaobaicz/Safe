package cc.xiaobaicz.safe.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Key - Value 缓存表
 * @author LaoBoCheng
 * @date 2020/03/19
 */
@Entity
data class Storage (
    @PrimaryKey(autoGenerate = true) val key: String,
    val value: String?
)