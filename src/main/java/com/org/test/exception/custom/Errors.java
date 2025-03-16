package com.org.test.exception.custom;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Errors {

    private String errorCode;
    private String errorMessage;
    private String errorDescription;
    private String traceId;
}
