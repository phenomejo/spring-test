package com.org.test.batch.processor;

import com.org.test.dto.ImportCustomerDTO;
import com.org.test.entity.CustomerEntity;
import com.org.test.mapper.CustomerMapper;
import com.org.test.repository.CustomerRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.CollectionUtils;

@Slf4j
public class ImportCustomerProcessor implements ItemProcessor<ImportCustomerDTO, List<CustomerEntity>> {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    public ImportCustomerProcessor(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @NotNull
    @Override
    public List<CustomerEntity> process(@NotNull ImportCustomerDTO item) throws Exception {
        List<CustomerEntity> entities = new ArrayList<CustomerEntity>();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<ImportCustomerDTO>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            var errorMessages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .toList();
            log.error("Skip import: {}, Validate error : {}", item, errorMessages);
            return entities;
        }

        List<CustomerEntity> customerEntities = customerRepository.findByEmail(item.getEmail());
        if (!CollectionUtils.isEmpty(customerEntities)) {
            log.info("Skip import: {} Email already exist", item);
            return entities;
        }

        entities.add(customerMapper.toImportCustomer(item));
        return entities;
    }
}
