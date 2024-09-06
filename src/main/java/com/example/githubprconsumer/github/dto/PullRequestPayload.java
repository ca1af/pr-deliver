package com.example.githubprconsumer.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record PullRequestPayload(String action,
                                 int number,
                                 @JsonProperty("pull_request") GithubPullRequest githubPullRequest) {
}
