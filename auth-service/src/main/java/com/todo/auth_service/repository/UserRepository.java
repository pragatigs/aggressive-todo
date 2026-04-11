package com.todo.auth_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.todo.auth_service.entity.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> // <EntityType, PK type>
{
    boolean existsById(String email);
}
