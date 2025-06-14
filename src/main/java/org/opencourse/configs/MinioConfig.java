package org.opencourse.configs;

import io.minio.MinioClient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO configurations.
 * 
 * @author !EEExp3rt
 */
@Configuration
@EnableConfigurationProperties(MinioConfig.MinioConfigProperties.class)
public class MinioConfig {

    private final MinioConfigProperties minioConfigProperties;

    /**
     * Constructor.
     * 
     * @param minioConfigProperties MinIO configuration properties.
     */
    public MinioConfig(MinioConfigProperties minioConfigProperties) {
        this.minioConfigProperties = minioConfigProperties;
    }

    /**
     * MinIO client bean.
     * 
     * @return MinIO client
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(minioConfigProperties.getEndpoint())
            .credentials(
                minioConfigProperties.getAccessKey(),
                minioConfigProperties.getSecretKey())
            .build();
    }

    /**
     * Get MinIO configuration properties.
     * 
     * @return MinIO configuration properties.
     */
    public MinioConfigProperties getMinioConfigProperties() {
        return minioConfigProperties;
    }

    /**
     * MinIO configuration properties.
     * 
     * @author !EEExp3rt
     */
    @ConfigurationProperties(prefix = "minio")
    public static class MinioConfigProperties {

        private String endpoint;
        private String accessKey;
        private String secretKey;
        private String bucketName;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        @Override
        public String toString() {
            return "MinioConfigProperties{" +
                    "endpoint='" + endpoint + '\'' +
                    ", accessKey='" + accessKey + '\'' +
                    ", secretKey='" + secretKey + '\'' +
                    ", bucketName='" + bucketName + '\'' +
                    '}';
        }
    }
}