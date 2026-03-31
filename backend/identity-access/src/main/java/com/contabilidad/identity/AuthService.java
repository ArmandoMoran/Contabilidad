package com.contabilidad.identity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("Credenciales inválidas"));

        if (!user.isActive() || user.isLocked()) {
            throw new AuthenticationException("Cuenta deshabilitada o bloqueada");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            if (user.getFailedAttempts() >= 5) {
                user.setLocked(true);
            }
            userRepository.save(user);
            throw new AuthenticationException("Credenciales inválidas");
        }

        user.setFailedAttempts(0);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, jwtTokenProvider.getAccessExpirySeconds());
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String userId = jwtTokenProvider.validateRefreshToken(request.refreshToken());
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new AuthenticationException("Usuario no encontrado"));

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return new AuthResponse(accessToken, refreshToken, jwtTokenProvider.getAccessExpirySeconds());
    }

    public void logout(RefreshRequest request) {
        jwtTokenProvider.revokeRefreshToken(request.refreshToken());
    }

    public UserInfo getCurrentUser() {
        // Will be populated through SecurityContext
        throw new UnsupportedOperationException("Implement with SecurityContext");
    }
}
