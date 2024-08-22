package com.example.githubprconsumer.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    private GithubPRResponse githubPRResponse;

    @BeforeEach
    void setUp() {
        githubPRResponse = new GithubPRResponse("prTitle", "prLink", "prAuthor");
    }

    @Test
    void testGetMessage_NoCollaborators() {
        // Given
        Long messengerId = 1L;

        // When
        String message = messageService.getMessage(messengerId, githubPRResponse, List.of());

        // Then
        assertThat(message).isEqualTo("PR 할당자가 없어요. 깃허브 Collaborator 를 추가 한 후 다시 시도 해 주세요");
    }

    @Test
    void testGetMessage_WithCollaboratorsAndExistingTemplate() {
        // Given
        Long messengerId = 1L;
        List<String> assigneeLogins = List.of("assignee1", "assignee2");
        Message existingTemplate = new Message("{author} {title} {assignee} {link}", messengerId);

        when(messageRepository.findByMessengerId(messengerId)).thenReturn(Optional.of(existingTemplate));

        // When
        String message = messageService.getMessage(messengerId, githubPRResponse, assigneeLogins);

        // Then
        assertThat(message).isEqualTo("prAuthor prTitle assignee1, assignee2 prLink");
        verify(messageRepository, never()).save(any(Message.class));
    }

    @Test
    void testGetMessage_WithCollaboratorsAndNewTemplate() {
        // Given
        Long messengerId = 1L;
        List<String> assigneeLogins = List.of("assignee1", "assignee2");

        when(messageRepository.findByMessengerId(messengerId)).thenReturn(Optional.empty());
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        String message = messageService.getMessage(messengerId, githubPRResponse, assigneeLogins);

        // Then
        // Default Template 가 출력된다.
        assertThat(message).isEqualTo("""
            안녕하세요 여러분!\s
            prAuthor님이 새로운 PR을 제출했어요: prTitle.\s
            리뷰는 assignee1, assignee2님께 할당되었습니다.\s
            여기서 확인할 수 있어요: [PR 링크](prLink)\s
            꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!
            """);
        verify(messageRepository, times(1)).save(any(Message.class));
    }

    @Test
    void testGetMessage_WithCustomTemplate() {
        // Given
        Long messengerId = 1L;
        List<String> assigneeLogins = List.of("assignee");
        Message customTemplate = new Message("{author} {title} {assignee} {link}", messengerId);

        when(messageRepository.findByMessengerId(messengerId)).thenReturn(Optional.of(customTemplate));

        // When
        String actualMessage = messageService.getMessage(messengerId, githubPRResponse, assigneeLogins);

        // Then
        String expected = "prAuthor prTitle assignee prLink";
        assertThat(actualMessage).isEqualTo(expected);
    }
}
