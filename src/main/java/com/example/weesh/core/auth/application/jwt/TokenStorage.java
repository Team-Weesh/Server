package com.example.weesh.core.auth.application.jwt;

public interface TokenStorage {
    String getStoredRefreshToken(String username);
    void invalidateRefreshToken(String username);
    void blacklistAccessToken(String accessToken);
    void storeNewRefreshToken(String username, String refreshToken, long validityMillis);
}
