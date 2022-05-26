package com.planetway.demo.fudosan.ui.accountsettings.myaccount.datafromlra

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.ui.translate
import com.planetway.planetid.rpsdk.AuthResponse


class DataFromLraViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _dataFromLraResultState = MutableLiveData(DataFromLraResult())
    val dataFromLraResultState: LiveData<DataFromLraResult> = _dataFromLraResultState

    fun getPerson() {
        getApplication<DemoRpApplication>().accountRepository.getPersonalInfo(
            getApplication(),
            { person ->
                Log.d("DataFromLraViewModel", "Success: $person")

                _dataFromLraResultState.value = DataFromLraResult(person)
            },
            { exception ->
                handleError(exception)
            })
    }

    fun consentCallback(authResponse: AuthResponse) {
        getApplication<DemoRpApplication>().apiService.consentCallback(authResponse).handle({
            getPerson()
        }, { exception, _ ->
            handleError(exception)
        })
    }

    private fun handleError(exception: Exception) {
        Log.e("DataFromLraViewModel", "Error $exception")

        _dataFromLraResultState.value = DataFromLraResult(error = translate(exception))
    }
}
