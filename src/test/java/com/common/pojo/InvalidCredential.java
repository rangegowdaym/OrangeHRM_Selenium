package com.common.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single invalid-credential entry used by the login DataProvider.
 * Maps directly to each object in the {@code invalidCredentials} JSON array.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InvalidCredential(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password,
        @JsonProperty("scenario") String scenario
) {
}
