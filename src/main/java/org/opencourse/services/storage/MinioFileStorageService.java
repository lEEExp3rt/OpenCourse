package org.opencourse.services.storage;

import io.minio.*;

import org.opencourse.configs.MinioConfig;
import org.opencourse.models.Resource.ResourceFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * MinIO file storage service manager.
 * 
 * @author !EEExp3rt
 */
@Service
public class MinioFileStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * Constructor.
     * 
     * @param minioClient MinIO client.
     * @param minioConfig MinIO configuration.
     */
    @Autowired
    public MinioFileStorageService(
        MinioClient minioClient,
        MinioConfig minioConfig
    ) {
        this.minioClient = minioClient;
        this.minioConfig = minioConfig;
        init();
    }

    @Override
    public ResourceFile storeFile(MultipartFile file, ResourceFile.FileType fileType, Short courseId) {
        try {
            // Generate a unique filename.
            String filename = file.getOriginalFilename();
            filename = UUID.randomUUID().toString() + filename.substring(filename.lastIndexOf("."));
            // Build the object path.
            String objectPath = "resources/" + courseId.toString() + "/" + filename;
            // Upload file to MinIO.
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(minioConfig.getMinioConfigProperties().getBucketName())
                    .object(objectPath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );
            // Calculate file size in MB.
            BigDecimal fileSize = calculateFileSizeMB(file.getSize());
            // Return the resource file object.
            return new ResourceFile(fileType, fileSize, objectPath);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public InputStream getFile(ResourceFile file) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(minioConfig.getMinioConfigProperties().getBucketName())
                    .object(file.getFilePath())
                    .build()
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(minioConfig.getMinioConfigProperties().getBucketName())
                    .object(filePath)
                    .build()
            );
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(minioConfig.getMinioConfigProperties().getBucketName())
                    .object(filePath)
                    .build()
            );
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public BigDecimal calculateFileSizeMB(long fileSize) {
        // Transform bytes to MB, keep two decimal places.
        return new BigDecimal(fileSize)
            .divide(new BigDecimal(1024 * 1024), 2, RoundingMode.HALF_UP);
    }

    private void init() throws RuntimeException {
        String bucket = minioConfig.getMinioConfigProperties().getBucketName();
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize MinIO service", e);
        }
    }
}