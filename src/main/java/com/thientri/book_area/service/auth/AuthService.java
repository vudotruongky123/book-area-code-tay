package com.thientri.book_area.service.auth;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thientri.book_area.dto.request.auth.LoginRequest;
import com.thientri.book_area.dto.response.auth.AuthResponse;
import com.thientri.book_area.model.user.RefreshToken;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.user.UserRepository;
import com.thientri.book_area.security.JwtService;
import com.thientri.book_area.service.user.RefreshTokenService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Khi đăng nhập sẽ tạo JWT và refreshToken để duy trì đăng nhập 7 ngày
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Khong tim thay email nguoi dung!"));

        // So sách mật khẩu thô và mật khẩu đã băm trong DB
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) { // Check email không có trên DB
            throw new RuntimeException("Sai mat khaau dang nhap!");
        }

        String jwtToken = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        // Nếu email tìm thấy trên DB => trả chuối JWT
        return AuthResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
