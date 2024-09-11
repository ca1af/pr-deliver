package com.example.githubprconsumer.github.domain;

import com.example.githubprconsumer.global.exception.NotAuthorizedException;
import com.example.githubprconsumer.global.exception.NotFoundException;
import com.example.githubprconsumer.global.exception.SystemException;

public class GithubRepositoryException extends RuntimeException{
    public static class GithubRepositoryNotFoundException extends NotFoundException {

        private static final String REPOSITORY_NOT_FOUND = "레포지토리를 찾지 못했습니다. ";
        private static final String REPOSITORY_ID = "repositoryId :";
        private static final String WEBHOOK_URL = "webhookUrl :";

        public GithubRepositoryNotFoundException(Long repositoryId) {
            super(REPOSITORY_NOT_FOUND +  REPOSITORY_ID + repositoryId);
        }

        public GithubRepositoryNotFoundException(String webhookUrl) {
            super(REPOSITORY_NOT_FOUND + WEBHOOK_URL + webhookUrl);
        }
    }

    public static class UnsupportedRepositoryException extends SystemException {

        private static final String INVALID_REPOSITORY = "등록되지 않은 레포지토리에 대한 요청입니다. fullName : ";

        public UnsupportedRepositoryException(String fullName) {
            super(INVALID_REPOSITORY + fullName);
        }
    }

    public static class NotMyGithubRepositoryException extends NotAuthorizedException {

        private static final String NOT_MY_REPOSITORY = "사용자가 소유한 레포지토리가 아닙니다. login: %s, repoFullName: %s";

        public NotMyGithubRepositoryException(String login, String repoFullName) {
            super(String.format(NOT_MY_REPOSITORY, login, repoFullName));
        }
    }
}
