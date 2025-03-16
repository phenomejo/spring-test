package com.org.test.mapper;

import com.org.test.dto.GenericPaginationDTO;
import com.org.test.dto.GenericPaginationDTO.Pagination;
import com.org.test.entity.CustomerEntity;
import com.org.test.dto.CustomerDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        builder = @Builder(disableBuilder = true))
public interface CustomerMapper {

    CustomerDTO fromEntity(CustomerEntity entity);

    @Mapping(target = "content", source = "entities.content")
    @Mapping(target = "pagination.totalPages", source = "entities.totalPages")
    @Mapping(target = "pagination.totalElements", source = "entities.totalElements")
    @Mapping(target = "pagination.numberOfElements", source = "entities.numberOfElements")
    @Mapping(target = "pagination.pageSize", source = "entities.pageable.pageSize")
    @Mapping(target = "pagination.pageNumber", source = "entities.pageable.pageNumber")
//    @Mapping(target = "pagination", expression = "java(afterMappingPageEntities(entities))")
    GenericPaginationDTO<CustomerDTO> fromPageEntities(Page<CustomerEntity> entities);

    @AfterMapping
    default void fromPageEntities(@MappingTarget Pagination pagination, Page<CustomerEntity> entities) {
        pagination.setHasNext(entities.hasNext());
        pagination.setHasPrevious(entities.hasPrevious());
    }

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "customerId", expression = "java(com.github.f4b6a3.uuid.UuidCreator.getTimeOrderedEpoch())")
    CustomerEntity toCreateCustomer(CustomerDTO dto);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void toUpdateCustomer(@MappingTarget CustomerEntity entity, CustomerDTO dto);
}
