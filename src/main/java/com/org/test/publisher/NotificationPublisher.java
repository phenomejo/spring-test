package com.org.test.publisher;

import com.org.protobuf.Notification;
import com.org.test.entity.CustomerEntity;
import com.org.test.mapper.NotificationMessageMapper;
import com.org.test.util.JsonUtil;
import com.org.test.config.ApplicationConstants;
import com.org.test.exception.custom.ResourceNotFoundException;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {

    private final NotificationMessageMapper notificationMessageMapper;
    private final KafkaTemplate<String, Notification> kafkaTemplate;
    private final ApplicationConstants applicationConstants;

    public void publishMessage(CustomerEntity customerEntity) {

        String key = customerEntity.getCustomerId().toString();
        String topicName = applicationConstants.getKafkaSendNotificationTopic();
        Notification message = notificationMessageMapper.toMessage(customerEntity);

        log.info("Sending... message: {} to topic:{}", JsonUtil.messageToJson(message), topicName);
        try {
            CompletableFuture<SendResult<String, Notification>> kafkaResultFuture = kafkaTemplate.send(topicName, key,
                    message);
            kafkaResultFuture.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("Error while sending message: {} to Topic: {}", message, topicName, throwable);
                } else {
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info(
                            "Sent successful response from kafka for \n message: {}, Topic: {} Partition: {}, Offset: {}, Timestamp: {}",
                            JsonUtil.messageToJson(message), metadata.topic(), metadata.partition(),
                            metadata.offset(), metadata.timestamp());
                }
            });
        } catch (KafkaException e) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}",
                    customerEntity.getCustomerId(), message,
                    e.getMessage());
            throw new ResourceNotFoundException(
                    "Error on kafka producer with key: " + customerEntity.getCustomerId() + " and message: " + message);
        }
    }

}
