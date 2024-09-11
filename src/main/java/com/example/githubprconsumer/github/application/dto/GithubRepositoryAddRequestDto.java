package com.example.githubprconsumer.github.application.dto;

import com.example.githubprconsumer.github.domain.GithubRepository;

public record GithubRepositoryAddRequestDto(
        String login,
        String fullName
) {
    public GithubRepository toEntity() {
        return new GithubRepository(
                login,
                fullName
        );
    }
}
