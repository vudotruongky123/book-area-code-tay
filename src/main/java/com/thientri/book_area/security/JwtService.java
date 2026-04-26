package com.thientri.book_area.security;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.thientri.book_area.model.user.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;

@Service
public class JwtService {

    // Lấy key secret và thời gian đã nhập trong application.properties
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration}")
    private long jwtExpiration;

    // May in the tạo ra chuỗi JWT
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail()) // Tên chủ thẻ
                .setIssuedAt(new Date(System.currentTimeMillis())) // Ngày tạo
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Thời hạn sử dụng
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // Đóng dấu bảo mật bằng thuật toán HS256
                .compact(); // Xuất ra thành chuỗi JWT
    }

    // Chìa khóa đóng dấu
    private Key getSignInKey() {
        // chuyển chuỗi String đó thành một mảng byte
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // Tạo ra một đối tượng Key chuẩn mật mã học.
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Giải mã token trả về email người dùng
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey()) // Dùng lại chìa khóa để mở
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // Lấy ra cái Subject (chính là email)
    }
}
