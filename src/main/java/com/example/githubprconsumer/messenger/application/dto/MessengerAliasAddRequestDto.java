package com.example.githubprconsumer.messenger.application.dto;

import com.example.githubprconsumer.messenger.domain.MessengerAlias;

public record MessengerAliasAddRequestDto(
        Long collaboratorId,
        Long messengerId,
        String alias
) {
    public MessengerAlias toEntity(){
        return new MessengerAlias(
                collaboratorId,
                messengerId,
                alias
        );
    }
}
