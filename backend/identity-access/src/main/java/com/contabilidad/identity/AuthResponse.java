package com.contabilidad.identity;

public record AuthResponse(String accessToken, String refreshToken, long expiresIn) {}
