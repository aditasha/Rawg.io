package com.aditasha.rawgio.core.domain.usecase

import androidx.paging.PagingData
import com.aditasha.rawgio.core.data.Resource
import com.aditasha.rawgio.core.domain.model.Favorite
import com.aditasha.rawgio.core.domain.model.Remote
import com.aditasha.rawgio.core.domain.model.Game
import com.aditasha.rawgio.core.domain.repository.IGameRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class GameInteractor @Inject constructor(private val gameRepository: IGameRepository): GameUseCase {
    override fun getListGames(query: String) = gameRepository.getListGames(query)

    override fun getFavoriteGame(): Flow<List<Favorite>> = gameRepository.getFavoriteGame()

    override fun getGameDetail(gameId: Int): Flow<Resource<Game>> = gameRepository.getGameDetail(gameId)

    override suspend fun insertFavoriteGame(favorite: Favorite) = gameRepository.insertFavoriteGame(favorite)

    override suspend fun deleteFavoriteGame(gameId: Int) = gameRepository.deleteFavoriteGame(gameId)


}