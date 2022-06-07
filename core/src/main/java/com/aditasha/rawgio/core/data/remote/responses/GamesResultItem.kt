package com.aditasha.rawgio.core.data.remote.responses

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamesResultItem(

    @field:SerializedName("name_original")
    val nameOriginal: String,

    @field:SerializedName("rating")
    val rating: Double,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("id")
    val id: Int,

    @field:SerializedName("added")
    val added: Int,

    @field:SerializedName("ratings_count")
    val ratingsCount: Int,

    @field:SerializedName("slug")
    val slug: String,

    @field:SerializedName("released")
    val released: String,

    @field:SerializedName("website")
    val website: String? = null,

    @field:SerializedName("background_image_additional")
    val backgroundImageAdditional: String? = null,

    @field:SerializedName("background_image")
    val backgroundImage: String,

    @field:SerializedName("developers")
    val developers: List<Names>? = null,

    @field:SerializedName("publishers")
    val publishers: List<Names>? = null,

    @field:SerializedName("genres")
    val genres: List<Names>? = null,

    @field:SerializedName("platforms")
    val platforms: List<Platforms>? = null,

    @field:SerializedName("short_screenshots")
    val shortScreenshots: List<ShortScreenshotsItem>? = null,

    @field:SerializedName("stores")
    val stores: List<Stores>? = null,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("updated")
    val updated: String
) : Parcelable

@Parcelize
data class Names(
    @field:SerializedName("name")
    val name: String = "empty"
) : Parcelable

@Parcelize
data class Platforms(
    @field:SerializedName("platform")
    val platform: Names
) : Parcelable

@Parcelize
data class Stores(
    @field:SerializedName("store")
    val store: Names
) : Parcelable

@Parcelize
data class ShortScreenshotsItem(

    @field:SerializedName("image")
    val image: String
) : Parcelable