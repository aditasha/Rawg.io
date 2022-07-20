package com.aditasha.rawgio.core.data.remote

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.aditasha.rawgio.core.BuildConfig
import com.aditasha.rawgio.core.data.local.database.RawgDatabase
import com.aditasha.rawgio.core.data.local.entity.GameEntity
import com.aditasha.rawgio.core.data.local.entity.RemoteKeysEntity
import com.aditasha.rawgio.core.data.remote.network.ApiService
import com.aditasha.rawgio.core.utils.DataMapper
import javax.inject.Inject

@ExperimentalPagingApi
class GameRemoteMediator @Inject constructor(
    private val rawgDatabase: RawgDatabase,
    private val apiService: ApiService,
    private val query: String
) : RemoteMediator<Int, GameEntity>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, GameEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = false)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
                nextKey
            }
        }

        return try {
            val responseData =
                when (query) {
                    "released" -> {
                        apiService.getListReleased(
                            BuildConfig.API_KEY, page,
                            when (loadType) {
                                LoadType.REFRESH -> state.config.initialLoadSize
                                else -> state.config.pageSize
                            }
                        )
                    }
                    "added" -> {
                        apiService.getListAdded(
                            BuildConfig.API_KEY, page,
                            when (loadType) {
                                LoadType.REFRESH -> state.config.initialLoadSize
                                else -> state.config.pageSize
                            }
                        )
                    }
                    else -> {
                        apiService.getListSearch(
                            BuildConfig.API_KEY, page,
                            when (loadType) {
                                LoadType.REFRESH -> state.config.initialLoadSize
                                else -> state.config.pageSize
                            }, query
                        )
                    }
                }

            val endOfPaginationReached =
                responseData.next == null
                        || responseData.results.isEmpty()

            rawgDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    rawgDatabase.remoteKeysDao.deleteRemoteKeys()
                    rawgDatabase.gameDao.deleteAll()
                    counter = 1
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endOfPaginationReached) null else page + 1
                val keys = responseData.results.map {
                    RemoteKeysEntity(id = it.id.toString(), prevKey = prevKey, nextKey = nextKey)
                }
                rawgDatabase.remoteKeysDao.insertAll(keys)
                val entity = DataMapper.mapResponsesToEntities(responseData.results, counter)
                rawgDatabase.gameDao.insertGame(entity)
                counter += entity.size + 1
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (exception: Exception) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, GameEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            rawgDatabase.remoteKeysDao.getRemoteKeysId(data.id.toString())
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, GameEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            rawgDatabase.remoteKeysDao.getRemoteKeysId(data.id.toString())
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, GameEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                rawgDatabase.remoteKeysDao.getRemoteKeysId(id.toString())
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
        var counter = 1
    }

}