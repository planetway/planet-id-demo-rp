package com.planetway.demo.fudosan.ui.accountsettings.myaccount.datafromlra

import com.planetway.demo.fudosan.data.model.Person

data class DataFromLraResult(
    val person: Person? = null,
    val error: Int? = null
)
