package com.animevosttv.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PreviewTitleModel(
    val title: String = "",
    val image: String? = "",
    val description: String? = "",
    val year: Int? = 0,
    val genre: String? = "",
    val episodesCount: String? = "",
    val type: String? = "",
    val rate: Int? = 0,
    val link: String? = "",
    val director: String? = "",
    val directorLink: String? = ""
):Parcelable
