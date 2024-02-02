package com.mcouture.auth;

public record AuthenticationRequest (
        String username,
        String password
) {
}
