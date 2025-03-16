package com.org.test.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.github.f4b6a3.uuid.UuidCreator;
import com.org.test.MockFactory;
import com.org.test.dto.GenericPaginationDTO;
import com.org.test.dto.CustomerDTO;
import com.org.test.service.CustomerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @InjectMocks
    private CustomerController mockCustomerController;

    @Mock
    private CustomerService mockCustomerService;


    @BeforeEach
    void setUp() {
    }

    @Test
    void test_getAllCustomers() throws Exception {
        when(mockCustomerService.getAllCustomers(any(), any())).thenReturn(
                ResponseEntity.ok(MockFactory.pageCustomerEntity()));

        ResponseEntity<GenericPaginationDTO<CustomerDTO>> response = mockCustomerController.getAllCustomers(any(),
                any());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_getCustomerById() {
        when(mockCustomerService.getCustomerById(any())).thenReturn(ResponseEntity.ok(MockFactory.customerDTO()));

        ResponseEntity<CustomerDTO> response = mockCustomerController.getCustomerById(any());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_createCustomer() {
        when(mockCustomerService.createCustomer(any())).thenReturn(
                ResponseEntity.status(HttpStatus.CREATED).body(MockFactory.customerDTO()));

        ResponseEntity<CustomerDTO> response = mockCustomerController.createCustomer(MockFactory.customerDTO());

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_updateCustomer() {
        when(mockCustomerService.updateCustomer(any(), any())).thenReturn(
                ResponseEntity.ok(MockFactory.customerDTO()));

        ResponseEntity<CustomerDTO> response = mockCustomerController.updateCustomer(UuidCreator.getTimeOrderedEpoch(),
                MockFactory.customerDTO());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_deleteCustomer() {
        doNothing().when(mockCustomerService).deleteCustomer(any());

        ResponseEntity<Void> response = mockCustomerController.deleteCustomer(any());
        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}