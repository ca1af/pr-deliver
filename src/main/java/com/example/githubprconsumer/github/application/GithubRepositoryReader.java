package com.example.githubprconsumer.github.application;

import com.example.githubprconsumer.github.application.dto.GithubRepositoryDetailResponseDto;
import com.example.githubprconsumer.github.application.dto.GithubRepositoryResponseDto;
import com.example.githubprconsumer.github.domain.GithubRepository;
import com.example.githubprconsumer.github.domain.GithubRepositoryException;
import com.example.githubprconsumer.github.domain.GithubRepositoryJpaRepository;
import com.example.githubprconsumer.messenger.application.MessengerReader;
import com.example.githubprconsumer.messenger.application.dto.MessengerResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubRepositoryReader {

    private final GithubRepositoryJpaRepository jpaRepository;

    private final MessengerReader messengerReader;

    public GithubRepositoryDetailResponseDto getGithubRepository(Long repositoryId){
        GithubRepository githubRepository = jpaRepository.findById(repositoryId).orElseThrow(
                () -> new GithubRepositoryException.GithubRepositoryNotFoundException(repositoryId)
        );

        List<MessengerResponseDto> messengerResponseDtoList = messengerReader.findAllByRepositoryId(repositoryId);

        return GithubRepositoryDetailResponseDto.of(githubRepository, messengerResponseDtoList);
    }

    public List<GithubRepositoryResponseDto> getGithubRepositories(String ownerLogin){
        List<GithubRepository> ownerRepositories = jpaRepository.findAllByOwnerLogin(ownerLogin);
        return ownerRepositories.stream().map(GithubRepositoryResponseDto::of).toList();
    }
}
