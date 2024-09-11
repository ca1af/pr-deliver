package com.example.githubprconsumer.github.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepositoryInfo(
        Long id,
        @JsonProperty("full_name") String fullName
) {
}
