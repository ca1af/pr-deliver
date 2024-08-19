package com.example.githubprconsumer.github;

import com.example.githubprconsumer.global.BadRequestException;
import com.example.githubprconsumer.global.NotFoundException;

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

        private static final String INVALID_COLLABORATOR_COUNT = "리뷰 할당자 수가 콜라보레이터 수보다 많습니다.";

        public InvalidCollaboratorCountException() {
            super(INVALID_COLLABORATOR_COUNT);
        }
    }
}
