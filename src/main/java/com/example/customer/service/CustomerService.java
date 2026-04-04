package com.example.customer.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.example.customer.dto.CustomerRequestDTO;
import com.example.customer.dto.CustomerResponseDTO;

public interface CustomerService {

    CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO);

    void uploadCustomers(MultipartFile file);

    CustomerResponseDTO getCustomerById(UUID id);

    Page<CustomerResponseDTO> getAllCustomers(Pageable pageable);
}
