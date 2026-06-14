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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "outbox_events")
public class OutboxEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (name = "aggregateid", nullable = false)
    private UUID aggregateId;
    @Column (name = "aggregatetype", nullable = false)
    private String aggregateType;

    public enum Event{
        TASK_CREATED, TASK_UPDATED, TASK_DELETED;
    }

    @Column (name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Event eventType;

    @Column (columnDefinition = "TEXT", nullable = false)
    private String payload;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private boolean processed;
}
