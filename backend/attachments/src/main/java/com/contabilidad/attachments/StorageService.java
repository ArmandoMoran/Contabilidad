package com.contabilidad.attachments;

import java.io.InputStream;

public interface StorageService {

    String upload(String bucket, String key, byte[] content, String contentType);

    InputStream download(String bucket, String key);

    String getPresignedUrl(String bucket, String key, int expirySeconds);

    void delete(String bucket, String key);
}
