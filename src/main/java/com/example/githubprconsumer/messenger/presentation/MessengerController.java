package com.example.githubprconsumer.messenger.presentation;

import com.example.githubprconsumer.auth.domain.CustomOauth2User;
import com.example.githubprconsumer.messenger.application.MessengerService;
import com.example.githubprconsumer.messenger.application.dto.MessengerAddRequestDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
public class MessengerController {

    private final MessengerService messengerService;

    @PostMapping("/messengers")
    @Operation(description = "레포지토리 웹훅을 수신할 새 메신저를 추가합니다.")
    public MessengerResponseDto addMessenger(@RequestBody MessengerAddRequestDto messengerAddRequestDto, @AuthenticationPrincipal CustomOauth2User customOauth2User){
        return messengerService.addNewMessenger(messengerAddRequestDto, customOauth2User.login());
    }

    @GetMapping("/{encryptedWebhookUrl}")
    @Operation(description = "사용자가 추가한 메신저를 활성화시키는 API. 메신저(디스코드 등)에 URL를 발급해서 사용한다")
    public void activateMessenger(@PathVariable String encryptedWebhookUrl){
        log.info("메신저 활성화 요청, 요청객체 : {}", encryptedWebhookUrl);
        messengerService.activateMessenger(encryptedWebhookUrl);
    }

    @DeleteMapping("/messengers/{messengerId}")
    @Operation(description = "사용자의 메신저를 삭제합니다.")
    public void deleteMessenger(@PathVariable Long messengerId, @AuthenticationPrincipal CustomOauth2User customOauth2User){
        messengerService.deleteMessenger(messengerId, customOauth2User.login());
    }
}
