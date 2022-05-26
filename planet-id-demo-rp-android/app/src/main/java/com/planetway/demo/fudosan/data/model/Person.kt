package com.planetway.demo.fudosan.data.model

data class Person(
    var firstNameKatakana: String?,
    var lastNameKatakana: String?,
    var firstNameKanji: String?,
    var lastNameKanji: String?,
    var firstNameRomaji: String?,
    var lastNameRomaji: String?,
    var dateOfBirth: String?,
    var address: List<Address>?
)
