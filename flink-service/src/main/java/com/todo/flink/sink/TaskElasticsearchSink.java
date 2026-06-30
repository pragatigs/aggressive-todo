package com.todo.flink.sink;

import com.todo.flink.model.TaskDocument;
import org.apache.flink.connector.elasticsearch.sink.Elasticsearch7SinkBuilder;
import org.apache.flink.connector.elasticsearch.sink.ElasticsearchSink;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;

import com.fasterxml.jackson.databind.ObjectMapper;

// import java.util.HashMap;
import java.util.Map;

public class TaskElasticsearchSink {

    private static final ObjectMapper mapper = new ObjectMapper()
    .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
    .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static ElasticsearchSink<TaskDocument> build() {
        System.out.println("PARSINGGGGGGGGGG called ES sinkkkkkk");
        return new Elasticsearch7SinkBuilder<TaskDocument>()
            .setBulkFlushMaxActions(1)
            .setHosts(new HttpHost("elasticsearch", 9200, "http"))
            .setEmitter((task, context, indexer) -> {
                try {
                    Map<String, Object> json = mapper.convertValue(task, Map.class);

                    IndexRequest request = Requests.indexRequest()
                            .index("tasks")
                            .id(task.getId())
                            .source(json);

                    indexer.add(request);
                    System.out.println("PARSINGGGGGGGGGG done ES sinkkkkkk");
                } catch (Exception e) {
                    throw new RuntimeException(
                            "Failed to convert TaskDocument to ES request", e);
                }
            })
            .build();
    }
}