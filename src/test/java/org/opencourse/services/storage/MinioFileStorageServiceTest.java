package org.opencourse.services.storage;

import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opencourse.configs.MinioConfig;
import org.opencourse.models.Resource.ResourceFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MinioFileStorageService}.
 * 
 * @author !EEExp3rt
 */
@ExtendWith(MockitoExtension.class)
class MinioFileStorageServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioConfig minioConfig;

    @Mock
    private MinioConfig.MinioConfigProperties minioConfigProperties;

    @Mock
    private MultipartFile multipartFile;

    private MinioFileStorageService minioFileStorageService;

    private static final String BUCKET_NAME = "opencourse-test";
    private static final Short COURSE_ID = 123;

    @BeforeEach
    void setUp() {
        minioFileStorageService = new MinioFileStorageService(minioClient, minioConfig);
        lenient().when(minioConfig.getMinioConfigProperties()).thenReturn(minioConfigProperties);
        lenient().when(minioConfigProperties.getBucketName()).thenReturn(BUCKET_NAME);
    }

    @Test
    @DisplayName("Should successfully store PDF file when upload is valid")
    void storeFile_WithValidPdfFile_ShouldReturnStoredResourceFile() throws Exception {
        // Given.
        String originalFilename = "test-document.pdf";
        long fileSize = 2048L; // 2KB.
        String contentType = "application/pdf";
        InputStream inputStream = new ByteArrayInputStream("test content".getBytes());

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        // When.
        ResourceFile result = minioFileStorageService.storeFile(
            multipartFile, 
            ResourceFile.FileType.PDF,
            COURSE_ID
        );

        // Then.
        assertThat(result).isNotNull();
        assertThat(result.getFileType()).isEqualTo(ResourceFile.FileType.PDF);
        assertThat(result.getFileSize()).isEqualTo(new BigDecimal("0.00")); // 2KB = 0.00MB.
        assertThat(result.getFilePath()).startsWith("resources/123/");
        assertThat(result.getFilePath()).endsWith(".pdf");

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should successfully store text file when upload is valid")
    void storeFile_WithValidTextFile_ShouldReturnStoredResourceFile() throws Exception {
        // Given.
        String originalFilename = "notes.txt";
        long fileSize = 1024L * 1024L * 2; // 2MB.
        String contentType = "text/plain";
        InputStream inputStream = new ByteArrayInputStream("text content".getBytes());

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getSize()).thenReturn(fileSize);
        when(multipartFile.getContentType()).thenReturn(contentType);
        when(multipartFile.getInputStream()).thenReturn(inputStream);

        // When.
        ResourceFile result = minioFileStorageService.storeFile(
            multipartFile, 
            ResourceFile.FileType.TEXT,
            COURSE_ID
        );

        // Then.
        assertThat(result).isNotNull();
        assertThat(result.getFileType()).isEqualTo(ResourceFile.FileType.TEXT);
        assertThat(result.getFileSize()).isEqualTo(new BigDecimal("2.00"));
        assertThat(result.getFilePath()).contains("resources/123/");
        assertThat(result.getFilePath()).endsWith(".txt");

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should throw exception when filename is null")
    void storeFile_WithNullFilename_ShouldThrowException() throws Exception {
        // Given.
        when(multipartFile.getOriginalFilename()).thenReturn(null);

        // When.
        ResourceFile result = minioFileStorageService.storeFile(
            multipartFile,
            ResourceFile.FileType.PDF,
            COURSE_ID
        );

        // Then.
        assertThat(result).isNull();
        verify(minioClient, never()).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should return null when MinIO throws exception during upload")
    void storeFile_WithMinioException_ShouldReturnNull() throws Exception {
        // Given.
        when(multipartFile.getOriginalFilename()).thenReturn("test.pdf");
        when(multipartFile.getSize()).thenReturn(1024L);
        when(multipartFile.getContentType()).thenReturn("application/pdf");
        when(multipartFile.getInputStream()).thenReturn(new ByteArrayInputStream("test".getBytes()));
        
        doThrow(new RuntimeException("MinIO connection error"))
            .when(minioClient).putObject(any(PutObjectArgs.class));

        // When.
        ResourceFile result = minioFileStorageService.storeFile(
            multipartFile,
            ResourceFile.FileType.PDF,
            COURSE_ID
        );

        // Then.
        assertThat(result).isNull();

        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    @DisplayName("Should successfully retrieve file when file exists")
    void getFile_WithExistingFile_ShouldReturnInputStream() throws Exception {
        // Given.
        ResourceFile resourceFile = new ResourceFile(
            ResourceFile.FileType.PDF,
            new BigDecimal("1.50"),
            "resources/123/test-file.pdf"
        );
        GetObjectResponse mockResponse = mock(GetObjectResponse.class);

        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(mockResponse);

        // When.
        InputStream result = minioFileStorageService.getFile(resourceFile);

        // Then.
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockResponse);

        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    @DisplayName("Should return null when MinIO throws exception during file retrieval")
    void getFile_WithMinioException_ShouldReturnNull() throws Exception {
        // Given.
        ResourceFile resourceFile = new ResourceFile(
            ResourceFile.FileType.PDF,
            new BigDecimal("1.50"),
            "resources/123/non-existent-file.pdf"
        );

        when(minioClient.getObject(any(GetObjectArgs.class)))
            .thenThrow(new RuntimeException("File not found"));

        // When.
        InputStream result = minioFileStorageService.getFile(resourceFile);

        // Then.
        assertThat(result).isNull();

        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    @DisplayName("Should successfully delete file when file exists")
    void deleteFile_WithExistingFile_ShouldReturnTrue() throws Exception {
        // Given.
        String filePath = "resources/123/test-file.pdf";

        // When.
        boolean result = minioFileStorageService.deleteFile(filePath);

        // Then.
        assertThat(result).isTrue();

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should return false when MinIO throws exception during file deletion")
    void deleteFile_WithMinioException_ShouldReturnFalse() throws Exception {
        // Given.
        String filePath = "resources/123/test-file.pdf";
        doThrow(new RuntimeException("Delete failed"))
            .when(minioClient).removeObject(any(RemoveObjectArgs.class));

        // When.
        boolean result = minioFileStorageService.deleteFile(filePath);

        // Then.
        assertThat(result).isFalse();

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("Should correctly calculate file size in MB for various file sizes")
    void calculateFileSizeMB_WithVariousFileSizes_ShouldReturnCorrectMBValues() {
        // Given & When & Then.
        // Test zero bytes.
        assertThat(minioFileStorageService.calculateFileSizeMB(0L))
            .isEqualTo(new BigDecimal("0.00"));

        // Test 1KB (should round to 0.00MB).
        assertThat(minioFileStorageService.calculateFileSizeMB(1024L))
            .isEqualTo(new BigDecimal("0.00"));

        // Test 1MB.
        assertThat(minioFileStorageService.calculateFileSizeMB(1024L * 1024L))
            .isEqualTo(new BigDecimal("1.00"));

        // Test 2.5MB.
        assertThat(minioFileStorageService.calculateFileSizeMB(1024L * 1024L * 5 / 2))
            .isEqualTo(new BigDecimal("2.50"));

        // Test 10.25MB.
        assertThat(minioFileStorageService.calculateFileSizeMB(1024L * 1024L * 1025 / 100))
            .isEqualTo(new BigDecimal("10.25"));
    }

    @Test
    @DisplayName("Should correctly calculate file size for large files")
    void calculateFileSizeMB_WithLargeFile_ShouldReturnCorrectMBValue() {
        // Given.
        long largeFileSize = 1024L * 1024L * 1024L; // 1GB.

        // When.
        BigDecimal result = minioFileStorageService.calculateFileSizeMB(largeFileSize);

        // Then.
        assertThat(result).isEqualTo(new BigDecimal("1024.00"));
    }
}
