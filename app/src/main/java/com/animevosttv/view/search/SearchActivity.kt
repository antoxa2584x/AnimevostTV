package com.animevosttv.view.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.animevosttv.R
import com.animevosttv.core.dataLoader.searchTitles
import com.animevosttv.core.helper.isGoogleTV
import com.animevosttv.core.model.PreviewTitleModel
import com.animevosttv.view.adapter.TitlesAdapter
import com.animevosttv.view.details.DetailsActivity
import kotlinx.android.synthetic.main.activity_search.*


class SearchActivity : AppCompatActivity(), TitlesAdapter.ItemClickListener {

    private lateinit var layoutManager: GridLayoutManager
    private lateinit var adapter: TitlesAdapter
    private var spanCount = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        search.setIconifiedByDefault(false);
        search.performClick();
        search.requestFocus();

        spanCount = if (isGoogleTV()) 6 else 2

        layoutManager = GridLayoutManager(this, spanCount)

        content.layoutManager = layoutManager
        adapter = TitlesAdapter(this@SearchActivity, mutableListOf(), showHeader = false)
        adapter.setClickListener(this)
        content.adapter = adapter

        progress_bar_layout.visibility = View.GONE

        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText?.isNotEmpty() == true && newText.length >= 4) {
                    progress_bar_layout.visibility = View.VISIBLE

                    searchTitles(newText, 0) {
                        adapter.clear()
                        adapter.addAll(it)
                        progress_bar_layout.visibility = View.GONE
                    }
                } else {
                    adapter.clear()
                    progress_bar_layout.visibility = View.GONE
                }

                return false
            }
        })
    }

    override fun onItemClick(view: View?, previewTitleModel: PreviewTitleModel) {
        startActivity(DetailsActivity.getIntent(this@SearchActivity, previewTitleModel))
    }
}