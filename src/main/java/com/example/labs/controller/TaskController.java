package com.example.labs.controller;

import com.example.labs.dto.TaskCreateRequest;
import com.example.labs.dto.TaskUpdateRequest;
import com.example.labs.model.Task;
import com.example.labs.security.CurrentUser;
import com.example.labs.service.TaskService;
import com.example.labs.service.TaskStatsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;
    private final TaskStatsService statsService;

    public TaskController(TaskService taskService, TaskStatsService statsService) {
        this.taskService = taskService;
        this.statsService = statsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Task create(@Valid @RequestBody TaskCreateRequest req) {
        return taskService.create(CurrentUser.email(), req);
    }

    @GetMapping
    public List<Task> myTasks() {
        return taskService.listMine(CurrentUser.email());
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Integer id,
                       @Valid @RequestBody TaskUpdateRequest req) {
        return taskService.updateMine(CurrentUser.email(), id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        taskService.deleteMine(CurrentUser.email(), id);
    }

    @GetMapping("/stats")
    public Object stats() {
        return statsService.countMine(CurrentUser.email());
    }
}
