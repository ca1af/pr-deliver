package com.example.githubprconsumer.message;

public record GithubPRResponse(
        String prTitle,
        String prLink,
        String prAuthor
) {
    public DefaultMessage toDefaultMessage(String reviewAssignee) {
        return new DefaultMessage(
                prTitle,
                prLink,
                prAuthor,
                reviewAssignee
        );
    }
}
