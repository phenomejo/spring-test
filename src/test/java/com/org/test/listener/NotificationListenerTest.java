package com.org.test.listener;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.google.protobuf.DynamicMessage;
import com.org.test.MockFactory;
import com.org.test.mapper.MailMessageMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class NotificationListenerTest {

    @InjectMocks
    private NotificationListener notificationListener;

    @Mock
    private JavaMailSender mockMailSender;

    @Mock
    private MimeMessage mockMimeMessage;

    @Mock
    private Configuration freemarker;

    @Mock
    private MailMessageMapper mailMessageMapper;

    private Template mockTemplate;

    @BeforeEach
    void setup() throws IOException {
        when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        mockTemplate = new Template("mock", new StringReader("mock"), null);
    }

    @Test
    void test_receiveMessage() throws TemplateException, MessagingException, IOException {
        doNothing().when(mockMailSender).send(mockMimeMessage);

        when(freemarker.getTemplate("email-notification.ftl")).thenReturn(mockTemplate);

        notificationListener.receiveMessage(DynamicMessage.newBuilder(MockFactory.notification()).build());
    }
}