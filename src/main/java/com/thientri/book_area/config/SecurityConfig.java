package com.thientri.book_area.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.thientri.book_area.security.JwtAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final List<String> allowedOriginPatterns;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthFilter,
            @Value("${app.cors.allowed-origin-patterns}") List<String> allowedOriginPatterns) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/favicon.ico")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/api/users",
                                "/api/auth/login",
                                "/api/auth/refresh")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/books").permitAll()
                        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET,
                                "/api/audiobooks",
                                "/api/audio-chapters",
                                "/api/authors",
                                "/api/book-images",
                                "/api/categories",
                                "/api/inventory-logs",
                                "/api/users")
                        .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST,
                                "/api/books",
                                "/api/audiobooks",
                                "/api/audio-chapters",
                                "/api/authors",
                                "/api/book-images",
                                "/api/categories",
                                "/api/inventory-logs")
                        .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT,
                                "/api/books/**",
                                "/api/audiobooks/**",
                                "/api/audio-chapters/**",
                                "/api/authors/**",
                                "/api/book-images/**",
                                "/api/categories/**",
                                "/api/inventory-logs/**",
                                "/api/users/**")
                        .hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/books/**",
                                "/api/audiobooks/**",
                                "/api/audio-chapters/**",
                                "/api/authors/**",
                                "/api/book-images/**",
                                "/api/categories/**",
                                "/api/inventory-logs/**",
                                "/api/users/**")
                        .hasAuthority("ADMIN")
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOriginPatterns(allowedOriginPatterns);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
