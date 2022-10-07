package com.planetway.demo.fudosan.ui.accountsettings.signeddocuments

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.data.model.SignedDocument


class SignedDocumentsViewModel(application: DemoRpApplication) : AndroidViewModel(application) {
    val signedDocuments = MutableLiveData<List<SignedDocument>>()

    init {
        getApplication<DemoRpApplication>().apiService.getSignedDocuments().handle({
            signedDocuments.value = it
        }, { e, _ ->
            handleError(e)
        })
    }

    fun handleError(exception: Exception) {
        Log.e("SignedDocsViewModel", "Error $exception.")
    }
}