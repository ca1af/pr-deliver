package com.example.githubprconsumer.github.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubPullRequest(GithubUser user, int number, String title,
                                @JsonProperty("html_url") String htmlUrl) {
}
