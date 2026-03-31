package com.contabilidad.parties;

import com.contabilidad.shared.RfcValidator;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class SupplierService {

    private static final Logger log = LoggerFactory.getLogger(SupplierService.class);

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public Page<Supplier> listSuppliers(UUID companyId, Pageable pageable) {
        log.info("Listing suppliers for companyId={}, page={}", companyId, pageable);
        Page<Supplier> page = supplierRepository.findByCompanyIdAndDeletedAtIsNull(companyId, pageable);
        log.debug("Found {} suppliers (total={})", page.getNumberOfElements(), page.getTotalElements());
        return page;
    }

    @Transactional(readOnly = true)
    public Supplier getSupplier(UUID companyId, UUID supplierId) {
        log.info("Getting supplier companyId={}, supplierId={}", companyId, supplierId);
        return supplierRepository.findByCompanyIdAndIdAndDeletedAtIsNull(companyId, supplierId)
            .orElseThrow(() -> {
                log.warn("Supplier not found: companyId={}, supplierId={}", companyId, supplierId);
                return new EntityNotFoundException("Supplier not found: " + supplierId);
            });
    }

    public Supplier createSupplier(UUID companyId, CreateSupplierRequest request) {
        log.info("Creating supplier companyId={}, rfc={}", companyId, request.rfc());
        if (!RfcValidator.isValid(request.rfc())) {
            log.warn("Invalid RFC: {}", request.rfc());
            throw new IllegalArgumentException("Invalid RFC: " + request.rfc());
        }
        if (supplierRepository.existsByCompanyIdAndRfc(companyId, request.rfc().toUpperCase().trim())) {
            log.warn("Duplicate RFC: {}", request.rfc());
            throw new DuplicateRfcException(request.rfc());
        }
        Supplier supplier = SupplierMapper.toEntity(companyId, request);
        supplier = supplierRepository.save(supplier);
        log.info("Created supplier id={}", supplier.getId());
        return supplier;
    }

    public Supplier updateSupplier(UUID companyId, UUID supplierId, UpdateSupplierRequest request) {
        Supplier supplier = getSupplier(companyId, supplierId);

        if (request.rfc() != null) {
            if (!RfcValidator.isValid(request.rfc())) {
                throw new IllegalArgumentException("Invalid RFC: " + request.rfc());
            }
            String newRfc = request.rfc().toUpperCase().trim();
            if (!newRfc.equals(supplier.getRfc()) && supplierRepository.existsByCompanyIdAndRfc(companyId, newRfc)) {
                throw new DuplicateRfcException(request.rfc());
            }
            supplier.setRfc(newRfc);
        }
        if (request.legalName() != null) supplier.setLegalName(request.legalName());
        if (request.tradeName() != null) supplier.setTradeName(request.tradeName());
        if (request.email() != null) supplier.setEmail(request.email());
        if (request.phone() != null) supplier.setPhone(request.phone());
        if (request.website() != null) supplier.setWebsite(request.website());
        if (request.fiscalRegimeCode() != null) supplier.setFiscalRegimeCode(request.fiscalRegimeCode());
        if (request.defaultFormaPagoCode() != null) supplier.setDefaultFormaPagoCode(request.defaultFormaPagoCode());
        if (request.nationality() != null) supplier.setNationality(request.nationality());
        if (request.diotOperationType() != null) supplier.setDiotOperationType(request.diotOperationType());
        if (request.notes() != null) supplier.setNotes(request.notes());
        if (request.active() != null) supplier.setActive(request.active());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(UUID companyId, UUID supplierId, UUID userId) {
        Supplier supplier = getSupplier(companyId, supplierId);
        supplier.softDelete(userId);
        supplierRepository.save(supplier);
    }
}
