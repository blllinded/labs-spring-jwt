package com.example.labs.repository;

import com.example.labs.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findAllByUserId(Integer userId);
    Optional<Task> findByIdAndUserId(Integer id, Integer userId);
}
