package com.ema.ema_backend.domain.auth.jwt;

import com.ema.ema_backend.domain.auth.dto.TokenResponse;
import com.ema.ema_backend.global.exception.BadRequestException;
import com.ema.ema_backend.global.exception.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class JwtProvider {
    private static final long ACCESS_TEN_HOURS = 1000 * 60 * 60 * 24; //24시간

    private static final long REFRESH_SEVEN_DAYS = 1000 * 60 * 60 * 24 * 7; //7일

    private final Key secretKey;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        this.secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "HmacSHA256");
    }

    public String generateAccessToken(String email) {
        log.info("JWT accessToken 생성, email: {}", email);
        return Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "access")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TEN_HOURS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String email) {
        log.info("JWT refreshToken 생성, email: {}", email);
        return Jwts.builder()
                .setSubject(email)
                .claim("tokenType", "refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_SEVEN_DAYS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }


    public String extractEmailFromAccessToken(String accessToken) {
        Claims claims = parseClaims(accessToken);
        if (!"access".equals(claims.get("tokenType", String.class))) {
            throw new BadRequestException("사용된 토큰이 엑세스 토큰이 아닙니다. 요청하신 로직에서는 엑세스 토큰으로만 처리가 가능합니다.");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new TokenExpiredException("액세스 토큰이 만료되었습니다. 리프레시 토큰으로 다시 액세스 토큰을 발급받으세요.");
        }
        return claims.getSubject();
    }

    public String extractEmailFromRefreshToken(String refreshToken) {
        Claims claims = parseClaims(refreshToken);
        if (!"refresh".equals(claims.get("tokenType", String.class))) {
            throw new BadRequestException("해당 토큰은 리프레쉬 토큰이 아닙니다. 요청하신 로직에서는 리프레쉬 토큰만 사용이 가능합니다.");
        }
        if (claims.getExpiration().before(new Date())) {
            throw new TokenExpiredException("리프레쉬 토큰이 만료되었습니다. 재로그인을 하세요.");
        }
        return claims.getSubject();
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new TokenExpiredException(e.getMessage());
        }
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        String email = extractEmailFromRefreshToken(refreshToken);

        String newAccessToken = generateAccessToken(email);
        String newRefreshToken = generateRefreshToken(email);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}
