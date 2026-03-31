package com.contabilidad.invoicing;

import com.contabilidad.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createDraft(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreateInvoiceDraftRequest request) {
        Invoice invoice = invoiceService.createDraft(companyId, request);
        return InvoiceMapper.toDto(invoice);
    }

    @GetMapping("/{id}")
    public InvoiceDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        Invoice invoice = invoiceService.getInvoice(companyId, id);
        return InvoiceMapper.toDto(invoice);
    }

    @GetMapping
    public PageResponse<InvoiceDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam(defaultValue = "DRAFT") String status,
            Pageable pageable) {
        Page<Invoice> page = invoiceService.listInvoices(companyId, status, pageable);
        return PageResponse.of(
            page.getContent().stream().map(InvoiceMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping("/{id}/validate")
    public ValidationResult validate(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        List<String> errors = invoiceService.validateInvoice(companyId, id);
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.withErrors(errors);
    }

    @PostMapping("/{id}/stamp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public InvoiceDto stamp(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        Invoice invoice = invoiceService.stampInvoice(companyId, id, idempotencyKey);
        return InvoiceMapper.toDto(invoice);
    }

    @PostMapping("/{id}/cancel")
    public InvoiceDto cancel(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody CancelRequest request) {
        Invoice invoice = invoiceService.cancelInvoice(companyId, id, request.reasonCode(), request.replacementUuid());
        return InvoiceMapper.toDto(invoice);
    }

    @PostMapping("/credit-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createCreditNote(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreateInvoiceDraftRequest request) {
        // Credit note is an invoice with type "E" (Egreso)
        Invoice invoice = invoiceService.createDraft(companyId, request);
        invoice.setInvoiceType("E");
        return InvoiceMapper.toDto(invoice);
    }
}
