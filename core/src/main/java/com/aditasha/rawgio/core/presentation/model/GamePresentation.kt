package com.aditasha.rawgio.core.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GamePresentation(
    var id: Int,
    var name: String? = null,
    var background: String? = null,
    var added: Int? = null,
    var rating: Double? = null,
    var ratings_count: Int? = null,
    var description: String? = null,
    var platforms: MutableList<String>? = null,
    var genre: MutableList<String>? = null,
    var release: String? = null,
    var developers: MutableList<String>? = null,
    var publishers: MutableList<String>? = null,
    var website: String? = null,
    var stores: MutableList<String>? = null,
    var screenshots: MutableList<String>? = null
) : Parcelable
