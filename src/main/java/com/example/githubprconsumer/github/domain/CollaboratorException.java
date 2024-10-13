package com.example.githubprconsumer.github.domain;

import com.example.githubprconsumer.global.exception.BadRequestException;
import com.example.githubprconsumer.global.exception.NotFoundException;
import com.example.githubprconsumer.global.exception.SystemException;

public class CollaboratorException extends RuntimeException {

    public static class CollaboratorNotFoundException extends NotFoundException {

        private static final String COLLABORATOR_NOT_FOUND_FROM_REPOSITORY = "콜라보레이터를 찾지 못했습니다. repositoryId : ";

        private static final String OWNER_NOT_FOUND = "PR 생성자를 찾지 못했습니다. prAuthor : ";

        public CollaboratorNotFoundException(Long repositoryId) {
            super(COLLABORATOR_NOT_FOUND_FROM_REPOSITORY + repositoryId);
        }

        public CollaboratorNotFoundException(String prAuthor) {
            super(OWNER_NOT_FOUND + prAuthor);
        }
    }

    public static class InvalidCollaboratorCountException extends BadRequestException {

        private static final String INVALID_COLLABORATOR_COUNT = "콜라보레이터 수가 적절하지 않습니다.";

        public InvalidCollaboratorCountException() {
            super(INVALID_COLLABORATOR_COUNT);
        }
    }

    public static class BotHasNotInvitedException extends SystemException {

        private static final String NOT_INVITED = "봇 계정에 대한 초대가 완료되지 않았습니다. fullName : %s";

        public BotHasNotInvitedException(String fullName) {
            super(String.format(NOT_INVITED, fullName));
        }
    }
}
