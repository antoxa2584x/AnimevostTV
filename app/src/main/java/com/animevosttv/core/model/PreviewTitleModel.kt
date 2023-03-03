package com.animevosttv.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
open class PreviewTitleModel(
    var title: String = "",
    var image: String? = "",
    var description: String? = "",
    var link: String? = "",
):Parcelable{

    fun getId() = link?.substringAfterLast("/")?.substringBefore("-")?:""
}
