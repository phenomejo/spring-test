package com.org.test.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.org.test.MockFactory;
import com.org.test.exception.custom.ResourceNotFoundException;
import com.org.test.mapper.FileMetadataMapper;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@ExtendWith(MockitoExtension.class)
class FileServiceTest {

    @InjectMocks
    private FileService mockFileService;

    @Mock
    private RedisTemplate<String, Object> mockRedisTemplate;

    @Mock
    private FileMetadataMapper mockFileMetadataMapper;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    void setup() {
        when(mockRedisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void test_uploadFile() throws IOException {
        when(mockFileMetadataMapper.toFileMetadata(any())).thenReturn(MockFactory.fileMetadataDTO());
        doNothing().when(valueOperations).set(any(), any(), any());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("mock", "mock.jpg", "image/jpeg",
                "test".getBytes());
        ResponseEntity<String> response = mockFileService.uploadFile(mockMultipartFile);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
    }

    @Test
    void test_downloadFile() {
        when(valueOperations.get(any())).thenReturn(MockFactory.fileMetadataDTO());

        ResponseEntity<StreamingResponseBody> response = mockFileService.downloadFile("1234");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(MediaType.APPLICATION_OCTET_STREAM, response.getHeaders().getContentType());
        Assertions.assertNotNull(response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    void test_downloadFile_file_notfound() {
        when(valueOperations.get(any())).thenReturn(null);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> mockFileService.downloadFile("1234"));
    }

}