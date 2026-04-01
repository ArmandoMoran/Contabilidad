package com.contabilidad.invoicing;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final TaxLineRepository taxLineRepository;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          InvoiceLineRepository invoiceLineRepository,
                          TaxLineRepository taxLineRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.taxLineRepository = taxLineRepository;
    }

    public Invoice createDraft(UUID companyId, CreateInvoiceDraftRequest request) {
        log.info("Creating invoice draft companyId={}, type={}, clientId={}", companyId, request.invoiceType(), request.clientId());
        Invoice invoice = InvoiceMapper.toEntity(companyId, request);
        invoice = invoiceRepository.save(invoice);
        log.debug("Saved invoice draft id={}", invoice.getId());

        if (request.lines() != null) {
            int lineNum = 1;
            for (InvoiceLineRequest lr : request.lines()) {
                InvoiceLine line = InvoiceMapper.toLineEntity(companyId, invoice.getId(), lineNum++, lr);
                invoiceLineRepository.save(line);
            }
            log.debug("Saved {} invoice lines", request.lines().size());
        }
        return invoice;
    }

    public Invoice createCreditNote(UUID companyId, CreateInvoiceDraftRequest request) {
        Invoice invoice = createDraft(companyId, request);
        invoice.setInvoiceType("E");
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoice(UUID companyId, UUID invoiceId) {
        log.info("Getting invoice companyId={}, invoiceId={}", companyId, invoiceId);
        return invoiceRepository.findByCompanyIdAndId(companyId, invoiceId)
            .orElseThrow(() -> {
                log.warn("Invoice not found: companyId={}, invoiceId={}", companyId, invoiceId);
                return new EntityNotFoundException("Invoice not found: " + invoiceId);
            });
    }

    @Transactional(readOnly = true)
    public Page<Invoice> listInvoices(UUID companyId, String status, Pageable pageable) {
        return invoiceRepository.findByCompanyIdAndStatus(companyId, status, pageable);
    }

    @Transactional(readOnly = true)
    public List<String> validateInvoice(UUID companyId, UUID invoiceId) {
        Invoice invoice = getInvoice(companyId, invoiceId);
        List<String> errors = new ArrayList<>();

        if (invoice.getIssuerRfc() == null || invoice.getIssuerRfc().isBlank()) {
            errors.add("issuer_rfc is required");
        }
        if (invoice.getReceiverRfc() == null || invoice.getReceiverRfc().isBlank()) {
            errors.add("receiver_rfc is required");
        }
        List<InvoiceLine> lines = invoiceLineRepository.findByInvoiceIdOrderByLineNumber(invoiceId);
        if (lines.isEmpty()) {
            errors.add("Invoice must have at least one line");
        }
        return errors;
    }

    public Invoice stampInvoice(UUID companyId, UUID invoiceId, String idempotencyKey) {
        log.info("Stamping invoice companyId={}, invoiceId={}", companyId, invoiceId);
        if (idempotencyKey != null) {
            var existing = invoiceRepository.findByCompanyIdAndIdempotencyKey(companyId, idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Invoice invoice = getInvoice(companyId, invoiceId);

        if (!"DRAFT".equals(invoice.getStatus()) && !"VALIDATED".equals(invoice.getStatus())) {
            throw new IllegalStateException("Invoice must be in DRAFT or VALIDATED status to stamp");
        }

        // TODO: call PAC integration to stamp the invoice
        invoice.setStatus("STAMPED");
        invoice.setIssuedAt(Instant.now());
        invoice.setIdempotencyKey(idempotencyKey);
        return invoiceRepository.save(invoice);
    }

    public Invoice cancelInvoice(UUID companyId, UUID invoiceId, String reasonCode, UUID replacementUuid) {
        log.info("Cancelling invoice companyId={}, invoiceId={}, reason={}", companyId, invoiceId, reasonCode);
        Invoice invoice = getInvoice(companyId, invoiceId);

        if (!"STAMPED".equals(invoice.getStatus())) {
            throw new IllegalStateException("Only stamped invoices can be cancelled");
        }

        invoice.setStatus("CANCEL_PENDING");
        invoice.setCancelReasonCode(reasonCode);
        invoice.setCancelReplacementUuid(replacementUuid);
        // TODO: call PAC integration to submit cancellation
        return invoiceRepository.save(invoice);
    }
}
