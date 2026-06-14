package com.todo.task_service.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todo.task_service.entity.TaskEntity;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, UUID> {

    Page<TaskEntity> findByEmail(String email, Pageable pageable);

}