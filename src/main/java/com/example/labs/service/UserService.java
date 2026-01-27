package com.example.labs.service;

import com.example.labs.dto.RegisterRequest;
import com.example.labs.model.User;
import com.example.labs.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.labs.exception.ConflictException;
import com.example.labs.exception.UnauthorizedException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email)) {
            throw new ConflictException("Email already in use");
        }

        User u = new User();
        u.setUsername(req.username);
        u.setEmail(req.email);
        u.setPassword(encoder.encode(req.password));
        u.setRole("ROLE_USER");

        return userRepository.save(u);
    }

    public User authenticate(String email, String rawPassword) {
        User u = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!encoder.matches(rawPassword, u.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        return u;
    }
}
