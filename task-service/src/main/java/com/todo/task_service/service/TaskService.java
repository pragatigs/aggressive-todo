package com.todo.task_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Locale.Category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.todo.task_service.dto.response.TaskResponse;
import com.todo.task_service.entity.TaskEntity;
import com.todo.task_service.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Page<TaskResponse> getAllTasks (String email, Pageable pageable){
        Page<TaskEntity> entityPage = taskRepository.findByEmail(email, pageable);

        Page<TaskResponse> responsePage = entityPage.map(task -> 
            TaskResponse.builder()
                .taskId(task.getId())
                .taskTitle(task.getTaskTitle())
                .taskDetails(task.getTaskDetails())
                .taskPriority(task.getTaskPriority().name())
                .taskStatus(task.getTaskStatus().name())
                .taskDueDate(task.getTaskDueDate())
                .createdAt(task.getTaskCreatedAt())
                .email(task.getEmail())
                .build()
        );

        return responsePage;
    }

    public TaskResponse addTask (String email, String taskTitle, String taskDetails, LocalDateTime taskDueDate, TaskEntity.Priority taskPriority, TaskEntity.Status taskStatus, TaskEntity.Category taskCategory) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setEmail(email);
        taskEntity.setTaskTitle(taskTitle);
        taskEntity.setTaskDetails(taskDetails);
        taskEntity.setTaskDueDate(taskDueDate);
        taskEntity.setTaskPriority(taskPriority);
        taskEntity.setTaskStatus(taskStatus);
        taskEntity.setTaskCategory(taskCategory);

        TaskEntity savedTask = taskRepository.save(taskEntity);

        return TaskResponse.builder().email(email)
        .taskId(savedTask.getId()).taskStatus("DONE").msg("None").build();
    }

    public TaskResponse deleteTask(UUID taskId, String email){
        try{
            taskRepository.deleteById(taskId);
            return TaskResponse.builder()
            .taskId(taskId)
            .msg("Deletion successful "+ email + " "+taskId)
            .build();
        }
        catch (Exception e){
            return TaskResponse.builder()
            .taskId(taskId)
            .msg("Deletion failed "+ email + " "+taskId)
            .build();
        }
    }

    public TaskResponse modifyTask(UUID taskId, String email, Object newValue, String fieldName) {
        Optional<TaskEntity> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isPresent()) {
            TaskEntity task = optionalTask.get();
            if (!task.getEmail().equals(email)) {
                return TaskResponse.builder()
                    .taskId(taskId)
                    .msg("Unauthorized modification attempt by " + email)
                    .build();
            }
            switch (fieldName) {
                case "taskTitle":
                    task.setTaskTitle((String) newValue);
                    break;
                case "taskDetails":
                    task.setTaskDetails((String) newValue);
                    break;
                case "taskDueDate":
                    task.setTaskDueDate((LocalDateTime) newValue);
                    break;
                case "taskPriority":
                    task.setTaskPriority((TaskEntity.Priority) newValue);
                    break;
                case "taskStatus":
                    task.setTaskStatus((TaskEntity.Status) newValue);
                    // Set delete timer if marking as DONE (7 days from now)
                    if (newValue == TaskEntity.Status.DONE) {
                        task.setTaskDeleteAfter(LocalDateTime.now().plusDays(7));
                    }
                    break;
                case "taskCategory":
                    task.setTaskCategory((TaskEntity.Category) newValue);
                    break;
                default:
                    return TaskResponse.builder()
                        .taskId(taskId)
                        .msg("Invalid field name: " + fieldName)
                        .build();
            }
            taskRepository.save(task);
            return TaskResponse.builder()
                .taskId(taskId)
                .msg("Modification successful for " + email + " on field " + fieldName)
                .build();
        } else {
            return TaskResponse.builder()
                .taskId(taskId)
                .msg("Task not found for modification: " + email + " with task ID " + taskId)
                .build();
        }
    }

    @Scheduled(fixedDelay = 300000)  // Run every 5 minutes
    public void cleanupExpiredTasks() {
        taskRepository.deleteAll(
            taskRepository.findAll().stream()
                .filter(t -> t.getTaskStatus() == TaskEntity.Status.DONE 
                        && t.getTaskDeleteAfter() != null 
                        && LocalDateTime.now().isAfter(t.getTaskDeleteAfter()))
                .collect(Collectors.toList())
        );
    }
}
