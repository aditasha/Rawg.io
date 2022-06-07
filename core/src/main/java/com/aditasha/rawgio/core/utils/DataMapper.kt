package com.aditasha.rawgio.core.utils

import android.util.Log
import androidx.paging.PagingData
import androidx.paging.map
import com.aditasha.rawgio.core.data.local.entity.FavoriteEntity
import com.aditasha.rawgio.core.data.local.entity.GameEntity
import com.aditasha.rawgio.core.data.remote.responses.GamesResultItem
import com.aditasha.rawgio.core.domain.model.Favorite
import com.aditasha.rawgio.core.domain.model.Game
import com.aditasha.rawgio.core.presentation.model.GamePresentation


object DataMapper {
    fun mapResponsesToEntities(input: List<GamesResultItem>, startCounter: Int): List<GameEntity> {
        val gameList = mutableListOf<GameEntity>()
        var counter = startCounter

        for (i in input) {
            val listPlatforms = mutableListOf<String>()
            val listGenres = mutableListOf<String>()
            val listDevelopers = mutableListOf<String>()
            val listPublishers = mutableListOf<String>()
            val listStores = mutableListOf<String>()
            val listImage = mutableListOf<String>()
            var desc = ""
            var web = ""

            if (i.platforms != null && i.platforms.isNotEmpty()) {
                for (p in i.platforms) {
                    listPlatforms.add(p.platform.name)
                }
            }

            if (i.genres != null && i.genres.isNotEmpty()) {
                for (g in i.genres) {
                    listGenres.add(g.name)
                }
            }

            if (i.stores != null && i.stores.isNotEmpty()) {
                for (s in i.stores) {
                    listStores.add(s.store.name)
                }
            }

            if (i.shortScreenshots != null && i.shortScreenshots.isNotEmpty()) {
                for (s in i.shortScreenshots) {
                    listImage.add(s.image)
                }
            }

            if (i.developers != null && i.developers.isNotEmpty()) {
                for (d in i.developers) {
                    listDevelopers.add(d.name)
                }
            }

            if (i.publishers != null && i.publishers.isNotEmpty()) {
                for (p in i.publishers) {
                    listPublishers.add(p.name)
                }
            }

            if (i.description != null && i.description.isNotEmpty()) {
                desc = i.description
            }

            if (i.website != null && i.website.isNotEmpty()) {
                web = i.website
            }

            val game = GameEntity(
                room_added = counter,
                id = i.id,
                name = i.name,
                background = i.backgroundImage,
                added = i.added,
                rating = i.rating,
                ratings_count = i.ratingsCount,
                description = desc,
                platforms = listPlatforms,
                genre = listGenres,
                release = i.released,
                developers = listDevelopers,
                publishers = listPublishers,
                website = web,
                stores = listStores,
                screenshots = listImage
            )
            counter++
            gameList.add(game)
        }

        return gameList
    }

    fun mapResponsesToDomain(input: GamesResultItem): Game {
        val listPlatforms = mutableListOf<String>()
        val listGenres = mutableListOf<String>()
        val listDevelopers = mutableListOf<String>()
        val listPublishers = mutableListOf<String>()
        val listStores = mutableListOf<String>()
        val listImage = mutableListOf<String>()
        var desc = ""
        var web = ""

        if (input.platforms != null && input.platforms.isNotEmpty()) {
            for (p in input.platforms) {
                listPlatforms.add(p.platform.name)
            }
        }

        if (input.genres != null && input.genres.isNotEmpty()) {
            for (g in input.genres) {
                listGenres.add(g.name)
            }
        }

        if (input.stores != null && input.stores.isNotEmpty()) {
            for (s in input.stores) {
                listStores.add(s.store.name)
            }
        }

        if (input.shortScreenshots != null && input.shortScreenshots.isNotEmpty()) {
            for (s in input.shortScreenshots) {
                listImage.add(s.image)
            }
        }

        if (input.developers != null && input.developers.isNotEmpty()) {
            for (d in input.developers) {
                listDevelopers.add(d.name)
            }
        }

        if (input.publishers != null && input.publishers.isNotEmpty()) {
            for (p in input.publishers) {
                listPublishers.add(p.name)
            }
        }

        if (input.description != null && input.description.isNotEmpty()) {
            desc = input.description
        }

        if (input.website != null && input.website.isNotEmpty()) {
            web = input.website
        }

        return Game(
            id = input.id,
            name = input.name,
            background = input.backgroundImage,
            added = input.added,
            rating = input.rating,
            ratings_count = input.ratingsCount,
            description = desc,
            platforms = listPlatforms,
            genre = listGenres,
            release = input.released,
            developers = listDevelopers,
            publishers = listPublishers,
            website = web,
            stores = listStores,
            screenshots = listImage
        )
    }

    fun mapDomainToPresentation(input: Game): GamePresentation =
        GamePresentation(
            id = input.id,
            name = input.name,
            background = input.background,
            added = input.added,
            rating = input.rating,
            ratings_count = input.ratings_count,
            description = input.description,
            platforms = input.platforms,
            genre = input.genre,
            release = input.release,
            developers = input.developers,
            publishers = input.publishers,
            website = input.website,
            stores = input.stores,
            screenshots = input.screenshots
        )

    fun favoriteEntitiesToDomain(input: List<FavoriteEntity>): List<Favorite> =
        input.map {
            Favorite(it.id)
        }

    fun pagingDataEntitiesToDomain(input: PagingData<GameEntity>): PagingData<Game> {
        Log.d("test_dataMap", input.toString())
        return input.map {
            Game(
                id = it.id,
                name = it.name,
                background = it.background,
                added = it.added,
                rating = it.rating,
                ratings_count = it.ratings_count,
                description = it.description,
                platforms = it.platforms,
                genre = it.genre,
                release = it.release,
                developers = it.developers,
                publishers = it.publishers,
                website = it.website,
                stores = it.stores,
                screenshots = it.screenshots
            )
        }
    }

    fun favoriteDomainToEntity(input: Favorite) = FavoriteEntity(
        id = input.id
    )

    fun pagingDataDomainToPresentation(input: PagingData<Game>): PagingData<GamePresentation> =
        input.map {
            GamePresentation(
                id = it.id,
                name = it.name,
                background = it.background,
                added = it.added,
                rating = it.rating,
                ratings_count = it.ratings_count,
                description = it.description,
                platforms = it.platforms,
                genre = it.genre,
                release = it.release,
                developers = it.developers,
                publishers = it.publishers,
                website = it.website,
                stores = it.stores,
                screenshots = it.screenshots
            )
        }
}