package com.planetway.demo.fudosan.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.planetway.demo.R


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        val beingRestored = savedInstanceState != null
        if (beingRestored) {
            intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        }
    }
}
