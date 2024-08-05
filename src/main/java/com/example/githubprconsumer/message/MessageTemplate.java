package com.example.githubprconsumer.message;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@Entity
@NoArgsConstructor
@Getter
public class MessageTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String template;

    @Column(nullable = false)
    private String githubRepositoryId;

    public MessageTemplate(String template, String githubRepositoryId) {
        validate(template);
        this.template = template;
        this.githubRepositoryId = githubRepositoryId;
    }

    private void validate(String template) {
        Arrays.stream(TemplatePlaceholder.values())
                .filter(placeholder -> !template.contains(placeholder.getPlaceholder()))
                .findFirst()
                .ifPresent(placeholder -> {
                    throw new MessageTemplateException.MissingPlaceholderTemplateException(placeholder.name());
                });
    }
}
