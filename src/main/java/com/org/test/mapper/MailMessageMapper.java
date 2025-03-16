package com.org.test.mapper;

import com.org.protobuf.Notification;
import jakarta.mail.MessagingException;
import java.util.HashMap;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.springframework.mail.javamail.MimeMessageHelper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MailMessageMapper {


    default HashMap<String, String> toContent(Notification notification) {
        HashMap<String, String> content = new HashMap<>();
        content.put("fullName", notification.getFirstName()
                .concat(" ")
                .concat(notification.getLastName()));

        return content;
    }

    @Mapping(target = "to", expression = "java(new String[]{ notification.getEmail() })")
    @Mapping(target = "subject", constant = "Create Customer successfully")
    void toMailMessage(@MappingTarget MimeMessageHelper messageHelper, Notification notification,
            String content) throws MessagingException;

    @AfterMapping
    default void toMailMessage(@MappingTarget MimeMessageHelper messageHelper, String content)
            throws MessagingException {
        messageHelper.setText(content, true);
    }

}
