package com.planetway.demo.fudosan.ui.sign

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
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody


class SignViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _signResultState = MutableLiveData(EmptyResult())

    val signDataResultState: LiveData<EmptyResult> = _signResultState

    fun sign() {
        Log.d("SignViewModel", "sign")

        // Just build a sample document to sign. The contents are not relevant.
        val formData = MultipartBody.Part.createFormData(
            "file",
            "foo.txt",
            RequestBody.create(MediaType.parse("text/plain"), "foo")
        )

        getApplication<DemoRpApplication>().apiService.sign(formData, getLanguage(getApplication())).handle(
            { authRequest ->
                PlanetId.invokeAction(getApplication(), authRequest!!, "fudosandemorp://${PlanetIdAction.ACTION_SIGN}")
            }, { e, _ ->
                handleError(e)
            }
        )
    }

    fun signCallback(authResponse: AuthResponse) {
        getApplication<DemoRpApplication>().apiService.signCallback(authResponse).handle( {
            _signResultState.value = EmptyResult(success = true)
        }, { exception, _ ->
            handleError(exception)
        })
    }

    fun reset() {
        _signResultState.value = EmptyResult()
    }

    private fun handleError(exception: Exception) {
        Log.e("SignViewModel", "Error $exception")

        _signResultState.value = EmptyResult(error = translate(exception))
    }
}
