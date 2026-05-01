package com.thientri.book_area.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Cấu hình bảo mật CORS cho ứng dụng
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(crsf -> crsf.disable()) // Tắt CSRF vì REST API không dùng session cookie
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/api/users",
                                "/api/auth/login",
                                "/api/auth/refresh")
                        .permitAll() // Cho phép ngay cả khi chưa đăng nhập

                        .requestMatchers(HttpMethod.GET, "/api/books")
                        .permitAll() // Cho phép ngay cả khi chưa đăng nhập

                        .requestMatchers(HttpMethod.GET,
                                "/api/audiobooks",
                                "/api/audio-chapters",
                                "/api/authors",
                                // "/api/books",
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Thay 5173 bằng cổng mà Vite đang chạy trên trình duyệt
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // Cho phép các phương thức giao tiếp
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Cho phép gửi kèm các header quan trọng (như token đăng nhập sau này)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Giấy thông hành (JWT) - Bắt đầu với việc Đăng nhập
}
