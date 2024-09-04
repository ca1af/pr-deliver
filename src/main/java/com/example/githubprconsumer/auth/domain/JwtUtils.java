package com.example.githubprconsumer.auth.domain;

public class JwtUtils {

    private JwtUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static final String BEARER = "Bearer ";

    public static String replaceBearerPrefix(String bearerToken) {
        if (!isValidBearerToken(bearerToken)) {
            throw new AuthenticationException.InvalidJwtTokenException();
        }

        return bearerToken.replace(BEARER, "");
    }

    public static boolean isValidBearerToken(String bearerToken) {
        return bearerToken.startsWith(JwtUtils.BEARER);
    }
}
