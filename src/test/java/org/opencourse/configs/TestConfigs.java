package org.opencourse.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for testing.
 * 
 * @author !EEExp3rt
 */
@Configuration
@EnableConfigurationProperties(TestConfigs.TestMinioConfigs.class)
public class TestConfigs {

    /**
     * Configuration properties for MinIO tests.
     * 
     * @author !EEExp3rt
     */
    @ConfigurationProperties(prefix = "minio")
    public static class TestMinioConfigs {
    
        @Value("${minio.endpoint}")
        private String minioEndpoint;
    
        @Value("${minio.access-key}")
        private String minioAccessKey;
    
        @Value("${minio.secret-key}")
        private String minioSecretKey;
    
        @Value("${minio.bucket-name}")
        private String minioBucketName;
    
        // Getters.
    
        public String getMinioEndpoint() {
            return minioEndpoint;
        }
    
        public String getMinioAccessKey() {
            return minioAccessKey;
        }
    
        public String getMinioSecretKey() {
            return minioSecretKey;
        }
    
        public String getMinioBucketName() {
            return minioBucketName;
        }
    }
}
