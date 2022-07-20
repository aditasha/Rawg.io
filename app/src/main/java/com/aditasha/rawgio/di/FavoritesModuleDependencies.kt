package com.aditasha.rawgio.di

import com.aditasha.rawgio.core.domain.usecase.GameUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface FavoritesModuleDependencies {

    fun gameUseCase(): GameUseCase
}