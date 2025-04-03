package com.ema.ema_backend.domain.auth.jwt;

import com.ema.ema_backend.global.exception.BadRequestException;
import com.ema.ema_backend.global.exception.TokenExpiredException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    private static final List<String> EXCLUDED_URLS = List.of(
            "/api/auth/oauth/kakao/login",
            "/api/auth/refresh"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();

        // 특정 URL은 토큰 검증 생략
        if (EXCLUDED_URLS.contains(requestURI)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Swagger 관련 경로는 JWT 필터 무시
        if (requestURI.startsWith("/swagger-ui") || requestURI.startsWith("/v3/api-docs")) {
            log.info("요청 URI: {}, Authorization: {}", requestURI, request.getHeader("Authorization"));

            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String email = jwtProvider.extractEmailFromAccessToken(token);

                // 인증 객체 생성 및 SecurityContext 설정
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (TokenExpiredException | BadRequestException e) {
                setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "ACCESS_TOKEN_ERROR", e.getMessage());
                return;
            } catch (Exception e) {
                setErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "UNAUTHORIZED", "유효하지 않은 토큰입니다.");
            }
        }

        filterChain.doFilter(request, response); // 토큰 없거나 정상 인증된 경우
    }

    private void setErrorResponse(HttpServletResponse response, int status, String title, String detail) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = String.format("""
            {
              "type": "about:blank",
              "title": "%s",
              "status": %d,
              "detail": "%s"
            }
        """, title, status, detail);

        response.getWriter().write(json);
    }
}
