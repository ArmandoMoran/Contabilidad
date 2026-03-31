package com.contabilidad.expenses;

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
public class ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseService.class);

    private final ExpenseRepository expenseRepository;

    public ExpenseService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public Expense create(UUID companyId, CreateExpenseRequest request) {
        log.info("Creating expense companyId={}, type={}, total={}", companyId, request.expenseType(), request.total());
        Expense expense = ExpenseMapper.toEntity(companyId, request);
        expense = expenseRepository.save(expense);
        log.info("Created expense id={}", expense.getId());
        return expense;
    }

    public Expense importXml(UUID companyId, ImportXmlRequest request) {
        // TODO: parse XML, extract CFDI data, populate expense fields
        Expense expense = new Expense();
        expense.setCompanyId(companyId);
        expense.setExpenseType("CFDI_RECEIVED");
        expense.setXmlObjectKey(request.xmlObjectKey());
        expense.setPdfObjectKey(request.pdfObjectKey());
        return expenseRepository.save(expense);
    }

    @Transactional(readOnly = true)
    public Expense getExpense(UUID companyId, UUID expenseId) {
        log.info("Getting expense companyId={}, expenseId={}", companyId, expenseId);
        return expenseRepository.findByCompanyIdAndId(companyId, expenseId)
            .orElseThrow(() -> {
                log.warn("Expense not found: companyId={}, expenseId={}", companyId, expenseId);
                return new EntityNotFoundException("Expense not found: " + expenseId);
            });
    }

    @Transactional(readOnly = true)
    public Page<Expense> listExpenses(UUID companyId, Pageable pageable) {
        log.info("Listing all expenses for companyId={}", companyId);
        return expenseRepository.findByCompanyId(companyId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Expense> listExpensesByStatus(UUID companyId, String status, Pageable pageable) {
        log.info("Listing expenses for companyId={}, status={}", companyId, status);
        return expenseRepository.findByCompanyIdAndStatus(companyId, status, pageable);
    }
}
