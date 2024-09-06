package com.example.githubprconsumer.github.dto;

import com.example.githubprconsumer.github.GithubRepository;

public record GithubRepositoryResponseDto(
        Long repositoryId,
        String fullName,
        String ownerLogin,
        String webhookUrl,
        boolean isActiveWebhook
) {
    public static GithubRepositoryResponseDto of(GithubRepository repository){
        return new GithubRepositoryResponseDto(
                repository.getId(),
                repository.getFullName(),
                repository.getOwnerLogin(),
                repository.getWebhookUrl(),
                repository.isActiveWebhook()
        );
    }
}
