package com.example.githubprconsumer.messenger.presentation;

import com.example.githubprconsumer.messenger.application.MessengerReader;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessengerReadController {

    private final MessengerReader messengerReader;

    @GetMapping("/repositories/{repositoryId}/messengers")
    @Operation(description = "레포지토리가 가진 메신저 목록을 리턴합니다.")
    public List<MessengerResponseDto> getAllMessagesByRepositoryId(@PathVariable Long repositoryId) {
        return messengerReader.findAllByRepositoryId(repositoryId);
    }
}
