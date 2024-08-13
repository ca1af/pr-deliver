package com.example.githubprconsumer.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public record InviterInfo(
        Long id,
        String login
) {
}