package com.projectflow.config;

import com.projectflow.domain.Role;
import com.projectflow.domain.User;
import com.projectflow.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final String initialName;
    private final String initialEmail;
    private final String initialPassword;

    public AdminSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.initial-name:System Administrator}") String initialName,
            @Value("${app.admin.initial-email:admin@projectflow.local}") String initialEmail,
            @Value("${app.admin.initial-password:Admin@123456}") String initialPassword) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.initialName = initialName;
        this.initialEmail = initialEmail;
        this.initialPassword = initialPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (initialEmail == null || initialEmail.isBlank()) {
            return;
        }

        if (userRepository.existsByEmailIgnoreCase(initialEmail)) {
            log.info("Admin user [{}] already exists; skipping seed.", initialEmail);
            return;
        }

        // If another admin already exists in the system, skip to avoid unwanted accounts
        if (userRepository.countByRole(Role.ADMIN) > 0) {
            log.info("An ADMIN user is already present in the database; skipping initial seed.");
            return;
        }

        User admin = new User(
                initialName,
                initialEmail.trim().toLowerCase(),
                passwordEncoder.encode(initialPassword),
                Role.ADMIN
        );
        userRepository.save(admin);
        log.info("Successfully seeded initial administrator account: [{}]", admin.getEmail());
    }
}
