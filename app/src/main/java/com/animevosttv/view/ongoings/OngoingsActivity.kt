package com.animevosttv.view.ongoings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.animevosttv.R
import com.animevosttv.view.details.CartoonsFragment
import com.animevosttv.view.details.FilmsFragment
import com.animevosttv.view.details.OngoingFragment
import com.animevosttv.view.search.SearchActivity
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import com.mikepenz.materialdrawer.model.interfaces.nameText
import com.mikepenz.materialdrawer.util.addItems
import kotlinx.android.synthetic.main.activity_ongoings.*


class OngoingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ongoings)

        search_badge.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        toolbar.title = "Цікава Ідея: Серіали"
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        toolbar.setNavigationOnClickListener {
            slider.drawerLayout?.open()
        }

        slider.addItems(PrimaryDrawerItem().apply { nameText = "Серілаи"; isSelectable = true })
        slider.addItems(PrimaryDrawerItem().apply { nameText = "Фільми"; isSelectable = true })
        slider.addItems(PrimaryDrawerItem().apply { nameText = "Анімаційні"; isSelectable = true })

        slider.selectedItemPosition = 0

        slider.onDrawerItemClickListener = { v, drawerItem, position ->
            when (position) {
                0 -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<OngoingFragment>(R.id.fragment_container_view)
                }
                1 -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<FilmsFragment>(R.id.fragment_container_view)
                }
                2 -> supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    add<CartoonsFragment>(R.id.fragment_container_view)
                }
            }
            false
        }
    }
}