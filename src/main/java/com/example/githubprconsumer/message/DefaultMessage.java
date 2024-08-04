package com.example.githubprconsumer.message;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class DefaultMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String prTitle;
    private String prLink;
    private String prAuthor;
    private String reviewAssignee;

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
