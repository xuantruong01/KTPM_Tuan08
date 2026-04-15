package com.movie.userservice.service;

import java.time.Instant;
import java.util.Map;

import com.movie.userservice.dto.ApiMessageResponse;
import com.movie.userservice.dto.AuthUserResponse;
import com.movie.userservice.dto.LoginRequest;
import com.movie.userservice.dto.LoginResponse;
import com.movie.userservice.dto.RegisterRequest;
import com.movie.userservice.dto.UserProfileResponse;
import com.movie.userservice.entity.User;
import com.movie.userservice.entity.UserRole;
import com.movie.userservice.entity.UserStatus;
import com.movie.userservice.event.EventConstants;
import com.movie.userservice.event.UserRegisteredEvent;
import com.movie.userservice.exception.BadRequestException;
import com.movie.userservice.exception.ResourceNotFoundException;
import com.movie.userservice.exception.UnauthorizedException;
import com.movie.userservice.repository.UserRepository;
import com.movie.userservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserEventPublisher userEventPublisher;

    @Override
    @Transactional
    public ApiMessageResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("Email already exists");
        }

        Instant now = Instant.now();
        User user = User
            .builder()
            .fullName(request.fullName().trim())
            .email(normalizedEmail)
            .password(passwordEncoder.encode(request.password()))
            .role(UserRole.USER)
            .status(UserStatus.ACTIVE)
            .createdAt(now)
            .updatedAt(now)
            .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully: userId={}, email={}", savedUser.getId(), savedUser.getEmail());

        UserRegisteredEvent event = UserRegisteredEvent
            .builder()
            .eventType(EventConstants.USER_REGISTERED_EVENT_TYPE)
            .userId(savedUser.getId())
            .email(savedUser.getEmail())
            .fullName(savedUser.getFullName())
            .createdAt(savedUser.getCreatedAt())
            .build();
        userEventPublisher.publishUserRegistered(event);

        return new ApiMessageResponse("Register success");
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        User user = userRepository
            .findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Email does not exist"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        String token = jwtService.generateToken(
            user.getEmail(),
            Map.of("role", user.getRole().name(), "userId", user.getId())
        );

        AuthUserResponse authUser = new AuthUserResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name()
        );

        log.info("User login successful: userId={}, email={}", user.getId(), user.getEmail());
        return new LoginResponse(token, authUser);
    }

    @Override
    public UserProfileResponse getCurrentUserProfile(String email) {
        User user = userRepository
            .findByEmail(normalizeEmail(email))
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return new UserProfileResponse(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getRole().name()
        );
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
