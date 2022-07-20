package com.aditasha.rawgio.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Favorite(
    var id: Int,
    var name: String,
    var picture: String,
    var screenshots: MutableList<String>? = null
) : Parcelable
