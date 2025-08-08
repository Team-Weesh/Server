package com.example.weesh.security.config;

import com.example.weesh.core.auth.application.jwt.TokenResolver;
import com.example.weesh.core.auth.application.jwt.TokenStorage;
import com.example.weesh.core.auth.application.jwt.TokenValidator;
import com.example.weesh.core.shared.ApiResponse;
import com.example.weesh.core.shared.PasswordValidator;
import com.example.weesh.data.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenValidator tokenValidator;
    private final TokenResolver tokenResolver;
    private final RedisService redisService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/v3/api-docs") || requestURI.startsWith("/swagger-ui")) {
            chain.doFilter(request, response);
            return;
        }

        String token = tokenResolver.resolveToken(request);
        String refreshToken = tokenResolver.resolveRefreshToken(request);

        if (refreshToken != null && !requestURI.equals("/auth/reissue")) {
            log.warn("리프레시 토큰 오용 감지: {}, URI: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())), requestURI);
            sendErrorResponse(response, "리프레시 토큰은 이 엔드포인트(" + requestURI + ")에 사용할 수 없습니다.");
            return;
        }

        if (token != null) {
            try {
                // 블랙리스트 확인
                String blacklisted = redisService.getValues("blacklist:" + token);
                if (blacklisted != null) {
                    log.warn("블랙리스트 토큰 감지: {}, URI: {}", token.substring(0, Math.min(10, token.length())), requestURI);
                    sendErrorResponse(response, "블랙리스트에 등록된 토큰입니다.");
                    return;
                }

                // 토큰 유효성 검증
                tokenValidator.validateToken(token);

                String username = tokenValidator.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (userDetails != null) {
                    var authentication = org.springframework.security.authentication.UsernamePasswordAuthenticationToken
                            .authenticated(userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.error("예상치 못한 오류: {}, URI: {}, 메시지: {}", token.substring(0, Math.min(10, token.length())), requestURI, e.getMessage());
                sendErrorResponse(response, "인증 처리 중 오류가 발생했습니다: " + e.getMessage());
                return;
            }
        } else {
            log.debug("토큰 없음: URI: {}", requestURI);
        }

        chain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        ApiResponse<Object> apiResponse = ApiResponse.error(message);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}