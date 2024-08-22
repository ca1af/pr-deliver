package com.example.githubprconsumer.message.application.dto;

public record GithubPRResponse(
        String prTitle,
        String prLink,
        String prAuthor
) {
}
