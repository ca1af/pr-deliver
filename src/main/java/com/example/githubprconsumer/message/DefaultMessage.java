package com.example.githubprconsumer.message;

public class DefaultMessage {

    private final String prTitle;
    private final String prLink;
    private final String prAuthor;
    private final String reviewAssignee;

    public DefaultMessage(String prTitle, String prLink, String prAuthor, String reviewAssignee) {
        this.prTitle = prTitle;
        this.prLink = prLink;
        this.prAuthor = prAuthor;
        this.reviewAssignee = reviewAssignee;
    }

    public String mergeMessage(String template) {
        return template
                .replace(TemplatePlaceholder.PR_TITLE.getPlaceholder(), prTitle)
                .replace(TemplatePlaceholder.AUTHOR_NAME.getPlaceholder(), prAuthor)
                .replace(TemplatePlaceholder.ASSIGNEE_NAME.getPlaceholder(), reviewAssignee)
                .replace(TemplatePlaceholder.PR_LINK.getPlaceholder(), prLink);
    }
}
