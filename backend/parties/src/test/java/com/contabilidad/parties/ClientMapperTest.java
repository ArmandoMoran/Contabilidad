package com.contabilidad.parties;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ClientMapperTest {

    @Test
    void toDto_mapsAllFields() {
        Client client = new Client();
        client.setCompanyId(UUID.randomUUID());
        client.setRfc("ABC123456XY9");
        client.setLegalName("Acme SA de CV");
        client.setTradeName("Acme");
        client.setEmail("info@acme.mx");
        client.setPhone("5551234567");
        client.setWebsite("https://acme.mx");
        client.setFiscalRegimeCode("601");
        client.setDefaultUsoCfdiCode("G03");
        client.setDefaultFormaPagoCode("01");
        client.setDefaultMetodoPagoCode("PUE");
        client.setDefaultPostalCode("06600");
        client.setActive(true);

        ClientDto dto = ClientMapper.toDto(client);

        assertThat(dto.id()).isEqualTo(client.getId());
        assertThat(dto.companyId()).isEqualTo(client.getCompanyId());
        assertThat(dto.rfc()).isEqualTo("ABC123456XY9");
        assertThat(dto.legalName()).isEqualTo("Acme SA de CV");
        assertThat(dto.tradeName()).isEqualTo("Acme");
        assertThat(dto.email()).isEqualTo("info@acme.mx");
        assertThat(dto.phone()).isEqualTo("5551234567");
        assertThat(dto.website()).isEqualTo("https://acme.mx");
        assertThat(dto.fiscalRegimeCode()).isEqualTo("601");
        assertThat(dto.defaultUsoCfdiCode()).isEqualTo("G03");
        assertThat(dto.defaultFormaPagoCode()).isEqualTo("01");
        assertThat(dto.defaultMetodoPagoCode()).isEqualTo("PUE");
        assertThat(dto.defaultPostalCode()).isEqualTo("06600");
        assertThat(dto.active()).isTrue();
    }

    @Test
    void toDto_handlesNullOptionalFields() {
        Client client = new Client();
        client.setCompanyId(UUID.randomUUID());
        client.setRfc("ABC123456XY9");
        client.setLegalName("Minimal Client");
        client.setFiscalRegimeCode("601");

        ClientDto dto = ClientMapper.toDto(client);

        assertThat(dto.rfc()).isEqualTo("ABC123456XY9");
        assertThat(dto.tradeName()).isNull();
        assertThat(dto.email()).isNull();
        assertThat(dto.phone()).isNull();
        assertThat(dto.website()).isNull();
        assertThat(dto.defaultUsoCfdiCode()).isNull();
        assertThat(dto.defaultFormaPagoCode()).isNull();
        assertThat(dto.defaultMetodoPagoCode()).isNull();
        assertThat(dto.defaultPostalCode()).isNull();
    }

    @Test
    void toEntity_setsCompanyIdAndMapsFields() {
        UUID companyId = UUID.randomUUID();
        CreateClientRequest request = new CreateClientRequest(
            "abc123456xy9",  // lowercase to verify uppercase conversion
            "Test Corp",
            "Test",
            "test@test.mx",
            "5559876543",
            "https://test.mx",
            "601",
            "G03",
            "01",
            "PUE",
            "06600",
            "some notes"
        );

        Client entity = ClientMapper.toEntity(companyId, request);

        assertThat(entity.getCompanyId()).isEqualTo(companyId);
        assertThat(entity.getRfc()).isEqualTo("ABC123456XY9");
        assertThat(entity.getLegalName()).isEqualTo("Test Corp");
        assertThat(entity.getTradeName()).isEqualTo("Test");
        assertThat(entity.getEmail()).isEqualTo("test@test.mx");
        assertThat(entity.getFiscalRegimeCode()).isEqualTo("601");
        assertThat(entity.getNotes()).isEqualTo("some notes");
        assertThat(entity.getId()).isNotNull();
    }

    @Test
    void toEntity_uppercasesAndTrimsRfc() {
        UUID companyId = UUID.randomUUID();
        CreateClientRequest request = new CreateClientRequest(
            "  abc123456xy9  ",
            "Test",
            null, null, null, null,
            "601",
            null, null, null, null, null
        );

        Client entity = ClientMapper.toEntity(companyId, request);

        assertThat(entity.getRfc()).isEqualTo("ABC123456XY9");
    }

    @Test
    void toEntity_generatesUniqueId() {
        UUID companyId = UUID.randomUUID();
        CreateClientRequest request = new CreateClientRequest(
            "ABC123456XY9", "A", null, null, null, null,
            "601", null, null, null, null, null
        );

        Client a = ClientMapper.toEntity(companyId, request);
        Client b = ClientMapper.toEntity(companyId, request);

        assertThat(a.getId()).isNotEqualTo(b.getId());
    }
}