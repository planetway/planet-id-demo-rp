package com.planetway.demo.fudosan.ui.login

import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.R
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.PlanetIdAction
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.ui.EmptyResult
import com.planetway.demo.fudosan.ui.getLanguage
import com.planetway.demo.fudosan.ui.translate
import com.planetway.planetid.rpsdk.AuthResponse
import com.planetway.planetid.rpsdk.PlanetId


class LoginViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _loginFormState = MutableLiveData(LoginFormState())
    private val _loginResultState = MutableLiveData(EmptyResult())

    val loginFormState: LiveData<LoginFormState> = _loginFormState
    val loginResultState: LiveData<EmptyResult> = _loginResultState

    fun credentialsChanged(username: String, password: String) {
        if (!isUsernameValid(username)) {
            _loginFormState.value =
                LoginFormState(R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginFormState.value =
                LoginFormState(null, R.string.invalid_password)
        } else {
            _loginFormState.value = LoginFormState(null, null, true)
        }
    }

    fun loginWithPlanetId() {
        Log.d("LoginViewModel", "Login with planet id.")

        getApplication<DemoRpApplication>().apiService.loginWithPlanetId(getLanguage(getApplication())).handle(
            { authRequest ->
                try {
                    PlanetId.invokeAction(
                        getApplication(),
                        authRequest!!,
                        "fudosandemorp://${PlanetIdAction.ACTION_LOGIN.action}"
                    )
                } catch (e: Exception) {
                    handleError(e)
                }
            }, { e, _ ->
                handleError(e)
            })
    }

    fun loginWithPlanetIdCallback(authResponse: AuthResponse) {
        Log.d("LoginViewModel", "Login with planet id callback $authResponse.")

        getApplication<DemoRpApplication>().accountRepository.loginWithPlanetIdCallback(authResponse, {
            _loginResultState.value = EmptyResult(success = true)
        }, this::handleError)
    }

    fun loginWithPassword(username: String, password: String) {
        Log.d("LoginViewModel", "Login with password $username.")

        getApplication<DemoRpApplication>().accountRepository.login(
            username, password,
            {
                _loginResultState.value = EmptyResult(success = true)
            }, this::handleError
        )
    }

    fun reset() {
        _loginFormState.value = LoginFormState()
        _loginResultState.value = EmptyResult()
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.isNotBlank()
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank()
    }

    private fun handleError(exception: Exception) {
        Log.e("LoginViewModel", "Error $exception.")

        _loginResultState.value = EmptyResult(error = translate(exception))
    }
}
