package com.example.weesh.security.authorization.validator;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Getter
@Component
public class PathValidator {
    private final Set<String> publicPaths;
    private final Set<String> refreshTokenAllowedPaths;
    private final Set<String> publicGetOnlyPaths;

    public PathValidator(@Value("${security.public.paths:/v3/api-docs/**,/swagger-ui/**,/users/register/**,/auth/login,/error,/advice}") String[] publicPaths,
                         @Value("${security.refresh.token.allowed.paths:/auth/reissue}") String[] refreshTokenAllowedPaths,
                         @Value("${security.public.get-only.paths:/unavailable-dates,/unavailable-times}") String[] publicGetOnlyPaths) {
        this.publicPaths = new HashSet<>(Set.of(publicPaths));
        this.refreshTokenAllowedPaths = new HashSet<>(Set.of(refreshTokenAllowedPaths));
        this.publicGetOnlyPaths = new HashSet<>(Set.of(publicGetOnlyPaths));
    }

    public boolean isPublicPath(String requestURI) {
        return publicPaths.stream().anyMatch(requestURI::startsWith);
    }

    public boolean isPublicGetOnlyPath(String requestURI) {
        return publicGetOnlyPaths.stream().anyMatch(requestURI::equals);
    }

    public boolean isRefreshTokenAllowed(String requestURI) {
        return refreshTokenAllowedPaths.contains(requestURI);
    }
}
