package com.aditasha.rawgio.core.data.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite")
data class FavoriteEntity (
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int
)