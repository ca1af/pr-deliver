package com.example.githubprconsumer.messenger.presentation;

import com.example.githubprconsumer.global.application.CustomApiResponse;
import com.example.githubprconsumer.messenger.application.MessengerReader;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "레포지토리가 가진 메신저 목록을 리턴합니다.",
            description = "특정 레포지토리 ID를 기반으로 해당 레포지토리에서 사용하는 모든 메신저의 목록을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메신저 목록을 반환합니다.",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessengerResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 레포지토리를 찾을 수 없습니다.", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러가 발생했습니다.", content = @Content)
    })
    public CustomApiResponse<List<MessengerResponseDto>> getAllMessagesByRepositoryId(
            @Parameter(description = "메신저를 찾고자 하는 레포지토리의 ID", example = "1")
            @PathVariable Long repositoryId) {
        List<MessengerResponseDto> data = messengerReader.findAllByRepositoryId(repositoryId);
        return CustomApiResponse.ofSuccess(data);
    }
}
