package com.aditasha.rawgio.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.aditasha.rawgio.MainActivity.Companion.DEFAULT
import com.aditasha.rawgio.MainActivity.Companion.QUERY
import com.aditasha.rawgio.MainActivity.Companion.SEARCH
import com.aditasha.rawgio.core.domain.usecase.GameUseCase
import com.aditasha.rawgio.core.utils.DataMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(private val gameUseCase: GameUseCase) : ViewModel() {

    private val queryFlow = MutableStateFlow("")
    private val queryKey = mutableMapOf<String, String>()

    var searchQuery = ""
    var requestedList = MutableStateFlow(DEFAULT)

    @OptIn(ExperimentalCoroutinesApi::class)
    val gameList = queryFlow
        .flatMapLatest { gameUseCase.getListGames(it) }
        .map { DataMapper.pagingDataDomainToPresentation(it) }
        .cachedIn(viewModelScope)

    fun addQuery(request: String, frag: String? = null) {
        if (request == SEARCH) {
            if (!checkQuery(searchQuery)) return
        } else {
            if (!checkQuery(request)) return
        }
        viewModelScope.launch {
            if (request == SEARCH && frag != null) {
                queryKey[QUERY] = searchQuery
                queryFlow.emit(searchQuery)
                requestedList.emit(frag)
            } else {
                queryKey[QUERY] = request
                queryFlow.emit(request)
                requestedList.emit(request)
            }

        }
    }

    private fun checkQuery(request: String): Boolean {
        return queryKey[QUERY] != request
    }

    fun gameDetail(gameId: Int) = gameUseCase.getGameDetail(gameId)
}