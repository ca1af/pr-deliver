package com.example.githubprconsumer.github.dto;

import com.example.githubprconsumer.github.GithubRepository;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubPayload(String action,
                            int number,
                            @JsonProperty("pull_request") GithubPullRequest githubPullRequest,
                            GithubRepository repository) {
}
