package com.aditasha.rawgio.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditasha.rawgio.core.domain.model.Favorite
import com.aditasha.rawgio.core.domain.usecase.GameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val gameUseCase: GameUseCase) : ViewModel() {

    fun addFavorite(
        gameId: Int,
        gameName: String?,
        gamePicture: String?,
        gameScreenshots: MutableList<String>?
    ) = viewModelScope.launch {
        if (gameName != null && gamePicture != null) {
            val favorite = Favorite(gameId, gameName, gamePicture, gameScreenshots)
            gameUseCase.insertFavoriteGame(favorite)
        }
    }

    fun getFavorite(gameId: Int): Flow<Boolean> {
        return gameUseCase.getFavoriteGameById(gameId).map {
            it != null
        }
    }

    fun deleteFavorite(gameId: Int) = viewModelScope.launch {
        gameUseCase.deleteFavoriteGame(gameId)
    }
}