package com.planetway.demo.fudosan.ui.createaccount

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.planetway.demo.R
import com.planetway.demo.fudosan.DemoRpApplication
import com.planetway.demo.fudosan.data.handle
import com.planetway.demo.fudosan.data.model.UserCredentials
import com.planetway.demo.fudosan.ui.EmptyResult
import com.planetway.demo.fudosan.ui.translate
import java.util.regex.Pattern


class CreateAccountViewModel(
    application: DemoRpApplication
) : AndroidViewModel(application) {

    private val _enterCredentialsFormState = MutableLiveData(EnterCredentialsFormState())
    private val _createAccountResultState = MutableLiveData(EmptyResult())

    val enterCredentialsFormState: LiveData<EnterCredentialsFormState> = _enterCredentialsFormState
    val createAccountResultState: LiveData<EmptyResult> = _createAccountResultState

    fun credentialsChanged(username: String, password: String, verifyPassword: String) {
        if (!isUsernameValid(username)) {
            _enterCredentialsFormState.value =
                EnterCredentialsFormState(R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _enterCredentialsFormState.value =
                EnterCredentialsFormState(
                    null,
                    R.string.invalid_password
                )
        } else if (!isVerifyPasswordValid(password, verifyPassword)) {
            _enterCredentialsFormState.value =
                EnterCredentialsFormState(
                    null,
                    null,
                    R.string.passwords_dont_match
                )
        } else {
            _enterCredentialsFormState.value =
                EnterCredentialsFormState(
                    null,
                    null,
                    null,
                    true
                )
        }
    }

    fun createAccount(username: String, password: String) {
        getApplication<DemoRpApplication>().apiService.createAccount(
            UserCredentials(username, password)
        ).handle(
            {
                _createAccountResultState.value = EmptyResult(success = true)
            }, { e, _ ->
                _createAccountResultState.value = EmptyResult(error = translate(e))
            })
    }

    private fun isUsernameValid(username: String): Boolean {
        return username.contains('@')
            .and(Pattern.matches(".+@.+", username))
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 5
    }

    private fun isVerifyPasswordValid(password: String, verifyPassword: String): Boolean {
        return password == verifyPassword
    }
}
