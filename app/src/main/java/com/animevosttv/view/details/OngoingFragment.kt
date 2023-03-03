package com.animevosttv.view.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.animevosttv.R
import com.animevosttv.core.dataLoader.loadOngoings
import com.animevosttv.core.helper.isGoogleTV
import com.animevosttv.core.model.PreviewTitleModel
import com.animevosttv.view.adapter.TitlesAdapter
import com.animevosttv.view.adapter.listener.OnLoadMoreListener
import com.animevosttv.view.adapter.listener.RecyclerViewLoadMoreScroll
import kotlinx.android.synthetic.main.activity_ongoings.*
import kotlinx.android.synthetic.main.onfoing_fragment_layout.*
import kotlinx.android.synthetic.main.onfoing_fragment_layout.search_badge

class OngoingFragment : Fragment(R.layout.onfoing_fragment_layout),
    TitlesAdapter.ItemClickListener {

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var scrollListener: RecyclerViewLoadMoreScroll
    private lateinit var adapter: TitlesAdapter
    private var spanCount = 0;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        spanCount = if (requireContext().isGoogleTV()) 6 else 2

        layoutManager = GridLayoutManager(requireContext(), spanCount)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = when (position) {
                0 -> spanCount
                else -> 1
            }
        }

        content.layoutManager = layoutManager
        adapter = TitlesAdapter(requireContext(), mutableListOf())
        adapter.setClickListener(this)
        content.adapter = adapter

        scrollListener =
            RecyclerViewLoadMoreScroll(content.layoutManager as GridLayoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore(page: Int) {
                loadData(page)
            }
        })

        content.addOnScrollListener(scrollListener)

        showLoading()

        loadData(1)
    }

    private fun loadData(page: Int) {
        requireContext().loadOngoings(page,"serialy") {
            hideLoading()

            adapter.addAll(it)
            scrollListener.setLoaded()
        }
    }

    private fun showLoading() {
        progress_bar_layout.visibility = View.VISIBLE
        search_badge.visibility = View.GONE
    }

    private fun hideLoading() {
        progress_bar_layout.visibility = View.GONE
        search_badge.visibility = View.VISIBLE
    }

    override fun onItemClick(view: View?, previewTitleModel: PreviewTitleModel) {
        startActivity(DetailsActivity.getIntent(requireActivity(), previewTitleModel))
    }
}