package com.example.githubprconsumer.github.application.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record InviterInfo(
        Long id,
        String login
) {
}