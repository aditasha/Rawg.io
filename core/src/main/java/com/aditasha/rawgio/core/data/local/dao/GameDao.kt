package com.aditasha.rawgio.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aditasha.rawgio.core.data.local.entity.GameEntity

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: List<GameEntity>)

    @Query("SELECT * FROM rawg ORDER BY `room_added` ASC")
    fun getAllGame(): PagingSource<Int, GameEntity>

    @Query("DELETE FROM rawg")
    suspend fun deleteAll()

}