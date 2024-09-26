package com.example.githubprconsumer.github.presentation;

import com.example.githubprconsumer.github.application.GithubRepositoryReader;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryDetailResponseDto;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.global.application.CustomApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GithubRepositoryReadController {

    private final GithubRepositoryReader githubRepositoryReader;

    @GetMapping("/repositories/{repositoryId}")
    @Operation(summary = "레포지토리의 상세 정보를 리턴하는 GET API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 레포지토리 상세 정보가 반환되었습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GithubRepositoryDetailResponseDto.class)))
    })
    public CustomApiResponse<GithubRepositoryDetailResponseDto> getRepositoryInfo(@PathVariable Long repositoryId){
        GithubRepositoryDetailResponseDto data = githubRepositoryReader.getGithubRepository(repositoryId);
        return CustomApiResponse.ofSuccess(data);
    }

    @GetMapping("/repositories")
    @Operation(summary = "List 형태의 레포지토리의 정보를 리턴하는 GET API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 레포지토리 리스트가 반환되었습니다.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = GithubRepositoryResponseDto.class)))
    })
    public CustomApiResponse<List<GithubRepositoryResponseDto>> getRepositories(@RequestParam String ownerLogin){
        List<GithubRepositoryResponseDto> data = githubRepositoryReader.getGithubRepositories(ownerLogin);
        return CustomApiResponse.ofSuccess(data);
    }
}
