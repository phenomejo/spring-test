package com.org.test.mapper;

import com.org.test.dto.FileMetadataDTO;
import java.io.IOException;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FileMetadataMapper {

    @Mapping(target = "originalFileName", source = "file.originalFilename")
    @Mapping(target = "contentType", source = "file.contentType")
    @Mapping(target = "base64", expression = "java(java.util.Base64.getEncoder().encodeToString(file.getBytes()))")
    FileMetadataDTO toFileMetadata(MultipartFile file) throws IOException;
}
