package com.planetway.demo.fudosan

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import com.planetway.demo.BuildConfig
import com.planetway.demo.fudosan.data.AccountRepository
import com.planetway.demo.fudosan.data.ApiConstants
import com.planetway.demo.fudosan.data.ApiService
import com.planetway.demo.fudosan.ui.LoginActivity
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DemoRpApplication : Application() {

    val apiService: ApiService = Retrofit.Builder()
        .baseUrl(getApiBaseUrl())
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient().newBuilder()
                .followRedirects(false)
                .addInterceptor(LoggingInterceptor())
                .addInterceptor(AuthenticationInterceptor(this))
                .cookieJar(SessionCookieJar())
                .build()
        )
        .build()
        .create(ApiService::class.java)

    lateinit var accountRepository: AccountRepository

    override fun onCreate() {
        super.onCreate()

        accountRepository = AccountRepository(apiService)
    }

    private fun getApiBaseUrl(): String {
        return when(BuildConfig.FLAVOR_backendUrl) {
            "backendProd" -> ApiConstants.PROD
            "backendPoc" -> ApiConstants.POC
            "backendTest" -> ApiConstants.TEST
            else -> ApiConstants.ANDROID_HOST
        }
    }

    class LoggingInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            Log.d("RETROFIT2", "request: $request")
            try {
                val response: Response = chain.proceed(request)
                Log.d("RETROFIT2", "response: $response")
                return response
            } catch (e: Exception) {
                Log.e("RETROFIT2", "error: $e")
                throw e
            }
        }
    }

    class AuthenticationInterceptor(private val context: Context) : Interceptor {

        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response = chain.proceed(chain.request())
            if (response.code() == 401) {
                context.startActivity(Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
            return response
        }
    }

    class SessionCookieJar : CookieJar {

        private var cookies: MutableList<Cookie> = mutableListOf()

        override fun saveFromResponse(url: HttpUrl, cookies: MutableList<Cookie>) {
            if (url.encodedPath() == "/api/logout") {
                Log.d("RETROFIT2", "Removing session cookie")
                this.cookies = mutableListOf()
                return
            }

            for (newCookie in cookies) {
                this.cookies.removeAll { it.name() == newCookie.name() }
                if (!newCookie.value().isNullOrBlank()) {
                    this.cookies.add(newCookie)
                }
            }
        }

        override fun loadForRequest(url: HttpUrl): MutableList<Cookie> {
            return cookies
        }
    }
}
