package com.example.githubprconsumer.messenger.presentation;

import com.example.githubprconsumer.global.application.CustomApiResponse;
import com.example.githubprconsumer.global.auth.domain.CustomOauth2User;
import com.example.githubprconsumer.messenger.application.MessengerService;
import com.example.githubprconsumer.messenger.application.dto.MessengerAddRequestDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Log4j2
@SecurityRequirement(name = "Bearer Authentication")
public class MessengerController {

    private final MessengerService messengerService;

    @PostMapping("/messengers")
    @Operation(summary = "레포지토리 웹훅을 수신할 새 메신저를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메신저가 추가되었습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessengerResponseDto.class)))
    })
    public CustomApiResponse<MessengerResponseDto> addMessenger(@RequestBody MessengerAddRequestDto messengerAddRequestDto, @AuthenticationPrincipal CustomOauth2User customOauth2User){
        MessengerResponseDto data = messengerService.addNewMessenger(messengerAddRequestDto, customOauth2User.login());
        return CustomApiResponse.ofSuccess(data);
    }

    @GetMapping("/messengers/applies")
    @Operation(summary = "사용자가 추가한 메신저를 활성화시키는 API. 메신저(디스코드 등)에 URL를 발급해서 사용한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메신저가 활성화되었습니다.", content = @Content)
    })
    public void activateMessenger(@RequestParam String encryptedWebhookUrl){
        String decodedUrl = encryptedWebhookUrl.replace(" ", "+");
        log.info("메신저 활성화 요청, 디코딩된 객체: {}", decodedUrl);

        messengerService.activateMessenger(decodedUrl);
    }

    @DeleteMapping("/messengers/{messengerId}")
    @Operation(summary = "사용자의 메신저를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메신저가 삭제되었습니다.", content = @Content)
    })
    public void deleteMessenger(@PathVariable Long messengerId, @AuthenticationPrincipal CustomOauth2User customOauth2User){
        messengerService.deleteMessenger(messengerId, customOauth2User.login());
    }
}
