package com.contabilidad.attachments;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.InputStream;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AttachmentService {

    private static final String BUCKET = "contabilidad";

    private final AttachmentRepository attachmentRepository;
    private final StorageService storageService;

    public AttachmentService(AttachmentRepository attachmentRepository, StorageService storageService) {
        this.attachmentRepository = attachmentRepository;
        this.storageService = storageService;
    }

    public Attachment upload(UUID companyId, String entityType, UUID entityId,
                             String fileName, String contentType, byte[] content, UUID uploadedBy) {
        String objectKey = companyId + "/" + entityType + "/" + entityId + "/" + UUID.randomUUID() + "/" + fileName;
        storageService.upload(BUCKET, objectKey, content, contentType);

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
        return storageService.download(BUCKET, attachment.getObjectKey());
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
