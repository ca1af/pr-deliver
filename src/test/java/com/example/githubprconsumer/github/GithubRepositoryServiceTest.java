package com.example.githubprconsumer.github;

import com.example.githubprconsumer.github.domain.Collaborator;
import com.example.githubprconsumer.github.domain.CollaboratorException;
import com.example.githubprconsumer.github.application.CollaboratorService;
import com.example.githubprconsumer.github.application.GithubRepositoryService;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryAddRequestDto;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.domain.GithubRepository;
import com.example.githubprconsumer.github.domain.GithubRepositoryException;
import com.example.githubprconsumer.github.domain.GithubRepositoryJpaRepository;
import com.example.githubprconsumer.message.application.dto.GithubPRResponse;
import com.example.githubprconsumer.messenger.application.MessengerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class GithubRepositoryServiceTest {

    @Autowired
    private GithubRepositoryJpaRepository jpaRepository;

    @Autowired
    private GithubRepositoryService githubRepositoryService;

    @MockBean
    private CollaboratorService collaboratorService;

    @MockBean
    private MessengerService messengerService;

    @Test
    @DisplayName("새로운 Github 저장소를 추가하면 Collaborator를 추가하고 저장소를 저장한다.")
    void testAddGithubRepository() {
        // Given
        GithubRepositoryAddRequestDto dto = new GithubRepositoryAddRequestDto("test-user-login", "repository-full-name");

        // When
        githubRepositoryService.addGithubRepository(dto);

        // Then
        Optional<GithubRepository> savedRepository = jpaRepository.findByFullName("repository-full-name");
        assertThat(savedRepository).isPresent();
        assertThat(savedRepository.get().getOwnerLogin()).isEqualTo("test-user-login");

        // Collaborator가 추가되었는지 확인
        verify(collaboratorService, times(1)).addCollaborators(savedRepository.get().getId(), "repository-full-name");
    }

    @Test
    @DisplayName("저장된 Github 저장소를 ID로 조회하면 정상적으로 반환된다.")
    void testGetGithubRepository() {
        // Given
        GithubRepository repository = new GithubRepository("owner-login", "repository-full-name");
        jpaRepository.save(repository);

        // When
        GithubRepositoryResponseDto responseDto = githubRepositoryService.getGithubRepository(repository.getId());

        // Then
        assertThat(responseDto.fullName()).isEqualTo("repository-full-name");
        assertThat(responseDto.webhookUrl()).isEqualTo(repository.getWebhookUrl());
    }

    @Test
    @DisplayName("저장된 Github 저장소를 ID로 조회할 때 존재하지 않으면 예외가 발생한다.")
    void testGetGithubRepository_NotFound() {
        // Given
        Long invalidId = 999L;

        // When & Then
        assertThatThrownBy(() -> githubRepositoryService.getGithubRepository(invalidId))
                .isInstanceOf(GithubRepositoryException.GithubRepositoryNotFoundException.class);
    }

    @Test
    @DisplayName("Assignee 수를 업데이트할 때, 유효한 수인지 검증하고 업데이트한다.")
    void testUpdateAssigneeCount_Valid() {
        // Given
        GithubRepository repository = new GithubRepository("owner-login", "repository-full-name");
        jpaRepository.save(repository);
        when(collaboratorService.getCollaboratorCount(repository.getId())).thenReturn(5);

        // When
        githubRepositoryService.updateAssigneeCount(repository.getId(), 3);

        // Then
        GithubRepository updatedRepository = jpaRepository.findById(repository.getId()).orElseThrow();
        assertThat(updatedRepository.getAssigneeCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("Assignee 수를 업데이트할 때, Collaborator 수보다 크면 예외가 발생한다.")
    void testUpdateAssigneeCount_Invalid() {
        // Given
        GithubRepository repository = new GithubRepository("owner-login", "repository-full-name");
        jpaRepository.save(repository);
        when(collaboratorService.getCollaboratorCount(repository.getId())).thenReturn(3);

        // When & Then
        assertThatThrownBy(() -> githubRepositoryService.updateAssigneeCount(repository.getId(), 4))
                .isInstanceOf(CollaboratorException.InvalidCollaboratorCountException.class);
    }

    @Test
    @DisplayName("Webhook을 통해 알림을 보내고, 메신저 서비스를 통해 메시지를 전송한다.")
    void testSendWebhookNotification() {
        // Given
        GithubRepository repository = new GithubRepository("owner-login", "repository-full-name");
        jpaRepository.save(repository);
        Collaborator collaborator = new Collaborator(repository.getId(), "collaborator-login", "avatar-url", "html-url");
        when(collaboratorService.getCollaborators(repository.getId(), "prAuthor"))
                .thenReturn(List.of(collaborator));

        GithubPRResponse githubPRResponse = new GithubPRResponse("prTitle", "prLink", "prAuthor");

        // When
        githubRepositoryService.sendWebhookNotification(repository.getWebhookUrl(), githubPRResponse);

        // Then
        verify(messengerService, times(1)).sendMessage(eq(repository.getId()), eq(githubPRResponse), anyList());
    }

    @Test
    @DisplayName("저장소를 삭제하면, 연관된 메신저 정보도 삭제하고 이벤트를 발행한다.")
    void testDeleteGithubRepository() {
        // Given
        String ownerLogin = "owner-login";
        GithubRepository repository = new GithubRepository(ownerLogin, "repository-full-name");
        jpaRepository.save(repository);

        // When
        githubRepositoryService.deleteGithubRepository(repository.getId(), ownerLogin);

        // Then
        Optional<GithubRepository> deletedRepository = jpaRepository.findById(repository.getId());
        assertThat(deletedRepository).isEmpty();

        // 메신저 서비스의 삭제 동작이 호출되었는지 확인
        verify(messengerService, times(1)).deleteAllByRepositoryId(repository.getId());
    }

    @Test
    @DisplayName("저장소 삭제 시 권한이 없다면 예외가 발생한다.")
    void throwsUnExceptionWhenNotAuthorized() {
        // Given
        String ownerLogin = "owner-login";
        GithubRepository repository = new GithubRepository(ownerLogin, "repository-full-name");
        jpaRepository.save(repository);

        // When
        assertThatThrownBy(() -> githubRepositoryService.deleteGithubRepository(repository.getId(), "notAuthorized"))
                .isInstanceOf(GithubRepositoryException.NotMyGithubRepositoryException.class);
    }
}
