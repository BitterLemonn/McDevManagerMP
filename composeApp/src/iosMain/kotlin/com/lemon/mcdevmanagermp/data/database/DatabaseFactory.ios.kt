package com.lemon.mcdevmanagermp.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import com.lemon.mcdevmanagermp.data.common.DATABASE_NAME
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseFactory {
    actual fun create(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = documentDirectory() + "/$DATABASE_NAME"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        ).setQueryCoroutineContext(Dispatchers.IO)
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }
}