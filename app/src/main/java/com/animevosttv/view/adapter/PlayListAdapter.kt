package com.animevosttv.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animevosttv.R
import com.animevosttv.core.model.PlaylistModel
import com.animevosttv.core.prefs.ApplicationPreferences
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.playlist_title_preview.view.*

class PlayListAdapter internal constructor(
    context: Context?,
    data: List<PlaylistModel>,
    private val titleId: String
) :
    TrackSelectionAdapter<PlayListAdapter.ViewHolder>() {
    private val mData: List<PlaylistModel> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.playlist_title_preview, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val data = mData[position]
        with(viewHolder.itemView) {
            Glide.with(this).load(data.preview).into(preview)

            if (ApplicationPreferences.watchedList.any { it == (titleId + data.name) }) {
                title.text = "Просмотрено\n${data.name}"
            } else {
                title.text = data.name
            }
        }
    }

    override fun getItemCount() = mData.size

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, previewTitleModel: PlaylistModel)
    }

    inner class ViewHolder internal constructor(itemView: View) :
        TrackSelectionAdapter.ViewHolder(itemView, true) {

        init {
            itemView.setOnClickListener {
                mClickListener?.onItemClick(it, mData[adapterPosition])
            }
        }
    }
}