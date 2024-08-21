package com.example.githubprconsumer.message;

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
    public void updateMessage(@PathVariable Long messengerId, @Valid @RequestBody MessageUpdateRequestDto messageUpdateRequestDto){
        messageService.updateMessage(messengerId, messageUpdateRequestDto.template());
    }
}
