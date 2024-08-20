package com.example.githubprconsumer.github;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GithubRepositoryServiceTest {

    @Mock
    private GithubRepositoryJpaRepository jpaRepository;

    @Mock
    private CollaboratorService collaboratorService;

    @InjectMocks
    private GithubRepositoryService githubRepositoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addGithubRepository_ShouldSaveIfRepositoryDoesNotExist() {
        // Given
        String fullName = "example/repository";
        GithubRepositoryAddRequestDto requestDto = new GithubRepositoryAddRequestDto(1L, fullName);

        when(jpaRepository.existsByFullName(fullName)).thenReturn(false);
        when(jpaRepository.save(any(GithubRepository.class))).thenReturn(mock(GithubRepository.class));

        // When
        githubRepositoryService.addGithubRepository(requestDto);

        // Then
        verify(jpaRepository, times(1)).save(any(GithubRepository.class));
    }
}