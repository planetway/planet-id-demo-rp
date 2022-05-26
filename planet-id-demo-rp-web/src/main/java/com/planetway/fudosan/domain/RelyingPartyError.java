package com.planetway.fudosan.domain;

public enum RelyingPartyError {
    INVALID_EMAIL("error.invalid.email"),
    PASSWORD_REQUIRED("error.password.required"),
    PASSWORDS_MISMATCH("error.passwords.mismatch"),
    EMAIL_ALREADY_EXISTS("error.login.email.already.exists"),
    ;

    String code;

    RelyingPartyError(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return getCode();
    }
}
