package com.example.labs.service;

import com.example.labs.dto.TaskCreateRequest;
import com.example.labs.dto.TaskUpdateRequest;
import com.example.labs.model.Task;
import com.example.labs.model.User;
import com.example.labs.repository.TaskRepository;
import com.example.labs.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.labs.exception.NotFoundException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    private Integer getUserIdByEmail(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return u.getId();
    }

    public Task create(String email, TaskCreateRequest req) {
        Integer userId = getUserIdByEmail(email);

        Task t = new Task();
        t.setUserId(userId);
        t.setTitle(req.title);
        t.setDescription(req.description);

        return taskRepository.save(t);
    }

    public List<Task> listMine(String email) {
        Integer userId = getUserIdByEmail(email);
        return taskRepository.findAllByUserId(userId);
    }

    public Task updateMine(String email, Integer taskId, TaskUpdateRequest req) {
        Integer userId = getUserIdByEmail(email);

        Task t = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        t.setTitle(req.title);
        t.setDescription(req.description);

        return taskRepository.save(t);
    }

    public void deleteMine(String email, Integer taskId) {
        Integer userId = getUserIdByEmail(email);

        Task t = taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        taskRepository.delete(t);
    }
}
