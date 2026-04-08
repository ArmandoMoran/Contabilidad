package com.contabilidad.invoicing;

import com.contabilidad.company.Company;
import com.contabilidad.company.CompanyRepository;
import com.contabilidad.integration.pac.CancelResult;
import com.contabilidad.integration.pac.PacClient;
import com.contabilidad.integration.pac.StampResult;
import com.contabilidad.parties.Address;
import com.contabilidad.parties.AddressRepository;
import com.contabilidad.parties.Client;
import com.contabilidad.parties.ClientRepository;
import com.contabilidad.products.Product;
import com.contabilidad.products.ProductRepository;
import com.contabilidad.products.ProductTaxProfile;
import com.contabilidad.products.ProductTaxProfileRepository;
import com.contabilidad.shared.BusinessValidationException;
import com.contabilidad.tax.TaxCalculationResult;
import com.contabilidad.tax.TaxCalculationService;
import com.contabilidad.tax.TaxRule;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);
    private static final String DEFAULT_SERIES = "A";
    private static final String DEFAULT_CURRENCY = "MXN";
    private static final String DEFAULT_PAYMENT_METHOD = "PUE";
    private static final String DEFAULT_PAYMENT_FORM = "03";
    private static final String DEFAULT_USO_CFDI = "G03";

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLineRepository invoiceLineRepository;
    private final TaxLineRepository taxLineRepository;
    private final CompanyRepository companyRepository;
    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final ProductTaxProfileRepository productTaxProfileRepository;
    private final TaxCalculationService taxCalculationService;
    private final PacClient pacClient;
    private final InvoiceDocumentRenderer invoiceDocumentRenderer;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public InvoiceService(
        InvoiceRepository invoiceRepository,
        InvoiceLineRepository invoiceLineRepository,
        TaxLineRepository taxLineRepository,
        CompanyRepository companyRepository,
        ClientRepository clientRepository,
        AddressRepository addressRepository,
        ProductRepository productRepository,
        ProductTaxProfileRepository productTaxProfileRepository,
        TaxCalculationService taxCalculationService,
        PacClient pacClient,
        InvoiceDocumentRenderer invoiceDocumentRenderer
    ) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLineRepository = invoiceLineRepository;
        this.taxLineRepository = taxLineRepository;
        this.companyRepository = companyRepository;
        this.clientRepository = clientRepository;
        this.addressRepository = addressRepository;
        this.productRepository = productRepository;
        this.productTaxProfileRepository = productTaxProfileRepository;
        this.taxCalculationService = taxCalculationService;
        this.pacClient = pacClient;
        this.invoiceDocumentRenderer = invoiceDocumentRenderer;
    }

    public Invoice createDraft(UUID companyId, CreateInvoiceDraftRequest request) {
        InvoiceComputationResult computation = computeInvoice(companyId, request);
        ensureValid(computation);
        return persistDraft(companyId, request, computation);
    }

    public Invoice createStamped(UUID companyId, CreateInvoiceDraftRequest request, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = invoiceRepository.findByCompanyIdAndIdempotencyKey(companyId, idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Invoice draft = createDraft(companyId, request);
        return stampInvoice(companyId, draft.getId(), idempotencyKey);
    }

    public Invoice createCreditNote(UUID companyId, CreateInvoiceDraftRequest request) {
        CreateInvoiceDraftRequest creditNoteRequest = new CreateInvoiceDraftRequest(
            request.clientId(),
            "E",
            request.series(),
            request.folio(),
            request.paymentMethodCode(),
            request.paymentFormCode(),
            request.usoCfdiCode(),
            request.currencyCode(),
            request.lines()
        );
        return createDraft(companyId, creditNoteRequest);
    }

    @Transactional(readOnly = true)
    public Invoice getInvoice(UUID companyId, UUID invoiceId) {
        return invoiceRepository.findByCompanyIdAndId(companyId, invoiceId)
            .orElseThrow(() -> new EntityNotFoundException("Invoice not found: " + invoiceId));
    }

    @Transactional(readOnly = true)
    public InvoiceDetailDto getInvoiceDetail(UUID companyId, UUID invoiceId) {
        Invoice invoice = getInvoice(companyId, invoiceId);
        List<InvoiceLine> lines = invoiceLineRepository.findByInvoiceIdOrderByLineNumber(invoiceId);
        List<TaxLine> taxLines = taxLineRepository.findBySourceTypeAndSourceId("INVOICE", invoiceId);
        return InvoiceMapper.toDetailDto(invoice, lines, taxLines);
    }

    @Transactional(readOnly = true)
    public Page<Invoice> listInvoices(UUID companyId, String status, String search, Pageable pageable) {
        String normalizedStatus = normalizeStatus(status);
        String normalizedSearch = normalizeBlank(search);
        UUID uuidSearch = parseUuid(normalizedSearch);
        return invoiceRepository.searchByCompanyId(companyId, normalizedStatus, normalizedSearch, uuidSearch, pageable);
    }

    @Transactional(readOnly = true)
    public ValidationResult validateInvoice(UUID companyId, CreateInvoiceDraftRequest request) {
        InvoiceComputationResult computation = computeInvoice(companyId, request);
        InvoicePreviewDto preview = canBuildPreview(computation) ? InvoiceMapper.toPreviewDto(computation) : null;
        return computation.valid()
            ? ValidationResult.ok(preview)
            : ValidationResult.withIssues(computation.issues(), preview);
    }

    public ValidationResult validateInvoice(UUID companyId, UUID invoiceId) {
        Invoice invoice = getInvoice(companyId, invoiceId);
        List<InvoiceLine> lines = invoiceLineRepository.findByInvoiceIdOrderByLineNumber(invoiceId);
        List<TaxLine> taxLines = taxLineRepository.findBySourceTypeAndSourceId("INVOICE", invoiceId);
        List<InvoiceValidationIssue> issues = validatePersistedInvoice(invoice, lines, taxLines);
        if (issues.isEmpty() && "DRAFT".equals(invoice.getStatus())) {
            invoice.setStatus("VALIDATED");
            invoiceRepository.save(invoice);
        }
        InvoicePreviewDto preview = InvoiceMapper.toPreviewDto(invoice, lines, taxLines);
        return issues.isEmpty() ? ValidationResult.ok(preview) : ValidationResult.withIssues(issues, preview);
    }

    public Invoice stampInvoice(UUID companyId, UUID invoiceId, String idempotencyKey) {
        if (idempotencyKey != null && !idempotencyKey.isBlank()) {
            var existing = invoiceRepository.findByCompanyIdAndIdempotencyKey(companyId, idempotencyKey);
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Invoice invoice = getInvoice(companyId, invoiceId);
        if ("STAMPED".equals(invoice.getStatus())) {
            return invoice;
        }
        if (!"DRAFT".equals(invoice.getStatus()) && !"VALIDATED".equals(invoice.getStatus())) {
            throw new IllegalStateException("Invoice must be in DRAFT or VALIDATED status to stamp");
        }

        List<InvoiceLine> lines = invoiceLineRepository.findByInvoiceIdOrderByLineNumber(invoiceId);
        List<TaxLine> taxLines = taxLineRepository.findBySourceTypeAndSourceId("INVOICE", invoiceId);
        List<InvoiceValidationIssue> issues = validatePersistedInvoice(invoice, lines, taxLines);
        if (!issues.isEmpty()) {
            throwBusinessValidation(issues);
        }

        InvoiceDetailDto detailBeforeStamp = InvoiceMapper.toDetailDto(invoice, lines, taxLines);
        String xmlContent = new String(invoiceDocumentRenderer.renderXml(detailBeforeStamp), StandardCharsets.UTF_8);
        StampResult stampResult = pacClient.stamp(xmlContent);

        invoice.setStatus("STAMPED");
        invoice.setIdempotencyKey(normalizeBlank(idempotencyKey));
        if (invoice.getIssuedAt() == null) {
            invoice.setIssuedAt(Instant.now());
        }
        invoice.setCertifiedAt(stampResult.stampDate());
        invoice.setStampDate(stampResult.stampDate());
        invoice.setPacUuid(stampResult.uuid());
        invoice.setPacSeal(stampResult.cfdiSeal());
        invoice.setSatSeal(stampResult.satSeal());
        invoice.setPacCertNumber("SIMULATED-PAC");
        invoice.setSatCertNumber(stampResult.satCertNumber());
        invoice.setPacStatus("STAMPED");
        invoice.setOriginalChain("||" + stampResult.uuid() + "|" + stampResult.stampDate() + "||");
        invoice.setXmlObjectKey(buildDocumentObjectKey(invoice, "xml"));
        invoice.setPdfObjectKey(buildDocumentObjectKey(invoice, "pdf"));
        invoiceRepository.save(invoice);
        return invoice;
    }

    public Invoice cancelInvoice(UUID companyId, UUID invoiceId, String reasonCode, UUID replacementUuid) {
        log.info("Cancelling invoice companyId={}, invoiceId={}, reason={}", companyId, invoiceId, reasonCode);
        Invoice invoice = getInvoice(companyId, invoiceId);

        if (!"STAMPED".equals(invoice.getStatus())) {
            throw new IllegalStateException("Only stamped invoices can be cancelled");
        }

        CancelResult cancelResult = pacClient.cancel(invoice.getPacUuid(), invoice.getIssuerRfc(), reasonCode, replacementUuid);
        invoice.setStatus("CANCELLED");
        invoice.setPacStatus(cancelResult.status());
        invoice.setCancelReasonCode(reasonCode);
        invoice.setCancelReplacementUuid(replacementUuid);
        invoice.setCancelAcuse(cancelResult.acuse());
        invoice.setCancelledAt(cancelResult.cancelDate());
        invoice.setAcuseObjectKey(buildDocumentObjectKey(invoice, "acuse"));
        return invoiceRepository.save(invoice);
    }

    @Transactional(readOnly = true)
    public byte[] downloadXml(UUID companyId, UUID invoiceId) {
        InvoiceDetailDto detail = getInvoiceDetail(companyId, invoiceId);
        return invoiceDocumentRenderer.renderXml(detail);
    }

    @Transactional(readOnly = true)
    public byte[] downloadPdf(UUID companyId, UUID invoiceId) {
        InvoiceDetailDto detail = getInvoiceDetail(companyId, invoiceId);
        return invoiceDocumentRenderer.renderPdf(detail);
    }

    private Invoice persistDraft(UUID companyId, CreateInvoiceDraftRequest request, InvoiceComputationResult computation) {
        Invoice invoice = new Invoice();
        invoice.setCompanyId(companyId);
        invoice.setClientId(request.clientId());
        invoice.setInvoiceType(request.invoiceType().trim().toUpperCase());
        invoice.setStatus("DRAFT");
        invoice.setSeries(computation.series());
        invoice.setFolio(computation.folio());
        invoice.setIssuedAt(Instant.now());
        invoice.setCurrencyCode(computation.currencyCode());
        invoice.setExchangeRate(BigDecimal.ONE);
        invoice.setPaymentMethodCode(computation.paymentMethodCode());
        invoice.setPaymentFormCode(computation.paymentFormCode());
        invoice.setUsoCfdiCode(computation.usoCfdiCode());
        invoice.setExportCode("01");
        invoice.setIssuerRfc(computation.company().getRfc());
        invoice.setIssuerName(computation.company().getLegalName());
        invoice.setIssuerRegimeCode(computation.company().getFiscalRegimeCode());
        invoice.setReceiverRfc(computation.client().getRfc());
        invoice.setReceiverName(computation.client().getLegalName());
        invoice.setReceiverRegimeCode(computation.client().getFiscalRegimeCode());
        invoice.setReceiverPostalCode(computation.receiverPostalCode());
        invoice.setSubtotal(scaleMoney(computation.subtotal()));
        invoice.setDiscount(scaleMoney(computation.discount()));
        invoice.setTransferredTaxTotal(scaleMoney(computation.transferredTaxTotal()));
        invoice.setWithheldTaxTotal(scaleMoney(computation.withheldTaxTotal()));
        invoice.setTotal(scaleMoney(computation.total()));
        Map<String, Object> issuerSnapshot = new LinkedHashMap<>();
        issuerSnapshot.put("rfc", computation.company().getRfc());
        issuerSnapshot.put("legalName", computation.company().getLegalName());
        issuerSnapshot.put("fiscalRegimeCode", computation.company().getFiscalRegimeCode());
        issuerSnapshot.put("postalCode", normalizeBlank(computation.company().getPostalCode()));
        Map<String, Object> receiverSnapshot = new LinkedHashMap<>();
        receiverSnapshot.put("rfc", computation.client().getRfc());
        receiverSnapshot.put("legalName", computation.client().getLegalName());
        receiverSnapshot.put("fiscalRegimeCode", computation.client().getFiscalRegimeCode());
        receiverSnapshot.put("postalCode", computation.receiverPostalCode());
        Map<String, Object> cfdiSnapshot = new LinkedHashMap<>();
        cfdiSnapshot.put("series", computation.series());
        cfdiSnapshot.put("folio", computation.folio());
        cfdiSnapshot.put("invoiceType", request.invoiceType().trim().toUpperCase());
        cfdiSnapshot.put("currencyCode", computation.currencyCode());
        cfdiSnapshot.put("paymentMethodCode", computation.paymentMethodCode());
        cfdiSnapshot.put("paymentFormCode", normalizeBlank(computation.paymentFormCode()));
        cfdiSnapshot.put("usoCfdiCode", computation.usoCfdiCode());
        Map<String, Object> fiscalSnapshot = new LinkedHashMap<>();
        fiscalSnapshot.put("issuer", issuerSnapshot);
        fiscalSnapshot.put("receiver", receiverSnapshot);
        fiscalSnapshot.put("cfdi", cfdiSnapshot);
        invoice.setFiscalSnapshot(writeJson(fiscalSnapshot));
        invoice = invoiceRepository.save(invoice);

        List<TaxLine> persistedTaxLines = new ArrayList<>();
        for (ComputedInvoiceLine computedLine : computation.lines()) {
            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setCompanyId(companyId);
            invoiceLine.setInvoiceId(invoice.getId());
            invoiceLine.setLineNumber(computedLine.lineNumber());
            invoiceLine.setProductId(computedLine.productId());
            invoiceLine.setSatProductCode(computedLine.satProductCode());
            invoiceLine.setDescription(computedLine.description());
            invoiceLine.setSatUnitCode(computedLine.satUnitCode());
            invoiceLine.setUnitName(computedLine.unitName());
            invoiceLine.setQuantity(computedLine.quantity());
            invoiceLine.setUnitPrice(computedLine.unitPrice());
            invoiceLine.setDiscount(scaleMoney(computedLine.discount()));
            invoiceLine.setSubtotal(scaleMoney(computedLine.subtotal()));
            invoiceLine.setObjetoImpCode(computedLine.objetoImpCode());
            invoiceLine.setTaxProfileSnapshot(writeJson(buildTaxProfileSnapshot(computedLine.taxes())));
            invoiceLineRepository.save(invoiceLine);

            for (TaxCalculationResult tax : computedLine.taxes()) {
                TaxLine taxLine = new TaxLine();
                taxLine.setCompanyId(companyId);
                taxLine.setSourceType("INVOICE");
                taxLine.setSourceId(invoice.getId());
                taxLine.setSourceLineId(invoiceLine.getId());
                taxLine.setTaxCode(tax.taxCode());
                taxLine.setFactorType(tax.factorType());
                taxLine.setRate(tax.rate());
                taxLine.setBaseAmount(scaleMoney(tax.baseAmount()));
                taxLine.setTaxAmount(scaleMoney(tax.taxAmount()));
                taxLine.setTransfer(tax.isTransfer());
                taxLine.setWithholding(tax.isWithholding());
                taxLine.setPeriodKey(periodKey(invoice.getIssuedAt()));
                taxLine.setIssuedAt(invoice.getIssuedAt());
                persistedTaxLines.add(taxLine);
            }
        }

        if (!persistedTaxLines.isEmpty()) {
            taxLineRepository.saveAll(persistedTaxLines);
        }

        return invoice;
    }

    private InvoiceComputationResult computeInvoice(UUID companyId, CreateInvoiceDraftRequest request) {
        Company company = companyRepository.findById(companyId)
            .orElseThrow(() -> new EntityNotFoundException("Company not found: " + companyId));

        List<InvoiceValidationIssue> issues = new ArrayList<>();
        String invoiceType = normalizeBlank(request.invoiceType());
        if (invoiceType == null || (!invoiceType.equalsIgnoreCase("I") && !invoiceType.equalsIgnoreCase("E"))) {
            issues.add(issue("invoiceType", "Tipo de comprobante inválido. Usa I o E.", "INVALID_INVOICE_TYPE"));
        }

        Client client = null;
        String receiverPostalCode = null;
        if (request.clientId() == null) {
            issues.add(issue("clientId", "Selecciona un cliente.", "CLIENT_REQUIRED"));
        } else {
            client = clientRepository.findByCompanyIdAndIdAndDeletedAtIsNull(companyId, request.clientId()).orElse(null);
            if (client == null) {
                issues.add(issue("clientId", "El cliente seleccionado no existe.", "CLIENT_NOT_FOUND"));
            } else if (!client.isActive()) {
                issues.add(issue("clientId", "El cliente seleccionado está inactivo.", "CLIENT_INACTIVE"));
            } else {
                receiverPostalCode = resolveReceiverPostalCode(client);
                if (receiverPostalCode == null) {
                    issues.add(issue("clientId", "El cliente no tiene código postal fiscal configurado.", "CLIENT_POSTAL_CODE_REQUIRED"));
                }
            }
        }

        List<InvoiceLineRequest> requestedLines = request.lines() == null ? List.of() : request.lines();
        if (requestedLines.isEmpty()) {
            issues.add(issue("lines", "Agrega al menos un concepto.", "LINES_REQUIRED"));
        }

        String series = normalizeBlank(request.series());
        if (series == null) {
            series = DEFAULT_SERIES;
        }

        String currencyCode = normalizeBlank(request.currencyCode());
        String paymentMethodCode = defaultIfBlank(request.paymentMethodCode(), client == null ? null : client.getDefaultMetodoPagoCode(), DEFAULT_PAYMENT_METHOD);
        String paymentFormCode = defaultIfBlank(request.paymentFormCode(), client == null ? null : client.getDefaultFormaPagoCode(), DEFAULT_PAYMENT_FORM);
        String usoCfdiCode = defaultIfBlank(request.usoCfdiCode(), client == null ? null : client.getDefaultUsoCfdiCode(), DEFAULT_USO_CFDI);

        List<ComputedInvoiceLine> computedLines = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal transferredTaxTotal = BigDecimal.ZERO;
        BigDecimal withheldTaxTotal = BigDecimal.ZERO;

        for (int index = 0; index < requestedLines.size(); index++) {
            InvoiceLineRequest lineRequest = requestedLines.get(index);
            String fieldBase = "lines[" + index + "]";

            if (lineRequest.productId() == null) {
                issues.add(issue(fieldBase + ".productId", "Selecciona un producto o servicio.", "PRODUCT_REQUIRED"));
                continue;
            }

            Product product = productRepository.findByCompanyIdAndIdAndDeletedAtIsNull(companyId, lineRequest.productId()).orElse(null);
            if (product == null) {
                issues.add(issue(fieldBase + ".productId", "El producto seleccionado no existe.", "PRODUCT_NOT_FOUND"));
                continue;
            }
            if (!product.isActive()) {
                issues.add(issue(fieldBase + ".productId", "El producto seleccionado está inactivo.", "PRODUCT_INACTIVE"));
                continue;
            }

            if (currencyCode == null) {
                currencyCode = defaultIfBlank(product.getCurrencyCode(), DEFAULT_CURRENCY);
            } else if (product.getCurrencyCode() != null && !currencyCode.equalsIgnoreCase(product.getCurrencyCode())) {
                issues.add(issue(fieldBase + ".productId", "Todos los productos deben coincidir con la moneda de la factura.", "CURRENCY_MISMATCH"));
                continue;
            }

            String description = defaultIfBlank(lineRequest.description(), product.getDescription(), product.getInternalName());
            if (description == null) {
                issues.add(issue(fieldBase + ".description", "La línea necesita una descripción.", "DESCRIPTION_REQUIRED"));
                continue;
            }

            BigDecimal quantity = lineRequest.quantity();
            if (quantity == null || quantity.signum() <= 0) {
                issues.add(issue(fieldBase + ".quantity", "La cantidad debe ser mayor a cero.", "INVALID_QUANTITY"));
                continue;
            }

            BigDecimal unitPrice = lineRequest.unitPrice() != null ? lineRequest.unitPrice() : product.getUnitPrice();
            if (unitPrice == null || unitPrice.signum() <= 0) {
                issues.add(issue(fieldBase + ".unitPrice", "El precio unitario debe ser mayor a cero.", "INVALID_UNIT_PRICE"));
                continue;
            }

            BigDecimal lineAmount = quantity.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);
            BigDecimal lineDiscount = defaultZero(lineRequest.discount()).setScale(2, RoundingMode.HALF_UP);
            if (lineDiscount.signum() < 0) {
                issues.add(issue(fieldBase + ".discount", "El descuento no puede ser negativo.", "INVALID_DISCOUNT"));
                continue;
            }
            if (lineDiscount.compareTo(lineAmount) > 0) {
                issues.add(issue(fieldBase + ".discount", "El descuento no puede ser mayor al importe del concepto.", "DISCOUNT_EXCEEDS_AMOUNT"));
                continue;
            }

            BigDecimal taxableBase = lineAmount.subtract(lineDiscount).setScale(2, RoundingMode.HALF_UP);
            List<TaxCalculationResult> taxes = calculateTaxes(product, taxableBase);
            computedLines.add(new ComputedInvoiceLine(
                index + 1,
                product.getId(),
                description,
                product.getSatProductCode(),
                product.getSatUnitCode(),
                null,
                quantity,
                unitPrice,
                lineDiscount,
                lineAmount,
                defaultIfBlank(product.getObjetoImpCode(), "02"),
                defaultIfBlank(product.getCurrencyCode(), DEFAULT_CURRENCY),
                taxes
            ));

            subtotal = subtotal.add(lineAmount);
            discount = discount.add(lineDiscount);
            transferredTaxTotal = transferredTaxTotal.add(sumTaxes(taxes, true));
            withheldTaxTotal = withheldTaxTotal.add(sumTaxes(taxes, false));
        }

        String normalizedCurrency = defaultIfBlank(currencyCode, DEFAULT_CURRENCY);
        String folio = normalizeBlank(request.folio());
        if (folio == null) {
            folio = nextFolio(companyId, series);
        }

        return new InvoiceComputationResult(
            company,
            client,
            receiverPostalCode,
            series,
            folio,
            paymentMethodCode,
            paymentFormCode,
            usoCfdiCode,
            normalizedCurrency,
            scaleMoney(subtotal),
            scaleMoney(discount),
            scaleMoney(transferredTaxTotal),
            scaleMoney(withheldTaxTotal),
            scaleMoney(subtotal.subtract(discount).add(transferredTaxTotal).subtract(withheldTaxTotal)),
            computedLines,
            issues
        );
    }

    private List<InvoiceValidationIssue> validatePersistedInvoice(Invoice invoice, List<InvoiceLine> lines, List<TaxLine> taxLines) {
        List<InvoiceValidationIssue> issues = new ArrayList<>();

        if (invoice.getClientId() == null) {
            issues.add(issue("clientId", "La factura no tiene cliente asociado.", "CLIENT_REQUIRED"));
        } else {
            Client client = clientRepository.findByCompanyIdAndIdAndDeletedAtIsNull(invoice.getCompanyId(), invoice.getClientId()).orElse(null);
            if (client == null) {
                issues.add(issue("clientId", "El cliente asociado ya no existe.", "CLIENT_NOT_FOUND"));
            }
        }

        if (normalizeBlank(invoice.getIssuerRfc()) == null) {
            issues.add(issue("issuerRfc", "Falta el RFC del emisor.", "ISSUER_RFC_REQUIRED"));
        }
        if (normalizeBlank(invoice.getReceiverRfc()) == null) {
            issues.add(issue("receiverRfc", "Falta el RFC del receptor.", "RECEIVER_RFC_REQUIRED"));
        }
        if (normalizeBlank(invoice.getReceiverPostalCode()) == null) {
            issues.add(issue("receiverPostalCode", "Falta el código postal fiscal del receptor.", "RECEIVER_POSTAL_CODE_REQUIRED"));
        }
        if (lines.isEmpty()) {
            issues.add(issue("lines", "La factura debe tener al menos un concepto.", "LINES_REQUIRED"));
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal discount = BigDecimal.ZERO;
        for (int index = 0; index < lines.size(); index++) {
            InvoiceLine line = lines.get(index);
            String fieldBase = "lines[" + index + "]";
            if (line.getProductId() == null) {
                issues.add(issue(fieldBase + ".productId", "El concepto no tiene producto asociado.", "PRODUCT_REQUIRED"));
            } else if (productRepository.findByCompanyIdAndIdAndDeletedAtIsNull(invoice.getCompanyId(), line.getProductId()).isEmpty()) {
                issues.add(issue(fieldBase + ".productId", "El producto asociado ya no existe.", "PRODUCT_NOT_FOUND"));
            }
            if (line.getQuantity() == null || line.getQuantity().signum() <= 0) {
                issues.add(issue(fieldBase + ".quantity", "La cantidad debe ser mayor a cero.", "INVALID_QUANTITY"));
            }
            if (line.getUnitPrice() == null || line.getUnitPrice().signum() <= 0) {
                issues.add(issue(fieldBase + ".unitPrice", "El precio unitario debe ser mayor a cero.", "INVALID_UNIT_PRICE"));
            }
            if (line.getDiscount() != null && line.getDiscount().signum() < 0) {
                issues.add(issue(fieldBase + ".discount", "El descuento no puede ser negativo.", "INVALID_DISCOUNT"));
            }
            subtotal = subtotal.add(defaultZero(line.getSubtotal()));
            discount = discount.add(defaultZero(line.getDiscount()));
        }

        BigDecimal transferredTaxTotal = taxLines.stream()
            .filter(TaxLine::isTransfer)
            .map(TaxLine::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withheldTaxTotal = taxLines.stream()
            .filter(TaxLine::isWithholding)
            .map(TaxLine::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal expectedTotal = subtotal.subtract(discount).add(transferredTaxTotal).subtract(withheldTaxTotal);

        if (invoice.getSubtotal().compareTo(scaleMoney(subtotal)) != 0) {
            issues.add(issue("subtotal", "El subtotal almacenado no coincide con los conceptos.", "SUBTOTAL_MISMATCH"));
        }
        if (invoice.getDiscount().compareTo(scaleMoney(discount)) != 0) {
            issues.add(issue("discount", "El descuento almacenado no coincide con los conceptos.", "DISCOUNT_MISMATCH"));
        }
        if (invoice.getTransferredTaxTotal().compareTo(scaleMoney(transferredTaxTotal)) != 0) {
            issues.add(issue("transferredTaxTotal", "Los impuestos trasladados no coinciden.", "TRANSFERRED_TAX_MISMATCH"));
        }
        if (invoice.getWithheldTaxTotal().compareTo(scaleMoney(withheldTaxTotal)) != 0) {
            issues.add(issue("withheldTaxTotal", "Los impuestos retenidos no coinciden.", "WITHHELD_TAX_MISMATCH"));
        }
        if (invoice.getTotal().compareTo(scaleMoney(expectedTotal)) != 0) {
            issues.add(issue("total", "El total almacenado no coincide con el cálculo actual.", "TOTAL_MISMATCH"));
        }

        return issues;
    }

    private List<TaxCalculationResult> calculateTaxes(Product product, BigDecimal lineSubtotal) {
        if (!"02".equals(defaultIfBlank(product.getObjetoImpCode(), "02"))) {
            return List.of();
        }

        List<TaxRule> rules = productTaxProfileRepository.findByProductIdAndActiveTrue(product.getId()).stream()
            .sorted(Comparator.comparing(ProductTaxProfile::getCreatedAt))
            .map(this::toTaxRule)
            .toList();
        if (rules.isEmpty()) {
            return List.of();
        }
        return taxCalculationService.calculateLineTaxes(lineSubtotal, rules);
    }

    private TaxRule toTaxRule(ProductTaxProfile profile) {
        TaxRule rule = new TaxRule();
        rule.setId(profile.getId());
        rule.setCompanyId(profile.getCompanyId());
        rule.setRuleName("product-profile-" + profile.getId());
        rule.setTaxCode(profile.getTaxCode());
        rule.setFactorType(profile.getFactorType());
        rule.setRate(profile.getRate());
        rule.setTransfer(profile.isTransfer());
        rule.setWithholding(profile.isWithholding());
        rule.setValidFrom(profile.getValidFrom() == null ? LocalDate.now() : profile.getValidFrom());
        rule.setValidTo(profile.getValidTo());
        rule.setActive(profile.isActive());
        return rule;
    }

    private String resolveReceiverPostalCode(Client client) {
        String postalCode = normalizeBlank(client.getDefaultPostalCode());
        if (postalCode != null) {
            return postalCode;
        }

        return addressRepository.findByPartyTypeAndPartyIdAndDeletedAtIsNull("CLIENT", client.getId()).stream()
            .filter(address -> normalizeBlank(address.getPostalCode()) != null)
            .sorted(Comparator
                .comparing(Address::isPrimary).reversed()
                .thenComparing(address -> "FISCAL".equalsIgnoreCase(address.getAddressType()) ? 0 : 1))
            .map(Address::getPostalCode)
            .findFirst()
            .map(this::normalizeBlank)
            .orElse(null);
    }

    private String nextFolio(UUID companyId, String series) {
        int maxNumericFolio = invoiceRepository.findByCompanyId(companyId).stream()
            .filter(invoice -> series.equalsIgnoreCase(defaultIfBlank(invoice.getSeries(), DEFAULT_SERIES)))
            .map(Invoice::getFolio)
            .map(this::normalizeBlank)
            .filter(folio -> folio != null && folio.chars().allMatch(Character::isDigit))
            .mapToInt(Integer::parseInt)
            .max()
            .orElse(0);
        return Integer.toString(maxNumericFolio + 1);
    }

    private BigDecimal sumTaxes(List<TaxCalculationResult> taxes, boolean transfer) {
        return taxes.stream()
            .filter(tax -> transfer ? tax.isTransfer() : tax.isWithholding())
            .map(TaxCalculationResult::taxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, Object> buildTaxProfileSnapshot(List<TaxCalculationResult> taxes) {
        List<Map<String, Object>> values = taxes.stream()
            .map(tax -> {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("taxCode", tax.taxCode());
                row.put("factorType", tax.factorType());
                row.put("rate", tax.rate());
                row.put("baseAmount", tax.baseAmount());
                row.put("taxAmount", tax.taxAmount());
                row.put("isTransfer", tax.isTransfer());
                row.put("isWithholding", tax.isWithholding());
                return row;
            })
            .toList();
        return Map.of("taxes", values);
    }

    private boolean canBuildPreview(InvoiceComputationResult computation) {
        return computation.company() != null && computation.client() != null && computation.receiverPostalCode() != null;
    }

    private void ensureValid(InvoiceComputationResult computation) {
        if (!computation.valid()) {
            throwBusinessValidation(computation.issues());
        }
    }

    private void throwBusinessValidation(List<InvoiceValidationIssue> issues) {
        throw new BusinessValidationException(issues.stream()
            .map(issue -> new BusinessValidationException.Violation(issue.fieldPath(), issue.message(), null))
            .toList());
    }

    private String buildDocumentObjectKey(Invoice invoice, String extension) {
        return "invoices/" + invoice.getId() + "/" + defaultIfBlank(invoice.getSeries(), DEFAULT_SERIES) + "-"
            + defaultIfBlank(invoice.getFolio(), invoice.getId().toString()) + "." + extension;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Could not serialize invoice snapshot", exception);
        }
    }

    private String periodKey(Instant issuedAt) {
        YearMonth period = YearMonth.from((issuedAt == null ? Instant.now() : issuedAt).atZone(ZoneOffset.UTC));
        return "%d-%02d".formatted(period.getYear(), period.getMonthValue());
    }

    private UUID parseUuid(String value) {
        if (value == null) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return defaultZero(value).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal defaultZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String normalizeStatus(String status) {
        String normalized = normalizeBlank(status);
        if (normalized == null || "ALL".equalsIgnoreCase(normalized)) {
            return null;
        }
        return normalized.toUpperCase();
    }

    private String defaultIfBlank(String first, String fallback) {
        String normalizedFirst = normalizeBlank(first);
        return normalizedFirst != null ? normalizedFirst : normalizeBlank(fallback);
    }

    private String defaultIfBlank(String first, String second, String fallback) {
        String value = defaultIfBlank(first, second);
        return value != null ? value : normalizeBlank(fallback);
    }

    private String normalizeBlank(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private InvoiceValidationIssue issue(String fieldPath, String message, String code) {
        return new InvoiceValidationIssue(fieldPath, message, code);
    }
}
