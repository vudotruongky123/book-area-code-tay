package com.thientri.book_area.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.thientri.book_area.model.user.User;
import com.thientri.book_area.repository.user.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    // Máy quét JWT các request gửi đến (Chot kiem tra an ninh)
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Kiểm tra request có JWT và Header có bắt đầu bằng "Bearer " hay không
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Kiểm tra thấy không có JWT trả lại request cho đi tiếp
            return; // Ngưng kiểm tra
        }

        final String jwt = authHeader.substring(7).trim(); // Lấy ra mã của Header bỏ phần "Bearer " đi
        System.out.println("Chuỗi JWT nhận được: " + jwt); // <-- Thêm dòng này
        final String userEmail = jwtService.extractEmail(jwt).trim(); // Kiểm tra & chuyển phần Header của JWT email
        System.out.println("Email giải mã được: " + userEmail); // <-- Thêm dòng này

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("Bắt đầu xác thực cho: " + userEmail);
            User userAuth = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("Khong tim thay user voi email duoc xac thuc!"));

            // 1. Tạo xác thực cho user nếu đăng nhập đúng token
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userAuth, null, userAuth.getAuthorities());

            // 2. Thêm thông tin phụ (VD: Địa chỉ IP của khách) vào thẻ
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            
            // 3. Lưu xác thực người dùng vào hệ thống (Hoàn tất đăng nhập cho request này)
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        filterChain.doFilter(request, response); // Kiểm tra thấy không có JWT trả lại request cho đi tiếp

    }
}
