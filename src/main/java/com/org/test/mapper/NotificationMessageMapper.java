package com.org.test.mapper;

import com.org.test.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NotificationMessageMapper {

    @Mapping(target = "firstName", source = "customerEntity.firstName")
    @Mapping(target = "lastName", source = "customerEntity.lastName")
    @Mapping(target = "email", source = "customerEntity.email")
    com.org.protobuf.Notification toMessage(CustomerEntity customerEntity);

}
