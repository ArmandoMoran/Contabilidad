package com.contabilidad.parties;

import com.contabilidad.shared.PageResponse;
import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public PageResponse<SupplierDto> list(Pageable pageable) {
        var companyId = SecurityContextUtils.currentCompanyId();
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
    public SupplierDto create(@Valid @RequestBody CreateSupplierRequest request) {
        Supplier supplier = supplierService.createSupplier(SecurityContextUtils.currentCompanyId(), request);
        return SupplierMapper.toDto(supplier);
    }

    @GetMapping("/{id}")
    public SupplierDto get(@PathVariable java.util.UUID id) {
        Supplier supplier = supplierService.getSupplier(SecurityContextUtils.currentCompanyId(), id);
        return SupplierMapper.toDto(supplier);
    }

    @PatchMapping("/{id}")
    public SupplierDto update(
            @PathVariable java.util.UUID id,
            @Valid @RequestBody UpdateSupplierRequest request) {
        Supplier supplier = supplierService.updateSupplier(SecurityContextUtils.currentCompanyId(), id, request);
        return SupplierMapper.toDto(supplier);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable java.util.UUID id) {
        supplierService.deleteSupplier(SecurityContextUtils.currentCompanyId(), id, SecurityContextUtils.currentUserId());
    }
}
