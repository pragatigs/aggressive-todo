package com.todo.flink.model;

import lombok.Data;

@Data
public class DebeziumEnvelope {
    private SchemaInfo schema;
    private String payload;  // this is a JSON STRING, not an object
}

@Data
class SchemaInfo {
    private String type;
    private boolean optional;
}
