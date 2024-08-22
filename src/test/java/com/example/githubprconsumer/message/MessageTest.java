package com.example.githubprconsumer.message;

import com.example.githubprconsumer.message.domain.Message;
import com.example.githubprconsumer.message.domain.MessageTemplateException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class MessageTest {

    @Test
    @DisplayName("필수 placeHolder 가 없을 시 예외가 발생한다.")
    void createValidationTest() {
        String userInput = "TEST";
        Long repoId = 1L;

        assertThatThrownBy(() -> new Message(userInput, repoId))
                .isInstanceOf(MessageTemplateException.MissingPlaceholderTemplateException.class)
                .hasMessage("필수 메시지 입력값이 누락되었습니다. 누락된 값 : AUTHOR_NAME");
    }

    @Test
    @DisplayName("ReviewMessage 객체 생성에 성공한다.")
    void createTest() {
        String template = "안녕하세요 여러분! {author}님이 새로운 PR을 제출했어요: {title}. 리뷰는 {assignee}님께 할당되었습니다."
                + "여기서 확인할 수 있어요: [PR 링크]({link}) 꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!";
        Long repoId = 1L;

        Message message = new Message(template, repoId);
        assertThat(message).isNotNull();
    }

    @Test
    @DisplayName("필수 메시지를 조합해서 하나의 메시지로 리턴 할 수 있다.")
    void createUnitedMessage() {
        String prTitle = "pr 제목";
        String prLink = "pr 링크";
        String prAuthor = "pr 작성자";
        String reviewAssignee = "리뷰 할당자";
        String template = "안녕하세요 여러분! {author}님이 새로운 PR을 제출했어요: {title}. 리뷰는 {assignee}님께 할당되었습니다."
                + "여기서 확인할 수 있어요: [PR 링크]({link}) 꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!";
        Message message = new Message(template, 1L);

        // when
        String mergedMessage = message.mergeMessage(prTitle, prLink, prAuthor, List.of(reviewAssignee));

        // then
        String expected = "안녕하세요 여러분! pr 작성자님이 새로운 PR을 제출했어요: pr 제목. 리뷰는 리뷰 할당자님께 할당되었습니다." +
                "여기서 확인할 수 있어요: [PR 링크](pr 링크) 꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!";
        assertSoftly(
                softly -> {
                    softly.assertThat(mergedMessage).contains(prTitle);
                    softly.assertThat(mergedMessage).contains(prLink);
                    softly.assertThat(mergedMessage).contains(prAuthor);
                    softly.assertThat(mergedMessage).contains(reviewAssignee);
                    softly.assertThat(mergedMessage).isEqualTo(expected);
                }
        );
    }
}