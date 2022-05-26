package com.planetway.demo.fudosan.ui.accountsettings.myaccount

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.model.UserInfo
import com.planetway.demo.fudosan.ui.EmptyResult
import com.planetway.demo.fudosan.ui.translate


class MyAccountViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _deleteAccountResultState = MutableLiveData(EmptyResult())
    val deleteAccountResultState: LiveData<EmptyResult> = _deleteAccountResultState

    private val _unlinkResultState = MutableLiveData(EmptyResult())
    val unlinkResultState: LiveData<EmptyResult> = _unlinkResultState

    fun isPlanetIdLinked(): Boolean {
        return getApplication<DemoRpApplication>().accountRepository.isLinkedWithPlanetId()
    }

    fun getLocalUserInfo(): UserInfo {
        return getApplication<DemoRpApplication>().accountRepository.getLocalUserInfo()
    }

    fun deleteAccount() {
        getApplication<DemoRpApplication>().accountRepository.deleteAccount({
            _deleteAccountResultState.value = EmptyResult(success = true)
        }, { e ->
            _deleteAccountResultState.value = EmptyResult(error = translate(e))
        })
    }

    fun unlink() {
        getApplication<DemoRpApplication>().accountRepository.unlinkWithPlanetId({
            _unlinkResultState.value = EmptyResult(success = true)
        }, { e ->
            _unlinkResultState.value = EmptyResult(error = translate(e))
        })
    }
}
