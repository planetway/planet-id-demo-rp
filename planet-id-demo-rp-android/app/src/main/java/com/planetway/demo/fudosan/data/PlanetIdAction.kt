package com.planetway.demo.fudosan.data

enum class PlanetIdAction(val action: String) {

    ACTION_LOGIN("login-with-planet-id"),
    ACTION_LINK("link-planet-id"),
    ACTION_DATA_BANK_CONSENT("data-bank-consent"),
    ACTION_SIGN("document-sign"),
    ACTION_LRA_CONSENT("lra-consent");

    override fun toString(): String {
        return action
    }}
