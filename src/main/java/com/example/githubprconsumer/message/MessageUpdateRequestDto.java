package com.example.githubprconsumer.message;

import jakarta.validation.constraints.Pattern;

public record MessageUpdateRequestDto(
        @Pattern(
                regexp = ".*\\{author}.*\\{title}.*\\{assignee}.*\\{link}.*",
                message = "템플릿에는 {author}, {title}, {assignee}, {link} 플레이스홀더가 모두 포함되어야 합니다"
        )
        String template) {
}
