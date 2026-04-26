package com.thientri.book_area.service.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.thientri.book_area.model.user.RefreshToken;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.user.RefreshTokenRepository;
import com.thientri.book_area.repository.user.UserRepository;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    // Tạo RefreshToken để người dùng có thể duy trì đăng nhập trong 7 ngày
    public RefreshToken createRefreshToken(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Khong tim thay nguoi dung de tao RefreshToken!"));
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .refreshToken(UUID.randomUUID().toString())
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();
        refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    // Kiểm tra RefreshToken có còn hạn sử dụng 7 hay không
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.deleteById(refreshToken.getId());
            throw new RuntimeException("Phien ban dang nhap tren thiet bi da het han, vui long dang nhap lai!");
        }
        return refreshToken;
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }
}
