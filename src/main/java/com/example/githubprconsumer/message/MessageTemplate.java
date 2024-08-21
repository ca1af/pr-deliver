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
    private Long messengerId;

    public MessageTemplate(String template, Long messengerId) {
        validate(template);
        this.template = template;
        this.messengerId = messengerId;
    }

    public MessageTemplate(Long messengerId){
        this.template = TemplateExample.PR_TEMPLATE.getMessage();
        this.messengerId = messengerId;
    }

    public void validate(String template) {
        Arrays.stream(TemplatePlaceholder.values())
                .filter(placeholder -> !template.contains(placeholder.getPlaceholder()))
                .findFirst()
                .ifPresent(placeholder -> {
                    throw new MessageTemplateException.MissingPlaceholderTemplateException(placeholder.name());
                });
    }

    public void updateMessage(String template) {
        validate(template);
        this.template = template;
    }
}
