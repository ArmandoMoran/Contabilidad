package com.contabilidad.company;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/{id}")
    public CompanyDto get(@PathVariable UUID id) {
        return CompanyMapper.toDto(companyService.getCompany(id));
    }

    @PatchMapping("/{id}")
    public CompanyDto update(@PathVariable UUID id, @Valid @RequestBody UpdateCompanyRequest request) {
        return CompanyMapper.toDto(companyService.updateCompany(id, request));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompanyDto create(@Valid @RequestBody CreateCompanyRequest request) {
        return CompanyMapper.toDto(companyService.createCompany(request));
    }
}
