package com.contabilidad.company;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public Company getCompany(UUID id) {
        return companyRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Company not found: " + id));
    }

    public Company createCompany(CreateCompanyRequest request) {
        Company company = CompanyMapper.toEntity(request);
        return companyRepository.save(company);
    }

    public Company updateCompany(UUID id, UpdateCompanyRequest request) {
        Company company = getCompany(id);
        CompanyMapper.patch(company, request);
        return companyRepository.save(company);
    }
}
