package com.example.githubprconsumer.bot.api;

import com.example.githubprconsumer.github.Collaborator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubCollaboratorInfo(
        @JsonProperty("login") String login,
        @JsonProperty("id") Long id,
        @JsonProperty("avatar_url") String avatarUrl,
        @JsonProperty("html_url") String htmlUrl
) {
    public Collaborator toEntity(){
        return new Collaborator(
                id,
                login,
                avatarUrl,
                htmlUrl
        );
    }
}