package com.planetway.demo.fudosan.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.planetway.demo.R
import com.planetway.demo.fudosan.data.PlanetIdAction
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(nav_host_fragment.findNavController())

        val beingRestored = savedInstanceState != null
        if (!beingRestored) {
            handleAppLink()
        } else {
            intent?.removeExtra("EXTRA_CALLBACK_ACTION")
        }
    }

    private fun handleAppLink() {
        val callbackAction = intent?.getStringExtra("EXTRA_CALLBACK_ACTION")
        if (callbackAction != null) {
            when (callbackAction) {
                PlanetIdAction.ACTION_LINK.action -> {
                    bottomNavigationView.selectedItemId = R.id.accountSettingsFragment
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_accountSettingsFragment_to_myAccountFragment)
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_myAccountFragment_to_linkPlanetIdFragment)
                }
                PlanetIdAction.ACTION_DATA_BANK_CONSENT.action -> {
                    bottomNavigationView.selectedItemId = R.id.fragment_home
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_fragment_home_to_fragment_apply)
                }
                PlanetIdAction.ACTION_CONSENT_REVOKE.action -> {
                    bottomNavigationView.selectedItemId = R.id.accountSettingsFragment
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_accountSettingsFragment_to_myAccountFragment)
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_myAccountFragment_to_signedDocumentsFragment)
                }
                PlanetIdAction.ACTION_SIGN.action -> {
                    bottomNavigationView.selectedItemId = R.id.fragment_home
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_fragment_home_to_fragment_apply)
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_fragment_apply_to_fragment_sign)
                }
                PlanetIdAction.ACTION_LRA_CONSENT.action -> {
                    bottomNavigationView.selectedItemId = R.id.accountSettingsFragment
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_accountSettingsFragment_to_myAccountFragment)
                    nav_host_fragment.findNavController()
                        .navigate(R.id.action_myAccountFragment_to_dataFromLraFragment)
                }
                else -> {
                    Log.e("MainActivity", "Unknown callback action $callbackAction")
                }
            }
        }
    }
}
