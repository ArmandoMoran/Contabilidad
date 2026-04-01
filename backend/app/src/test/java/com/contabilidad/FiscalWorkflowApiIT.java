package com.contabilidad;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FiscalWorkflowApiIT extends AbstractApiIntegrationTest {

    @Test
    void generatesOperationalReportsAndMonthlyWorkpapersFromTransactionalData() throws Exception {
        String accessToken = loginAndGetAccessToken();
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        String periodKey = String.format("%d-%02d", today.getYear(), today.getMonthValue());

        HttpResponse<String> incomeExpenseResponse = get(
            "/reports/income-expense?from=" + today.minusDays(1) + "&to=" + today.plusDays(1),
            accessToken
        );
        assertEquals(200, incomeExpenseResponse.statusCode(), incomeExpenseResponse.body());
        assertTrue(jsonBody(incomeExpenseResponse).path("totalIncome").decimalValue().signum() > 0);
        assertTrue(jsonBody(incomeExpenseResponse).path("totalExpenses").decimalValue().signum() > 0);

        HttpResponse<String> taxResponse = get("/reports/taxes?periodKey=" + periodKey, accessToken);
        assertEquals(200, taxResponse.statusCode(), taxResponse.body());
        assertEquals(periodKey, jsonBody(taxResponse).path("periodKey").asText());
        assertFalse(jsonBody(taxResponse).path("lines").isMissingNode());

        HttpResponse<String> declarationResponse = post(
            "/declarations/monthly-workpapers:generate",
            Map.of(
                "year", today.getYear(),
                "month", today.getMonthValue()
            ),
            accessToken
        );
        assertEquals(201, declarationResponse.statusCode(), declarationResponse.body());
        assertEquals("GENERATED", jsonBody(declarationResponse).path("status").asText());
        assertTrue(jsonBody(declarationResponse).path("totalIncome").decimalValue().signum() > 0);
        assertTrue(jsonBody(declarationResponse).path("taxDetermined").decimalValue().signum() >= 0);
    }

    @Test
    void persistsCreditNotesAsEgresoDocuments() throws Exception {
        String accessToken = loginAndGetAccessToken();

        HttpResponse<String> createResponse = post("/invoices/credit-notes", Map.of(
            "clientId", "33333333-3333-7333-8333-333333333301",
            "invoiceType", "I",
            "paymentMethodCode", "PUE",
            "paymentFormCode", "03",
            "usoCfdiCode", "G03",
            "currencyCode", "MXN",
            "lines", List.of(Map.of(
                "description", "Bonificacion comercial",
                "quantity", 1,
                "unitPrice", 1000,
                "satProductCode", "84111506",
                "satUnitCode", "ACT"
            ))
        ), accessToken);

        assertEquals(201, createResponse.statusCode(), createResponse.body());
        String invoiceId = jsonBody(createResponse).path("id").asText();
        assertEquals("E", jsonBody(createResponse).path("invoiceType").asText());

        HttpResponse<String> fetchResponse = get("/invoices/" + invoiceId, accessToken);
        assertEquals(200, fetchResponse.statusCode(), fetchResponse.body());
        assertEquals("E", jsonBody(fetchResponse).path("invoiceType").asText());
    }
}
