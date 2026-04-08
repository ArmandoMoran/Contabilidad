package com.contabilidad.invoicing;

import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class InvoiceDocumentRenderer {

    private static final DateTimeFormatter XML_DATE_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    public byte[] renderXml(InvoiceDetailDto invoice) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<cfdi:Comprobante");
        xml.append(" xmlns:cfdi=\"http://www.sat.gob.mx/cfd/4\"");
        xml.append(" Version=\"").append(escapeXml(invoice.cfdiVersion())).append("\"");
        xml.append(" Serie=\"").append(escapeXml(nullToEmpty(invoice.series()))).append("\"");
        xml.append(" Folio=\"").append(escapeXml(nullToEmpty(invoice.folio()))).append("\"");
        if (invoice.issuedAt() != null) {
            xml.append(" Fecha=\"").append(XML_DATE_FORMAT.format(invoice.issuedAt().atOffset(ZoneOffset.UTC))).append("\"");
        }
        xml.append(" Moneda=\"").append(escapeXml(invoice.currencyCode())).append("\"");
        xml.append(" MetodoPago=\"").append(escapeXml(nullToEmpty(invoice.paymentMethodCode()))).append("\"");
        xml.append(" FormaPago=\"").append(escapeXml(nullToEmpty(invoice.paymentFormCode()))).append("\"");
        xml.append(" SubTotal=\"").append(invoice.subtotal()).append("\"");
        xml.append(" Descuento=\"").append(invoice.discount()).append("\"");
        xml.append(" Total=\"").append(invoice.total()).append("\"");
        xml.append(" TipoDeComprobante=\"").append(escapeXml(invoice.invoiceType())).append("\"");
        xml.append(" Exportacion=\"").append(escapeXml(invoice.exportCode())).append("\"");
        if (invoice.pacUuid() != null) {
            xml.append(" UUID=\"").append(invoice.pacUuid()).append("\"");
        }
        xml.append(">\n");
        xml.append("  <cfdi:Emisor Rfc=\"").append(escapeXml(invoice.issuerRfc())).append("\" Nombre=\"")
            .append(escapeXml(invoice.issuerName())).append("\" RegimenFiscal=\"")
            .append(escapeXml(invoice.issuerRegimeCode())).append("\" />\n");
        xml.append("  <cfdi:Receptor Rfc=\"").append(escapeXml(invoice.receiverRfc())).append("\" Nombre=\"")
            .append(escapeXml(invoice.receiverName())).append("\" RegimenFiscalReceptor=\"")
            .append(escapeXml(invoice.receiverRegimeCode())).append("\" DomicilioFiscalReceptor=\"")
            .append(escapeXml(invoice.receiverPostalCode())).append("\" UsoCFDI=\"")
            .append(escapeXml(invoice.usoCfdiCode())).append("\" />\n");
        xml.append("  <cfdi:Conceptos>\n");
        for (InvoiceLineDto line : invoice.lines()) {
            xml.append("    <cfdi:Concepto ClaveProdServ=\"").append(escapeXml(line.satProductCode()))
                .append("\" Cantidad=\"").append(line.quantity())
                .append("\" ClaveUnidad=\"").append(escapeXml(line.satUnitCode()))
                .append("\" Descripcion=\"").append(escapeXml(line.description()))
                .append("\" ValorUnitario=\"").append(line.unitPrice())
                .append("\" Importe=\"").append(line.subtotal())
                .append("\" ObjetoImp=\"").append(escapeXml(line.objetoImpCode()))
                .append("\" />\n");
        }
        xml.append("  </cfdi:Conceptos>\n");
        if (!invoice.taxLines().isEmpty()) {
            xml.append("  <cfdi:Impuestos TotalImpuestosTrasladados=\"").append(invoice.transferredTaxTotal())
                .append("\" TotalImpuestosRetenidos=\"").append(invoice.withheldTaxTotal()).append("\">\n");
            for (TaxLineDto tax : invoice.taxLines()) {
                xml.append("    <cfdi:")
                    .append(tax.isTransfer() ? "Traslado" : "Retencion")
                    .append(" Impuesto=\"").append(escapeXml(tax.taxCode()))
                    .append("\" TipoFactor=\"").append(escapeXml(tax.factorType()))
                    .append("\" TasaOCuota=\"").append(tax.rate())
                    .append("\" Base=\"").append(tax.baseAmount())
                    .append("\" Importe=\"").append(tax.taxAmount())
                    .append("\" />\n");
            }
            xml.append("  </cfdi:Impuestos>\n");
        }
        xml.append("</cfdi:Comprobante>\n");
        return xml.toString().getBytes(StandardCharsets.UTF_8);
    }

    public byte[] renderPdf(InvoiceDetailDto invoice) {
        List<String> lines = new ArrayList<>();
        lines.add("Factura " + nullSafeJoin(invoice.series(), invoice.folio()));
        lines.add("Estado: " + invoice.status());
        lines.add("UUID: " + nullToEmpty(invoice.pacUuid() == null ? null : invoice.pacUuid().toString()));
        lines.add("Emisor: " + invoice.issuerName() + " (" + invoice.issuerRfc() + ")");
        lines.add("Receptor: " + invoice.receiverName() + " (" + invoice.receiverRfc() + ")");
        lines.add("Metodo de pago: " + nullToEmpty(invoice.paymentMethodCode()));
        lines.add("Forma de pago: " + nullToEmpty(invoice.paymentFormCode()));
        lines.add("Uso CFDI: " + nullToEmpty(invoice.usoCfdiCode()));
        lines.add(" ");
        lines.add("Conceptos:");
        for (InvoiceLineDto line : invoice.lines()) {
            lines.add(String.format("%s | Cant. %s | P.U. %s | Subtotal %s",
                safeText(line.description()),
                line.quantity().stripTrailingZeros().toPlainString(),
                line.unitPrice().stripTrailingZeros().toPlainString(),
                line.subtotal().stripTrailingZeros().toPlainString()));
        }
        lines.add(" ");
        lines.add("Subtotal: " + invoice.subtotal());
        lines.add("Impuestos trasladados: " + invoice.transferredTaxTotal());
        lines.add("Impuestos retenidos: " + invoice.withheldTaxTotal());
        lines.add("Total: " + invoice.total() + " " + invoice.currencyCode());
        return buildPdf(lines);
    }

    private byte[] buildPdf(List<String> lines) {
        StringBuilder content = new StringBuilder();
        content.append("BT\n/F1 11 Tf\n50 760 Td\n");
        for (int index = 0; index < lines.size(); index++) {
            if (index > 0) {
                content.append("0 -15 Td\n");
            }
            content.append("(").append(escapePdf(lines.get(index))).append(") Tj\n");
        }
        content.append("ET");

        byte[] contentBytes = content.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();

        write(out, "%PDF-1.4\n");
        offsets.add(out.size());
        write(out, "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n");
        offsets.add(out.size());
        write(out, "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n");
        offsets.add(out.size());
        write(out, "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj\n");
        offsets.add(out.size());
        write(out, "4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n");
        offsets.add(out.size());
        write(out, "5 0 obj << /Length " + contentBytes.length + " >> stream\n");
        out.writeBytes(contentBytes);
        write(out, "\nendstream\nendobj\n");

        int xrefStart = out.size();
        write(out, "xref\n0 6\n0000000000 65535 f \n");
        for (int offset : offsets) {
            write(out, String.format("%010d 00000 n \n", offset));
        }
        write(out, "trailer << /Size 6 /Root 1 0 R >>\n");
        write(out, "startxref\n" + xrefStart + "\n%%EOF");
        return out.toByteArray();
    }

    private void write(ByteArrayOutputStream out, String value) {
        out.writeBytes(value.getBytes(StandardCharsets.UTF_8));
    }

    private String escapeXml(String value) {
        return safeText(value)
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private String escapePdf(String value) {
        return safeText(value)
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)")
            .replace("\r", " ")
            .replace("\n", " ");
    }

    private String safeText(String value) {
        return value == null ? "" : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String nullSafeJoin(String left, String right) {
        return nullToEmpty(left) + nullToEmpty(right);
    }
}
