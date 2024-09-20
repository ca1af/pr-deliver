package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.domain.GithubRepository;
import com.example.githubprconsumer.github.domain.GithubRepositoryException;
import com.example.githubprconsumer.github.domain.GithubRepositoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubRepositoryReader {

    private final GithubRepositoryJpaRepository jpaRepository;

    public GithubRepositoryResponseDto getGithubRepository(Long repositoryId){
        GithubRepository githubRepository = jpaRepository.findById(repositoryId).orElseThrow(
                () -> new GithubRepositoryException.GithubRepositoryNotFoundException(repositoryId)
        );

        return GithubRepositoryResponseDto.of(githubRepository);
    }

    public List<GithubRepositoryResponseDto> getGithubRepositories(String ownerLogin){
        List<GithubRepository> ownerRepositories = jpaRepository.findAllByOwnerLogin(ownerLogin);
        return ownerRepositories.stream().map(GithubRepositoryResponseDto::of).toList();
    }
}
