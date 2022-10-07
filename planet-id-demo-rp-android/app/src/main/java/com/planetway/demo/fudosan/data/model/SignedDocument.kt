package com.planetway.demo.fudosan.data.model

data class SignedDocument(
    val uuid: String,
    val signatureType: String,
    val consentUuid: String?,
    val revokeDocumentUuid: String?
)
