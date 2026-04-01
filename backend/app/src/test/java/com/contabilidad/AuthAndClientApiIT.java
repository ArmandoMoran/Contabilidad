package com.contabilidad;

import org.junit.jupiter.api.Test;

import java.net.http.HttpResponse;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthAndClientApiIT extends AbstractApiIntegrationTest {

    @Test
    void rejectsUnauthenticatedAccessToClientsApi() throws Exception {
        HttpResponse<String> response = get("/clients");
        assertTrue(response.statusCode() == 401 || response.statusCode() == 403);
    }

    @Test
    void returnsAuthenticatedUserContextFromJwtClaims() throws Exception {
        HttpResponse<String> response = get("/auth/me", loginAndGetAccessToken());

        assertEquals(200, response.statusCode());
        assertEquals("admin@demo.com", jsonBody(response).path("email").asText());
        assertEquals("11111111-1111-7111-8111-111111111111", jsonBody(response).path("companyId").asText());
        assertEquals("admin", jsonBody(response).path("role").asText());
    }

    @Test
    void createsClientInsideAuthenticatedCompany() throws Exception {
        String accessToken = loginAndGetAccessToken();

        HttpResponse<String> createResponse = post("/clients", Map.of(
            "rfc", "TES010101AB1",
            "legalName", "Cliente API de Prueba S.A. de C.V.",
            "email", "api@cliente.mx",
            "fiscalRegimeCode", "601",
            "defaultUsoCfdiCode", "G03",
            "defaultFormaPagoCode", "03",
            "defaultPostalCode", "06600"
        ), accessToken);

        assertEquals(201, createResponse.statusCode(), createResponse.body());
        String createdClientId = jsonBody(createResponse).path("id").asText();
        assertNotNull(createdClientId);
        assertEquals("11111111-1111-7111-8111-111111111111", jsonBody(createResponse).path("companyId").asText());
        assertEquals("TES010101AB1", jsonBody(createResponse).path("rfc").asText());

        HttpResponse<String> fetchResponse = get("/clients/" + createdClientId, accessToken);
        assertEquals(200, fetchResponse.statusCode(), fetchResponse.body());
        assertEquals(createdClientId, jsonBody(fetchResponse).path("id").asText());
        assertEquals("Cliente API de Prueba S.A. de C.V.", jsonBody(fetchResponse).path("legalName").asText());
    }
}
