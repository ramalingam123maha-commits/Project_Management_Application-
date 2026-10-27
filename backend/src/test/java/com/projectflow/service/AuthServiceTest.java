package com.projectflow.service;

import com.projectflow.domain.Role;
import com.projectflow.domain.User;
import com.projectflow.dto.auth.AuthResponse;
import com.projectflow.dto.auth.RegisterRequest;
import com.projectflow.exception.ConflictException;
import com.projectflow.mapper.DtoMapper;
import com.projectflow.repository.UserRepository;
import com.projectflow.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtService jwtService;
    @Spy DtoMapper mapper = new DtoMapper();
    @InjectMocks AuthService authService;

    @Test
    void registerCreatesMemberWithHashedPasswordAndNormalisedEmail() {
        when(userRepository.existsByEmailIgnoreCase("ann@x.com")).thenReturn(false);
        when(passwordEncoder.encode("password1")).thenReturn("HASH");
        when(userRepository.saveAndFlush(any(User.class))).thenAnswer(i -> i.getArgument(0));
        when(jwtService.generateToken(any(com.projectflow.security.UserPrincipal.class))).thenReturn("tok");

        AuthResponse response = authService.register(new RegisterRequest(" Ann ", " Ann@X.com ", "password1"));

        ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(saved.capture());
        assertEquals("ann@x.com", saved.getValue().getEmail());
        assertEquals("Ann", saved.getValue().getName());
        assertEquals("HASH", saved.getValue().getPasswordHash());
        assertEquals(Role.MEMBER, saved.getValue().getRole());
        assertEquals("tok", response.token());
        assertEquals("Bearer", response.tokenType());
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(userRepository.existsByEmailIgnoreCase("ann@x.com")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> authService.register(new RegisterRequest("Ann", "ann@x.com", "password1")));
        verify(userRepository, never()).saveAndFlush(any());
    }
}
