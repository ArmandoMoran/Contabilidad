package com.contabilidad.attachments;

import io.minio.GetObjectArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Service
public class MinioStorageService implements StorageService {

    private final MinioClient minioClient;

    public MinioStorageService(
            @Value("${app.storage.endpoint}") String endpoint,
            @Value("${app.storage.region:}") String region,
            @Value("${app.storage.access-key}") String accessKey,
            @Value("${app.storage.secret-key}") String secretKey) {
        MinioClient.Builder builder = MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey);
        if (!region.isBlank()) {
            builder.region(region);
        }
        this.minioClient = builder.build();
    }

    @Override
    public String upload(String bucket, String key, byte[] content, String contentType) {
        try (InputStream is = new ByteArrayInputStream(content)) {
            minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(key)
                .stream(is, content.length, -1)
                .contentType(contentType)
                .build());
            return key;
        } catch (Exception e) {
            throw new StorageException("Failed to upload object: " + key, e);
        }
    }

    @Override
    public InputStream download(String bucket, String key) {
        try {
            return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket)
                .object(key)
                .build());
        } catch (Exception e) {
            throw new StorageException("Failed to download object: " + key, e);
        }
    }

    @Override
    public String getPresignedUrl(String bucket, String key, int expirySeconds) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucket)
                .object(key)
                .expiry(expirySeconds, TimeUnit.SECONDS)
                .build());
        } catch (Exception e) {
            throw new StorageException("Failed to generate presigned URL: " + key, e);
        }
    }

    @Override
    public void delete(String bucket, String key) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(key)
                .build());
        } catch (Exception e) {
            throw new StorageException("Failed to delete object: " + key, e);
        }
    }

    public static class StorageException extends RuntimeException {
        public StorageException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
