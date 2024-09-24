package com.example.githubprconsumer.message.presentation;

import com.example.githubprconsumer.message.application.MessageService;
import com.example.githubprconsumer.message.application.dto.MessageUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {
    
    private final MessageService messageService;
    
    @PutMapping("/messengers/{messengerId}/messages")
    @Operation(summary = "PR 메시지를 수정한다 (메신저 별)")
    public void updateMessage(@PathVariable Long messengerId, @Valid @RequestBody MessageUpdateRequestDto messageUpdateRequestDto){
        messageService.updateMessage(messengerId, messageUpdateRequestDto.template());
    }
}
