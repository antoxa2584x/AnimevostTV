package com.animevosttv.core.dataLoader

import android.content.Context
import android.provider.MediaStore.Video
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.animevosttv.core.model.DetailsTitleModel
import com.animevosttv.core.model.PlaylistModel
import com.animevosttv.core.model.PreviewTitleModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.io.IOException


fun Context.loadOngoings(page: Int, dataReadyListener: (MutableList<PreviewTitleModel>) -> Unit) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val doc = Jsoup.connect("https://animevost.org/ongoing/page/${page}/").get()
                .getElementById("dle-content")
            val titles = doc?.getElementsByClass("shortstory")

            val titleModels = mutableListOf<PreviewTitleModel>()
            if (titles != null) {
                for (item: Element in titles) {
                    val content = item.select("table > tbody > tr > td > p")
                    titleModels.add(
                        PreviewTitleModel(
                            title = item.select("a").firstOrNull()?.text() ?: "",
                            link = item.select("a").firstOrNull()?.attr("href") ?: "",
                            image = "https://animevost.org/" + item.getElementsByClass("imgRadius")
                                .attr("src"),
                            description = content.firstOrNull {
                                it.toString().contains("<strong>Описание: </strong>")
                            }?.text()?.replace("Описание: ", ""),
                            year = content.firstOrNull {
                                it.toString().contains("<strong>Год выхода: </strong>")
                            }?.text()?.replace("Год выхода: ", "")?.toIntOrNull(),
                            type = content.firstOrNull {
                                it.toString().contains("<strong>Тип: </strong>")
                            }?.text()?.replace("Тип: ", ""),
                            genre = content.firstOrNull {
                                it.toString().contains("<strong>Жанр: </strong>")
                            }?.text()?.replace("Жанр: ", ""),
                            episodesCount = content.firstOrNull {
                                it.toString().contains("<strong>Количество серий: </strong>")
                            }?.text()?.replace("Количество серий: ", ""),
                            rate = item.getElementsByClass("current-rating").text().toIntOrNull(),
                            director = content.firstOrNull {
                                it.toString().contains("<strong>Режиссёр: </strong>")
                            }?.text()?.replace("Режиссёр: ", ""),
                            directorLink = (content.firstOrNull {
                                it.toString().contains("<strong>Режиссёр: </strong>")
                            }?.childNode(1) as Element?)?.attr("href")
                        )
                    )
                }
            }

            Log.i("", titleModels.joinToString())

            (this@loadOngoings as AppCompatActivity).runOnUiThread {
                dataReadyListener(titleModels)
            }
        }

    }

    downloadThread.start()
}

fun Context.loadDetails(link: String, dataReadyListener: (DetailsTitleModel?) -> Unit) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val doc = Jsoup.connect(link).get()
                .getElementById("dle-content")
            val titles = doc?.getElementsByClass("shortstory")

            var titleModels: DetailsTitleModel? = null

            if (titles != null) {
                val content = titles.select("table > tbody > tr > td > p")
                titleModels = DetailsTitleModel(
                    title = titles.select("div.shortstoryHead > h1").text() ?: "",
                    link = titles.select("div.shortstoryHead > h1").firstOrNull()?.attr("href")
                        ?: "",
                    image = "https://animevost.org/" + doc.getElementsByClass("imgRadius")
                        .attr("src"),
                    description = content.firstOrNull {
                        it.toString().contains("<strong>Описание: </strong>")
                    }?.text()?.replace("Описание: ", ""),
                    year = content.firstOrNull {
                        it.toString().contains("<strong>Год выхода: </strong>")
                    }?.text()?.replace("Год выхода: ", "")?.toIntOrNull(),
                    type = content.firstOrNull {
                        it.toString().contains("<strong>Тип: </strong>")
                    }?.text()?.replace("Тип: ", ""),
                    genre = content.firstOrNull {
                        it.toString().contains("<strong>Жанр: </strong>")
                    }?.text()?.replace("Жанр: ", ""),
                    episodesCount = content.firstOrNull {
                        it.toString().contains("<strong>Количество серий: </strong>")
                    }?.text()?.replace("Количество серий: ", ""),
                    rate = titles.select("current-rating").text().toIntOrNull(),
                    director = content.firstOrNull {
                        it.toString().contains("<strong>Режиссёр: </strong>")
                    }?.text()?.replace("Режиссёр: ", ""),
                    directorLink = (content.firstOrNull {
                        it.toString().contains("<strong>Режиссёр: </strong>")
                    }?.childNode(1) as Element?)?.attr("href"),
                    simpleDetails = titles.select("div.shortstoryContent > table > tbody > tr > td > p")
                        .joinToString(separator = "<br>") { it.html() },
                    screensList = doc.getElementsByClass("skrin").select("a")
                        .map { "https://animevost.org/" + it.attr("href") }
                )
            }

            Log.i("", titleModels.toString())

            (this@loadDetails as AppCompatActivity).runOnUiThread {
                dataReadyListener(titleModels)
            }
        }

    }

    downloadThread.start()
}

fun Context.loadPlayList(link: String, onPlaylistReady: (List<PlaylistModel>) -> Unit) {
    val downloadThread: Thread = object : Thread() {
        override fun run() {
            val titleId = link.substringAfterLast("/").substringBefore("-")

            val client = OkHttpClient()

            val formBody = FormBody.Builder()
                .add("id", titleId)
                .build()
            val request: Request = Request.Builder()
                .url("https://api.animevost.org/v1/playlist")
                .post(formBody)
                .build()

            try {
                val response = client.newCall(request).execute()

                val playlist =
                    Gson().fromJson(response.body?.string(), Array<PlaylistModel>::class.java)

                (this@loadPlayList as AppCompatActivity).runOnUiThread {
                    onPlaylistReady(
                        playlist.toList().sortedBy { it.name.substringBefore(" ").toInt() })
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    }

    downloadThread.start()
}

