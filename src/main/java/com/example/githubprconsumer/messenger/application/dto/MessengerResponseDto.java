package com.example.githubprconsumer.messenger.application.dto;

import com.example.githubprconsumer.messenger.domain.Messenger;
import com.example.githubprconsumer.messenger.domain.MessengerType;

import java.util.ArrayList;
import java.util.List;

public record MessengerResponseDto(Long messengerId,
                                   MessengerType messengerType,
                                   String encryptedWebhookUrl,
                                   List<MessengerAliasResponseDto> messengerAliasList) {

    public static MessengerResponseDto of(Messenger messenger, List<MessengerAliasResponseDto> messengerAliasResponseDtoList) {
        return new MessengerResponseDto(
                messenger.getId(),
                messenger.getMessengerType(),
                messenger.getWebhookUrl(),
                messengerAliasResponseDtoList
        );
    }

    public static MessengerResponseDto ofNew(Messenger messenger) {
        return new MessengerResponseDto(
                messenger.getId(),
                messenger.getMessengerType(),
                messenger.getWebhookUrl(),
                new ArrayList<>()
        );
    }
}
