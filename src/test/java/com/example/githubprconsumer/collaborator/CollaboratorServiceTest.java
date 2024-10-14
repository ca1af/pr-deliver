package com.example.githubprconsumer.collaborator;

import com.example.githubprconsumer.github.application.AssigneeService;
import com.example.githubprconsumer.github.application.CollaboratorService;
import com.example.githubprconsumer.github.application.GithubApiService;
import com.example.githubprconsumer.github.application.dto.AssigneeAddRequestDto;
import com.example.githubprconsumer.github.application.dto.GithubCollaboratorInfo;
import com.example.githubprconsumer.github.domain.Collaborator;
import com.example.githubprconsumer.github.domain.CollaboratorException;
import com.example.githubprconsumer.github.domain.CollaboratorJpaRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

    @Autowired
    private AssigneeService assigneeService;

    private static final String BOT_LOGIN = "calafs-review-bot";

    @Test
    @DisplayName("Github 저장소에서 Collaborators를 가져와 저장소에 저장한다.")
    void testAddCollaborators() {
        String fullName = "test/repository";
        long repositoryId = 99999999L;
        GithubCollaboratorInfo collaboratorInfo = new GithubCollaboratorInfo("testLogin", "avatarUrl", "htmlUrl");
        GithubCollaboratorInfo botInfo = new GithubCollaboratorInfo(BOT_LOGIN, "avatarUrl", "htmlUrl");
        when(githubApiService.getCollaborators(fullName)).thenReturn(List.of(collaboratorInfo, botInfo));

        collaboratorService.addCollaborators(repositoryId, fullName);

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
        String fullName = "test/repository";
        GithubCollaboratorInfo collaboratorInfo = new GithubCollaboratorInfo("testLogin", "avatarUrl", "htmlUrl");
        when(githubApiService.getCollaborators(fullName)).thenReturn(List.of(collaboratorInfo));

        assertThatThrownBy(() -> collaboratorService.addCollaborators(99999999L, fullName))
                .isInstanceOf(CollaboratorException.BotHasNotInvitedException.class);
    }

    @Test
    @DisplayName("저장된 Collaborator의 수를 정확하게 반환한다.")
    void testGetCollaboratorCount() {
        Long repositoryId = 1L;
        collaboratorJpaRepository.save(new Collaborator(repositoryId, "login1", "avatarUrl", "htmlUrl"));
        collaboratorJpaRepository.save(new Collaborator(repositoryId, "login2", "avatarUrl", "htmlUrl"));

        int count = collaboratorService.getCollaboratorCount(repositoryId);

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Assignee 가 없을 경우 PR 작성자를 제외한 Collaborators 목록을 정확하게 반환한다.")
    void testGetAssigneeLogins_NoAssignees() {
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";
        Collaborator collaborator1 = new Collaborator(repositoryId, prAuthorLogin, "avatarUrl", "htmlUrl");
        Collaborator collaborator2 = new Collaborator(repositoryId, "otherCollaborator", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.saveAll(List.of(collaborator1, collaborator2));

        List<String> assignees = collaboratorService.getAssigneeLogins(repositoryId, 1, prAuthorLogin);

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(assignees).hasSize(1);
                    softly.assertThat(assignees.get(0)).isEqualTo("otherCollaborator");
                }
        );
    }


    @Test
    @DisplayName("Assignee가 있을 경우 Assignee 목록을 반환한다.")
    void testGetAssigneeLogins_WithAssignees() {
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";
        Collaborator register = new Collaborator(repositoryId, prAuthorLogin, "avatarUrl", "htmlUrl");
        Collaborator assignee = new Collaborator(repositoryId, "assigneeCollaborator", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.saveAll(List.of(register, assignee));

        AssigneeAddRequestDto assigneeDto = new AssigneeAddRequestDto(assignee.getId(), assignee.getLogin());
        assigneeService.addAssignee(register.getId(), List.of(assigneeDto));

        List<String> assignees = collaboratorService.getAssigneeLogins(repositoryId, 1, prAuthorLogin);

        assertThat(assignees.get(0)).isEqualTo("assigneeCollaborator");
    }

    @ParameterizedTest
    @ValueSource(ints = {1,5,7,100})
    @DisplayName("할당자 수가 N명 이라도 할당자가 있다면 할당자 수만큼 할당된다.")
    void testGetAssigneeLogins_ifCollaboratorCountExceeded(int assigneeCount) {
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";
        Collaborator register = new Collaborator(repositoryId, prAuthorLogin, "avatarUrl", "htmlUrl");
        Collaborator assignee = new Collaborator(repositoryId, "assigneeCollaborator", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.saveAll(List.of(register, assignee));

        AssigneeAddRequestDto assigneeDto = new AssigneeAddRequestDto(assignee.getId(), assignee.getLogin());
        assigneeService.addAssignee(register.getId(), List.of(assigneeDto));

        List<String> assignees = collaboratorService.getAssigneeLogins(repositoryId, assigneeCount, prAuthorLogin);

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(assignees).hasSize(1);
                    softly.assertThat(assignees.get(0)).isEqualTo("assigneeCollaborator");
                }
        );
    }

    @Test
    @DisplayName("Collaborators가 없을 경우 예외를 던진다.")
    void testGetAssigneeLogins_ThrowsExceptionWhenNoCollaboratorsFound() {
        Long repositoryId = 1L;

        assertThatThrownBy(() -> collaboratorService.getAssigneeLogins(repositoryId, 1, "testAuthor"))
                .isInstanceOf(CollaboratorException.CollaboratorNotFoundException.class);
    }

    @Test
    @DisplayName("PR 작성자가 Collaborators 목록에 없을 경우 예외를 던진다.")
    void testGetAssigneeLogins_ThrowsExceptionWhenPRAuthorNotFound() {
        Long repositoryId = 1L;
        Collaborator collaborator = new Collaborator(repositoryId, "differentAuthor", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.save(collaborator);

        assertThatThrownBy(() -> collaboratorService.getAssigneeLogins(repositoryId, 1, "testAuthor"))
                .isInstanceOf(CollaboratorException.CollaboratorNotFoundException.class);
    }

    @Test
    @DisplayName("PR 작성자를 제외하고 지정된 수 만큼의 Collaborators를 반환한다.")
    void testGetAssigneeLogins_LimitedCollaborators() {
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";
        Collaborator collaborator1 = new Collaborator(repositoryId, prAuthorLogin, "avatarUrl", "htmlUrl");
        Collaborator collaborator2 = new Collaborator(repositoryId, "otherCollaborator1", "avatarUrl", "htmlUrl");
        Collaborator collaborator3 = new Collaborator(repositoryId, "otherCollaborator2", "avatarUrl", "htmlUrl");

        collaboratorJpaRepository.saveAll(List.of(collaborator1, collaborator2, collaborator3));

        List<String> assignees = collaboratorService.getAssigneeLogins(repositoryId, 1, prAuthorLogin);

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(assignees).hasSize(1);
                    softly.assertThat(assignees.get(0)).isEqualTo("otherCollaborator1");
                }
        );
    }
}
