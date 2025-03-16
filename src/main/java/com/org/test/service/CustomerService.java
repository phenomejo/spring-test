package com.org.test.service;

import com.org.test.dto.CustomerDTO;
import com.org.test.dto.GenericPaginationDTO;
import com.org.test.entity.CustomerEntity;
import com.org.test.exception.custom.InvalidInputException;
import com.org.test.exception.custom.ResourceNotFoundException;
import com.org.test.mapper.CustomerMapper;
import com.org.test.publisher.NotificationPublisher;
import com.org.test.repository.CustomerRepository;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final NotificationPublisher notificationPublisher;

    public ResponseEntity<GenericPaginationDTO<CustomerDTO>> getAllCustomers(Integer pageNumber, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber, pageSize);

        Page<CustomerEntity> customerEntityPage = customerRepository.findAll(pageRequest);

        return ResponseEntity.ok(customerMapper.fromPageEntities(customerEntityPage));
    }


    public ResponseEntity<CustomerDTO> getCustomerById(UUID customerId) {
        var customerDTO = customerRepository.findById(customerId)
                .map(customerMapper::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("CustomerId not found"));

        return ResponseEntity.ok(customerDTO);
    }

    public ResponseEntity<CustomerDTO> createCustomer(CustomerDTO customer) {
        List<CustomerEntity> entities = customerRepository.findByEmail(customer.getEmail());
        if (!CollectionUtils.isEmpty(entities)) {
            throw new InvalidInputException("Email already exist");
        }

        CustomerEntity savedCustomerEntity = customerRepository.save(customerMapper.toCreateCustomer(customer));

        notificationPublisher.publishMessage(savedCustomerEntity);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(customerMapper.fromEntity(savedCustomerEntity));
    }

    public ResponseEntity<CustomerDTO> updateCustomer(UUID customerId, CustomerDTO updatedCustomer) {

        if (Objects.isNull(updatedCustomer.getCustomerId())) {
            throw new InvalidInputException("CustomerId must not empty");
        }

        CustomerDTO customerDTO = customerRepository.findById(customerId)
                .map(customer -> {
                    customerMapper.toUpdateCustomer(customer, updatedCustomer);
                    return customerRepository.save(customer);
                })
                .map(customerMapper::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return ResponseEntity.ok(customerDTO);
    }

    public void deleteCustomer(UUID customerId) {
        customerRepository.deleteById(customerId);
    }

}
