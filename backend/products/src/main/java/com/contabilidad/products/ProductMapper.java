package com.contabilidad.products;

import java.util.List;
import java.util.UUID;

public final class ProductMapper {

    private ProductMapper() {}

    public static ProductDto toDto(Product p, List<ProductTaxProfile> taxProfiles) {
        return new ProductDto(
            p.getId(),
            p.getCompanyId(),
            p.getInternalCode(),
            p.getInternalName(),
            p.getDescription(),
            p.getSatProductCode(),
            p.getSatUnitCode(),
            p.getUnitPrice(),
            p.getCurrencyCode(),
            p.getObjetoImpCode(),
            p.getCuentaPredial(),
            p.isActive(),
            p.getCreatedAt(),
            p.getUpdatedAt(),
            taxProfiles.stream().map(ProductMapper::toTaxProfileDto).toList()
        );
    }

    public static ProductTaxProfileDto toTaxProfileDto(ProductTaxProfile tp) {
        return new ProductTaxProfileDto(
            tp.getId(),
            tp.getProductId(),
            tp.getTaxCode(),
            tp.getFactorType(),
            tp.getRate(),
            tp.isTransfer(),
            tp.isWithholding(),
            tp.getValidFrom(),
            tp.getValidTo(),
            tp.isActive()
        );
    }

    public static Product toEntity(UUID companyId, CreateProductRequest r) {
        Product p = new Product();
        p.setCompanyId(companyId);
        p.setInternalCode(r.internalCode());
        p.setInternalName(r.internalName());
        p.setDescription(r.description());
        p.setSatProductCode(r.satProductCode());
        p.setSatUnitCode(r.satUnitCode());
        p.setUnitPrice(r.unitPrice());
        if (r.currencyCode() != null) p.setCurrencyCode(r.currencyCode());
        if (r.objetoImpCode() != null) p.setObjetoImpCode(r.objetoImpCode());
        p.setCuentaPredial(r.cuentaPredial());
        return p;
    }

    public static ProductTaxProfile toTaxProfileEntity(UUID productId, UUID companyId, CreateTaxProfileRequest r) {
        ProductTaxProfile tp = new ProductTaxProfile();
        tp.setProductId(productId);
        tp.setCompanyId(companyId);
        tp.setTaxCode(r.taxCode());
        tp.setFactorType(r.factorType());
        tp.setRate(r.rate());
        if (r.isTransfer() != null) tp.setTransfer(r.isTransfer());
        if (r.isWithholding() != null) tp.setWithholding(r.isWithholding());
        tp.setValidFrom(r.validFrom());
        return tp;
    }
}
