package com.example.githubprconsumer.github.presentation;

import com.example.githubprconsumer.github.application.dto.AssigneeAddRequestDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AssigneeRequestDtoWrapper(@NotNull(message = "등록자의 CollaboratorID 가 필요합니다") Long registerCollaboratorId,
                                        @NotNull(message = "리스트가 제공되지 않았습니다.")
                                        @Size(min = 1, max = 3, message = "할당자 요청 사이즈는 1~3 이어야 합니다.")
                                        List<AssigneeAddRequestDto> assigneeAddRequestDtoList) {

}
