package com.contabilidad.invoicing;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public final class InvoiceMapper {

    private InvoiceMapper() {}

    public static InvoiceDto toSummaryDto(Invoice inv) {
        return new InvoiceDto(
            inv.getId(),
            inv.getCompanyId(),
            inv.getClientId(),
            inv.getInvoiceType(),
            inv.getStatus(),
            inv.getSeries(),
            inv.getFolio(),
            inv.getIssuedAt(),
            inv.getCurrencyCode(),
            inv.getSubtotal(),
            inv.getTransferredTaxTotal(),
            inv.getWithheldTaxTotal(),
            inv.getTotal(),
            inv.getPacUuid(),
            inv.getReceiverRfc(),
            inv.getReceiverName()
        );
    }

    public static InvoiceDetailDto toDetailDto(Invoice invoice, List<InvoiceLine> lines, List<TaxLine> taxLines) {
        List<InvoiceLineDto> lineDtos = toLineDtos(lines, taxLines);
        List<TaxLineDto> taxLineDtos = taxLines.stream()
            .map(InvoiceMapper::toTaxLineDto)
            .toList();

        return new InvoiceDetailDto(
            invoice.getId(),
            invoice.getCompanyId(),
            invoice.getClientId(),
            invoice.getInvoiceType(),
            invoice.getStatus(),
            invoice.getSeries(),
            invoice.getFolio(),
            invoice.getCfdiVersion(),
            invoice.getIssuedAt(),
            invoice.getCertifiedAt(),
            invoice.getCancelledAt(),
            invoice.getCurrencyCode(),
            invoice.getExchangeRate(),
            invoice.getPaymentMethodCode(),
            invoice.getPaymentFormCode(),
            invoice.getUsoCfdiCode(),
            invoice.getExportCode(),
            invoice.getIssuerRfc(),
            invoice.getIssuerName(),
            invoice.getIssuerRegimeCode(),
            invoice.getReceiverRfc(),
            invoice.getReceiverName(),
            invoice.getReceiverRegimeCode(),
            invoice.getReceiverPostalCode(),
            invoice.getSubtotal(),
            invoice.getDiscount(),
            invoice.getTransferredTaxTotal(),
            invoice.getWithheldTaxTotal(),
            invoice.getTotal(),
            invoice.getPacUuid(),
            invoice.getPacCertNumber(),
            invoice.getSatCertNumber(),
            invoice.getPacStatus(),
            invoice.getCancelReasonCode(),
            invoice.getCancelReplacementUuid(),
            lineDtos,
            taxLineDtos,
            buildArtifacts(invoice)
        );
    }

    public static InvoicePreviewDto toPreviewDto(InvoiceComputationResult result) {
        List<TaxLineDto> taxLineDtos = new ArrayList<>();
        List<InvoiceLineDto> lineDtos = new ArrayList<>();

        for (ComputedInvoiceLine line : result.lines()) {
            BigDecimal transferredTaxTotal = BigDecimal.ZERO;
            BigDecimal withheldTaxTotal = BigDecimal.ZERO;

            for (var tax : line.taxes()) {
                TaxLineDto taxLineDto = new TaxLineDto(
                    null,
                    "INVOICE",
                    null,
                    null,
                    tax.taxCode(),
                    tax.factorType(),
                    tax.rate(),
                    tax.baseAmount(),
                    tax.taxAmount(),
                    tax.isTransfer(),
                    tax.isWithholding(),
                    null
                );
                taxLineDtos.add(taxLineDto);
                if (tax.isTransfer()) {
                    transferredTaxTotal = transferredTaxTotal.add(tax.taxAmount());
                }
                if (tax.isWithholding()) {
                    withheldTaxTotal = withheldTaxTotal.add(tax.taxAmount());
                }
            }

            lineDtos.add(new InvoiceLineDto(
                null,
                null,
                line.lineNumber(),
                line.productId(),
                line.satProductCode(),
                line.description(),
                line.satUnitCode(),
                line.unitName(),
                line.quantity(),
                line.unitPrice(),
                line.discount(),
                line.subtotal(),
                line.objetoImpCode(),
                transferredTaxTotal,
                withheldTaxTotal,
                line.subtotal().subtract(line.discount()).add(transferredTaxTotal).subtract(withheldTaxTotal)
            ));
        }

        return new InvoicePreviewDto(
            result.series(),
            result.folio(),
            result.currencyCode(),
            result.paymentMethodCode(),
            result.paymentFormCode(),
            result.usoCfdiCode(),
            result.company().getRfc(),
            result.company().getLegalName(),
            result.company().getFiscalRegimeCode(),
            result.client().getRfc(),
            result.client().getLegalName(),
            result.client().getFiscalRegimeCode(),
            result.receiverPostalCode(),
            result.subtotal(),
            result.discount(),
            result.transferredTaxTotal(),
            result.withheldTaxTotal(),
            result.total(),
            lineDtos,
            taxLineDtos
        );
    }

    public static InvoicePreviewDto toPreviewDto(Invoice invoice, List<InvoiceLine> lines, List<TaxLine> taxLines) {
        return new InvoicePreviewDto(
            invoice.getSeries(),
            invoice.getFolio(),
            invoice.getCurrencyCode(),
            invoice.getPaymentMethodCode(),
            invoice.getPaymentFormCode(),
            invoice.getUsoCfdiCode(),
            invoice.getIssuerRfc(),
            invoice.getIssuerName(),
            invoice.getIssuerRegimeCode(),
            invoice.getReceiverRfc(),
            invoice.getReceiverName(),
            invoice.getReceiverRegimeCode(),
            invoice.getReceiverPostalCode(),
            invoice.getSubtotal(),
            invoice.getDiscount(),
            invoice.getTransferredTaxTotal(),
            invoice.getWithheldTaxTotal(),
            invoice.getTotal(),
            toLineDtos(lines, taxLines),
            taxLines.stream().map(InvoiceMapper::toTaxLineDto).toList()
        );
    }

    public static TaxLineDto toTaxLineDto(TaxLine taxLine) {
        return new TaxLineDto(
            taxLine.getId(),
            taxLine.getSourceType(),
            taxLine.getSourceId(),
            taxLine.getSourceLineId(),
            taxLine.getTaxCode(),
            taxLine.getFactorType(),
            taxLine.getRate(),
            taxLine.getBaseAmount(),
            taxLine.getTaxAmount(),
            taxLine.isTransfer(),
            taxLine.isWithholding(),
            taxLine.getPeriodKey()
        );
    }

    private static List<InvoiceLineDto> toLineDtos(List<InvoiceLine> lines, List<TaxLine> taxLines) {
        Map<UUID, List<TaxLine>> lineTaxes = taxLines.stream()
            .filter(taxLine -> taxLine.getSourceLineId() != null)
            .collect(Collectors.groupingBy(TaxLine::getSourceLineId));

        return lines.stream()
            .sorted(Comparator.comparingInt(InvoiceLine::getLineNumber))
            .map(line -> toLineDto(line, lineTaxes.getOrDefault(line.getId(), List.of())))
            .toList();
    }

    private static InvoiceLineDto toLineDto(InvoiceLine line, List<TaxLine> taxes) {
        BigDecimal transferredTaxTotal = taxes.stream()
            .filter(TaxLine::isTransfer)
            .map(TaxLine::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal withheldTaxTotal = taxes.stream()
            .filter(TaxLine::isWithholding)
            .map(TaxLine::getTaxAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new InvoiceLineDto(
            line.getId(),
            line.getInvoiceId(),
            line.getLineNumber(),
            line.getProductId(),
            line.getSatProductCode(),
            line.getDescription(),
            line.getSatUnitCode(),
            line.getUnitName(),
            line.getQuantity(),
            line.getUnitPrice(),
            line.getDiscount(),
            line.getSubtotal(),
            line.getObjetoImpCode(),
            transferredTaxTotal,
            withheldTaxTotal,
            line.getSubtotal().subtract(line.getDiscount()).add(transferredTaxTotal).subtract(withheldTaxTotal)
        );
    }

    private static List<InvoiceArtifactDto> buildArtifacts(Invoice invoice) {
        if (invoice.getId() == null) {
            return List.of();
        }

        String baseName = buildBaseName(invoice);
        String baseUrl = "/api/v1/invoices/" + invoice.getId();
        return List.of(
            new InvoiceArtifactDto("xml", baseName + ".xml", "application/xml", baseUrl + "/xml"),
            new InvoiceArtifactDto("pdf", baseName + ".pdf", "application/pdf", baseUrl + "/pdf")
        );
    }

    private static String buildBaseName(Invoice invoice) {
        StringBuilder name = new StringBuilder("factura");
        if (invoice.getSeries() != null && !invoice.getSeries().isBlank()) {
            name.append("-").append(invoice.getSeries());
        }
        if (invoice.getFolio() != null && !invoice.getFolio().isBlank()) {
            name.append("-").append(invoice.getFolio());
        } else if (invoice.getId() != null) {
            name.append("-").append(invoice.getId());
        }
        return name.toString();
    }
}
