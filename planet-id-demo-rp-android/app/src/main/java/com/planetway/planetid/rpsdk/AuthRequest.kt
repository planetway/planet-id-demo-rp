package com.planetway.planetid.rpsdk

import com.google.gson.annotations.SerializedName

data class AuthRequest(

    @SerializedName("action")
    var action: String,

    @SerializedName("provider_uri")
    var providerUri: String,

    @SerializedName("client_id")
    var clientId: String,

    @SerializedName("nonce")
    var nonce: String,

    @SerializedName("state")
    var state: String,

    @SerializedName("redirect_uri")
    var redirectUri: String,

    @SerializedName("level")
    var level: Int?,

    @SerializedName("language")
    var language: String?,

    @SerializedName("scope")
    var scope: String,

    @SerializedName("response_type")
    var responseType: String,

    @SerializedName("payload")
    var payload: String?,

    @SerializedName("planet_id")
    var planetId: String?
)
