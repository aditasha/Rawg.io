package com.aditasha.rawgio.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditasha.rawgio.core.domain.usecase.GameUseCase
import com.aditasha.rawgio.core.presentation.model.FavoritePresentation
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FavoriteViewModel(private val gameUseCase: GameUseCase) : ViewModel() {

    private val _favoriteFlow = MutableSharedFlow<List<FavoritePresentation>>()
    val favoriteFlow = _favoriteFlow.asSharedFlow()

    init {
        getAllFavorite()
    }
    fun getAllFavorite() {
        viewModelScope.launch {
            gameUseCase.getFavoriteGame()
                .first { list ->
                    _favoriteFlow.emit(list.map {
                        FavoritePresentation(
                            it.id,
                            it.name,
                            it.picture,
                            it.screenshots
                        )
                    })
                    true
                }
        }
    }

    fun gameDetail(gameId: Int) = gameUseCase.getGameDetail(gameId)
}