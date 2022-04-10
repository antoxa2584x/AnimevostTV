package com.animevosttv.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.animevosttv.R
import com.animevosttv.view.ongoings.OngoingsActivity


class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            val i = Intent(this@SplashActivity, OngoingsActivity::class.java)
            startActivity(i)
            finish()
        }, 3000)
    }
}