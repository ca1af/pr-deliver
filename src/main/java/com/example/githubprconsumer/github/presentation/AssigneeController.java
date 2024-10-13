package com.example.githubprconsumer.github.presentation;

import com.example.githubprconsumer.github.application.AssigneeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AssigneeController {
    private final AssigneeService assigneeService;

    @PostMapping("/assignees")
    @Operation(summary = "registerCollaborator 가, 자신의 PR 할당자를 추가합니다. 할당자를 추가하면 할당자에게만 PR이 갑니다.")
    public void addAssignee(@RequestBody @Valid AssigneeRequestDtoWrapper assigneeRequestDtoWrapper) {
        assigneeService.addAssignee(assigneeRequestDtoWrapper.registerCollaboratorId(), assigneeRequestDtoWrapper.assigneeAddRequestDtoList());
    }
}
