package com.contabilidad.invoicing;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/validate")
    public ValidationResult validateDraft(@Valid @RequestBody CreateInvoiceDraftRequest request) {
        return invoiceService.validateInvoice(SecurityContextUtils.currentCompanyId(), request);
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createDraft(@Valid @RequestBody CreateInvoiceDraftRequest request) {
        Invoice invoice = invoiceService.createDraft(SecurityContextUtils.currentCompanyId(), request);
        return InvoiceMapper.toSummaryDto(invoice);
    }

    @PostMapping("/stamped")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createStamped(
        @Valid @RequestBody CreateInvoiceDraftRequest request,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Invoice invoice = invoiceService.createStamped(SecurityContextUtils.currentCompanyId(), request, idempotencyKey);
        return InvoiceMapper.toSummaryDto(invoice);
    }

    @GetMapping("/{id}")
    public InvoiceDetailDto get(@PathVariable UUID id) {
        return invoiceService.getInvoiceDetail(SecurityContextUtils.currentCompanyId(), id);
    }

    @GetMapping
    public PageResponse<InvoiceDto> list(
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String search,
        Pageable pageable
    ) {
        Page<Invoice> page = invoiceService.listInvoices(SecurityContextUtils.currentCompanyId(), status, search, pageable);
        return PageResponse.of(
            page.getContent().stream().map(InvoiceMapper::toSummaryDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping("/{id}/validate")
    public ValidationResult validatePersistedDraft(@PathVariable UUID id) {
        return invoiceService.validateInvoice(SecurityContextUtils.currentCompanyId(), id);
    }

    @PostMapping("/{id}/stamp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public InvoiceDto stamp(
        @PathVariable UUID id,
        @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey
    ) {
        Invoice invoice = invoiceService.stampInvoice(SecurityContextUtils.currentCompanyId(), id, idempotencyKey);
        return InvoiceMapper.toSummaryDto(invoice);
    }

    @GetMapping("/{id}/xml")
    public ResponseEntity<byte[]> downloadXml(@PathVariable UUID id) {
        byte[] content = invoiceService.downloadXml(SecurityContextUtils.currentCompanyId(), id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_XML)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura-" + id + ".xml\"")
            .body(content);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable UUID id) {
        byte[] content = invoiceService.downloadPdf(SecurityContextUtils.currentCompanyId(), id);
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"factura-" + id + ".pdf\"")
            .body(content);
    }

    @PostMapping("/{id}/cancel")
    public InvoiceDto cancel(@PathVariable UUID id, @Valid @RequestBody CancelRequest request) {
        Invoice invoice = invoiceService.cancelInvoice(
            SecurityContextUtils.currentCompanyId(),
            id,
            request.reasonCode(),
            request.replacementUuid()
        );
        return InvoiceMapper.toSummaryDto(invoice);
    }

    @PostMapping("/credit-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createCreditNote(@Valid @RequestBody CreateInvoiceDraftRequest request) {
        Invoice invoice = invoiceService.createCreditNote(SecurityContextUtils.currentCompanyId(), request);
        return InvoiceMapper.toSummaryDto(invoice);
    }
}
