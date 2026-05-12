package com.thientri.book_area.service.auth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.thientri.book_area.dto.request.auth.LoginRequest;
import com.thientri.book_area.dto.response.auth.AuthResponse;
import com.thientri.book_area.dto.response.user.UserResponse;
import com.thientri.book_area.model.user.Address;
import com.thientri.book_area.model.user.RefreshToken;
import com.thientri.book_area.model.user.Role;
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

    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findDetailedByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoac mat khau khong dung!"));

        if (!verifyPassword(user, loginRequest.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoac mat khau khong dung!");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return buildAuthResponse(user, refreshToken.getRefreshToken());
    }

    private boolean verifyPassword(User user, String rawPassword) {
        String storedPassword = user.getPassword();

        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }

        // Migrate legacy seed data that was inserted as plaintext into BCrypt on first login.
        if (storedPassword != null && storedPassword.equals(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public AuthResponse buildAuthResponse(User user, String refreshToken) {
        return AuthResponse.builder()
                .token(jwtService.generateToken(user))
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();
    }

    public AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findDetailedByEmail(email)
                .orElseThrow(() -> new RuntimeException("Khong tim thay thong tin nguoi dung hien tai!"));
        return mapToUserResponse(user);
    }

    private UserResponse mapToUserResponse(User user) {
        Set<String> roles = new HashSet<>();
        List<Long> addressIds = new ArrayList<>();

        for (Role role : user.getRoles()) {
            roles.add(role.getName());
        }

        for (Address address : user.getAddresses()) {
            addressIds.add(address.getId());
        }

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .roles(roles)
                .addressIds(addressIds)
                .build();
    }
}
