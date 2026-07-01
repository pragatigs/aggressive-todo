package com.todo.flink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskPayload {
    private String id;
    private String email;
    private String taskTitle;
    private String taskDetails;
    private int[] taskCreatedAt;   // converted manually in ParseDebeziumEvent.java
    private Object taskDeleteAfter;
    private int[] taskDueDate;
    private String taskPriority;
    private String taskStatus;
    private String taskCategory;
}