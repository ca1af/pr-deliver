package com.example.githubprconsumer.github.dto;

import com.example.githubprconsumer.github.GithubRepository;

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
