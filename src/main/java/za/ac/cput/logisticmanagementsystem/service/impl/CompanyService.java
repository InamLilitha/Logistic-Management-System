/*
 * 28/07/2026
 *CompanyService.java
 *CompanyService model class in impl folder in service folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.factory.CompanyFactory;
import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.service.ICompanyService;

import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class CompanyService implements ICompanyService {
    private final ICompanyRepository repository;
    private final IInventoryRepository inventoryRepository;

    public CompanyService(ICompanyRepository repository, IInventoryRepository inventoryRepository) {
        this.repository = repository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public CompanyDomain create(CompanyDomain companyDomain) {
        if (companyDomain == null) {
            return null;
        }
        CompanyDomain validCompany = CompanyFactory.createCompanyDomain(
                companyDomain.getCompanyName(),
                companyDomain.getTaxId(),
                companyDomain.getPhoneNumber(),
                companyDomain.getEmailAddress()
        );
        return validCompany == null ? null : repository.save(validCompany);
    }

    @Override
    public CompanyDomain read(String companyId) {
        if (companyId == null) {
            return null;
        }
        return repository.findById(companyId).orElse(null);
    }

    @Override
    public CompanyDomain update(CompanyDomain companyDomain) {
        if (companyDomain == null || companyDomain.getCompanyId() == null
                || !repository.existsById(companyDomain.getCompanyId())) {
            return null;
        }
        CompanyDomain validCompany = CompanyFactory.createCompanyDomain(
                companyDomain.getCompanyName(),
                companyDomain.getTaxId(),
                companyDomain.getPhoneNumber(),
                companyDomain.getEmailAddress()
        );
        if (validCompany == null) {
            return null;
        }
        CompanyDomain updatedCompany = new CompanyDomain.Builder()
                .copy(validCompany)
                .setCompanyId(companyDomain.getCompanyId())
                .setIsActive(companyDomain.isActive())
                .build();
        return repository.save(updatedCompany);
    }

    @Override
    public boolean delete(String companyId) {
        if (companyId == null || !repository.existsById(companyId)
                || inventoryRepository.existsByCompanyId(companyId)) {
            return false;
        }
        repository.deleteById(companyId);
        return true;
    }

    @Override
    public Set<CompanyDomain> getAll() {
        return new HashSet<>(repository.findAll());
    }

    @Override
    public CompanyDomain activateCompanyDomain(String companyId) {
        return setActive(companyId, true);
    }

    @Override
    public CompanyDomain deactivateCompanyDomain(String companyId) {
        return setActive(companyId, false);
    }

    private CompanyDomain setActive(String companyId, boolean active) {
        CompanyDomain company = read(companyId);
        if (company == null) {
            return null;
        }
        CompanyDomain updated = new CompanyDomain.Builder()
                .copy(company)
                .setIsActive(active)
                .build();
        return repository.save(updated);
    }
}
