package com.example.githubprconsumer.message;

import com.example.githubprconsumer.message.application.MessageService;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.message.domain.Message;
import com.example.githubprconsumer.message.domain.MessageRepository;
import com.example.githubprconsumer.messenger.MessengerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class MessageServiceTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    private GithubPRResponse githubPRResponse;

    @BeforeEach
    void setUp() {
        githubPRResponse = new GithubPRResponse("prTitle", "prLink", "prAuthor");
    }

    @Test
    @DisplayName("PR 할당자가 없을 때, 적절한 메시지를 반환한다.")
    void testGetMessage_NoCollaborators() {
        // Given
        Long messengerId = 1L;

        // When
        String message = messageService.getMessage(messengerId, githubPRResponse, List.of());

        // Then
        assertThat(message).isEqualTo("PR 할당자가 없어요. 깃허브 Collaborator 를 추가 한 후 다시 시도 해 주세요");
    }

    @Test
    @DisplayName("기존에 저장된 템플릿이 있고, 할당자가 있을 때 올바른 메시지를 생성한다.")
    void testGetMessage_WithCollaboratorsAndExistingTemplate() {
        // Given
        Long messengerId = 1L;
        List<String> assigneeLogins = List.of("assignee1", "assignee2");
        Message existingTemplate = new Message("{author} {title} {assignee} {link}", messengerId);
        messageRepository.save(existingTemplate);

        // When
        String message = messageService.getMessage(messengerId, githubPRResponse, assigneeLogins);

        // Then
        assertThat(message).isEqualTo("prAuthor prTitle assignee1, assignee2 prLink");
    }

    @Test
    @DisplayName("기존 템플릿이 없을 때, 새로운 기본 템플릿을 저장하고 올바른 메시지를 생성한다.")
    void testGetMessage_WithCollaboratorsAndNewTemplate() {
        // Given
        Long messengerId = 1L;
        List<String> assigneeLogins = List.of("assignee1", "assignee2");

        // When
        String message = messageService.getMessage(messengerId, githubPRResponse, assigneeLogins);

        // Then
        assertThat(message).isEqualTo("""
            안녕하세요 여러분!\s
            prAuthor님이 새로운 PR을 제출했어요: prTitle.\s
            리뷰는 assignee1, assignee2님께 할당되었습니다.\s
            여기서 확인할 수 있어요: [PR 링크](prLink)\s
            꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!
            """);

        // 새로운 템플릿이 저장되었는지 확인
        Optional<Message> savedMessage = messageRepository.findByMessengerId(messengerId);
        assertThat(savedMessage).isPresent();
    }

    @Test
    @DisplayName("사용자 정의 템플릿을 사용할 때, 올바른 메시지를 생성한다.")
    void testGetMessage_WithCustomTemplate() {
        // Given
        Long messengerId = 1L;
        List<String> assigneeLogins = List.of("assignee");
        Message customTemplate = new Message("{author} {title} {assignee} {link}", messengerId);
        messageRepository.save(customTemplate);

        // When
        String actualMessage = messageService.getMessage(messengerId, githubPRResponse, assigneeLogins);

        // Then
        String expected = "prAuthor prTitle assignee prLink";
        assertThat(actualMessage).isEqualTo(expected);
    }

    @Test
    @DisplayName("메신저 ID로 메시지를 업데이트하고, 저장된 템플릿이 변경된다.")
    void testUpdateMessage_Success() {
        // Given
        Long messengerId = 1L;
        Message existingMessage = new Message("{author} {title} {assignee} {link}", messengerId);
        messageRepository.save(existingMessage);
        String newTemplate = "새로운 템플릿: {author}, {title}, {assignee}, {link}";

        // When
        messageService.updateMessage(messengerId, newTemplate);

        // Then
        Message updatedMessage = messageRepository.findByMessengerId(messengerId).orElseThrow();
        assertThat(updatedMessage.getTemplate()).isEqualTo(newTemplate);
    }

    @Test
    @DisplayName("메신저 ID로 메시지를 삭제하고, 해당 메신저 ID의 템플릿이 모두 삭제된다.")
    void testDeleteMessage_ByMessengerId_Success() {
        // Given
        Long messengerId = 1L;
        Message message = new Message("{author} {title} {assignee} {link}", messengerId);
        messageRepository.save(message);

        // When
        messageService.deleteAllMessagesByMessengerId(messengerId);

        // Then
        List<Message> remainingMessages = messageRepository.findAllByMessengerId(messengerId);
        assertThat(remainingMessages).isEmpty();
    }

    @Test
    @DisplayName("메신저 ID로 저장된 메시지가 없을 때 예외가 발생한다.")
    void testUpdateMessage_MessengerNotFound() {
        // Given
        Long messengerId = 1L;
        String newTemplate = "새로운 템플릿: {author}, {title}, {assignee}, {link}";

        // When & Then
        assertThatThrownBy(() -> messageService.updateMessage(messengerId, newTemplate))
                .isInstanceOf(MessengerException.MessengerNotFoundException.class);
    }
}
