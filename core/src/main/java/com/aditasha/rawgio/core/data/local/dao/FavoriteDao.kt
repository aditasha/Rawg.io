package com.aditasha.rawgio.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aditasha.rawgio.core.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorite")
    fun getAllFavoriteId(): Flow<List<FavoriteEntity>>

    @Query("DELETE FROM favorite WHERE id = :gameId")
    suspend fun deleteFavorite(gameId: Int)

}