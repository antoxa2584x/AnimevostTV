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
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.animevosttv.R
import com.animevosttv.core.dataLoader.loadDetails
import com.animevosttv.core.dataLoader.loadFile
import com.animevosttv.core.helper.isGoogleTV
import com.animevosttv.core.model.EpisodeModel
import com.animevosttv.core.model.LatestWatchedTitleEpisode
import com.animevosttv.core.model.PreviewTitleModel
import com.animevosttv.core.prefs.ApplicationPreferences
import com.animevosttv.view.adapter.PlayListAdapter
import com.bumptech.glide.Glide

class DetailsActivity : AppCompatActivity(), PlayListAdapter.ItemClickListener {
    companion object {
        const val TITLE = "title_model"

        fun getIntent(
            callActivity: FragmentActivity,
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

        progressBar.visibility = View.VISIBLE

        (intent.extras?.get(TITLE) as PreviewTitleModel?)?.let { preview ->
            this.previewModel = preview
            lastWatched =
                ApplicationPreferences.watchedTitles.firstOrNull { it.titleId == previewModel.getId() }

            title.text = preview.title

            Glide.with(this).load(preview.image).into(titlePreview)

//            loadPlayList(preview.getId()) {
//                episodesList.adapter =
//                    PlayListAdapter(this@DetailsActivity, it, preview.getId()).apply {
//                        setClickListener(this@DetailsActivity)
//                    }
//
//                lastWatched?.let {
//                    if (it.episode > 1)
//                        episodesList.layoutManager?.scrollToPosition(it.episode - 1)
//                }
//            }
            loadDetails(preview.link ?: "") { details ->
                details?.let {
                    title.text = it.title

                    Glide.with(this).load(it.image).into(titlePreview)

                    val info = it.additionalInfo

                    infoTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(info, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(info)
                    }

                    aboutTextView.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(it.description, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(it.description)
                    }

                    progressBar.visibility = View.GONE

                    episodesList.adapter =
                        it.playList?.firstOrNull()?.episodes?.let { season ->
                            PlayListAdapter(this@DetailsActivity, season, preview.image, preview.getId()).apply {
                                setClickListener(this@DetailsActivity)
                            }
                        }

                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemClick(view: View?, previewTitleModel: EpisodeModel) {
        if (ApplicationPreferences.watchedList.none { it == previewTitleModel.id }) {
            val watchedList = ApplicationPreferences.watchedList
            previewTitleModel.id?.let { watchedList.add(it) }
            ApplicationPreferences.watchedList = watchedList
            (episodesList.adapter as PlayListAdapter).notifyDataSetChanged()
        }

        loadFile(previewTitleModel.link){

            if(it?.contains("Файл не найден") == true){
                Toast.makeText(this, "Файл не знайдено", Toast.LENGTH_SHORT).show()
                return@loadFile
            }

            val intent = Intent(Intent.ACTION_VIEW)
            val videoUri = Uri.parse(it)
            intent.setDataAndType(videoUri, "application/x-mpegURL")
            intent.putExtra(
                "title",
                title.text.toString()
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
}