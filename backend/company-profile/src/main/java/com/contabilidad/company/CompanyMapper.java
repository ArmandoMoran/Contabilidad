package com.contabilidad.company;

public final class CompanyMapper {

    private CompanyMapper() {}

    public static CompanyDto toDto(Company c) {
        return new CompanyDto(
            c.getId(),
            c.getRfc(),
            c.getLegalName(),
            c.getTaxpayerType(),
            c.getFiscalRegimeCode(),
            c.getTaxZoneProfile(),
            c.getPostalCode(),
            c.getLogoUrl(),
            c.isActive(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }

    public static Company toEntity(CreateCompanyRequest r) {
        Company c = new Company();
        c.setRfc(r.rfc());
        c.setLegalName(r.legalName());
        c.setTaxpayerType(r.taxpayerType());
        c.setFiscalRegimeCode(r.fiscalRegimeCode());
        if (r.taxZoneProfile() != null) c.setTaxZoneProfile(r.taxZoneProfile());
        c.setPostalCode(r.postalCode());
        c.setLogoUrl(r.logoUrl());
        return c;
    }

    public static void patch(Company c, UpdateCompanyRequest r) {
        if (r.legalName() != null) c.setLegalName(r.legalName());
        if (r.taxpayerType() != null) c.setTaxpayerType(r.taxpayerType());
        if (r.fiscalRegimeCode() != null) c.setFiscalRegimeCode(r.fiscalRegimeCode());
        if (r.taxZoneProfile() != null) c.setTaxZoneProfile(r.taxZoneProfile());
        if (r.postalCode() != null) c.setPostalCode(r.postalCode());
        if (r.logoUrl() != null) c.setLogoUrl(r.logoUrl());
        if (r.active() != null) c.setActive(r.active());
    }
}
