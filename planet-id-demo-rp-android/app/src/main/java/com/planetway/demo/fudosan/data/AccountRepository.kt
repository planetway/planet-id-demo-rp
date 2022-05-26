package com.planetway.demo.fudosan.data

import android.content.Context
import android.util.Log
import com.planetway.demo.fudosan.data.model.Person
import com.planetway.demo.fudosan.data.model.UserCredentials
import com.planetway.demo.fudosan.data.model.UserInfo
import com.planetway.demo.fudosan.ui.getLanguage
import com.planetway.planetid.rpsdk.AuthResponse
import com.planetway.planetid.rpsdk.PlanetId.invokeAction


class AccountRepository(
    private val apiService: ApiService
) {

    private var userInfo: UserInfo? = null
    private var person: Person? = null

    fun login(
        username: String,
        password: String,
        onSuccess: (UserInfo) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.login(UserCredentials(username, password)).handle(
            { userInfo ->
                this.userInfo = userInfo!!
                onSuccess(userInfo)
            }, { e, _ ->
                onFailure(e)
            }
        )
    }

    fun loginWithPlanetIdCallback(
        authResponse: AuthResponse,
        onSuccess: (UserInfo) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.loginWithPlanetIdCallback(authResponse).handle({ userInfo ->
            this.userInfo = userInfo!!
            onSuccess(userInfo)
        }, { e, _ ->
            onFailure(e)
        })
    }

    fun linkWithPlanetIdCallback(
        authResponse: AuthResponse,
        onSuccess: (UserInfo) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.linkCallback(authResponse).handle({ userInfo ->
            this.userInfo = userInfo!!
            onSuccess(userInfo)
        }, { e, _ ->
            onFailure(e)
        })
    }

    fun unlinkWithPlanetId(
        onSuccess: (UserInfo) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.unlink().handle(
            { userInfo ->
                this.userInfo = userInfo!!
                onSuccess(userInfo)
            }, { e, _ ->
                onFailure(e)
            })
    }

    fun isLinkedWithPlanetId(): Boolean {
        return userInfo!!.planetId != null
    }

    fun getAccount(
        onSuccess: (UserInfo) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.getAccount().handle(
            { userInfo ->
                this.userInfo = userInfo!!
                onSuccess(userInfo)
            }, { e, _ ->
                onFailure(e)
            }
        )
    }

    fun getLocalUserInfo(): UserInfo {
        return this.userInfo!!
    }

    fun deleteAccount(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.deleteAccount().handle(
            {
                userInfo = null
                onSuccess()
            }, { e, _ ->
                onFailure(e)
            }
        )
    }

    fun logout(
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.logout().handle(
            {
                userInfo = null
                onSuccess()
            }, { e, _ ->
                onFailure(e)
            }
        )
    }

    fun getPersonalInfo(
        context: Context,
        onSuccess: (Person) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        apiService.lraPerson().handle(
            { person ->
                this.person = person!!
                onSuccess(person)
            }, { e, response ->
                if (response?.code() == 403) {
                    apiService.lraConsent(getLanguage(context)).handle(
                        { authRequest ->
                            try {
                                Log.i("", "Successfully got authRequest $authRequest")

                                invokeAction(
                                    context,
                                    authRequest!!,
                                    "fudosandemorp://${PlanetIdAction.ACTION_LRA_CONSENT.action}"
                                )
                            } catch (e: java.lang.Exception) {
                                Log.e("", "LRA consent error", e)

                                onFailure(e)
                            }
                        }, { e, _ ->
                            onFailure(e)
                        }
                    )
                } else {
                    onFailure(e)
                }
            }
        )
    }

    fun getPeron(): Person? {
        return person
    }
}
