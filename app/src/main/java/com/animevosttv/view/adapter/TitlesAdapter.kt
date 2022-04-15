package com.animevosttv.view.adapter

import android.content.Context
import com.animevosttv.core.model.PreviewTitleModel
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.animevosttv.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.default_title_item.view.*

class TitlesAdapter internal constructor(
    context: Context?,
    data: MutableList<PreviewTitleModel>,
    val showHeader: Boolean = true
) :
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
        if (!showHeader)
            return CONTENT

        return when (position) {
            0 -> HEADER
            else -> CONTENT
        }
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        if (showHeader && position == HEADER)
            return

        val data = mData[position - if (showHeader) 1 else 0]
        with(viewHolder.itemView) {
            Glide.with(this).load(data.image).into(preview)
            animeTitle.text = data.title
            rating.rating = (((data.rate?.toFloat() ?: 0f) / 100.0) * 5).toFloat()
            genre.text = data.genre

            rating.visibility = if (data.rate == null) View.GONE else View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return mData.size + if (showHeader) 1 else 0
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun addAll(list: MutableList<PreviewTitleModel>) {
        val inititalSzie = mData.size
        mData.addAll(list)
        notifyItemRangeInserted(inititalSzie + if (showHeader) 1 else 0, list.size)
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, previewTitleModel: PreviewTitleModel)
    }

    inner class ViewHolder internal constructor(itemView: View, animate: Boolean) :
        TrackSelectionAdapter.ViewHolder(itemView, animate) {

        init {
            itemView.setOnClickListener {
                if (showHeader) {
                    if (adapterPosition != 0)
                        mClickListener?.onItemClick(it, mData[adapterPosition - 1])
                } else {
                    mClickListener?.onItemClick(it, mData[adapterPosition])
                }
            }
        }

        override fun onFocus() {
            super.onFocus()
            itemView.genre?.visibility = View.VISIBLE

            if (itemView.rating.rating != 0f)
                itemView.rating?.visibility = View.VISIBLE
        }

        override fun onUnFocus() {
            super.onUnFocus()
            itemView.genre?.visibility = View.GONE

            if (itemView.rating.rating != 0f)
                itemView.rating?.visibility = View.GONE
        }
    }
}