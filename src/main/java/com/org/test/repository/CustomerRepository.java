package com.org.test.repository;

import com.org.test.entity.CustomerEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CustomerRepository extends JpaRepository<CustomerEntity, UUID>,
        JpaSpecificationExecutor<CustomerEntity> {

    List<CustomerEntity> findByEmail(String email);


}