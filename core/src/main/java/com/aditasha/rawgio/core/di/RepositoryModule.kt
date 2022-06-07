package com.aditasha.rawgio.core.di

import androidx.paging.ExperimentalPagingApi
import com.aditasha.rawgio.core.data.GameRepository
import com.aditasha.rawgio.core.domain.repository.IGameRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DatabaseModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @OptIn(ExperimentalPagingApi::class)
    @Binds
    abstract fun provideRepository(gameRepository: GameRepository): IGameRepository

}