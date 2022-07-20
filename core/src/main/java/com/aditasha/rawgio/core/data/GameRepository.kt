package com.aditasha.rawgio.core.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.aditasha.rawgio.core.data.local.database.RawgDatabase
import com.aditasha.rawgio.core.data.remote.GameRemoteMediator
import com.aditasha.rawgio.core.data.remote.network.ApiService
import com.aditasha.rawgio.core.domain.model.Favorite
import com.aditasha.rawgio.core.domain.model.Game
import com.aditasha.rawgio.core.domain.repository.IGameRepository
import com.aditasha.rawgio.core.utils.DataMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalPagingApi
class GameRepository @Inject constructor(
    private val rawgDatabase: RawgDatabase,
    private val apiService: ApiService
) : IGameRepository {

    override fun getListGames(query: String): Flow<PagingData<Game>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 10,
                initialLoadSize = 30,
                enablePlaceholders = true,
                prefetchDistance = 30
            ),
            remoteMediator = GameRemoteMediator(rawgDatabase, apiService, query),
            pagingSourceFactory = {
                rawgDatabase.gameDao.getAllGame()
            }
        ).flow
        return pager.map {
            DataMapper.pagingDataEntitiesToDomain(it)
        }
    }

    override fun getFavoriteGame(): Flow<List<Favorite>> =
        rawgDatabase.favoriteDao.getAllFavoriteId().map {
            DataMapper.favoriteEntitiesToDomain(it)
        }

    override fun getFavoriteGameById(gameId: Int): Flow<Favorite?> {
        return rawgDatabase.favoriteDao.getFavoriteById(gameId).map {
            if (it == null) null
            else Favorite(it.id, it.name, it.picture)
        }
    }

    override fun getGameDetail(gameId: Int): Flow<Resource<Game>> = flow {
        emit(Resource.Loading())
        try {
            val client = apiService.getDetail(gameId)
            val data = DataMapper.mapResponsesToDomain(client)
            emit(Resource.Success(data))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.toString()))
        }
    }

    override suspend fun insertFavoriteGame(favorite: Favorite) =
        rawgDatabase.favoriteDao.addFavorite(
            DataMapper.favoriteDomainToEntity(favorite)
        )

    override suspend fun deleteFavoriteGame(gameId: Int) =
        rawgDatabase.favoriteDao.deleteFavorite(gameId)

}