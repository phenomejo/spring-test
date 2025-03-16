package com.org.test.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadataDTO {

    private String originalFileName;
    private String contentType;
    private String base64;

}
