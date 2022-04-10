package com.animevosttv.core.model


import com.google.gson.annotations.SerializedName
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistModel(
    @SerializedName("hd")
    val hd: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("preview")
    val preview: String,
    @SerializedName("std")
    val std: String
) : Parcelable