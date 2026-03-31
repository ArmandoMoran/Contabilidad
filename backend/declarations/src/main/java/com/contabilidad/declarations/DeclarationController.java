package com.contabilidad.declarations;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/declarations")
public class DeclarationController {

    private final DeclarationService declarationService;

    public DeclarationController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @PostMapping("/monthly-workpapers:generate")
    @ResponseStatus(HttpStatus.CREATED)
    public DeclarationRunDto generateMonthlyWorkpapers(
            @RequestHeader("X-Company-Id") UUID companyId,
            @Valid @RequestBody GenerateWorkpapersRequest request) {
        DeclarationRun run = declarationService.generateMonthlyWorkpapers(companyId, request);
        return toDto(run);
    }

    @PostMapping("/annual-summary:generate")
    @ResponseStatus(HttpStatus.CREATED)
    public DeclarationRunDto generateAnnualSummary(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam int year) {
        DeclarationRun run = declarationService.generateAnnualSummary(companyId, year);
        return toDto(run);
    }

    @GetMapping("/{id}")
    public DeclarationRunDto get(
            @RequestHeader("X-Company-Id") UUID companyId,
            @PathVariable UUID id) {
        return toDto(declarationService.getDeclaration(companyId, id));
    }

    @GetMapping
    public List<DeclarationRunDto> list(
            @RequestHeader("X-Company-Id") UUID companyId,
            @RequestParam int fiscalYear) {
        return declarationService.listDeclarations(companyId, fiscalYear).stream()
            .map(this::toDto)
            .toList();
    }

    private DeclarationRunDto toDto(DeclarationRun r) {
        return new DeclarationRunDto(
            r.getId(), r.getCompanyId(), r.getTemplateId(),
            r.getDeclarationType(), r.getPeriodKey(),
            r.getFiscalYear(), r.getFiscalMonth(), r.getStatus(),
            r.getTotalIncome(), r.getTotalDeductions(),
            r.getTaxBase(), r.getTaxDetermined(),
            r.getTaxWithheld(), r.getTaxPaidPrevious(),
            r.getTaxPayable(), r.getTaxInFavor(),
            r.getFrozenAt(), r.getNotes(),
            r.getCreatedAt(), r.getUpdatedAt()
        );
    }
}
