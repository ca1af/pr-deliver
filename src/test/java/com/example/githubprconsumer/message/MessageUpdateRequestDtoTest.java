package com.example.githubprconsumer.message;

import com.example.githubprconsumer.message.application.dto.MessageUpdateRequestDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class MessageUpdateRequestDtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldThrowExceptionWhenTemplateIsMissingPlaceholder() {
        // Given
        String invalidTemplate = "{author} {title}";  // {assignee} {link} 미포함

        // When
        MessageUpdateRequestDto dto = new MessageUpdateRequestDto(invalidTemplate);
        Set<ConstraintViolation<MessageUpdateRequestDto>> constraintViolations = validator.validate(dto);

        // then
        assertThat(constraintViolations).hasSize(1);
        ConstraintViolation<MessageUpdateRequestDto> violation = constraintViolations.iterator().next();
        assertThat(violation.getMessage())
                .isEqualTo("템플릿에는 {author}, {title}, {assignee}, {link} 플레이스홀더가 모두 포함되어야 합니다");
    }

    @Test
    void shouldPassWhenTemplateContainsAllPlaceholders() {
        // Given
        String validTemplate = "{author} {title} {assignee} {link}";

        // When
        MessageUpdateRequestDto dto = new MessageUpdateRequestDto(validTemplate);

        // Then
        Set<ConstraintViolation<MessageUpdateRequestDto>> constraintViolations = validator.validate(dto);
        assertThat(constraintViolations).isEmpty();
    }
}
