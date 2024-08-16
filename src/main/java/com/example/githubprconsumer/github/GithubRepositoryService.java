package com.example.githubprconsumer.github;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GithubRepositoryService {

    private final GithubRepositoryJpaRepository jpaRepository;

    private final CollaboratorService collaboratorService;

    public void addGithubRepository(GithubRepositoryAddRequestDto githubRepositoryAddRequestDto){
        GithubRepository githubRepository = githubRepositoryAddRequestDto.toEntity();
        collaboratorService.addCollaborators(githubRepository.getFullName());
        jpaRepository.save(githubRepository);
    }
}

