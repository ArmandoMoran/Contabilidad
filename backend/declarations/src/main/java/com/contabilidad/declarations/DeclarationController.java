package com.contabilidad.declarations;

import com.contabilidad.shared.SecurityContextUtils;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/declarations")
public class DeclarationController {

    private final DeclarationService declarationService;

    public DeclarationController(DeclarationService declarationService) {
        this.declarationService = declarationService;
    }

    @PostMapping("/monthly-workpapers:generate")
    @ResponseStatus(HttpStatus.CREATED)
    public DeclarationRunDto generateMonthlyWorkpapers(@Valid @RequestBody GenerateWorkpapersRequest request) {
        DeclarationRun run = declarationService.generateMonthlyWorkpapers(SecurityContextUtils.currentCompanyId(), request);
        return toDto(run);
    }

    @PostMapping("/annual-summary:generate")
    @ResponseStatus(HttpStatus.CREATED)
    public DeclarationRunDto generateAnnualSummary(@RequestParam int year) {
        DeclarationRun run = declarationService.generateAnnualSummary(SecurityContextUtils.currentCompanyId(), year);
        return toDto(run);
    }

    @GetMapping("/{id}")
    public DeclarationRunDto get(@PathVariable java.util.UUID id) {
        return toDto(declarationService.getDeclaration(SecurityContextUtils.currentCompanyId(), id));
    }

    @GetMapping
    public List<DeclarationRunDto> list(@RequestParam int fiscalYear) {
        return declarationService.listDeclarations(SecurityContextUtils.currentCompanyId(), fiscalYear).stream()
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
