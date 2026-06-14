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
import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todo.task_service.dto.request.TaskRequest;
import com.todo.task_service.dto.response.TaskResponse;
import com.todo.task_service.entity.OutboxEventEntity;
import com.todo.task_service.entity.TaskEntity;
import com.todo.task_service.repository.OutboxEventRepository;
import com.todo.task_service.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

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
        OutboxEventEntity outboxEvent = new OutboxEventEntity();
        outboxEvent.setAggregateId(savedTask.getId());
        outboxEvent.setAggregateType("TASK");
        outboxEvent.setEventType(OutboxEventEntity.Event.TASK_CREATED);

        String payload = "";
        try {
            payload = objectMapper.writeValueAsString(taskEntity);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        System.out.println("Serialized TaskEntity payload: " + payload);
        outboxEvent.setPayload(payload);
        outboxEventRepository.save(outboxEvent);

        return TaskResponse.builder().email(email)
        .taskId(savedTask.getId()).taskStatus("DONE").msg("None").build();
    }

    public TaskResponse deleteTask(UUID taskId, String email){
        try{
            TaskEntity taskEntity = taskRepository.findById(taskId)
            .orElseThrow(() -> new RuntimeException("Task not found"));

            OutboxEventEntity outboxEvent = new OutboxEventEntity();
            outboxEvent.setAggregateId(taskEntity.getId());
            outboxEvent.setAggregateType("TASK");
            outboxEvent.setEventType(OutboxEventEntity.Event.TASK_DELETED);

            String payload = "";
            try {
                payload = objectMapper.writeValueAsString(taskEntity);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            System.out.println("Serialized TaskEntity payload: " + payload);
            outboxEvent.setPayload(payload);
            outboxEventRepository.save(outboxEvent);
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

    public TaskResponse modifyTask(String email, TaskRequest request) {

        UUID taskId = request.getTaskId();

        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getEmail().equals(email)) {
            return TaskResponse.builder()
                    .taskId(taskId)
                    .msg("Unauthorized")
                    .build();
        }

        if (request.getTaskTitle() != null) {
            task.setTaskTitle(request.getTaskTitle());
        }

        if (request.getTaskDetails() != null) {
            task.setTaskDetails(request.getTaskDetails());
        }

        if (request.getTaskDueDate() != null) {
            task.setTaskDueDate(request.getTaskDueDate());
        }

        if (request.getTaskPriority() != null) {
            task.setTaskPriority(request.getTaskPriority());
        }

        if (request.getTaskStatus() != null) {
            task.setTaskStatus(request.getTaskStatus());

            if (request.getTaskStatus() == TaskEntity.Status.DONE) {
                task.setTaskDeleteAfter(LocalDateTime.now().plusDays(7));
            }
        }

        if (request.getTaskCategory() != null) {
            task.setTaskCategory(request.getTaskCategory());
        }

        TaskEntity savedTask = taskRepository.save(task);

        OutboxEventEntity event = new OutboxEventEntity();
        event.setAggregateId(savedTask.getId());
        event.setAggregateType("TASK");
        event.setEventType(OutboxEventEntity.Event.TASK_UPDATED);
        try {
            event.setPayload(objectMapper.writeValueAsString(savedTask));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        outboxEventRepository.save(event);

        return TaskResponse.builder()
                .taskId(taskId)
                .msg("Task updated")
                .build();
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
