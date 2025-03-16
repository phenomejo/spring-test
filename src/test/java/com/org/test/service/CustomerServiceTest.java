package com.org.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.github.f4b6a3.uuid.UuidCreator;
import com.org.test.MockFactory;
import com.org.test.dto.CustomerDTO;
import com.org.test.dto.GenericPaginationDTO;
import com.org.test.entity.CustomerEntity;
import com.org.test.exception.custom.InvalidInputException;
import com.org.test.exception.custom.ResourceNotFoundException;
import com.org.test.mapper.CustomerMapper;
import com.org.test.publisher.NotificationPublisher;
import com.org.test.repository.CustomerRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService mockCustomerService;

    @Mock
    private CustomerRepository mockCustomerRepository;

    @Mock
    private NotificationPublisher notificationPublisher;

    @Mock
    private Page<CustomerEntity> mockCustomerPage;

    @Mock
    private CustomerMapper mockCustomerMapper;

    @BeforeEach
    void setUp() {}

    @Test
    void test_getAllCustomers() {
        when(mockCustomerRepository.findAll(any(Pageable.class))).thenReturn(mockCustomerPage);
        when(mockCustomerMapper.fromPageEntities(mockCustomerPage)).thenReturn(MockFactory.pageCustomerEntity());

        ResponseEntity<GenericPaginationDTO<CustomerDTO>> response = mockCustomerService.getAllCustomers(0, 10);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getContent());
        Assertions.assertNotNull(response.getBody().getPagination());
    }

    @Test
    void test_getCustomerById() {
        when(mockCustomerRepository.findById(any())).thenReturn(Optional.of(MockFactory.customerEntity()));
        when(mockCustomerMapper.fromEntity(any())).thenReturn(MockFactory.customerDTO());

        ResponseEntity<CustomerDTO> response = mockCustomerService.getCustomerById(UuidCreator.getTimeOrderedEpoch());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }


    @Test
    void test_getCustomerById_notfound() {
        when(mockCustomerRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> mockCustomerService.getCustomerById(UuidCreator.getTimeOrderedEpoch()));
    }


    @Test
    void test_createCustomer() {
        when(mockCustomerRepository.findByEmail(any())).thenReturn(new ArrayList<>());
        when(mockCustomerMapper.toCreateCustomer(any())).thenReturn(MockFactory.customerEntity());
        doNothing().when(notificationPublisher).publishMessage(any());
        when(mockCustomerMapper.fromEntity(any())).thenReturn(MockFactory.customerDTO());

        ResponseEntity<CustomerDTO> response = mockCustomerService.createCustomer(MockFactory.customerDTO());
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_createCustomer_duplicateEmail() {
        when(mockCustomerRepository.findByEmail(any())).thenReturn(List.of(MockFactory.customerEntity()));

        Assertions.assertThrows(InvalidInputException.class,
                () -> mockCustomerService.createCustomer(MockFactory.customerDTO()));
    }

    @Test
    void test_updateCustomer() {
        when(mockCustomerRepository.findById(any())).thenReturn(Optional.of(MockFactory.customerEntity()));
        doNothing().when(mockCustomerMapper).toUpdateCustomer(any(), any());
        when(mockCustomerRepository.save(any())).thenReturn(MockFactory.customerEntity());
        when(mockCustomerMapper.fromEntity(any())).thenReturn(MockFactory.customerDTO());

        ResponseEntity<CustomerDTO> response = mockCustomerService.updateCustomer(UuidCreator.getTimeOrderedEpoch(), MockFactory.customerDTO());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_updateCustomer_notfound() {
        when(mockCustomerRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> mockCustomerService.updateCustomer(UuidCreator.getTimeOrderedEpoch(), MockFactory.customerDTO()));
    }

    @Test
    void test_updateCustomer_empty_customerId() {
        CustomerDTO customerDTO = MockFactory.customerDTO();
        customerDTO.setCustomerId(null);

        Assertions.assertThrows(InvalidInputException.class,
                () -> mockCustomerService.updateCustomer(UuidCreator.getTimeOrderedEpoch(), customerDTO));
    }

    @Test
    void test_deleteCustomer() {
        doNothing().when(mockCustomerRepository).deleteById(any());
        mockCustomerService.deleteCustomer(UuidCreator.getTimeOrderedEpoch());
    }
}