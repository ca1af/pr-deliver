package com.example.githubprconsumer.github.application.dto;

import com.example.githubprconsumer.github.domain.GithubRepository;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;

import java.util.List;

public record GithubRepositoryDetailResponseDto(
        Long repositoryId,
        String fullName,
        String ownerLogin,
        String webhookUrl,
        boolean isActiveWebhook,
        List<MessengerResponseDto> messengerList
) {
    public static GithubRepositoryDetailResponseDto of(GithubRepository repository, List<MessengerResponseDto> messengerResponseDtoList){
        return new GithubRepositoryDetailResponseDto(
                repository.getId(),
                repository.getFullName(),
                repository.getOwnerLogin(),
                repository.getWebhookUrl(),
                repository.isActiveWebhook(),
                messengerResponseDtoList
        );
    }
}
