package com.learn.auth.dto;

// The response after login or register — just the JWT token
public record AuthResponse(String token) {}

