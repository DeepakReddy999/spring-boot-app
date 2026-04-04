package com.example.employeetaskmanagement.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
    // Spring Data JPA auditing automatically fills timestamp fields such as
    // createdAt, updatedAt, and performedAt when entities are saved.
}
