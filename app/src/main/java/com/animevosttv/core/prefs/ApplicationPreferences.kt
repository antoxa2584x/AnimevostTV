package com.animevosttv.core.prefs

import com.animevosttv.core.model.LatestWatchedTitleEpisode
import com.chibatching.kotpref.KotprefModel
import com.chibatching.kotpref.gsonpref.gsonPref

object ApplicationPreferences : KotprefModel() {
    var watchedList by gsonPref(mutableListOf<String>())
    var watchedTitles by gsonPref(mutableListOf<LatestWatchedTitleEpisode>())

}