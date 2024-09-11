package com.example.githubprconsumer.github.presentation;

import com.example.githubprconsumer.auth.domain.CustomOauth2User;
import com.example.githubprconsumer.github.application.GithubBotService;
import com.example.githubprconsumer.github.application.GithubRepositoryService;
import com.example.githubprconsumer.github.application.dto.AssigneeUpdateRequestDto;
import com.example.githubprconsumer.github.application.dto.GithubPullRequest;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.application.dto.PingPayload;
import com.example.githubprconsumer.github.application.dto.PullRequestPayload;
import com.example.githubprconsumer.github.application.dto.RepositoryInfo;
import com.example.githubprconsumer.github.application.dto.WebhookResponse;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class GithubRepositoryController {

    private final GithubRepositoryService githubRepositoryService;
    private final ObjectMapper mapper;
    private final GithubBotService githubBotService;


    @PostMapping("/repositories")
    @Operation(description = "사용자가 레포지토리에 bot 계정을 추가 한 후 이 API 를 호출하면, 우리 서비스에 레포지토리의 정보가 등록된다.")
    public void registerRepository(@RequestParam @Parameter(description = "레포지토리의 풀네임 ex : ca1af/review-provider") String fullName){
        githubBotService.checkAndApproveInvitations(fullName);
    }

    @PostMapping("/{hookUrl}")
    @Operation(description = "PR, Ping 등 웹훅 이벤트가 발생되었을 때 호출되는 API (프론트측에서 사용하지 않는 API)")
    public void createWebhookMessage(@RequestHeader("X-GitHub-Event") String eventType,
                                     @PathVariable String hookUrl,
                                     @RequestBody WebhookResponse webhookResponse) throws JsonProcessingException {

        if ("ping".equals(eventType)) {
            // 핑이 왔다면 해당 레포지토리의 웹훅이 잘 등록 된 것이다.
            PingPayload pingPayload = mapper.readValue(webhookResponse.payload(), PingPayload.class);
            RepositoryInfo repositoryInfo = pingPayload.repository();
            githubRepositoryService.activateWebhook(repositoryInfo);
        }

        if ("pull_request".equals(eventType)) {
            // Pull Request 이벤트 처리
            PullRequestPayload pullRequestPayload = mapper.readValue(webhookResponse.payload(), PullRequestPayload.class);
            GithubPullRequest githubPullRequest = pullRequestPayload.githubPullRequest();
            GithubPRResponse githubPRResponse = new GithubPRResponse(githubPullRequest.title(), githubPullRequest.htmlUrl(), githubPullRequest.user().login());
            githubRepositoryService.sendWebhookNotification(hookUrl, githubPRResponse);
        }

        // 여기서 예외처리를 해야한다.
    }

    @GetMapping("/repositories/{repositoryId}/webhooks")
    @Operation(description = "레포지토리의 정보를 리턴하는 GET API")
    public GithubRepositoryResponseDto getRepositoryInfo(@PathVariable Long repositoryId){
        return githubRepositoryService.getGithubRepository(repositoryId);
    }

    // TODO : 인증 방식이 결정되면, 인증 유저만 수정/삭제 하도록 한다.
    @PutMapping("/repositories/{repositoryId}/counts")
    @Operation(description = "레포지토리에서 발생한 PR의 리뷰어 수를 변경하는 API. 콜라보레이터가 4명(자신포함) 이라면 최대 3명까지 가능하다.")
    public void updateAssigneeCount(@PathVariable Long repositoryId,
                                    @RequestBody AssigneeUpdateRequestDto assigneeUpdateRequestDto){
        githubRepositoryService.updateAssigneeCount(repositoryId, assigneeUpdateRequestDto.count());
    }

    // TODO : 인증 방식이 결정되면, 인증 유저만 수정/삭제 하도록 한다.
    @DeleteMapping("/repositories/{repositoryId}")
    @Operation(description = "내가 가입한 레포지토리에서 나온다")
    public void deleteGithubRepository(@PathVariable Long repositoryId, @AuthenticationPrincipal CustomOauth2User customOauth2User){
        githubRepositoryService.deleteGithubRepository(repositoryId, customOauth2User.login());
    }
}
