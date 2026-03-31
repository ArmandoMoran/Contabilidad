package com.contabilidad.declarations;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DeclarationService {

    private final DeclarationRunRepository declarationRunRepository;

    public DeclarationService(DeclarationRunRepository declarationRunRepository) {
        this.declarationRunRepository = declarationRunRepository;
    }

    public DeclarationRun generateMonthlyWorkpapers(UUID companyId, GenerateWorkpapersRequest request) {
        String periodKey = String.format("%d-%02d", request.year(), request.month());
        DeclarationRun run = new DeclarationRun();
        run.setCompanyId(companyId);
        run.setDeclarationType("MONTHLY");
        run.setPeriodKey(periodKey);
        run.setFiscalYear(request.year());
        run.setFiscalMonth(request.month());
        run.setStatus("DRAFT");
        // TODO: compute totals from invoices/expenses for the period
        return declarationRunRepository.save(run);
    }

    public DeclarationRun generateAnnualSummary(UUID companyId, int year) {
        String periodKey = String.valueOf(year);
        DeclarationRun run = new DeclarationRun();
        run.setCompanyId(companyId);
        run.setDeclarationType("ANNUAL");
        run.setPeriodKey(periodKey);
        run.setFiscalYear(year);
        run.setStatus("DRAFT");
        // TODO: aggregate monthly data into annual summary
        return declarationRunRepository.save(run);
    }

    @Transactional(readOnly = true)
    public DeclarationRun getDeclaration(UUID companyId, UUID id) {
        DeclarationRun run = declarationRunRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Declaration not found: " + id));
        if (!run.getCompanyId().equals(companyId)) {
            throw new EntityNotFoundException("Declaration not found: " + id);
        }
        return run;
    }

    @Transactional(readOnly = true)
    public List<DeclarationRun> listDeclarations(UUID companyId, int fiscalYear) {
        return declarationRunRepository.findByCompanyIdAndFiscalYear(companyId, fiscalYear);
    }
}
