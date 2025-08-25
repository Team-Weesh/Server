package com.example.weesh.core.auth.application.jwt;

import io.jsonwebtoken.Claims;

public interface TokenValidator {
    void validateToken(String token);
    String getUsername(String token);
    String getTokenType(String token);
    Claims parseToken(String token);
}
