package com.planetway.demo.fudosan.ui.apply

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.ui.getLanguage
import com.planetway.demo.fudosan.ui.translate
import com.planetway.planetid.rpsdk.AuthResponse
import com.planetway.planetid.rpsdk.PlanetId

class ApplyViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _retrieveDataResultState = MutableLiveData(ApplyResult())

    val retrieveDataResultState: LiveData<ApplyResult> = _retrieveDataResultState

    fun apply(dataBank: String) {
        Log.d("ApplyViewModel", "apply $dataBank")

        retrieveData(dataBank, true)
    }

    fun reset() {
        _retrieveDataResultState.value = ApplyResult()
    }

    private fun retrieveData(dataBank: String, handleNoConsent: Boolean) {
        getApplication<DemoRpApplication>().apiService.dataBankPerson(dataBank).handle({
            Log.d("ApplyViewModel", "Success: $it")

            _retrieveDataResultState.value = ApplyResult(it)
        }, { e, response ->
            if (response?.code() == 403 && handleNoConsent) {
                getApplication<DemoRpApplication>().apiService.dataBankConsent(
                    dataBank,
                    getLanguage(getApplication())
                )
                    .handle({ authRequest ->
                        try {
                            Log.i("", "Successfully got authRequest $authRequest")

                            PlanetId.invokeAction(
                                getApplication(),
                                authRequest!!,
                                "fudosandemorp://${PlanetIdAction.ACTION_DATA_BANK_CONSENT.action}?data-bank=$dataBank"
                            )
                        } catch (e: Exception) {
                            Log.e("", "Login error", e)

                            handleError(e)
                        }
                    }, { e, _ ->
                        handleError(e)
                    })
            } else {
                handleError(e)
            }
        }
        )
    }

    fun consentCallback(dataBank: String, authResponse: AuthResponse) {
        getApplication<DemoRpApplication>().apiService.consentCallback(authResponse)
            .handle({
                retrieveData(dataBank, false)
            }, { e, _ ->
                handleError(e)
            })
    }

    private fun handleError(exception: Exception) {
        Log.e("ApplyViewModel", "Error $exception")

        _retrieveDataResultState.value = ApplyResult(error = translate(exception))
    }
}
