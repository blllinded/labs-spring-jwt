package com.example.labs.service;

import com.example.labs.model.User;
import com.example.labs.repository.UserRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.labs.exception.NotFoundException;

import java.util.Map;

@Service
public class TaskStatsService {

    private final JdbcTemplate jdbcTemplate;
    private final UserRepository userRepository;

    public TaskStatsService(JdbcTemplate jdbcTemplate, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userRepository = userRepository;
    }

    public Map<String, Object> countMine(String email) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tasks WHERE user_id = ?",
                Integer.class,
                u.getId()
        );

        return Map.of("userId", u.getId(), "tasksCount", cnt);
    }
}
