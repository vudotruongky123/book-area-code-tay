package com.thientri.book_area.controller.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thientri.book_area.dto.request.auth.LoginRequest;
import com.thientri.book_area.dto.request.auth.TokenRefreshRequest;
import com.thientri.book_area.dto.response.auth.AuthResponse;
import com.thientri.book_area.model.user.RefreshToken;
import com.thientri.book_area.security.JwtService;
import com.thientri.book_area.service.auth.AuthService;
import com.thientri.book_area.service.user.RefreshTokenService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    // Gửi thông tin đăng nhập email và password người dùng lên api
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest)); // Trả về chuỗi Token được bao bọc dữ liệu
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        // Tìm thẻ trong Db, kiểm tra hạn, và lấy ra thẻ hợp lệ
        // map của Java Optional giúp nối chuỗi hành động
        return refreshTokenService.findByToken(requestRefreshToken) // Tìm thấy
                .map(refreshTokenService::verifyExpiration) // Kiểm tra hạn 7 ngày
                .map(RefreshToken::getUser) // Còn hạn => lấy thông tin user
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user); // Tạo JWT mới

                    return ResponseEntity.ok(AuthResponse.builder()
                            .token(newAccessToken)
                            .refreshToken(requestRefreshToken)
                            .build());
                }).orElseThrow(() -> new RuntimeException("The Refresh Token khong ton tai trong he thong!"));
    }
}
