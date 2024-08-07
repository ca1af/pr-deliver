package com.example.githubprconsumer.message;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class MessageServiceTest {

    @MockBean
    private MessageTemplateRepository messageTemplateRepository;

    @Autowired
    private MessageService messageService;

    @Test
    void testGetDefaultMessage() {
        // arrange
        String githubRepositoryId = "githubRepositoryId";
        String assignee = "assignee";
        String template = "{author} {title} {assignee} {link}";
        GithubPRResponse githubPRResponse = new GithubPRResponse("prTitle", "prLink", "prAuthor");
        MessageTemplate messageTemplate = new MessageTemplate(template, githubRepositoryId);
        when(messageTemplateRepository.findByGithubRepositoryId(githubRepositoryId)).thenReturn(Optional.of(messageTemplate));

        // act
        String defaultMessage = messageService.getDefaultMessage(githubRepositoryId, githubPRResponse, assignee);

        // assert
        String expected = "prAuthor prTitle assignee prLink";
        assertThat(defaultMessage).isEqualTo(expected);
    }
}