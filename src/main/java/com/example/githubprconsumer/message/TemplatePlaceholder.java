package com.example.githubprconsumer.message;

import lombok.Getter;

@Getter
public enum TemplatePlaceholder {
    AUTHOR_NAME("{author}"),
    PR_TITLE("{title}"),
    ASSIGNEE_NAME("{assignee}"),
    PR_LINK("{link}");

    private final String placeholder;

    TemplatePlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
