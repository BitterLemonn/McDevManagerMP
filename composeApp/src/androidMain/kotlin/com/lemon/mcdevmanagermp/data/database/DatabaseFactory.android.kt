package com.lemon.mcdevmanagermp.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lemon.mcdevmanagermp.data.common.DATABASE_NAME
import kotlinx.coroutines.Dispatchers

actual class DatabaseFactory(private val context: Context) {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath(DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context, name = dbFile.absolutePath
        ).setQueryCoroutineContext(Dispatchers.IO)
    }
}