package com.planetway.planetid.rpsdk

import android.content.Context
import android.content.Intent
import android.net.Uri

object PlanetId {

    fun invokeAction(context: Context, authRequest: AuthRequest, returnAppUri: String) {
        val uriBuilder = Uri.Builder()
        uriBuilder.scheme("planetid")
        uriBuilder.authority("invoke-action")

        uriBuilder.appendQueryParameter("client_id", authRequest.clientId)
        uriBuilder.appendQueryParameter("signature_type", authRequest.action)
        uriBuilder.appendQueryParameter("scope", authRequest.scope)
        uriBuilder.appendQueryParameter("action", authRequest.action)
        uriBuilder.appendQueryParameter("redirect_uri", authRequest.redirectUri)
        uriBuilder.appendQueryParameter("response_type", authRequest.responseType)
        uriBuilder.appendQueryParameter("nonce", authRequest.nonce)
        uriBuilder.appendQueryParameter("state", authRequest.state)
        uriBuilder.appendQueryParameter("language", authRequest.language)
        uriBuilder.appendQueryParameter("return_app_uri", returnAppUri)
        if (authRequest.payload != null) {
            uriBuilder.appendQueryParameter("payload", authRequest.payload)
        }

        context.startActivity(
            Intent(Intent.ACTION_VIEW, uriBuilder.build()).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    fun getAuthResponse(uri: Uri): AuthResponse {
        val code = uri.getQueryParameter("code")!!
        val state = uri.getQueryParameter("state")
        val callback = uri.getQueryParameter("callback")!!
        return AuthResponse(
            code,
            state,
            callback
        )
    }

    fun isRejected(uri: Uri): Boolean {
        return uri.getQueryParameter("rejected") != null
    }

    fun getError(uri: Uri): String? {
        return uri.getQueryParameter("error")
    }
}
