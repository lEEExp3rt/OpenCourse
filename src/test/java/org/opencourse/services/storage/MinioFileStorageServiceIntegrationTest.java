package org.opencourse.services.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.opencourse.configs.MinioConfig;
import org.opencourse.configs.TestConfigs;
import org.opencourse.models.Resource.ResourceFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link MinioFileStorageService}.
 * 
 * @author !EEExp3rt
 */
@SpringBootTest(classes = {TestConfigs.class})
@EnableConfigurationProperties(TestConfigs.TestMinioConfigs.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MinioFileStorageServiceIntegrationTest {

    // Service and client to be tested.
    private MinioFileStorageService minioFileStorageService;
    private MinioClient minioClient;

    // Test files to be cleaned up after each test.
    private List<String> filesToCleanup;

    // Test configuration properties.
    @Autowired
    TestConfigs.TestMinioConfigs testConfigs;

    // Test data.
    private static final Short COURSE_ID = 123;

    @BeforeAll
    void setUpOnce() throws Exception {
        // Build MinIO client.
        minioClient = MinioClient.builder()
            .endpoint(testConfigs.getMinioEndpoint())
            .credentials(
                testConfigs.getMinioAccessKey(),
                testConfigs.getMinioSecretKey()
            )
            .build();

        // Test MinIO connection.
        try {
            minioClient.listBuckets();
        } catch (Exception e) {
            throw new RuntimeException("MinIO connection failed", e);
        }

        // Build test bucket if it doesn't exist.
        try {
            if (!minioClient.bucketExists(
                BucketExistsArgs.builder()
                .bucket(testConfigs.getMinioBucketName())
                .build())
            ) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                    .bucket(testConfigs.getMinioBucketName())
                    .build()
                );
            }
        } catch (Exception e) {
            throw new RuntimeException("Test bucket setup failed", e);
        }

        // Build MinioConfig for testing.
        MinioConfig minioConfig = new MinioConfig() {
            @Override
            public String getBucketName() {
                return testConfigs.getMinioBucketName();
            }
        };

        // Build MinioFileStorageService.
        minioFileStorageService = new MinioFileStorageService(minioClient, minioConfig);
    }

    @BeforeEach
    void setUp() {
        filesToCleanup = new ArrayList<>();
    }

    @AfterEach
    void tearDown() {
        filesToCleanup.forEach(path -> {
            if (!minioFileStorageService.deleteFile(path)) {
                System.err.println("❌ Failed to cleanup file: " + path);
            }
        });
    }

    @Test
    @DisplayName("Should successfully store and retrieve PDF file")
    void storeAndRetrieveFile_WithPdfFile_ShouldWorkCorrectly() throws Exception {
        // Given.
        String content = "This is a test PDF content for integration testing";
        MultipartFile mockFile = new MockMultipartFile(
            "file",
            "test-document.pdf",
            "application/pdf",
            content.getBytes()
        );

        // When - Store file.
        ResourceFile storedFile = minioFileStorageService.storeFile(
            mockFile,
            ResourceFile.FileType.PDF,
            COURSE_ID
        );

        // Add to cleanup list if store succeeded.
        if (storedFile != null) {
            filesToCleanup.add(storedFile.getFilePath());
        }

        // Then - Verify store operation.
        assertThat(storedFile)
            .isNotNull()
            .satisfies(file -> {
                assertThat(file.getFileType()).isEqualTo(ResourceFile.FileType.PDF);
                assertThat(file.getFilePath()).startsWith("resources/123/");
                assertThat(file.getFilePath()).endsWith(".pdf");
                assertThat(file.getFileSize()).isEqualTo(new BigDecimal("0.00")); // Small file.
            });

        // When - Retrieve file.
        InputStream retrievedStream = minioFileStorageService.getFile(storedFile);

        // Then - Verify retrieve operation.
        assertThat(retrievedStream).isNotNull();
        byte[] retrievedContent = retrievedStream.readAllBytes();
        assertThat(new String(retrievedContent)).isEqualTo(content);
        retrievedStream.close();
    }

    @Test
    @DisplayName("Should successfully store and retrieve text file")
    void storeAndRetrieveFile_WithTextFile_ShouldWorkCorrectly() throws Exception {
        // Given.
        String content = "This is a test text file content for integration testing with more content to reach larger size";
        MultipartFile mockFile = new MockMultipartFile(
            "file",
            "notes.txt",
            "text/plain",
            content.getBytes()
        );

        // When - Store file.
        ResourceFile storedFile = minioFileStorageService.storeFile(
            mockFile,
            ResourceFile.FileType.TEXT,
            COURSE_ID
        );

        if (storedFile != null) {
            filesToCleanup.add(storedFile.getFilePath());
        }

        // Then - Verify store operation.
        assertThat(storedFile)
            .isNotNull()
            .satisfies(file -> {
                assertThat(file.getFileType()).isEqualTo(ResourceFile.FileType.TEXT);
                assertThat(file.getFilePath()).startsWith("resources/123/");
                assertThat(file.getFilePath()).endsWith(".txt");
            });

        // When - Retrieve file.
        InputStream retrievedStream = minioFileStorageService.getFile(storedFile);

        // Then - Verify retrieve operation.
        assertThat(retrievedStream).isNotNull();
        byte[] retrievedContent = retrievedStream.readAllBytes();
        assertThat(new String(retrievedContent)).isEqualTo(content);
        retrievedStream.close();
    }

    @Test
    @DisplayName("Should successfully store, retrieve and delete file")
    void storeRetrieveAndDeleteFile_WithValidFile_ShouldWorkCorrectly() throws Exception {
        // Given.
        String content = "Sample file content for testing complete workflow";
        MultipartFile mockFile = new MockMultipartFile(
            "file",
            "workflow-test.txt",
            "text/plain",
            content.getBytes()
        );

        // When - Store file.
        ResourceFile storedFile = minioFileStorageService.storeFile(
            mockFile,
            ResourceFile.FileType.OTHER,
            (short) 456
        );

        // Then - Verify store operation.
        assertThat(storedFile).isNotNull();
        assertThat(storedFile.getFileType()).isEqualTo(ResourceFile.FileType.OTHER);
        assertThat(storedFile.getFilePath()).startsWith("resources/456/");

        // When - Retrieve file.
        InputStream retrievedStream = minioFileStorageService.getFile(storedFile);

        // Then - Verify retrieve operation.
        assertThat(retrievedStream).isNotNull();
        byte[] retrievedContent = retrievedStream.readAllBytes();
        assertThat(new String(retrievedContent)).isEqualTo(content);
        retrievedStream.close();

        // When - Delete file.
        boolean deleteResult = minioFileStorageService.deleteFile(storedFile.getFilePath());

        // Then - Verify delete operation.
        assertThat(deleteResult).isTrue();

        // Verify file is actually deleted.
        InputStream deletedFileStream = minioFileStorageService.getFile(storedFile);
        assertThat(deletedFileStream).isNull();
    }

    @Test
    @DisplayName("Should return false when deleting non-existent file")
    void deleteFile_WithNonExistentFile_ShouldReturnFalse() {
        // Given.
        String nonExistentFilePath = "resources/999/non-existent-file.pdf";

        // When.
        boolean result = minioFileStorageService.deleteFile(nonExistentFilePath);

        // Then.
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should handle different file types correctly")
    void storeFile_WithDifferentFileTypes_ShouldWorkCorrectly() throws Exception {
        // Given.
        List<MultipartFile> testFiles = List.of(
            new MockMultipartFile("pdf", "document.pdf", "application/pdf", "PDF content".getBytes()),
            new MockMultipartFile("txt", "notes.txt", "text/plain", "Text content".getBytes()),
            new MockMultipartFile("json", "data.json", "application/json", "{\"test\": \"data\"}".getBytes())
        );

        List<ResourceFile.FileType> fileTypes = List.of(
            ResourceFile.FileType.PDF,
            ResourceFile.FileType.TEXT,
            ResourceFile.FileType.OTHER
        );

        // When & Then.
        for (int i = 0; i < testFiles.size(); i++) {
            MultipartFile file = testFiles.get(i);
            ResourceFile.FileType expectedType = fileTypes.get(i);

            Integer courseId = i;
            ResourceFile result = minioFileStorageService.storeFile(
                file,
                expectedType,
                courseId.shortValue()
            );
            
            if (result != null) {
                filesToCleanup.add(result.getFilePath());
            }

            assertThat(result)
                .isNotNull()
                .satisfies(r -> {
                    assertThat(r.getFileType()).isEqualTo(expectedType);
                    assertThat(r.getFilePath()).startsWith("resources/" + courseId + "/");
                });
        }
    }

    @Test
    @DisplayName("Should handle empty file correctly")
    void storeAndRetrieveFile_WithEmptyFile_ShouldWorkCorrectly() throws Exception {
        // Given.
        MultipartFile mockFile = new MockMultipartFile(
            "file",
            "empty-file.txt",
            "text/plain",
            new byte[0]
        );

        // When - Store file.
        ResourceFile storedFile = minioFileStorageService.storeFile(
            mockFile,
            ResourceFile.FileType.TEXT,
            COURSE_ID
        );
        
        if (storedFile != null) {
            filesToCleanup.add(storedFile.getFilePath());
        }

        // Then - Verify store operation.
        assertThat(storedFile)
            .isNotNull()
            .satisfies(file -> {
                assertThat(file.getFileType()).isEqualTo(ResourceFile.FileType.TEXT);
                assertThat(file.getFileSize()).isEqualTo(new BigDecimal("0.00"));
            });

        // When - Retrieve file.
        InputStream retrievedStream = minioFileStorageService.getFile(storedFile);

        // Then - Verify retrieve operation.
        assertThat(retrievedStream).isNotNull();
        byte[] retrievedContent = retrievedStream.readAllBytes();
        assertThat(retrievedContent).hasSize(0);
        retrievedStream.close();
    }

    @Test
    @DisplayName("Should return null when storing file with null filename")
    void storeFile_WithNullFilename_ShouldReturnNull() throws Exception {
        // Given.
        MultipartFile mockFile = new MockMultipartFile(
            "file",
            null, // null filename.
            "text/plain",
            "Some content".getBytes()
        );

        // When.
        ResourceFile result = minioFileStorageService.storeFile(
            mockFile,
            ResourceFile.FileType.TEXT,
            COURSE_ID
        );

        // Then.
        assertThat(result).isNull();
    }
}
