package com.planetway.demo.fudosan.data

import com.google.gson.Gson
import com.planetway.demo.fudosan.data.exception.ApiException
import com.planetway.demo.fudosan.data.exception.NetworkException
import com.planetway.demo.fudosan.data.exception.UnauthorizedException
import com.planetway.demo.fudosan.data.model.ApiError
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun <T> Call<T>.handle(onSuccess: (T?) -> Unit, onFailure: (Exception, Response<T?>?) -> Unit) {
    this.enqueue(object : Callback<T> {
        override fun onFailure(call: Call<T?>, throwable: Throwable) {
            onFailure(NetworkException(throwable), null)
        }

        override fun onResponse(call: Call<T?>, response: Response<T?>) {
            if (response.isSuccessful) {
                onSuccess(response.body())
            } else if (response.code() == 401) {
                onFailure(UnauthorizedException(), response)
            } else {
                val errorBody = response.errorBody()
                if (errorBody != null) {
                    val errorString = errorBody.string()
                    try {
                        val apiError = Gson().fromJson(errorString, ApiError::class.java)
                        onFailure(ApiException(apiError.errorCode), response)
                    } catch (e: Exception) {
                        onFailure(RuntimeException("Api error: $errorString"), response)
                    }
                } else {
                    onFailure(RuntimeException("Unknown api error"), response)
                }
            }
        }
    })
}
