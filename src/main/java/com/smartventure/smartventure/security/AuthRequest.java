package com.smartventure.smartventure.security;

public record AuthRequest(
        String username,
        String password
) {}