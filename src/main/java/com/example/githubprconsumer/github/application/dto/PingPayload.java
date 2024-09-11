package com.example.githubprconsumer.github.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PingPayload(RepositoryInfo repository) {
}
