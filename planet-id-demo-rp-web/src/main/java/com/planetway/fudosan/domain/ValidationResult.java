package com.planetway.fudosan.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ValidationResult {
    private final List<RelyingPartyError> errors = new ArrayList<>();

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
