package com.spring.project.library.config.jwt;

import com.spring.project.library.config.jwt.JwtTokenProvider;
import com.spring.project.library.config.jwt.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// OncePerRequestFilter đảm bảo filter chỉ chạy một lần cho mỗi request
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 1. Lấy JWT từ Header
            String jwt = parseJwt(request);

            // 2. Xác thực và xử lý
            if (jwt != null && tokenProvider.validateToken(jwt)) {

                // Lấy Username từ Token
                String username = tokenProvider.getUserNameFromJwtToken(jwt);

                // Tải UserDetails (bao gồm cả ROLE) từ Database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Tạo đối tượng Xác thực (Authentication)
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                // Thêm chi tiết request (IP, session, v.v.)
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Đặt đối tượng Authentication vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Không thể thiết lập xác thực người dùng: {}", e.getMessage());
        }

        // Chuyển Request đến filter tiếp theo
        filterChain.doFilter(request, response);
    }

    /**
     * Trích xuất JWT từ Header Authorization: Bearer <token>.
     */
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7); // Bỏ "Bearer " (7 ký tự)
        }

        return null;
    }
}