/*
 * 28/07/2026
 *CompanyService.java
 *CompanyService model class in impl folder in service folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.stereotype.Service;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.repository.CompanyRepository;
import za.ac.cput.logisticmanagementsystem.service.ICompanyService;

import java.util.Set;

@Service
public class CompanyService implements ICompanyService{

private final CompanyRepository repository;

public CompanyService(){
    this.repository = CompanyRepository.getRepository();
}

    @Override
    public CompanyDomain create(CompanyDomain companyDomain) {
        return repository.create(companyDomain);
    }

    @Override
    public CompanyDomain read(String companyId) {
        return repository.read(companyId);
    }

    @Override
    public CompanyDomain update(CompanyDomain companyDomain) {
        return repository.update(companyDomain);
    }

    @Override
    public boolean delete(String companyId) {
        return repository.delete(companyId);
    }

    @Override
    public Set<CompanyDomain> getAll() {
        return repository.getAll();
    }

    @Override
    public CompanyDomain activateCompanyDomain(String companyId) {
        CompanyDomain companyDomain = repository.read(companyId);
        if (companyDomain != null ){
            CompanyDomain updated = new CompanyDomain.Builder().copy(companyDomain).setIsActive(true).build();
        return repository.update(updated);
        }
        return null;
    }

    @Override
    public CompanyDomain deactivateCompanyDomain(String companyId) {
        CompanyDomain companyDomain = repository.read(companyId);
        if (companyDomain != null ){
            CompanyDomain updated = new CompanyDomain.Builder().copy(companyDomain).setIsActive(false).build();
            return repository.update(updated);
        }
        return null;
    }
}
