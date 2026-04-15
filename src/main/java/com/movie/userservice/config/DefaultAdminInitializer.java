package com.movie.userservice.config;

import java.time.Instant;

import com.movie.userservice.entity.User;
import com.movie.userservice.entity.UserRole;
import com.movie.userservice.entity.UserStatus;
import com.movie.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.enabled:true}")
    private boolean enabled;

    @Value("${app.default-admin.full-name:System Admin}")
    private String fullName;

    @Value("${app.default-admin.email:admin@movie.com}")
    private String email;

    @Value("${app.default-admin.password:Admin@123456}")
    private String password;

    @Override
    public void run(ApplicationArguments args) {
        if (!enabled) {
            log.info("Default admin initialization is disabled");
            return;
        }

        try {
            String normalizedEmail = normalizeEmail(email);
            if (userRepository.existsByEmail(normalizedEmail)) {
                log.info("Default admin already exists: {}", normalizedEmail);
                return;
            }

            Instant now = Instant.now();
            User admin = User
                .builder()
                .fullName(fullName.trim())
                .email(normalizedEmail)
                .password(passwordEncoder.encode(password))
                .role(UserRole.ADMIN)
                .status(UserStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();

            userRepository.save(admin);
            log.warn("Default admin created with email={}. Please change password immediately.", normalizedEmail);
        } catch (Exception ex) {
            log.warn("Skip default admin initialization because database is not reachable yet: {}", ex.getMessage());
        }
    }

    private String normalizeEmail(String rawEmail) {
        return rawEmail == null ? null : rawEmail.trim().toLowerCase();
    }
}
