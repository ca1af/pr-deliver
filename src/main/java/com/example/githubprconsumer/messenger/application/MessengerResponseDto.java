package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.domain.MessengerType;

public record MessengerResponseDto(Long repositoryId, MessengerType messengerType, String encryptedWebhookUrl) {
}
