package com.planetway.demo.fudosan.data

import com.planetway.demo.fudosan.data.model.*
import com.planetway.planetid.rpsdk.AuthResponse
import com.planetway.planetid.rpsdk.AuthRequest
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("/api/user")
    fun getAccount(): Call<UserInfo>

    @POST("/api/user")
    fun createAccount(@Body userCredentials: UserCredentials): Call<Any>

    @POST("/api/authenticate")
    fun login(@Body userCredentials: UserCredentials): Call<UserInfo>

    @POST("/api/authenticate-planet-id")
    fun loginWithPlanetId(@Query("lang") language: String): Call<AuthRequest>

    @POST("/api/authenticate-planet-id/callback")
    fun loginWithPlanetIdCallback(@Body authResponse: AuthResponse): Call<UserInfo>

    @POST("/api/logout")
    fun logout(): Call<Any>

    @DELETE("/api/user")
    fun deleteAccount(): Call<Any>

    @POST("/api/user/link")
    fun link(@Query("lang") language: String): Call<AuthRequest>

    @POST("/api/user/unlink")
    fun unlink(): Call<UserInfo>

    @POST("/api/callback/link")
    fun linkCallback(@Body authResponse: AuthResponse): Call<UserInfo>

    @GET("/api/data-bank/{dataBank}/person")
    fun dataBankPerson(@Path("dataBank") dataBank: String): Call<Map<String, String>>

    @POST("/api/data-bank/{dataBank}/consent")
    fun dataBankConsent(@Path("dataBank") dataBank: String, @Query("lang") language: String): Call<AuthRequest>

    @GET("/api/revoke-consent-request")
    fun getConsentRevokeRequest(@Query("consentUuid") consentUuid: String): Call<AuthRequest>

    @Multipart
    @POST("/api/document/sign")
    fun sign(@Part file: MultipartBody.Part, @Query("lang") language: String): Call<AuthRequest>

    @POST("/api/callback/consent")
    fun consentCallback(@Body authResponse: AuthResponse): Call<Unit>

    @POST("/api/callback/sign")
    fun signCallback(@Body authResponse: AuthResponse): Call<Unit>

    @GET("/api/lra/person")
    fun lraPerson(): Call<Person>

    @POST("/api/lra/consent")
    fun lraConsent(@Query("lang") language: String): Call<AuthRequest>

    @GET("/api/signed-documents")
    fun getSignedDocuments(): Call<List<SignedDocument>>
}
