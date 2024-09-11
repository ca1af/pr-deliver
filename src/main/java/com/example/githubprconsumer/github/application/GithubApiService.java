package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.GithubCollaboratorInfo;
import com.example.githubprconsumer.github.application.dto.GithubInvitationsInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@Log4j2
public class GithubApiService {

    @Value("${github.bot.token}")
    private String botAuthToken;

    @Value("${github.bot.username}")
    private String botUsername;

    private final RestClient restClient;

    public GithubApiService(RestClient restClient) {
        this.restClient = restClient;
    }

    private HttpHeaders createAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + botAuthToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    public List<GithubInvitationsInfo> fetchInvitations() {
        String invitationsUrl = GithubBotApiUrl.REPOSITORY_INVITATIONS.getUrl();

        try {
            GithubInvitationsInfo[] response = restClient.get()
                    .uri(invitationsUrl)
                    .headers(headers -> headers.addAll(createAuthHeaders()))
                    .retrieve()
                    .body(GithubInvitationsInfo[].class);

            return Arrays.asList(Objects.requireNonNull(response));
        } catch (RestClientException e) {
            log.error("Failed to fetch invitations: {}", e.getMessage());
            throw e;
        }
    }

    public void approveInvitation(Integer invitationId) {
        String invitationApproveUrl = GithubBotApiUrl.getInvitationApproveUrl(invitationId);

        try {
            restClient.patch()
                    .uri(invitationApproveUrl)
                    .headers(headers -> headers.addAll(createAuthHeaders()))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully approved invitation with ID: {}", invitationId);
        } catch (RestClientException e) {
            log.error("Failed to approve invitation with ID {}: {}", invitationId, e.getMessage());
            throw e;
        }
    }

    public List<GithubCollaboratorInfo> getCollaborators(String fullName) {
        String collaboratorsUrl = GithubBotApiUrl.getCollaboratorsUrl(fullName);

        try {
            GithubCollaboratorInfo[] response = restClient.get()
                    .uri(collaboratorsUrl)
                    .headers(headers -> headers.addAll(createAuthHeaders()))
                    .retrieve()
                    .body(GithubCollaboratorInfo[].class);

            List<GithubCollaboratorInfo> collaboratorInfos = Arrays.asList(Objects.requireNonNull(response));
            log.info("Successfully retrieved collaborators: {}", collaboratorInfos);
            return collaboratorInfos;
        } catch (RestClientException e) {
            log.error("Failed to fetch collaborators for {}: {}", fullName, e.getMessage());
            throw e;
        }
    }

    public void removeSelfFromRepository(String fullName) {
        String removeCollaboratorUrl = String.format("https://api.github.com/repos/%s/collaborators/%s", fullName, botUsername);

        try {
            restClient.delete()
                    .uri(removeCollaboratorUrl)
                    .headers(headers -> headers.addAll(createAuthHeaders()))
                    .retrieve()
                    .toBodilessEntity();

            log.info("Successfully removed user '{}' from repository: {}", botUsername, fullName);
        } catch (RestClientException e) {
            log.error("Failed to remove user '{}' from repository: {}. Error: {}", botUsername, fullName, e.getMessage());
            throw e;
        }
    }

}
