package com.animevosttv.core.dataLoader

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.animevosttv.core.model.DetailsTitleModel
import com.animevosttv.core.model.EpisodeModel
import com.animevosttv.core.model.PreviewTitleModel
import com.animevosttv.core.model.SeasonModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

const val BASE_URL = "https://cikava-ideya.top"

fun Context.loadOngoings(
    page: Int,
    base: String,
    dataReadyListener: (MutableList<PreviewTitleModel>) -> Unit
) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val doc = try {
                Jsoup.connect("$BASE_URL/${base}/page/${page}/").get()
                    .getElementById("dle-content")
            } catch (e: java.lang.Exception) {
                null
            }

            (this@loadOngoings as AppCompatActivity).runOnUiThread {
                dataReadyListener(parseData(doc))
            }
        }

    }

    downloadThread.start()
}

fun Context.searchTitles(
    search: String?,
    dataReadyListener: (MutableList<PreviewTitleModel>) -> Unit
) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val doc =
                Jsoup.connect("$BASE_URL/index.php?do=search&subaction=search&search_start=0&full_search=0&result_from=1&story=$search")
                    .get()
                    .getElementById("dle-content")

            (this@searchTitles as AppCompatActivity).runOnUiThread {
                dataReadyListener(parseData(doc))
            }
        }

    }

    downloadThread.start()
}

private fun parseData(doc: Element?): MutableList<PreviewTitleModel> {
    val titles = doc?.getElementsByClass("th-item")

    val titleModels = mutableListOf<PreviewTitleModel>()
    if (titles != null) {
        for (item: Element in titles) {
            titleModels.add(
                PreviewTitleModel(
                    title = item.getElementsByClass("th-title nowrap").firstOrNull()?.text() ?: "",
                    link = item.getElementsByClass("th-in").firstOrNull()?.attr("href") ?: "",
                    image = "$BASE_URL/" + item.getElementsByClass("th-img")
                        .firstOrNull()?.getElementsByClass("anim")?.firstOrNull()?.attr("src"),
                )
            )
        }
    }

    Log.i("", titleModels.joinToString())

    return titleModels
}

fun Context.loadDetails(detailsLink: String, dataReadyListener: (DetailsTitleModel?) -> Unit) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val form = URL(detailsLink)
            val connection1 = form.openConnection() as HttpURLConnection
            connection1.readTimeout = 10000
            val whole = StringBuilder()
            val reader = BufferedReader(
                InputStreamReader(BufferedInputStream(connection1.inputStream))
            )
            var inputLine: String?
            while (reader.readLine().also { inputLine = it } != null) whole.append(inputLine)
            reader.close()
            val doc = Jsoup.parse(whole.toString())
            val titles = doc.getElementById("dle-content")?.getElementsByClass("shortstory")

            var titleModels: DetailsTitleModel? = null

            if (titles != null) {
                titleModels = DetailsTitleModel().apply {
                    title = doc.getElementsByClass("fright fx-1").select("h1").firstOrNull()?.text()
                        ?: ""
                    image =
                        "$BASE_URL/" + doc.getElementsByClass("fposter img-box img-fit")
                            .firstOrNull()?.getElementsByClass("anim")?.firstOrNull()?.attr("src")
                    description = doc.getElementsByClass("fdesc clr full-text clearfix").html()
                    additionalInfo =
                        doc.getElementsByClass("flist").firstOrNull()?.html()?.replace("<li>", "")
                            ?.replace("</li>", "<br>")
                    playList = run {
                        val playlistJson =
                            doc.getElementsByClass("fplayer tabs-box").firstOrNull()?.children()
                                ?.last()?.html()?.substringAfter("return ")
                                ?.substringBefore("; \t}")

                        val playlist =
                            Gson().fromJson(playlistJson, JsonObject::class.java).get("Player1")

                        if (playlist.isJsonPrimitive) {
                            return@run listOf(
                                SeasonModel(
                                    "0", listOf(
                                        EpisodeModel(
                                            "Фільм",
                                            playlist.asString,
                                            "",
                                            detailsLink.substringAfter("top/").substringBefore("-")
                                        )
                                    )
                                )
                            )
                        }

                        if (!playlist.toString().contains("сезон")) {
                            val empMapType =
                                object : TypeToken<Map<String, String>>() {}.type
                            val episodes =
                                Gson().fromJson<Map<String, String>>(
                                    playlist,
                                    empMapType
                                )

                            listOf(
                                SeasonModel(
                                    "0",
                                    episodes.map { episode ->
                                        EpisodeModel(
                                            episode.key,
                                            episode.value,
                                            "",
                                            "${
                                                detailsLink.substringAfter("top/")
                                                    .substringBefore("-")
                                            }${episode.key.filter { it.isDigit() }}"
                                        )
                                    })
                            )
                        } else {
                            val empMapType =
                                object : TypeToken<Map<String, Map<String, String>>>() {}.type
                            val seasonModel =
                                Gson().fromJson<Map<String, Map<String, String>>>(
                                    playlist,
                                    empMapType
                                )

                            seasonModel.map { season ->
                                SeasonModel(
                                    season.key,
                                    season.value.map { episode ->
                                        EpisodeModel(
                                            episode.key,
                                            episode.value,
                                            "",
                                            "${
                                                detailsLink.substringAfter("top/")
                                                    .substringBefore("-")
                                            }${season.key.filter { it.isDigit() }}${episode.key.filter { it.isDigit() }}"
                                        )
                                    })
                            }
                        }
                    }
                }
            }
//
            Log.i("", titleModels.toString())

            (this@loadDetails as AppCompatActivity).runOnUiThread {
                dataReadyListener(titleModels)
            }
        }

    }

    downloadThread.start()
}


fun Context.loadFile(detailsLink: String, dataReadyListener: (String?) -> Unit) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val link = Jsoup.connect(detailsLink)
                .get().html().substringAfter("file:\"").substringBefore("m3u8") + "m3u8"

            Log.i("", link)

            (this@loadFile as AppCompatActivity).runOnUiThread {
                dataReadyListener(link)
            }
        }

    }

    downloadThread.start()
}

