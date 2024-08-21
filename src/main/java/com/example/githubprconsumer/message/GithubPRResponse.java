package com.example.githubprconsumer.message;

import java.util.List;

public record GithubPRResponse(
        String prTitle,
        String prLink,
        String prAuthor
) {
    public DefaultMessage toDefaultMessage(List<String> reviewAssignees) {
        return new DefaultMessage(
                prTitle,
                prLink,
                prAuthor,
                String.join(", ", reviewAssignees)
        );
    }
}
