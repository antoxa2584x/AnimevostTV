package com.animevosttv.view.details

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.animevosttv.R
import com.animevosttv.core.dataLoader.loadDetails
import com.animevosttv.core.dataLoader.loadPlayList
import com.animevosttv.core.model.PlaylistModel
import com.animevosttv.core.model.PreviewTitleModel
import com.animevosttv.view.adapter.PlayListAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_details.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        progress_bar.visibility = View.VISIBLE

        (intent.extras?.get(TITLE) as PreviewTitleModel?)?.let { preview ->
            animeTitle.text = preview.title

            Glide.with(this).load(preview.image).into(this@DetailsActivity.preview)

            loadPlayList(preview.link ?: "") {
                playlist.adapter = PlayListAdapter(this@DetailsActivity, it).apply {
                    setClickListener(this@DetailsActivity)
                }
            }
            loadDetails(preview.link ?: "") { details ->
                details?.let {
                    animeTitle.text = it.title

                    Glide.with(this).load(it.image).into(this@DetailsActivity.preview)

                    info.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Html.fromHtml(it.simpleDetails, Html.FROM_HTML_MODE_LEGACY)
                    } else {
                        Html.fromHtml(it.simpleDetails)
                    }

                    rating.rating = (((preview.rate?.toFloat() ?: 0f) / 100.0) * 5).toFloat()

                    progress_bar.visibility = View.GONE
                }
            }
        }
    }

    override fun onItemClick(view: View?, previewTitleModel: PlaylistModel) {
        val intent = Intent(Intent.ACTION_VIEW)
        val videoUri = Uri.parse(previewTitleModel.hd)
        intent.setDataAndType(videoUri, "application/x-mpegURL")
        intent.putExtra(
            "title",
            animeTitle.text.toString().substringBefore("[") + "\n" + previewTitleModel.name
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