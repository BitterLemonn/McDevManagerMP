package com.lemon.mcdevmanagermp.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.lemon.mcdevmanagermp.data.AppConstant
import java.io.File

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = File(System.getProperty("java.io.tmpdir"), AppConstant.DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath,
        )
    }
}
