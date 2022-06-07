package com.aditasha.rawgio.core.di

import android.content.Context
import androidx.room.Room
import com.aditasha.rawgio.core.data.local.database.RawgDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): RawgDatabase =
        Room.databaseBuilder(
            context,
            RawgDatabase::class.java,
            "Rawg.db"
        ).fallbackToDestructiveMigration().build()
}