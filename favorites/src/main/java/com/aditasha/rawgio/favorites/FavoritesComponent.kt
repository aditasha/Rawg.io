package com.aditasha.rawgio.favorites

import android.content.Context
import com.aditasha.rawgio.di.FavoritesModuleDependencies
import dagger.BindsInstance
import dagger.Component

@Component(dependencies = [FavoritesModuleDependencies::class])
interface FavoritesComponent {

    fun inject(activity: FavoriteActivity)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(favoritesModuleDependencies: FavoritesModuleDependencies): Builder
        fun build(): FavoritesComponent
    }

}