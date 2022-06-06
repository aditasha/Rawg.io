package com.aditasha.rawgio.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.aditasha.rawgio.core.domain.model.Game
import com.aditasha.rawgio.core.domain.usecase.GameUseCase
import com.aditasha.rawgio.core.presentation.model.GamePresentation
import com.aditasha.rawgio.core.utils.DataMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@HiltViewModel
class SharedViewModel @Inject constructor(private val gameUseCase: GameUseCase) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val queryKey = mutableMapOf<String, String>()

    var fromFragment = ""

    init {
        viewModelScope.launch {
            if (!queryKey.containsKey(QUERY)) {
                queryKey[QUERY] = RELEASED
                queryFlow.emit(RELEASED)
                fromFragment = RELEASED
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val gameList = queryFlow
        .flatMapLatest { gameUseCase.getListGames(it) }
        .map { DataMapper.pagingDataDomainToPresentation(it) }
        .cachedIn(viewModelScope)

    fun addQuery(request: String) {
        if (!checkQuery(request)) return
        viewModelScope.launch {
            queryKey[QUERY] = request
            queryFlow.emit(request)
            fromFragment = request
        }
    }

    private fun checkQuery(request: String): Boolean {
        return queryKey[QUERY] != request
    }

    fun gameDetail(gameId: Int) = gameUseCase.getGameDetail(gameId)

    companion object {
        const val RELEASED = "released"
        const val QUERY = "query"
    }

}