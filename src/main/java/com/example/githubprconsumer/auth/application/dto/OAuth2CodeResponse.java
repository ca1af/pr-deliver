package com.example.githubprconsumer.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OAuth2CodeResponse(
        @JsonProperty("access_token") String accessToken
) {
}
