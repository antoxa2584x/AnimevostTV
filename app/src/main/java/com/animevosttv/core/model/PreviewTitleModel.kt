package com.animevosttv.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class PreviewTitleModel(
    var title: String = "",
    var image: String? = "",
    var description: String? = "",
    var year: Int? = 0,
    var genre: String? = "",
    var episodesCount: String? = "",
    var type: String? = "",
    var rate: Int? = 0,
    var link: String? = "",
    var director: String? = "",
    var directorLink: String? = ""
):Parcelable{

    fun getId() = link?.substringAfterLast("/")?.substringBefore("-")?:""
}
