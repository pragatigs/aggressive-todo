package com.todo.task_service.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import com.todo.task_service.entity.TaskEntity.Category;
import com.todo.task_service.entity.TaskEntity.Priority;
import com.todo.task_service.entity.TaskEntity.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {
    private UUID taskId; //for deletion
    private String taskTitle;
    private String taskDetails;
    private LocalDateTime taskDueDate;
    private Priority taskPriority;
    private Status taskStatus;
    private Category taskCategory;
}
