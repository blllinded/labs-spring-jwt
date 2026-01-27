package com.example.labs.service;

import com.example.labs.exception.NotFoundException;
import com.example.labs.exception.UnauthorizedException;
import com.example.labs.model.RefreshToken;
import com.example.labs.model.User;
import com.example.labs.repository.RefreshTokenRepository;
import com.example.labs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class RefreshTokenService {

    public record IssueResult(String token) {}
    public record RotateResult(User user, String newToken) {}

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    @Value("${jwt.refresh.ttlSeconds:604800}")
    private long refreshTtlSeconds;

    private static final DateTimeFormatter ISO =
            DateTimeFormatter.ISO_INSTANT.withZone(ZoneOffset.UTC);

    public RefreshTokenService(RefreshTokenRepository repo, UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    // выдаём новый refresh токен пользователю (для login)
    public IssueResult issueForEmail(String email) {
        User u = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        RefreshToken rt = new RefreshToken();
        rt.setUserId(u.getId());
        rt.setToken(randomToken());
        rt.setExpiresAt(ISO.format(Instant.now().plusSeconds(refreshTtlSeconds)));
        rt.setRevoked(0);

        repo.save(rt);
        return new IssueResult(rt.getToken());
    }

    // refresh ротация: старый токен -> revoked, новый -> сохранён, вернуть user + новый токен
    public RotateResult rotate(String oldToken) {
        RefreshToken existing = repo.findByToken(oldToken)
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));

        if (existing.getRevoked() != null && existing.getRevoked() == 1) {
            throw new UnauthorizedException("Refresh token revoked");
        }

        Instant exp = Instant.parse(existing.getExpiresAt());
        if (Instant.now().isAfter(exp)) {
            throw new UnauthorizedException("Refresh token expired");
        }

        User u = userRepo.findById(existing.getUserId().longValue())
                .orElseThrow(() -> new NotFoundException("User not found"));

        RefreshToken newRt = new RefreshToken();
        newRt.setUserId(u.getId());
        newRt.setToken(randomToken());
        newRt.setExpiresAt(ISO.format(Instant.now().plusSeconds(refreshTtlSeconds)));
        newRt.setRevoked(0);
        repo.save(newRt);

        existing.setRevoked(1);
        existing.setReplacedByToken(newRt.getToken());
        repo.save(existing);

        return new RotateResult(u, newRt.getToken());
    }

    public void revoke(String token) {
        repo.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(1);
            repo.save(rt);
        });
    }

    private static String randomToken() {
        return UUID.randomUUID().toString() + "-" + UUID.randomUUID();
    }
}
