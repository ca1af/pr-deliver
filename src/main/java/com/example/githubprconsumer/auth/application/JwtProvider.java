package com.example.githubprconsumer.auth.application;

import com.example.githubprconsumer.auth.domain.AuthenticationException;
import com.example.githubprconsumer.auth.domain.CustomOauth2User;
import com.example.githubprconsumer.auth.domain.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private SecretKey key;

    private static final long EXPIRATION_TIME_IN_MILLISECONDS = 1000L * 60 * 60 * 6;

    @PostConstruct
    protected void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Authentication getAuthentication(String token) {
        String login = getLoginFromToken(token);
        CustomOauth2User principal = new CustomOauth2User(login);
        return new UsernamePasswordAuthenticationToken(principal, null, null);
    }

    public String createToken(String login) {

        Claims claims = Jwts.claims().setSubject(login);
        Date now = new Date();

        return JwtUtils.BEARER + Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + EXPIRATION_TIME_IN_MILLISECONDS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getLoginFromToken(String bearerToken) {
        bearerToken = JwtUtils.replaceBearerPrefix(bearerToken);

        try {
            Jws<Claims> parsedClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(bearerToken);
            String login = parsedClaims.getBody().getSubject();

            if (Objects.isNull(login)){
                throw new AuthenticationException.InvalidJWTPayloadException();
            }

            return login;
        } catch (MalformedJwtException e) {
            throw new AuthenticationException.InvalidJwtTokenException();
        }
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && JwtUtils.isValidBearerToken(bearerToken)) {
            return bearerToken;
        }

        return null;
    }

    public boolean isValidToken(String token) {
        token = JwtUtils.replaceBearerPrefix(token);
        Jws<Claims> claimsJws = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return !claimsJws.getBody().getExpiration().before(new Date());
    }
}
