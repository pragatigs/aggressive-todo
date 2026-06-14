package com.todo.task_service.controller;

import org.springframework.web.bind.annotation.*;

import com.todo.task_service.dto.request.TaskRequest;
import com.todo.task_service.dto.response.TaskResponse;
import com.todo.task_service.entity.TaskEntity;
import com.todo.task_service.service.TaskService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.security.core.Authentication;

@RestController
@RequiredArgsConstructor
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;

    @GetMapping("/getAll")
    public Page<TaskResponse> getAllTasks(Authentication authentication,
            @PageableDefault(size = 10, sort = "taskCreatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String email = authentication.getName();
        return taskService.getAllTasks(email, pageable);
    }

    @PostMapping("/add")
    public TaskResponse addTask(@RequestBody TaskRequest taskRequest, Authentication authentication) {

        String email = authentication.getName();

        return taskService.addTask(email,
                taskRequest.getTaskTitle(),
                taskRequest.getTaskDetails(),
                taskRequest.getTaskDueDate(),
                taskRequest.getTaskPriority(),
                taskRequest.getTaskStatus(),
                taskRequest.getTaskCategory());
    }

    @PostMapping("/delete")
    public TaskResponse deleteTask(@RequestBody TaskRequest taskRequest, Authentication authentication) {

        String email = authentication.getName();
        UUID taskId = taskRequest.getTaskId();

        return taskService.deleteTask(taskId, email);
    }

    @PatchMapping("/modify")
    public TaskResponse modifyTask(@RequestBody TaskRequest taskRequest, Authentication authentication) {
        String email = authentication.getName();
        return taskService.modifyTask(email, taskRequest);
    }
}
