package com.example.githubprconsumer.messenger.application.dto;

import com.example.githubprconsumer.messenger.domain.MessengerAlias;

public record MessengerAliasResponseDto(
        Long messengerId,
        Long collaboratorId,
        String alias
) {
    public static MessengerAliasResponseDto of(MessengerAlias messengerAlias) {
        return new MessengerAliasResponseDto(
                messengerAlias.getMessengerId(),
                messengerAlias.getCollaboratorId(),
                messengerAlias.getAlias()
        );
    }
}
