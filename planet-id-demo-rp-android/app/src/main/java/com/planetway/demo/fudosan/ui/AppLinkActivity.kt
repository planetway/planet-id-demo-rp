package com.planetway.demo.fudosan.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.planetway.demo.fudosan.data.PlanetIdAction

class AppLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("AppLinkActivity", "${intent?.data}")
        if (intent?.data != null) {
            handleCallbackUri(intent.data!!)
        }

        finish()
    }

    private fun handleCallbackUri(uri: Uri) {
        when (uri.authority) {
            PlanetIdAction.ACTION_LOGIN.action -> {
                startActivity(
                    Intent(
                        this, LoginActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("EXTRA_CALLBACK_ACTION", uri.authority)
                        putExtra("EXTRA_CALLBACK_URI", uri.toString())
                    })
            }
            PlanetIdAction.ACTION_DATA_BANK_CONSENT.action,
            PlanetIdAction.ACTION_SIGN.action,
            PlanetIdAction.ACTION_LRA_CONSENT.action,
            PlanetIdAction.ACTION_LINK.action -> {
                startActivity(
                    Intent(
                        this, MainActivity::class.java
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        putExtra("EXTRA_CALLBACK_ACTION", uri.authority)
                        putExtra("EXTRA_CALLBACK_URI", uri.toString())
                    })
            }
            else -> {
                Log.e("", "Unknown callback uri $uri.")
            }
        }
    }
}
