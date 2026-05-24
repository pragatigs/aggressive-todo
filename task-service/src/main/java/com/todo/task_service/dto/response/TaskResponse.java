package com.todo.task_service.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponse {
    private String email;
    private UUID taskId;
    private String taskTitle;
    private String taskDetails;
    private String taskPriority;
    private String taskStatus;
    private LocalDateTime taskDueDate;
    private LocalDateTime createdAt;
    private String msg;
}
