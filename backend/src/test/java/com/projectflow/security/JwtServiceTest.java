package com.projectflow.security;

import com.projectflow.domain.Role;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET = "dGhpcy1pcy1hLXZlcnktc2VjdXJlLTI1Ni1iaXQtc2VjcmV0LWZvci1kZXYtZW52aXJvbm1lbnQtb25seSE=";

    private UserPrincipal principal() {
        return new UserPrincipal(7L, "Ann", "ann@x.com", "hash", Role.PROJECT_MANAGER, Collections.emptyList());
    }

    @Test
    void generatedTokenContainsClaims() {
        JwtService service = new JwtService(SECRET, 60_000);
        String token = service.generateToken(principal());

        assertTrue(service.validateToken(token));
        assertEquals("ann@x.com", service.extractUsername(token));
        assertEquals(7L, service.extractUserId(token));
        assertEquals("PROJECT_MANAGER", service.extractRole(token));
    }

    @Test
    void expiredTokenIsInvalid() {
        JwtService service = new JwtService(SECRET, -1_000);
        assertFalse(service.validateToken(service.generateToken(principal())));
    }

    @Test
    void tamperedTokenIsInvalid() {
        JwtService service = new JwtService(SECRET, 60_000);
        String token = service.generateToken(principal());
        assertFalse(service.validateToken(token.substring(0, token.length() - 3) + "abc"));
    }

    @Test
    void tokenSignedWithOtherSecretIsInvalid() {
        JwtService other = new JwtService("another-secret-that-is-long-enough-for-hs256-signing-0123456789", 60_000);
        JwtService service = new JwtService(SECRET, 60_000);
        assertFalse(service.validateToken(other.generateToken(principal())));
    }

    @Test
    void garbageIsInvalid() {
        assertFalse(new JwtService(SECRET, 60_000).validateToken("not-a-jwt"));
    }
}
