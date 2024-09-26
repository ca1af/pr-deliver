package com.example.githubprconsumer.messenger.presentation;

import com.example.githubprconsumer.global.application.CustomApiResponse;
import com.example.githubprconsumer.messenger.application.MessengerAliasService;
import com.example.githubprconsumer.messenger.application.dto.MessengerAliasAddRequestDto;
import com.example.githubprconsumer.messenger.application.dto.MessengerAliasResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MessengerAliasController {

    private final MessengerAliasService messengerAliasService;

    @PostMapping("/messengers/alias")
    @Operation(summary = "콜라보레이터의 메신저 별명 (ex:@125162... -> '@DK'님, 과 같은 멘션가능) 을 설정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메신저 별명이 설정되었습니다.", content = @Content)
    })
    public void createAlias(@RequestBody List<MessengerAliasAddRequestDto> messengerAliasAddRequestDto) {
        messengerAliasService.addMessengerAlias(messengerAliasAddRequestDto);
    }

    @GetMapping("/messengers/{messengerId}/alias")
    @Operation(summary = "메신저 아이디 별로 맵핑되어있는 메신저 별명 리스트를 리턴합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메신저 별명 리스트가 반환되었습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MessengerAliasResponseDto.class)))
    })
    public CustomApiResponse<List<MessengerAliasResponseDto>> getMessengerAliasByMessengerType(@PathVariable Long messengerId) {
        List<MessengerAliasResponseDto> data = messengerAliasService.findAllByMessengerId(messengerId);
        return CustomApiResponse.ofSuccess(data);
    }
}
