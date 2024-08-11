package com.example.githubprconsumer.bot.api;

import com.example.githubprconsumer.bot.GithubInvitationsInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GithubApiService {

    @Value("${github.bot.token}")
    private String botAuthToken;

    private final RestTemplate restTemplate;

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + botAuthToken);
        return headers;
    }

    public List<GithubInvitationsInfo> fetchInvitations() {
        String invitationsUrl = GithubBotApiUrl.REPOSITORY_INVITATIONS.getUrl();
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<GithubInvitationsInfo[]> response = restTemplate.exchange(
                invitationsUrl,
                HttpMethod.GET,
                entity,
                GithubInvitationsInfo[].class
        );

        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }

    public void approveInvitation(Integer invitationId) {
        String invitationApproveUrl = GithubBotApiUrl.getInvitationApproveUrl(invitationId);
        HttpHeaders headers = createAuthHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                invitationApproveUrl,
                HttpMethod.PATCH,
                entity,
                Void.class
        );
    }
}
