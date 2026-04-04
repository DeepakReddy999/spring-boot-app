package com.example.customer.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.customer.dto.CustomerRequestDTO;
import com.example.customer.dto.CustomerResponseDTO;
import com.example.customer.entity.Customer;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.service.CustomerService;
import com.example.employeetaskmanagement.exception.BadRequestException;
import com.example.employeetaskmanagement.exception.ConflictException;
import com.example.employeetaskmanagement.exception.ResourceNotFoundException;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        String normalizedEmail = normalizeEmail(customerRequestDTO.getEmail());

        if (customerRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("A customer with this email already exists.");
        }

        Customer customer = mapToEntity(customerRequestDTO);
        customer.setEmail(normalizedEmail);

        try {
            return mapToResponse(customerRepository.save(customer));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("A customer with this email already exists.");
        }
    }

    @Override
    @Transactional
    public void uploadCustomers(MultipartFile file) {
        validateFile(file);

        try (BufferedReader reader = new BufferedReader(
            new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)
        )) {
            String line;
            boolean headerSkipped = false;
            int rowNumber = 0;

            while ((line = reader.readLine()) != null) {
                rowNumber++;

                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }

                if (line.isBlank()) {
                    continue;
                }

                Customer customer = parseCustomerRow(line, rowNumber);
                String normalizedEmail = normalizeEmail(customer.getEmail());

                if (customerRepository.existsByEmail(normalizedEmail)) {
                    throw new ConflictException("A customer with email " + normalizedEmail + " already exists.");
                }

                customer.setEmail(normalizedEmail);
                customerRepository.save(customer);
            }
        } catch (IOException exception) {
            throw new BadRequestException("Unable to read uploaded file.");
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Bulk upload contains duplicate customer emails.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Customer not found for id: " + id));
        return mapToResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerResponseDTO> getAllCustomers(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::mapToResponse);
    }

    private Customer mapToEntity(CustomerRequestDTO customerRequestDTO) {
        Customer customer = new Customer();
        customer.setFirstName(customerRequestDTO.getFirstName());
        customer.setLastName(customerRequestDTO.getLastName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setPhone(customerRequestDTO.getPhone());
        return customer;
    }

    private CustomerResponseDTO mapToResponse(Customer customer) {
        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();
        customerResponseDTO.setId(customer.getId());
        customerResponseDTO.setFirstName(customer.getFirstName());
        customerResponseDTO.setLastName(customer.getLastName());
        customerResponseDTO.setEmail(customer.getEmail());
        customerResponseDTO.setPhone(customer.getPhone());
        customerResponseDTO.setCreatedAt(customer.getCreatedAt());
        return customerResponseDTO;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please upload a non-empty CSV file.");
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            throw new BadRequestException("Only CSV files are supported for bulk upload.");
        }
    }

    private Customer parseCustomerRow(String line, int rowNumber) {
        String[] columns = line.split(",");
        if (columns.length < 4) {
            throw new BadRequestException("CSV row " + rowNumber + " is invalid. Expected 4 columns.");
        }

        Customer customer = new Customer();
        customer.setFirstName(columns[0].trim());
        customer.setLastName(columns[1].trim());
        customer.setEmail(columns[2].trim());
        customer.setPhone(columns[3].trim());

        if (customer.getFirstName().isBlank() || customer.getLastName().isBlank() || customer.getEmail().isBlank()) {
            throw new BadRequestException("CSV row " + rowNumber + " is missing required customer fields.");
        }

        return customer;
    }
}
