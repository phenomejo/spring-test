package com.org.test.controller;

import com.org.test.service.FileService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadCustomerProfile(@RequestParam("file") final MultipartFile file)
            throws IOException {
        return fileService.uploadFile(file);
    }

    @GetMapping(value = "/{documentId}/download")
    public ResponseEntity<StreamingResponseBody> downloadCustomerProfile(@PathVariable String documentId){
        return fileService.downloadFile(documentId);
    }

}
