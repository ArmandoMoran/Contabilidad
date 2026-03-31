package com.contabilidad.parties;

import com.contabilidad.shared.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public PageResponse<SupplierDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            Pageable pageable) {
        Page<Supplier> page = supplierService.listSuppliers(companyId, pageable);
        return PageResponse.of(
            page.getContent().stream().map(SupplierMapper::toDto).toList(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SupplierDto create(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody CreateSupplierRequest request) {
        Supplier supplier = supplierService.createSupplier(companyId, request);
        return SupplierMapper.toDto(supplier);
    }

    @GetMapping("/{id}")
    public SupplierDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        Supplier supplier = supplierService.getSupplier(companyId, id);
        return SupplierMapper.toDto(supplier);
    }

    @PatchMapping("/{id}")
    public SupplierDto update(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSupplierRequest request) {
        Supplier supplier = supplierService.updateSupplier(companyId, id, request);
        return SupplierMapper.toDto(supplier);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable UUID id) {
        supplierService.deleteSupplier(companyId, id, userId);
    }
}
