package com.org.test.listener;

import com.google.protobuf.DynamicMessage;
import com.org.protobuf.Notification;
import com.org.test.mapper.MailMessageMapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationListener {

    private final JavaMailSender mailSender;
    private final Configuration freemarker;
    private final MailMessageMapper mailMessageMapper;

    @KafkaListener(id = "#{@applicationConstants.kafkaConsumerGroupId}",
            topics = "#{@applicationConstants.kafkaSendNotificationTopic}")
    public void receiveMessage(DynamicMessage receivedMessage)
            throws IOException, TemplateException, MessagingException {
        Notification notificationMessage = Notification.parseFrom(receivedMessage.toByteArray());

        Template template = freemarker.getTemplate("email-notification.ftl");
        StringWriter stringWriter = new StringWriter();
        Map<String, String> content = mailMessageMapper.toContent(notificationMessage);
        template.process(content, stringWriter);

        MimeMessage emailMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(emailMessage, true);
        mailMessageMapper.toMailMessage(helper, notificationMessage, stringWriter.toString());

        try {
            mailSender.send(emailMessage);
            log.info("Send Notification to E-Mail: {} successfully", notificationMessage.getEmail());
        } catch (MailException ex) {
            log.error("Email notification Exception: ", ex);
        }
    }
}
