package com.example.githubprconsumer.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PingPayload(RepositoryInfo repository) {
}
