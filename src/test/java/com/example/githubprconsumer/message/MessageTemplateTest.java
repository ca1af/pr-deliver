package com.example.githubprconsumer.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MessageTemplateTest {

    @Test
    @DisplayName("필수 placeHolder 가 없을 시 예외가 발생한다.")
    void createValidationTest() {
        String userInput = "TEST";
        Long repoId = 1L;

        assertThatThrownBy(() -> new MessageTemplate(userInput, repoId))
                .isInstanceOf(MessageTemplateException.MissingPlaceholderTemplateException.class)
                .hasMessage("필수 메시지 입력값이 누락되었습니다. 누락된 값 : AUTHOR_NAME");
    }

    @Test
    @DisplayName("ReviewMessage 객체 생성에 성공한다.")
    void createTest() {
        String template = "안녕하세요 여러분! {author}님이 새로운 PR을 제출했어요: {title}. 리뷰는 {assignee}님께 할당되었습니다."
                + "여기서 확인할 수 있어요: [PR 링크]({link}) 꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!";
        Long repoId = 1L;

        MessageTemplate messageTemplate = new MessageTemplate(template, repoId);
        assertThat(messageTemplate).isNotNull();
    }
}