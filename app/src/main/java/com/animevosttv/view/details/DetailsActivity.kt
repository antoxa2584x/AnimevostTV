package com.animevosttv.view.details

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.RatingBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.animevosttv.R
import com.animevosttv.core.dataLoader.loadDetails
import com.animevosttv.core.dataLoader.loadPlayList
import com.animevosttv.core.helper.isGoogleTV
import com.animevosttv.core.model.LatestWatchedTitleEpisode
import com.animevosttv.core.model.PlaylistModel
import com.animevosttv.core.model.PreviewTitleModel
import com.animevosttv.core.prefs.ApplicationPreferences
import com.animevosttv.view.adapter.PlayListAdapter
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity(), PlayListAdapter.ItemClickListener {
    companion object {
        const val TITLE = "title_model"

        fun getIntent(
            callActivity: AppCompatActivity,
            previewTitleModel: PreviewTitleModel
        ): Intent {
            val myIntent = Intent(callActivity, DetailsActivity::class.java)
            myIntent.putExtra(TITLE, previewTitleModel)
            return myIntent
        }
    }

    private lateinit var previewModel: PreviewTitleModel
    private var lastWatched: LatestWatchedTitleEpisode? = null

    private lateinit var title: AppCompatTextView
    private lateinit var progressBar: View
    private lateinit var episodesList: RecyclerView
    private lateinit var titlePreview: AppCompatImageView
    private lateinit var infoTextView: AppCompatTextView
    private lateinit var aboutTextView: AppCompatTextView
    private lateinit var ratingBar: RatingBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(if (isGoogleTV()) R.layout.activity_details else R.layout.activity_details_mobile)

        if (isGoogleTV().not()) {
            findViewById<View>(R.id.back).setOnClickListener {
                onBackPressed()
            }
        }

        title = findViewById(R.id.animeTitle)
        progressBar = findViewById(R.id.progress_bar)
        episodesList = findViewById(R.id.playlist)
        titlePreview = findViewById(R.id.preview)
        infoTextView = findViewById(R.id.info)
        aboutTextView = findViewById(R.id.details)
        ratingBar = findViewById(R.id.rating)

        progressBar.visibility = View.VISIBLE

        (intent.extras?.get(TITLE) as PreviewTitleModel?)?.let { preview ->
            this.previewModel = preview
            lastWatched =
                ApplicationPreferences.watchedTitles.firstOrNull { it.titleId == previewModel.getId() }

            title.text = preview.title

            Glide.with(this).load(preview.image).into(titlePreview)

            loadPlayList(preview.getId()) {
                episodesList.adapter =
                    PlayListAdapter(this@DetailsActivity, it, preview.getId()).apply {
                        setClickListener(this@DetailsActivity)
                    }

                lastWatched?.let {
                    if (it.episode > 1)
                        episodesList.layoutManager?.scrollToPosition(it.episode - 1)
                }
            }
            loadDetails(preview.link ?: "") { details ->
                details?.let {
                    title.text = it.title

                    Glide.with(this).load(it.image).into(titlePreview)

                    var info = it.simpleDetails?.substringBefore("Описание")
                    if (isGoogleTV().not()) {
                        info = "<b>Эпизоды:</b> [" + it.title.substringAfter("[") +"<br><br>"+ info
                    }

                    val detailsText = "Описание" + it.simpleDetails?.substringAfter("Описание")

                    infoTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(info)
                    }


                    aboutTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(detailsText, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(detailsText)
                    }

                    ratingBar.rating = (((details.rate?.toFloat() ?: 0f) / 100.0) * 5).toFloat()

                    progressBar.visibility = View.GONE
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(view: View?, previewTitleModel: PlaylistModel) {
        if (ApplicationPreferences.watchedList.none { it == previewTitleModel.hd }) {
            val watchedList = ApplicationPreferences.watchedList
            watchedList.add(previewTitleModel.hd)
            ApplicationPreferences.watchedList = watchedList

            (episodesList.adapter as PlayListAdapter).notifyDataSetChanged()
        }

        val episode = previewTitleModel.name.replace(Regex("\\D+"), "").toInt()

        val watchedTitles = ApplicationPreferences.watchedTitles

        lastWatched?.let {
            if (it.episode < episode) {
                watchedTitles.remove(it)
                watchedTitles.add(
                    LatestWatchedTitleEpisode(
                        previewModel.getId(),
                        episode
                    )
                )
            }
        } ?: kotlin.run {
            watchedTitles.add(
                LatestWatchedTitleEpisode(
                    previewModel.getId(),
                    episode
                )
            )
        }

        ApplicationPreferences.watchedTitles = watchedTitles

        val intent = Intent(Intent.ACTION_VIEW)
        val videoUri = Uri.parse(previewTitleModel.hd)
        intent.setDataAndType(videoUri, "application/x-mpegURL")
        intent.putExtra(
            "title",
            title.text.toString().substringBefore("[") + "\n" + previewTitleModel.name
        )

        try {
            intent.setPackage("com.mxtech.videoplayer.pro")
            startActivity(intent);
        } catch (e: ActivityNotFoundException) {
            try {
                intent.setPackage("com.mxtech.videoplayer.ad")
                startActivity(intent);
            } catch (e: ActivityNotFoundException) {
                val goToMarket =
                    Intent(Intent.ACTION_VIEW).setData(Uri.parse("market://search?q='MX Player'"))
                startActivity(goToMarket)

                Toast.makeText(
                    this,
                    "Для корректной работы необходим MX Player",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}