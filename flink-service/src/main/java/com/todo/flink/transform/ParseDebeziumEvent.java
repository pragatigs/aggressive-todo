package com.todo.flink.transform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.flink.model.DebeziumEnvelope;
import com.todo.flink.model.TaskDocument;
import com.todo.flink.model.TaskPayload;
import org.apache.flink.api.common.functions.MapFunction;

public class ParseDebeziumEvent implements MapFunction<String, TaskDocument> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public TaskDocument map(String rawMessage) throws Exception {
        DebeziumEnvelope envelope = mapper.readValue(rawMessage, DebeziumEnvelope.class);
        TaskPayload task = mapper.readValue(envelope.getPayload(), TaskPayload.class);

        return new TaskDocument(
            task.getId(),
            task.getEmail(),
            task.getTaskTitle(),
            task.getTaskDetails(),
            DateConverter.fromArray(task.getTaskCreatedAt()),
            DateConverter.fromArray(task.getTaskDueDate()),
            task.getTaskPriority(),
            task.getTaskStatus(),
            task.getTaskCategory()
        );
    }
}