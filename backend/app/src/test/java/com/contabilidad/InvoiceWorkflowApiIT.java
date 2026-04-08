package com.contabilidad;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceWorkflowApiIT extends AbstractApiIntegrationTest {

    private static final String CLIENT_ID = "33333333-3333-7333-8333-333333333301";
    private static final String PRODUCT_ID = "55555555-5555-7555-8555-555555555501";

    @Test
    void rejectsInvalidClientAndProductReferences() throws Exception {
        String accessToken = loginAndGetAccessToken();

        HttpResponse<String> invalidClientResponse = post("/invoices/drafts", Map.of(
            "clientId", "33333333-3333-7333-8333-999999999999",
            "invoiceType", "I",
            "lines", List.of(Map.of(
                "productId", PRODUCT_ID,
                "quantity", 1,
                "unitPrice", 1000
            ))
        ), accessToken);

        assertEquals(422, invalidClientResponse.statusCode(), invalidClientResponse.body());
        assertEquals("clientId", jsonBody(invalidClientResponse).path("fieldErrors").get(0).path("field").asText());

        HttpResponse<String> invalidProductResponse = post("/invoices/drafts", Map.of(
            "clientId", CLIENT_ID,
            "invoiceType", "I",
            "lines", List.of(Map.of(
                "productId", "55555555-5555-7555-8555-999999999999",
                "quantity", 1,
                "unitPrice", 1000
            ))
        ), accessToken);

        assertEquals(422, invalidProductResponse.statusCode(), invalidProductResponse.body());
        assertEquals("lines[0].productId", jsonBody(invalidProductResponse).path("fieldErrors").get(0).path("field").asText());
    }

    @Test
    void validatesTotalsAndPersistsDraftWithConcepts() throws Exception {
        String accessToken = loginAndGetAccessToken();
        Map<String, Object> payload = Map.of(
            "clientId", CLIENT_ID,
            "invoiceType", "I",
            "paymentMethodCode", "PUE",
            "paymentFormCode", "03",
            "usoCfdiCode", "G03",
            "currencyCode", "MXN",
            "lines", List.of(Map.of(
                "productId", PRODUCT_ID,
                "quantity", 2,
                "unitPrice", 1000,
                "discount", 100
            ))
        );

        HttpResponse<String> validateResponse = post("/invoices/validate", payload, accessToken);
        assertEquals(200, validateResponse.statusCode(), validateResponse.body());
        assertTrue(jsonBody(validateResponse).path("valid").asBoolean());
        assertEquals("2000", jsonBody(validateResponse).path("preview").path("subtotal").decimalValue().stripTrailingZeros().toPlainString());
        assertEquals("2204", jsonBody(validateResponse).path("preview").path("total").decimalValue().stripTrailingZeros().toPlainString());

        HttpResponse<String> createResponse = post("/invoices/drafts", payload, accessToken);
        assertEquals(201, createResponse.statusCode(), createResponse.body());
        String invoiceId = jsonBody(createResponse).path("id").asText();

        HttpResponse<String> detailResponse = get("/invoices/" + invoiceId, accessToken);
        assertEquals(200, detailResponse.statusCode(), detailResponse.body());
        assertEquals(1, jsonBody(detailResponse).path("lines").size());
        assertEquals(PRODUCT_ID, jsonBody(detailResponse).path("lines").get(0).path("productId").asText());
        assertEquals("2000", jsonBody(detailResponse).path("subtotal").decimalValue().stripTrailingZeros().toPlainString());
    }

    @Test
    void stampsInvoicesAtomicallyAndServesDocuments() throws Exception {
        String accessToken = loginAndGetAccessToken();
        Map<String, Object> payload = Map.of(
            "clientId", CLIENT_ID,
            "invoiceType", "I",
            "paymentMethodCode", "PUE",
            "paymentFormCode", "03",
            "usoCfdiCode", "G03",
            "currencyCode", "MXN",
            "lines", List.of(Map.of(
                "productId", PRODUCT_ID,
                "quantity", 1,
                "unitPrice", 15000
            ))
        );

        HttpResponse<String> stampedResponse = post("/invoices/stamped", payload, accessToken);
        assertEquals(201, stampedResponse.statusCode(), stampedResponse.body());
        String invoiceId = jsonBody(stampedResponse).path("id").asText();
        assertEquals("STAMPED", jsonBody(stampedResponse).path("status").asText());

        HttpResponse<String> detailResponse = get("/invoices/" + invoiceId, accessToken);
        assertEquals(200, detailResponse.statusCode(), detailResponse.body());
        assertEquals("STAMPED", jsonBody(detailResponse).path("status").asText());
        assertTrue(jsonBody(detailResponse).path("pacUuid").isTextual());
        assertEquals(2, jsonBody(detailResponse).path("artifacts").size());

        HttpResponse<byte[]> xmlResponse = getBytes("/invoices/" + invoiceId + "/xml", accessToken);
        assertEquals(200, xmlResponse.statusCode());
        assertTrue(new String(xmlResponse.body()).contains("<cfdi:Comprobante"));
        assertTrue(xmlResponse.headers().firstValue("content-type").orElse("").contains("application/xml"));

        HttpResponse<byte[]> pdfResponse = getBytes("/invoices/" + invoiceId + "/pdf", accessToken);
        assertEquals(200, pdfResponse.statusCode());
        assertTrue(new String(pdfResponse.body(), 0, 8).startsWith("%PDF-1."));
        assertTrue(pdfResponse.headers().firstValue("content-type").orElse("").contains("application/pdf"));
    }
}
