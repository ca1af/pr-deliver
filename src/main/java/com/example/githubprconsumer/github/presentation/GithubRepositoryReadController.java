package com.example.githubprconsumer.github.presentation;

import com.example.githubprconsumer.github.application.GithubRepositoryReader;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryDetailResponseDto;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import io.swagger.v3.oas.annotations.Operation;
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
    public GithubRepositoryDetailResponseDto getRepositoryInfo(@PathVariable Long repositoryId){
        return githubRepositoryReader.getGithubRepository(repositoryId);
    }

    @GetMapping("/repositories")
    @Operation(summary = "List 형태의 레포지토리의 정보를 리턴하는 GET API")
    public List<GithubRepositoryResponseDto> getRepositories(@RequestParam String ownerLogin){
        return githubRepositoryReader.getGithubRepositories(ownerLogin);
    }
}
