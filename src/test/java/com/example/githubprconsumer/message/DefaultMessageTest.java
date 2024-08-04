package com.example.githubprconsumer.message;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class DefaultMessageTest {

    @Test
    @DisplayName("사용자가 리뷰를 요청 할 때 사용하는 필수 메시지를 가진 객체를 생성 할 수 있다.")
    void createDefaultMessage() {
        String prTitle = "pr 제목";
        String prLink = "pr 링크";
        String prAuthor = "pr 작성자";
        String reviewAssignee = "리뷰 할당자";

        DefaultMessage defaultMessage = new DefaultMessage(prTitle, prLink, prAuthor, reviewAssignee);
        assertThat(defaultMessage).isNotNull();
    }

    @Test
    @DisplayName("필수 메시지를 조합해서 하나의 메시지로 리턴 할 수 있다.")
    void createUnitedMessage() {
        String prTitle = "pr 제목";
        String prLink = "pr 링크";
        String prAuthor = "pr 작성자";
        String reviewAssignee = "리뷰 할당자";

        DefaultMessage defaultMessage = new DefaultMessage(prTitle, prLink, prAuthor, reviewAssignee);

        String template = "안녕하세요 여러분! {author}님이 새로운 PR을 제출했어요: {title}. 리뷰는 {assignee}님께 할당되었습니다."
                + "여기서 확인할 수 있어요: [PR 링크]({link}) 꼼꼼하게 리뷰하고 피드백 부탁드려요. 감사합니다!";
        String mergedMessage = defaultMessage.mergeMessage(template);

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
