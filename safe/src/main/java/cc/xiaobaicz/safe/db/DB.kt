package cc.xiaobaicz.safe.db

import androidx.room.Room
import cc.xiaobaicz.safe.App

object DB {
    val safe by lazy {
        Room.databaseBuilder(App.app!!, SafeDB::class.java, "safe.db").build()
    }
    val app by lazy {
        Room.databaseBuilder(App.app!!, AppDB::class.java, "app.db").build()
    }
}