package com.example.githubprconsumer.messenger.application;

import com.example.githubprconsumer.messenger.domain.MessengerType;

public record MessengerAddRequestDto(Long repositoryId, MessengerType messengerType, String webhookUrl) {
}
