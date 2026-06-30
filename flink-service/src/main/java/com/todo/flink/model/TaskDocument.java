package com.todo.flink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDocument {
    private String id;
    private String email;
    private String taskTitle;
    private String taskDetails;
    private LocalDateTime taskCreatedAt;
    private LocalDateTime taskDueDate;
    private String taskPriority;
    private String taskStatus;
    private String taskCategory;
}