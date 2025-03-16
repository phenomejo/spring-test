package com.org.test.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtil {

    private JsonUtil() {
    }

    public static String messageToJson(Message message) {
        try {
            return JsonFormat.printer().print(message);
        } catch (InvalidProtocolBufferException e) {
            log.error("Error during print message: {}", message);
            return "";
        }
    }

}
