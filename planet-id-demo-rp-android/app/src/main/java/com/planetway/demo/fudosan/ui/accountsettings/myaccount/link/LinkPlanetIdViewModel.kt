package com.planetway.demo.fudosan.ui.accountsettings.myaccount.link

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.ui.EmptyResult
import com.planetway.demo.fudosan.ui.getLanguage
import com.planetway.demo.fudosan.ui.translate
import com.planetway.planetid.rpsdk.AuthResponse
import com.planetway.planetid.rpsdk.PlanetId


class LinkPlanetIdViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _linkPlanetIdResultState = MutableLiveData(EmptyResult())
    val linkPlanetIdResultState: LiveData<EmptyResult> = _linkPlanetIdResultState

    fun linkWithPlanetID() {
        Log.d("LinkPlanetIdViewModel", "Link planet id.")

        getApplication<DemoRpApplication>().apiService.link(getLanguage(getApplication())).handle(
            { authRequest ->
                try {
                    PlanetId.invokeAction(
                        getApplication(),
                        authRequest!!,
                        "fudosandemorp://${PlanetIdAction.ACTION_LINK.action}"
                    )
                } catch (e: Exception) {
                    handleError(e)
                }
            }, { e, _ ->
                handleError(e)
            })
    }

    fun linkCallback(authResponse: AuthResponse) {
        Log.d("LinkPlanetIdViewModel", "Link planet id callback $authResponse.")

        getApplication<DemoRpApplication>().accountRepository.linkWithPlanetIdCallback(
            authResponse,
            {
                _linkPlanetIdResultState.value = EmptyResult(success = true)
            },
            this::handleError
        )
    }

    private fun handleError(exception: Exception) {
        Log.e("LinkPlanetIdViewModel", "Error $exception")

        _linkPlanetIdResultState.value = EmptyResult(error = translate(exception))
    }
}
