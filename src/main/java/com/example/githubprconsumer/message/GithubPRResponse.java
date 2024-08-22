package com.example.githubprconsumer.message;

public record GithubPRResponse(
        String prTitle,
        String prLink,
        String prAuthor
) {
}
