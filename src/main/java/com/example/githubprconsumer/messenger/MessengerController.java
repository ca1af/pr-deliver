package com.example.githubprconsumer.messenger;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessengerController {

    private final MessengerService messengerService;

    @PostMapping("/messengers")
    public void addMessenger(@RequestBody MessengerAddRequestDto messengerAddRequestDto){
        messengerService.addNewMessenger(messengerAddRequestDto);
    }

    @GetMapping("/{encryptedWebhookUrl}")
    public void activateMessenger(@PathVariable String encryptedWebhookUrl){
        messengerService.activateMessenger(encryptedWebhookUrl);
        // TODO : 여기서, 생성된 랜덤 URL 을 ... 리다이렉트를 어떻게할지?
    }

    @DeleteMapping("/messengers/{messengerId}")
    public void deleteMessenger(@PathVariable Long messengerId){
        messengerService.deleteMessenger(messengerId);
    }
}
