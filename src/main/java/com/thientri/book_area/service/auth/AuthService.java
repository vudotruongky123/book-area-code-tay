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
import com.thientri.book_area.dto.request.auth.RegisterRequest;
import com.thientri.book_area.dto.request.user.UserRequest;
import com.thientri.book_area.dto.response.auth.AuthResponse;
import com.thientri.book_area.dto.response.user.UserResponse;
import com.thientri.book_area.model.user.Address;
import com.thientri.book_area.model.user.RefreshToken;
import com.thientri.book_area.model.user.Role;
import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.user.UserRepository;
import com.thientri.book_area.security.JwtService;
import com.thientri.book_area.service.user.RefreshTokenService;
import com.thientri.book_area.service.user.UserService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public void register(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu đăng ký không hợp lệ.");
        }

        String fullName = normalize(registerRequest.getFullName());
        String email = normalize(registerRequest.getEmail());
        String password = registerRequest.getPassword() == null ? "" : registerRequest.getPassword();

        if (fullName.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Họ tên không được để trống.");
        }

        if (email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email không được để trống.");
        }

        if (password.length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu phải có tối thiểu 6 ký tự.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email này đã được sử dụng.");
        }

        userService.createUser(UserRequest.builder()
                .fullName(fullName)
                .email(email)
                .password(password)
                .build());
    }

    public AuthResponse login(LoginRequest loginRequest) {
        if (loginRequest == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dữ liệu đăng nhập không hợp lệ.");
        }

        String email = normalize(loginRequest.getEmail());
        String password = loginRequest.getPassword() == null ? "" : loginRequest.getPassword();

        User user = userRepository.findDetailedByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng!"));

        if (!verifyPassword(user, password)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email hoặc mật khẩu không đúng!");
        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());
        return buildAuthResponse(user, jwtService.generateToken(user), refreshToken.getRefreshToken());
    }

    private boolean verifyPassword(User user, String rawPassword) {
        String storedPassword = user.getPassword();

        if (passwordEncoder.matches(rawPassword, storedPassword)) {
            return true;
        }

        if (storedPassword != null && storedPassword.equals(rawPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }

    public AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .token(accessToken)
                .refreshToken(refreshToken)
                .user(mapToUserResponse(user))
                .build();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findDetailedByEmail(normalize(email))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng hiện tại!"));
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

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
