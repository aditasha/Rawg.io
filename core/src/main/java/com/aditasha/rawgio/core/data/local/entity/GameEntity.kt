package com.aditasha.rawgio.core.data.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rawg")
data class GameEntity(
    @ColumnInfo(name = "room_added")
    var room_added: Int,

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    var id: Int,

    @ColumnInfo(name = "name")
    var name: String? = null,

    @ColumnInfo(name = "background")
    var background: String? = null,

    @ColumnInfo(name = "added")
    var added: Int? = null,

    @ColumnInfo(name = "rating")
    var rating: Double? = null,

    @ColumnInfo(name = "ratings_count")
    var ratings_count: Int? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "platforms")
    var platforms: MutableList<String>? = null,

    @ColumnInfo(name = "genre")
    var genre: MutableList<String>? = null,

    @ColumnInfo(name = "release")
    var release: String? = null,

    @ColumnInfo(name = "developers")
    var developers: MutableList<String>? = null,

    @ColumnInfo(name = "publishers")
    var publishers: MutableList<String>? = null,

    @ColumnInfo(name = "website")
    var website: String? = null,

    @ColumnInfo(name = "stores")
    var stores: MutableList<String>? = null,

    @ColumnInfo(name = "screenshots")
    var screenshots: MutableList<String>? = null
)
