package com.example.githubprconsumer.messenger.application.dto;

import com.example.githubprconsumer.messenger.domain.MessengerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MessengerAddRequestDto(@NotNull Long repositoryId,
                                     @NotNull MessengerType messengerType,
                                     @NotBlank(message = "웹훅 url 이 없습니다") String webhookUrl) {
}
