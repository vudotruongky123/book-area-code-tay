package com.thientri.book_area.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.auth.LoginRequest;
import com.thientri.book_area.dto.request.auth.TokenRefreshRequest;
import com.thientri.book_area.dto.response.auth.AuthResponse;
import com.thientri.book_area.dto.response.user.UserResponse;
import com.thientri.book_area.model.user.RefreshToken;
import com.thientri.book_area.security.JwtService;
import com.thientri.book_area.service.auth.AuthService;
import com.thientri.book_area.service.user.RefreshTokenService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.getCurrentUser(authentication.getName()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    return ResponseEntity.ok(authService.buildAuthResponse(user, newAccessToken, requestRefreshToken));
                }).orElseThrow(() -> new RuntimeException("The Refresh Token khong ton tai trong he thong!"));
    }
}
