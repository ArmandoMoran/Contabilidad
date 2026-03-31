package com.contabilidad.parties;

import java.util.UUID;

public final class ClientMapper {

    private ClientMapper() {}

    public static ClientDto toDto(Client c) {
        return new ClientDto(
            c.getId(),
            c.getCompanyId(),
            c.getRfc(),
            c.getLegalName(),
            c.getTradeName(),
            c.getEmail(),
            c.getPhone(),
            c.getWebsite(),
            c.getFiscalRegimeCode(),
            c.getDefaultUsoCfdiCode(),
            c.getDefaultFormaPagoCode(),
            c.getDefaultMetodoPagoCode(),
            c.getDefaultPostalCode(),
            c.isActive(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }

    public static Client toEntity(UUID companyId, CreateClientRequest r) {
        Client c = new Client();
        c.setCompanyId(companyId);
        c.setRfc(r.rfc().toUpperCase().trim());
        c.setLegalName(r.legalName());
        c.setTradeName(r.tradeName());
        c.setEmail(r.email());
        c.setPhone(r.phone());
        c.setWebsite(r.website());
        c.setFiscalRegimeCode(r.fiscalRegimeCode());
        c.setDefaultUsoCfdiCode(r.defaultUsoCfdiCode());
        c.setDefaultFormaPagoCode(r.defaultFormaPagoCode());
        c.setDefaultMetodoPagoCode(r.defaultMetodoPagoCode());
        c.setDefaultPostalCode(r.defaultPostalCode());
        c.setNotes(r.notes());
        return c;
    }
}
