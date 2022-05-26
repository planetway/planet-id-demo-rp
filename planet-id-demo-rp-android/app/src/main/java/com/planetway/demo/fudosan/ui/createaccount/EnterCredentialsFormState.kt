package com.planetway.demo.fudosan.ui.createaccount


data class EnterCredentialsFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val verifyPasswordError: Int? = null,
    val isDataValid: Boolean = false
)
