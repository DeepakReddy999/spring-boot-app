package com.example.employeetaskmanagement.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.employeetaskmanagement.entity.Role;
import com.example.employeetaskmanagement.entity.User;
import com.example.employeetaskmanagement.repository.UserRepository;

@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    public CommandLineRunner loadDemoData(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Seed only once so repeated restarts do not duplicate records.
            if (userRepository.count() > 0) {
                log.info("action=skip_demo_seed reason=existing_users_detected");
                return;
            }

            // These sample users make local demos predictable and easy to explain.
            // In enterprise environments, real user identities would come from an external identity provider.
            User admin = buildUser("Admin User", "admin@company.com", "Admin@123", Role.ADMIN, passwordEncoder);
            User manager = buildUser("Manager User", "manager@company.com", "Manager@123", Role.MANAGER, passwordEncoder);
            User employee = buildUser("Employee User", "employee@company.com", "Employee@123", Role.EMPLOYEE, passwordEncoder);
            User employeeTwo = buildUser(
                "Anita Sharma",
                "anita.sharma@company.com",
                "Employee@123",
                Role.EMPLOYEE,
                passwordEncoder
            );

            admin = userRepository.save(admin);
            manager = userRepository.save(manager);
            employee = userRepository.save(employee);
            employeeTwo = userRepository.save(employeeTwo);

            log.info(
                "action=seed_demo_users usersSeeded={} seededEmails={}",
                4,
                List.of(admin.getEmail(), manager.getEmail(), employee.getEmail(), employeeTwo.getEmail())
            );
        };
    }

    private User buildUser(String fullName, String email, String rawPassword, Role role, PasswordEncoder encoder) {
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(normalizeEmail(email));
        user.setPassword(encoder.encode(rawPassword));
        user.setRole(role);
        user.setActive(true);
        return user;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }
}
