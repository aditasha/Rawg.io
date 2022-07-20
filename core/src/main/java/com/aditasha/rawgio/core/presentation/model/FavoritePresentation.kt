package com.aditasha.rawgio.core.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FavoritePresentation(
    var id: Int,
    var name: String,
    var picture: String,
    var screenshots: MutableList<String>? = null
) : Parcelable
