package com.example.githubprconsumer.messenger;

public record MessengerAddRequestDto(Long repositoryId, MessengerType messengerType, String webhookUrl) {
}
