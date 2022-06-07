package com.aditasha.rawgio.core.domain.repository

import androidx.paging.PagingData
import com.aditasha.rawgio.core.data.Resource
import com.aditasha.rawgio.core.domain.model.Favorite
import com.aditasha.rawgio.core.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface IGameRepository {

    fun getListGames(query: String): Flow<PagingData<Game>>

    fun getFavoriteGame(): Flow<List<Favorite>>

    fun getGameDetail(gameId: Int): Flow<Resource<Game>>

    suspend fun insertFavoriteGame(favorite: Favorite)

    suspend fun deleteFavoriteGame(gameId: Int)

}