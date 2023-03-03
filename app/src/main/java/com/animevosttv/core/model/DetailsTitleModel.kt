package com.animevosttv.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DetailsTitleModel(
    var additionalInfo: String? = "",
    var playList: List<SeasonModel>? = emptyList(),
    var rating: String? = "",
) : PreviewTitleModel(), Parcelable

@Parcelize
class SeasonModel(
    var season: String,
    var episodes: List<EpisodeModel>,
) : Parcelable

@Parcelize
class EpisodeModel(
    var episode: String,
    var link: String,
    var preview: String,
    var id:String?,
) : Parcelable
