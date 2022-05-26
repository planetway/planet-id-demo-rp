package com.planetway.demo.fudosan.ui.home

import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.navigation.findNavController
import com.planetway.demo.R
import com.planetway.demo.fudosan.DemoRpApplication


class HomeViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    fun apply(view: View) {
        view.findNavController().navigate(R.id.action_fragment_home_to_fragment_apply)
    }

    fun isPlanetIdLinked(): Boolean {
        return getApplication<DemoRpApplication>().accountRepository.isLinkedWithPlanetId()
    }
}
