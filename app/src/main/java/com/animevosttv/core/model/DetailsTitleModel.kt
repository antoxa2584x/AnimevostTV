package com.animevosttv.core.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DetailsTitleModel(
    var simpleDetails: String? = "",

    ) : PreviewTitleModel(), Parcelable
