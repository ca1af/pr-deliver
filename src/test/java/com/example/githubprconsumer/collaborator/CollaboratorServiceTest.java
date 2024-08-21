package com.example.githubprconsumer.collaborator;

import com.example.githubprconsumer.bot.api.GithubApiService;
import com.example.githubprconsumer.bot.api.GithubCollaboratorInfo;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CollaboratorServiceTest {

    @Mock
    private GithubApiService githubApiService;

    @Mock
    private CollaboratorJpaRepository collaboratorJpaRepository;

    @InjectMocks
    private CollaboratorService collaboratorService;

    @Test
    void testAddCollaborators() {
        String fullName = "test/repository";
        GithubCollaboratorInfo collaboratorInfo = mock(GithubCollaboratorInfo.class);
        Collaborator collaboratorEntity = mock(Collaborator.class);

        when(githubApiService.getCollaborators(fullName)).thenReturn(Collections.singletonList(collaboratorInfo));
        when(collaboratorInfo.toEntity()).thenReturn(collaboratorEntity);

        collaboratorService.addCollaborators(fullName);

        ArgumentCaptor<List<Collaborator>> captor = ArgumentCaptor.forClass(List.class);
        verify(collaboratorJpaRepository).saveAll(captor.capture());

        List<Collaborator> savedEntities = captor.getValue();
        Assertions.assertThat(savedEntities).hasSize(1);
        Assertions.assertThat(savedEntities.get(0)).isEqualTo(collaboratorEntity);
    }

    @Test
    void testGetCollaboratorCount() {
        Long repositoryId = 1L;
        int expectedCount = 5;

        when(collaboratorJpaRepository.countByRepositoryId(repositoryId)).thenReturn(expectedCount);

        int count = collaboratorService.getCollaboratorCount(repositoryId);

        Assertions.assertThat(expectedCount).isEqualTo(count);
    }

    @Test
    void testGetCollaborators() {
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";

        Collaborator collaborator1 = mock(Collaborator.class);
        Collaborator collaborator2 = mock(Collaborator.class);

        when(collaborator1.getLogin()).thenReturn(prAuthorLogin);
        when(collaboratorJpaRepository.findByRepositoryId(repositoryId)).thenReturn(Arrays.asList(collaborator1, collaborator2));

        List<Collaborator> collaborators = collaboratorService.getCollaborators(repositoryId, prAuthorLogin);

        SoftAssertions.assertSoftly(softly -> {
                    softly.assertThat(collaborators).doesNotContain(collaborator1);
                    softly.assertThat(collaborators).contains(collaborator2);
                }
        );
    }

    @Test
    void testGetCollaborators_ThrowsExceptionWhenNoCollaboratorsFound() {
        Long repositoryId = 1L;

        when(collaboratorJpaRepository.findByRepositoryId(repositoryId)).thenReturn(Collections.emptyList());

        Assertions.assertThatThrownBy(() -> collaboratorService.getCollaborators(repositoryId, "testAuthor"))
                .isInstanceOf(CollaboratorException.CollaboratorNotFoundException.class);
    }

    @Test
    void testGetCollaborators_ThrowsExceptionWhenPRAuthorNotFound() {
        Long repositoryId = 1L;
        String prAuthorLogin = "testAuthor";

        Collaborator collaborator = mock(Collaborator.class);

        when(collaboratorJpaRepository.findByRepositoryId(repositoryId)).thenReturn(Collections.singletonList(collaborator));
        when(collaborator.getLogin()).thenReturn("differentAuthor");

        Assertions.assertThatThrownBy(() -> collaboratorService.getCollaborators(repositoryId, prAuthorLogin))
                .isInstanceOf(CollaboratorException.CollaboratorNotFoundException.class);
    }
}