package com.org.test.service;

import com.github.f4b6a3.uuid.UuidCreator;
import com.org.test.dto.FileMetadataDTO;
import com.org.test.exception.custom.ResourceNotFoundException;
import com.org.test.mapper.FileMetadataMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final FileMetadataMapper fileMetadataMapper;

    public ResponseEntity<String> uploadFile(MultipartFile file) throws IOException {

        String documentId = UuidCreator.getTimeOrderedEpoch().toString().replace("-", "");

        FileMetadataDTO metadataDTO = fileMetadataMapper.toFileMetadata(file);
        redisTemplate.opsForValue().set(documentId, metadataDTO, Duration.ofDays(999));

        return ResponseEntity.ok(documentId);
    }

    public ResponseEntity<StreamingResponseBody> downloadFile(String documentId) {

        FileMetadataDTO metadata = (FileMetadataDTO) redisTemplate.opsForValue().get(documentId);
        if (Objects.isNull(metadata) || Objects.isNull(metadata.getBase64())) {
            throw new ResourceNotFoundException("File not found");
        }

        byte[] fileBytes = Base64.getDecoder().decode(metadata.getBase64());
        StreamingResponseBody responseBody = outputStream -> {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(fileBytes)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    outputStream.flush();
                }
            }
        };

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + metadata.getOriginalFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(responseBody);
    }

}
