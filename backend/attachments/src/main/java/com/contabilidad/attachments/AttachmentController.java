package com.contabilidad.attachments;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/attachments")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDto upload(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam String entityType,
            @RequestParam UUID entityId,
            @RequestParam(required = false) UUID uploadedBy,
            @RequestParam("file") MultipartFile file) throws Exception {
        Attachment attachment = attachmentService.upload(
            companyId, entityType, entityId,
            file.getOriginalFilename(),
            file.getContentType(),
            file.getBytes(),
            uploadedBy
        );
        return toDto(attachment);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> download(@PathVariable UUID id) {
        Attachment attachment = attachmentService.getAttachment(id);
        InputStream stream = attachmentService.download(id);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
            .contentType(MediaType.parseMediaType(attachment.getContentType()))
            .contentLength(attachment.getFileSize())
            .body(new InputStreamResource(stream));
    }

    private AttachmentDto toDto(Attachment a) {
        return new AttachmentDto(
            a.getId(), a.getCompanyId(), a.getEntityType(), a.getEntityId(),
            a.getFileName(), a.getContentType(), a.getFileSize(),
            a.getObjectKey(), a.getChecksumSha256(),
            a.getUploadedBy(), a.getCreatedAt()
        );
    }

    public record AttachmentDto(
        UUID id, UUID companyId, String entityType, UUID entityId,
        String fileName, String contentType, long fileSize,
        String objectKey, String checksumSha256,
        UUID uploadedBy, java.time.Instant createdAt
    ) {}
}
