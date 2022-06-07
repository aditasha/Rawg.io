package com.aditasha.rawgio.core.data.remote.network

import com.aditasha.rawgio.core.BuildConfig
import com.aditasha.rawgio.core.data.remote.responses.GamesListResponse
import com.aditasha.rawgio.core.data.remote.responses.GamesResultItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.time.LocalDate

interface ApiService {
    @GET("games")
    suspend fun getListReleased(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("page") page: Int,
        @Query("page_size") size: Int,
        @Query("dates") date: String = toCurrentDate,
        @Query("ordering") ordering: String = "-released"
    ): GamesListResponse

    @GET("games")
    suspend fun getListAdded(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("page") page: Int,
        @Query("page_size") size: Int,
        @Query("ordering") ordering: String = "-added"
    ): GamesListResponse

    @GET("games")
    suspend fun getListSearch(
        @Query("key") key: String = BuildConfig.API_KEY,
        @Query("page") page: Int,
        @Query("page_size") size: Int,
        @Query("search") search: String
    ): GamesListResponse

    @GET("games/{id}")
    suspend fun getDetail(
        @Path("id") id: Int,
        @Query("key") key: String = BuildConfig.API_KEY
    ): GamesResultItem

    companion object {
        val toCurrentDate = "1960-01-01,${LocalDate.now()}"
    }
}