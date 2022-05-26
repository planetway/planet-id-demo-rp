package com.planetway.planetid.rpsdk

data class AuthResponse(
    var code: String,
    var state: String?,
    var callback: String
)
