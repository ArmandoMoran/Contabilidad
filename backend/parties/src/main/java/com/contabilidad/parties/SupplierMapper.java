package com.contabilidad.parties;

import java.util.UUID;

public final class SupplierMapper {

    private SupplierMapper() {}

    public static SupplierDto toDto(Supplier s) {
        return new SupplierDto(
            s.getId(),
            s.getCompanyId(),
            s.getRfc(),
            s.getLegalName(),
            s.getTradeName(),
            s.getEmail(),
            s.getPhone(),
            s.getWebsite(),
            s.getFiscalRegimeCode(),
            s.getDefaultFormaPagoCode(),
            s.getNationality(),
            s.getDiotOperationType(),
            s.isActive(),
            s.getCreatedAt(),
            s.getUpdatedAt()
        );
    }

    public static Supplier toEntity(UUID companyId, CreateSupplierRequest r) {
        Supplier s = new Supplier();
        s.setCompanyId(companyId);
        s.setRfc(r.rfc().toUpperCase().trim());
        s.setLegalName(r.legalName());
        s.setTradeName(r.tradeName());
        s.setEmail(r.email());
        s.setPhone(r.phone());
        s.setWebsite(r.website());
        s.setFiscalRegimeCode(r.fiscalRegimeCode());
        s.setDefaultFormaPagoCode(r.defaultFormaPagoCode());
        s.setNationality(r.nationality() != null ? r.nationality() : "NATIONAL");
        s.setDiotOperationType(r.diotOperationType());
        s.setNotes(r.notes());
        return s;
    }
}
