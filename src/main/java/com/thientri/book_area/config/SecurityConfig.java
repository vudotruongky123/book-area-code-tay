package com.thientri.book_area.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.thientri.book_area.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    // Ổ khóa (Mã hóa mật khẩu)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Được sử dụng để mã hóa mật khẩu
    }

    // Người gác cổng (SecurityFilterChain)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(crsf -> crsf.disable()) // Tắt CSRF vì REST API không dùng session cookie
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/users",
                                "/api/auth/login",
                                "/api/auth/refresh")
                        .permitAll() // Cho phép ngay cả khi chưa đăng nhập

                        .requestMatchers(HttpMethod.GET,
                                "/api/audiobooks",
                                "/api/audio-chapters",
                                "/api/authors",
                                "/api/books",
                                "/api/book-images",
                                "/api/categories",
                                "/api/inventory-logs",
                                "/api/users")
                        .hasAuthority("ADMIN")

                        .anyRequest().authenticated()) // Phải đăng nhập mới được thao tác

                // Lệnh chốt hạ: Đặt máy quét thẻ lên trước cửa xác thực mặc định
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Giấy thông hành (JWT) - Bắt đầu với việc Đăng nhập
}
