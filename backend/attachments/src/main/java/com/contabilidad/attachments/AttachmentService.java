package com.contabilidad.attachments;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;
    private final String bucket;

    public AttachmentService(
            AttachmentRepository attachmentRepository,
            StorageService storageService,
            @Value("${app.storage.bucket-attachments:contabilidad-attachments}") String bucket) {
        this.attachmentRepository = attachmentRepository;
        this.storageService = storageService;
        this.bucket = bucket;
    }

    public Attachment upload(UUID companyId, String entityType, UUID entityId,
                             String fileName, String contentType, byte[] content, UUID uploadedBy) {
        String objectKey = companyId + "/" + entityType + "/" + entityId + "/" + UUID.randomUUID() + "/" + fileName;
        storageService.upload(bucket, objectKey, content, contentType);

        Attachment attachment = new Attachment();
        attachment.setCompanyId(companyId);
        attachment.setEntityType(entityType);
        attachment.setEntityId(entityId);
        attachment.setFileName(fileName);
        attachment.setContentType(contentType);
        attachment.setFileSize(content.length);
        attachment.setObjectKey(objectKey);
        attachment.setUploadedBy(uploadedBy);
        attachment.setCreatedAt(Instant.now());
        return attachmentRepository.save(attachment);
    }

    @Transactional(readOnly = true)
    public InputStream download(UUID id) {
        Attachment attachment = attachmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id));
        return storageService.download(bucket, attachment.getObjectKey());
    }

    @Transactional(readOnly = true)
    public Attachment getAttachment(UUID id) {
        return attachmentRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Attachment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Attachment> findByEntity(String entityType, UUID entityId) {
        return attachmentRepository.findByEntityTypeAndEntityId(entityType, entityId);
    }
}
