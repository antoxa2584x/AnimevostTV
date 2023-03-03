package com.animevosttv.view.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.animevosttv.R
import com.animevosttv.view.main.fragment.CartoonsFragment
import com.animevosttv.view.main.fragment.FilmsFragment
import com.animevosttv.view.main.fragment.SerialFragment
import com.animevosttv.view.search.SearchActivity
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.addItems
import kotlinx.android.synthetic.main.activity_ongoings.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoings)

        search_badge.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        toolbar.apply {
            title = getString(R.string.toolbar_serials)

            setNavigationOnClickListener {
                slider.drawerLayout?.open()
            }
        }

        setSupportActionBar(toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        slider.apply {
            addItems(PrimaryDrawerItem().apply {
                nameText = getString(R.string.menu_serials); isSelectable = true
            })
            addItems(PrimaryDrawerItem().apply {
                nameText = getString(R.string.menu_films); isSelectable = true
            })
            addItems(PrimaryDrawerItem().apply {
                nameText = getString(R.string.menu_animation); isSelectable = true
            })
            selectedItemPosition = 0
            onDrawerItemClickListener = { _, _, position ->
                when (position) {
                    0 -> supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add<SerialFragment>(R.id.fragment_container_view)

                        toolbar.title = getString(R.string.toolbar_serials)
                    }
                    1 -> supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add<FilmsFragment>(R.id.fragment_container_view)

                        toolbar.title = getString(R.string.toolbar_films)
                    }
                    2 -> supportFragmentManager.commit {
                        setReorderingAllowed(true)
                        add<CartoonsFragment>(R.id.fragment_container_view)

                        toolbar.title = getString(R.string.toolbar_animation)
                    }
                }
                false
            }
        }
    }
}