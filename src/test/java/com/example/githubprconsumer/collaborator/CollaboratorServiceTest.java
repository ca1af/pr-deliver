package com.example.githubprconsumer.collaborator;

import com.example.githubprconsumer.github.application.CollaboratorService;
import com.example.githubprconsumer.github.application.GithubApiService;
import com.example.githubprconsumer.github.application.dto.GithubCollaboratorInfo;
import com.example.githubprconsumer.github.domain.Collaborator;
import com.example.githubprconsumer.github.domain.CollaboratorException;
import com.example.githubprconsumer.github.domain.CollaboratorJpaRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@Transactional
class CollaboratorServiceTest {

    @Autowired
    private CollaboratorService collaboratorService;

    @Autowired
    private CollaboratorJpaRepository collaboratorJpaRepository;

    @MockBean
    private GithubApiService githubApiService;

    private static final String BOT_LOGIN = "calafs-review-bot";

    @Test
    @DisplayName("Github 저장소에서 Collaborators를 가져와 저장소에 저장한다.")
    void testAddCollaborators() {
        // Given
        String fullName = "test/repository";
        long repositoryId = 99999999L;
        GithubCollaboratorInfo collaboratorInfo = new GithubCollaboratorInfo("testLogin", "avatarUrl", "htmlUrl");
        GithubCollaboratorInfo botInfo = new GithubCollaboratorInfo(BOT_LOGIN, "avatarUrl", "htmlUrl");
        when(githubApiService.getCollaborators(fullName)).thenReturn(List.of(collaboratorInfo, botInfo));

        // When
        collaboratorService.addCollaborators(repositoryId, fullName);

        // Then
        List<Collaborator> savedCollaborators = collaboratorJpaRepository.findAllByRepositoryId(repositoryId);
        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(savedCollaborators).hasSize(1);
                    softly.assertThat(savedCollaborators.get(0).getLogin()).isEqualTo("testLogin");
                    softly.assertThat(savedCollaborators.get(0).getAvatar()).isEqualTo("avatarUrl");
                    softly.assertThat(savedCollaborators.get(0).getHtmlUrl()).isEqualTo("htmlUrl");
                }
        );
    }

    @Test
    @DisplayName("콜라보레이터 목록에 봇 계정이 추가되어 있지 않다면, 예외를 발생시킨다.")
    void throwsWhenBotHasNotInvited() {
        // Given
        String fullName = "test/repository";
        GithubCollaboratorInfo collaboratorInfo = new GithubCollaboratorInfo("testLogin", "avatarUrl", "htmlUrl");
        when(githubApiService.getCollaborators(fullName)).thenReturn(List.of(collaboratorInfo));

        assertThatThrownBy(() -> collaboratorService.addCollaborators(99999999L, fullName))
                .isInstanceOf(CollaboratorException.BotHasNotInvitedException.class);
    }


    @Test
    @DisplayName("저장된 Collaborator의 수를 정확하게 반환한다.")
    void testGetCollaboratorCount() {
        // Given
        Long repositoryId = 1L;
        collaboratorJpaRepository.save(new Collaborator(repositoryId, "login1", "avatarUrl", "htmlUrl"));
        collaboratorJpaRepository.save(new Collaborator(repositoryId, "login2", "avatarUrl", "htmlUrl"));

        // When
        int count = collaboratorService.getCollaboratorCount(repositoryId);

        // Then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("PR 작성자를 제외한 Collaborators 목록을 정확하게 반환한다.")
    void testGetCollaborators() {
        // Given
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";
        Collaborator collaborator1 = new Collaborator(repositoryId, prAuthorLogin, "avatarUrl", "htmlUrl");
        Collaborator collaborator2 = new Collaborator(repositoryId, "otherCollaborator", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.saveAll(List.of(collaborator1, collaborator2));

        // When
        List<Collaborator> collaborators = collaboratorService.getCollaborators(repositoryId, prAuthorLogin);

        // Then
        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(collaborators).hasSize(1);
                    softly.assertThat(collaborators.get(0).getLogin()).isEqualTo("otherCollaborator");
                }
        );
    }

    @Test
    @DisplayName("Collaborators가 없을 경우 예외를 던진다.")
    void testGetCollaborators_ThrowsExceptionWhenNoCollaboratorsFound() {
        // Given
        Long repositoryId = 1L;

        // When & Then
        assertThatThrownBy(() -> collaboratorService.getCollaborators(repositoryId, "testAuthor"))
                .isInstanceOf(CollaboratorException.CollaboratorNotFoundException.class);
    }

    @Test
    @DisplayName("PR 작성자가 Collaborators 목록에 없을 경우 예외를 던진다.")
    void testGetCollaborators_ThrowsExceptionWhenPRAuthorNotFound() {
        // Given
        Long repositoryId = 1L;
        Collaborator collaborator = new Collaborator(repositoryId, "differentAuthor", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.save(collaborator);

        // When & Then
        assertThatThrownBy(() -> collaboratorService.getCollaborators(repositoryId, "testAuthor"))
                .isInstanceOf(CollaboratorException.CollaboratorNotFoundException.class);
    }
}
