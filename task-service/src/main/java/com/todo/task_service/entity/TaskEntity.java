package com.todo.task_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class TaskEntity {

    public enum Priority {
        LOW, MEDIUM, HIGH
    }

    public enum Status {
        TODO, IN_PROGRESS, DONE
    }

    public enum Category{
        WORK, PERSONAL, HEALTH, LEARNING, OTHER
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    @Column(nullable = false)
    private String taskTitle;

    private String taskDetails;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime taskCreatedAt;

    @Column
    private LocalDateTime taskDeleteAfter;

    @Column(nullable = false)
    private LocalDateTime taskDueDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority taskPriority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status taskStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category taskCategory;

    public TaskEntity(UUID id, String email, String taskTitle, String taskDetails, LocalDateTime taskDueDate,
            Priority taskPriority, Status taskStatus, Category taskCategory) {
        this.id = id;
        this.email = email;
        this.taskTitle = taskTitle;
        this.taskDetails = taskDetails;
        this.taskDueDate = taskDueDate;
        this.taskPriority = taskPriority;
        this.taskStatus = taskStatus;
        this.taskCategory = taskCategory;
    }

}