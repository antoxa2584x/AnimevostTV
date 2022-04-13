package com.animevosttv.view

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.chibatching.kotpref.gsonpref.gson
import com.google.gson.GsonBuilder

class AnimeVostApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        Kotpref.init(applicationContext)
        Kotpref.gson = GsonBuilder().create()
    }
}