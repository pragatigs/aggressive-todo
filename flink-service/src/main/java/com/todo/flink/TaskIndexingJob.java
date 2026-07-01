package com.todo.flink;

import com.todo.flink.model.TaskDocument;
import com.todo.flink.sink.TaskElasticsearchSink;
import com.todo.flink.transform.ParseDebeziumEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class TaskIndexingJob {

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        KafkaSource<String> source = KafkaSource.<String>builder()
            .setBootstrapServers("kafka:9092")
            .setTopics("outbox.event.TASK_CREATED", "outbox.event.TASK_UPDATED", "outbox.event.TASK_DELETED")
            .setGroupId("flink-task-indexer")
            .setStartingOffsets(OffsetsInitializer.earliest())
            .setValueOnlyDeserializer(new SimpleStringSchema())
            .build();

        DataStream<String> rawEvents = env.fromSource(
            source,
            WatermarkStrategy.noWatermarks(),
            "kafka-source"
        );

        DataStream<TaskDocument> parsedEvents = rawEvents.map(new ParseDebeziumEvent());

        // parsedEvents.print();

        // System.out.println("PARSINGGGGGGGGGG calling ES sinkkkkkk");

        parsedEvents.sinkTo(TaskElasticsearchSink.build());
        env.execute("Task Indexing Job");
    }
}