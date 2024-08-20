package com.example.githubprconsumer.github;

public record GithubRepositoryAddRequestDto(
        Long memberId,
        String fullName
) {
    public GithubRepository toEntity() {
        return new GithubRepository(
                memberId,
                fullName
        );
    }
}
