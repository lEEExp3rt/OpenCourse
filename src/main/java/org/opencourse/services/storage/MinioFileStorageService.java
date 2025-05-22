package org.opencourse.services.storage;

import io.minio.*;
import org.opencourse.models.Resource.ResourceFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${minio.bucket.name}")
    private String bucketName;

    /**
     * Constructor.
     * 
     * @param minioClient MinIO client.
     */
    @Autowired
    public MinioFileStorageService(MinioClient minioClient) {
        this.minioClient = minioClient;
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
                    .bucket(bucketName)
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
            throw new RuntimeException("Failed to upload resource file " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream getFile(String filePath) {
        try {
            return minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file " + e.getMessage(), e);
        }
    }

    @Override
    public boolean deleteFile(String filePath) {
        try {
            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
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
}