package com.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Top-level wrapper that maps the {@code login_test_data.json} structure.
 * Used by {@code LoginTest} to drive data-provider scenarios from a JSON file.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginTestData {

    @JsonProperty("invalidCredentials")
    private List<InvalidCredential> invalidCredentials;

    public List<InvalidCredential> getInvalidCredentials() {
        return invalidCredentials == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(invalidCredentials);
    }

    public void setInvalidCredentials(List<InvalidCredential> invalidCredentials) {
        this.invalidCredentials = Objects.requireNonNull(invalidCredentials, "invalidCredentials must not be null");
    }
}
