package com.org.test;

import com.github.f4b6a3.uuid.UuidCreator;
import com.org.protobuf.Notification;
import com.org.test.dto.CustomerDTO;
import com.org.test.dto.FileMetadataDTO;
import com.org.test.dto.GenericPaginationDTO;
import com.org.test.dto.GenericPaginationDTO.Pagination;
import com.org.test.entity.CustomerEntity;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.support.SendResult;

public class MockFactory {


    public static GenericPaginationDTO<CustomerDTO> pageCustomerEntity() {
        return GenericPaginationDTO.<CustomerDTO>builder()
                .content(new ArrayList<>())
                .pagination(Pagination.builder()
                        .hasNext(false)
                        .hasPrevious(false)
                        .pageSize(0)
                        .pageNumber(10)
                        .numberOfElements(0)
                        .totalElements(0L)
                        .totalPages(0)
                        .build())
                .build();
    }

    public static CustomerEntity customerEntity() {
        return CustomerEntity.builder()
                .customerId(UuidCreator.getTimeOrderedEpoch())
                .firstName("f")
                .lastName("l")
                .email("test@gmail.com")
                .documentId("019593d750667bb7a397fd9a0ad7dd7d")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    public static CustomerDTO customerDTO() {
        return CustomerDTO.builder()
                .customerId(UuidCreator.getTimeOrderedEpoch())
                .firstName("f")
                .lastName("l")
                .email("test@gmail.com")
                .documentId("019593d750667bb7a397fd9a0ad7dd7d")
                .build();
    }

    public static FileMetadataDTO fileMetadataDTO() {
        return FileMetadataDTO.builder()
                .originalFileName("mock.jpg")
                .contentType("image/jpeg")
                .base64("bW9jaw==")
                .build();
    }

    public static Notification notification() {
        return Notification.newBuilder()
                .setEmail("test@gmail.com")
                .setFirstName("First")
                .setLastName("Last")
                .build();
    }

    public static CompletableFuture<SendResult<String, Notification>> sendResultCompletableFuture() {
        RecordMetadata recordMetadata = new RecordMetadata(
                new TopicPartition("test-topic", 0),
                0, 0, 0L,  0, 0
        );
        SendResult<String, Notification> sendResult = new SendResult<>(null, recordMetadata);

        return CompletableFuture.completedFuture(sendResult);
    }
}
