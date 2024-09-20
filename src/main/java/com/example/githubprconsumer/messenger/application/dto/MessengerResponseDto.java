package com.example.githubprconsumer.messenger.application.dto;

import com.example.githubprconsumer.messenger.domain.Messenger;
import com.example.githubprconsumer.messenger.domain.MessengerType;

public record MessengerResponseDto(Long messengerId, MessengerType messengerType, String encryptedWebhookUrl) {
    public static MessengerResponseDto of(Messenger messenger) {
        return new MessengerResponseDto(messenger.getId(), messenger.getMessengerType(), messenger.getWebhookUrl());
    }
}
