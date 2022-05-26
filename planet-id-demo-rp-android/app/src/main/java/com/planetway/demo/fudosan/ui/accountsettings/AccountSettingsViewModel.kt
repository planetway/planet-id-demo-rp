package com.planetway.demo.fudosan.ui.accountsettings

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.ui.EmptyResult
import com.planetway.demo.fudosan.ui.translate


class AccountSettingsViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _logOutResultState = MutableLiveData(EmptyResult())
    val logOutResultState: LiveData<EmptyResult> = _logOutResultState

    fun logOut() {
        getApplication<DemoRpApplication>().accountRepository.logout({
            _logOutResultState.value = EmptyResult(success = true)
        }, { e ->
            _logOutResultState.value = EmptyResult(error = translate(e))
        })
    }
}
