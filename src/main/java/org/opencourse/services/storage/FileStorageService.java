package org.opencourse.services.storage;

import org.opencourse.models.Resource.ResourceFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;

/**
 * File storage service interface.
 * 
 * @author !EEExp3rt
 */
public interface FileStorageService {

    /**
     * Store a file.
     * 
     * @param file     The file to be stored.
     * @param fileType The type of the file.
     * @param courseId The ID of the course associated with the file.
     * @return The stored file information.
     * @throws RuntimeException If the file storage fails.
     */
    ResourceFile storeFile(MultipartFile file, ResourceFile.FileType fileType, Short courseId);

    /**
     * Get a file.
     * 
     * @param filePath The file path.
     * @return The file content as an {@link InputStream}.
     * @throws RuntimeException If the file access fails.
     */
    InputStream getFile(String filePath);

    /**
     * Delete a file.
     * 
     * @param filePath The file path.
     * @return True if the file is deleted successfully, false otherwise.
     */
    boolean deleteFile(String filePath);

    /**
     * Calculate the file size.
     * 
     * @param fileSize File size in bytes.
     * @return File size in MB.
     */
    BigDecimal calculateFileSizeMB(long fileSize);
}