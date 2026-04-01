package com.contabilidad.invoicing;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createDraft(@Valid @RequestBody CreateInvoiceDraftRequest request) {
        Invoice invoice = invoiceService.createDraft(SecurityContextUtils.currentCompanyId(), request);
        return InvoiceMapper.toDto(invoice);
    }

    @GetMapping("/{id}")
    public InvoiceDto get(@PathVariable java.util.UUID id) {
        Invoice invoice = invoiceService.getInvoice(SecurityContextUtils.currentCompanyId(), id);
        return InvoiceMapper.toDto(invoice);
    }

    @GetMapping
    public PageResponse<InvoiceDto> list(
            @RequestParam(defaultValue = "DRAFT") String status,
            Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
        Page<Invoice> page = invoiceService.listInvoices(companyId, status, pageable);
        return PageResponse.of(
            page.getContent().stream().map(InvoiceMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping("/{id}/validate")
    public ValidationResult validate(@PathVariable java.util.UUID id) {
        List<String> errors = invoiceService.validateInvoice(SecurityContextUtils.currentCompanyId(), id);
        return errors.isEmpty() ? ValidationResult.ok() : ValidationResult.withErrors(errors);
    }

    @PostMapping("/{id}/stamp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public InvoiceDto stamp(
            @PathVariable java.util.UUID id,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        Invoice invoice = invoiceService.stampInvoice(SecurityContextUtils.currentCompanyId(), id, idempotencyKey);
        return InvoiceMapper.toDto(invoice);
    }

    @PostMapping("/{id}/cancel")
    public InvoiceDto cancel(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody CancelRequest request) {
        Invoice invoice = invoiceService.cancelInvoice(
            SecurityContextUtils.currentCompanyId(),
            id,
            request.reasonCode(),
            request.replacementUuid()
        );
        return InvoiceMapper.toDto(invoice);
    }

    @PostMapping("/credit-notes")
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceDto createCreditNote(@Valid @RequestBody CreateInvoiceDraftRequest request) {
        Invoice invoice = invoiceService.createCreditNote(SecurityContextUtils.currentCompanyId(), request);
        return InvoiceMapper.toDto(invoice);
    }
}
