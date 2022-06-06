package com.aditasha.rawgio.core.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.aditasha.rawgio.core.data.local.dao.FavoriteDao
import com.aditasha.rawgio.core.data.local.dao.GameDao
import com.aditasha.rawgio.core.data.local.dao.RemoteKeysDao
import com.aditasha.rawgio.core.data.local.entity.FavoriteEntity
import com.aditasha.rawgio.core.data.local.entity.GameEntity
import com.aditasha.rawgio.core.data.local.entity.RemoteKeysEntity

@Database(
    entities = [GameEntity::class, RemoteKeysEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DataConverter::class)
abstract class RawgDatabase : RoomDatabase() {

    abstract val gameDao: GameDao
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val favoriteDao: FavoriteDao

}