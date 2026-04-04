package com.example.employeetaskmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.example.employeetaskmanagement", "com.example.customer"})
@EntityScan(basePackages = {"com.example.employeetaskmanagement.entity", "com.example.customer.entity"})
@EnableJpaRepositories(
    basePackages = {"com.example.employeetaskmanagement.repository", "com.example.customer.repository"}
)
public class EmployeeTaskManagementApplication {

    // Standard Spring Boot entry point for the backend application.
    public static void main(String[] args) {
        SpringApplication.run(EmployeeTaskManagementApplication.class, args);
    }
}
