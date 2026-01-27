package com.example.labs.controller;

import com.example.labs.dto.AuthTokensResponse;
import com.example.labs.dto.LoginRequest;
import com.example.labs.dto.RegisterRequest;
import com.example.labs.model.User;
import com.example.labs.security.JwtService;
import com.example.labs.service.RefreshTokenService;
import com.example.labs.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // ---------- REGISTER ----------
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public void register(@Valid @RequestBody RegisterRequest req) {
        userService.register(req);
    }

    // ---------- LOGIN ----------
    // returns accessToken JSON + sets refreshToken cookie
    @PostMapping("/login")
    public AuthTokensResponse login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        User u = userService.authenticate(req.email, req.password);

        String accessToken = jwtService.generateAccessToken(u.getEmail());
        String refreshToken = refreshTokenService.issueForEmail(u.getEmail()).token();

        setRefreshCookie(response, refreshToken);

        return new AuthTokensResponse(accessToken);
    }

    // ---------- REFRESH ----------
    // reads refreshToken from HttpOnly cookie, rotates it, returns new accessToken + new cookie
    @PostMapping("/refresh")
    public AuthTokensResponse refresh(HttpServletRequest request, HttpServletResponse response) {
        String oldRefresh = readCookie(request, "refreshToken");
        if (oldRefresh == null || oldRefresh.isBlank()) {
            throw new com.example.labs.exception.UnauthorizedException("Missing refresh token");
        }

        RefreshTokenService.RotateResult rotated = refreshTokenService.rotate(oldRefresh);

        String newAccess = jwtService.generateAccessToken(rotated.user().getEmail());
        setRefreshCookie(response, rotated.newToken());

        return new AuthTokensResponse(newAccess);
    }

    // ---------- LOGOUT ----------
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refresh = readCookie(request, "refreshToken");
        if (refresh != null && !refresh.isBlank()) {
            refreshTokenService.revoke(refresh);
        }
        clearRefreshCookie(response);
    }

    // ---------- helpers ----------
    private static String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) return c.getValue();
        }
        return null;
    }

    private static void setRefreshCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(false)          // если будет HTTPS -> true
                .sameSite("Strict")
                .path("/auth")
                .maxAge(60L * 60 * 24 * 7)  // 7 days
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private static void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/auth")
                .maxAge(0)
                .build();
        response.addHeader("Set-Cookie", cookie.toString());
    }
}
