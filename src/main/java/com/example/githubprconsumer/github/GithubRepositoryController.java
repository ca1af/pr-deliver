package com.example.githubprconsumer.github;

import com.example.githubprconsumer.github.dto.AssigneeUpdateRequestDto;
import com.example.githubprconsumer.github.dto.GithubPayload;
import com.example.githubprconsumer.github.dto.GithubPullRequest;
import com.example.githubprconsumer.github.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.dto.WebhookResponse;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GithubRepositoryController {

    private final GithubRepositoryService githubRepositoryService;
    private final ObjectMapper mapper;

    @PostMapping("/{hookUrl}")
    public void createWebhookMessage(@PathVariable String hookUrl, @RequestBody WebhookResponse webhookResponse) throws JsonProcessingException {
        GithubPayload githubPayload = mapper.readValue(webhookResponse.payload(), GithubPayload.class);
        GithubPullRequest githubPullRequest = githubPayload.githubPullRequest();
        GithubPRResponse githubPRResponse = new GithubPRResponse(githubPullRequest.title(), githubPullRequest.htmlUrl(), githubPullRequest.user().login());
        githubRepositoryService.sendWebhookNotification(hookUrl, githubPRResponse);
    }

    @GetMapping("/repositories/{repositoryId}/webhooks")
    public GithubRepositoryResponseDto getRepositoryInfo(@PathVariable Long repositoryId){
        return githubRepositoryService.getGithubRepository(repositoryId);
    }

    // TODO : 인증 방식이 결정되면, 인증 유저만 수정/삭제 하도록 한다.
    @PutMapping("/repositories/{repositoryId}/counts")
    public void updateAssigneeCount(@PathVariable Long repositoryId,
                                    @RequestBody AssigneeUpdateRequestDto assigneeUpdateRequestDto){
        githubRepositoryService.updateAssigneeCount(repositoryId, assigneeUpdateRequestDto.count());
    }

    // TODO : 인증 방식이 결정되면, 인증 유저만 수정/삭제 하도록 한다.
    @DeleteMapping("/repositories/{repositoryId}")
    public void deleteGithubRepository(@PathVariable Long repositoryId){
        githubRepositoryService.deleteGithubRepository(repositoryId);
    }
}
