package com.animevosttv.view.adapter

import android.content.Context
import com.animevosttv.core.model.PreviewTitleModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animevosttv.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.default_title_item.view.*

class TitlesAdapter internal constructor(context: Context?, data: MutableList<PreviewTitleModel>) :
    TrackSelectionAdapter<TitlesAdapter.ViewHolder>() {
    private val mData: MutableList<PreviewTitleModel> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    companion object {
        const val HEADER = 0
        const val CONTENT = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (viewType) {
            HEADER -> mInflater.inflate(R.layout.default_header_item, parent, false)
            CONTENT -> mInflater.inflate(R.layout.default_title_item, parent, false)
            else -> View(parent.context)
        }

        return ViewHolder(view, viewType != 0)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            else -> CONTENT
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (position == HEADER)
            return

        val data = mData[position - 1]
        with(viewHolder.itemView) {
            Glide.with(this).load(data.image).into(preview)
            animeTitle.text = data.title
            rating.rating = (((data.rate?.toFloat() ?: 0f) / 100.0) * 5).toFloat()
            genre.text = data.genre
        }
    }

    override fun getItemCount(): Int {
        return mData.size + 1
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun addAll(list: MutableList<PreviewTitleModel>) {
        val inititalSzie = mData.size
        mData.addAll(list)
        notifyItemRangeInserted(inititalSzie + 1, list.size)
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, previewTitleModel: PreviewTitleModel)
    }

    inner class ViewHolder internal constructor(itemView: View, animate: Boolean) :
        TrackSelectionAdapter.ViewHolder(itemView, animate) {

        init {
            itemView.setOnClickListener {
                if (adapterPosition != 0)
                    mClickListener?.onItemClick(it, mData[adapterPosition - 1])
            }
        }

        override fun onFocus() {
            super.onFocus()
            itemView.genre?.visibility = View.VISIBLE
            itemView.rating?.visibility = View.VISIBLE
        }

        override fun onUnFocus() {
            super.onUnFocus()
            itemView.genre?.visibility = View.GONE
            itemView.rating?.visibility = View.GONE
        }
    }
}