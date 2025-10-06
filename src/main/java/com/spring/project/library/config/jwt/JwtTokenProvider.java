package com.spring.project.library.config.jwt;

import com.spring.project.library.config.jwt.UserDetailsImpl;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    // Lấy Khóa Bí Mật và Thời Gian Hết Hạn từ application.properties
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Helper method để tạo Khóa (Key) an toàn từ chuỗi Base64
    private Key key() {
        // Giải mã chuỗi Base64 thành byte array, sau đó tạo Key HMAC
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    /**
     * Tạo JWT mới sau khi người dùng đăng nhập thành công.
     */
    public String generateToken(UserDetailsImpl userDetails) {
        // Token chứa Username làm Chủ thể (Subject)
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Thời gian hết hạn
                .signWith(key(), SignatureAlgorithm.HS512) // Ký Token bằng HS512 và Khóa Bí Mật
                .compact();
    }

    /**
     * Lấy Username từ JWT.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Xác thực tính hợp lệ của Token (chữ ký và thời gian hết hạn).
     */
    public boolean validateToken(String authToken) {
        try {
            // Thử giải mã Token bằng khóa bí mật. Nếu thành công, nó hợp lệ.
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT không hợp lệ: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT đã hết hạn: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT không được hỗ trợ: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Chuỗi Token JWT rỗng hoặc null: {}", e.getMessage());
        } catch (SignatureException e) {
            logger.error("Chữ ký JWT không hợp lệ (có thể Token bị giả mạo): {}", e.getMessage());
        }
        return false;
    }
}