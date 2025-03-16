package com.org.test.publisher;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.org.protobuf.Notification;
import com.org.test.MockFactory;
import com.org.test.config.ApplicationConstants;
import com.org.test.mapper.NotificationMessageMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherTest {

    @InjectMocks
    private NotificationPublisher notificationPublisher;

    @Mock
    private NotificationMessageMapper notificationMessageMapper;

    @Mock
    private KafkaTemplate<String, Notification> kafkaTemplate;

    @Mock
    private ApplicationConstants applicationConstants;

    @BeforeEach
    void setup() {
    }

    @Test
    void test_publishMessage() {
        when(applicationConstants.getKafkaSendNotificationTopic()).thenReturn("send-notification-request");
        when(notificationMessageMapper.toMessage(any())).thenReturn(MockFactory.notification());
        when(kafkaTemplate.send(any(), any(), any())).thenReturn(MockFactory.sendResultCompletableFuture());

        notificationPublisher.publishMessage(MockFactory.customerEntity());
    }
}