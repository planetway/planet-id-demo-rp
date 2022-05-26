package com.planetway.demo.fudosan.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.planetway.demo.fudosan.DemoRpApplication

class AndroidViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(DemoRpApplication::class.java).newInstance(application)
    }
}
