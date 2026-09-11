/*
 * 28/07/2026
 *CompanyRepository.java
 *CompanyRepository model class in repository folder
 *Maghdie Petersen 230600204
 *  */
package za.ac.cput.logisticmanagementsystem.repository;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import java.util.HashSet;
import java.util.Set;

public class CompanyRepository implements ICompanyRepository {

    private static CompanyRepository repository = null;

    private Set<CompanyDomain> companyDB;

    private CompanyRepository(){
        companyDB = new HashSet<>();
    }

    public static CompanyRepository getRepository(){
        if (repository == null){
            repository = new CompanyRepository();
        }
        return repository;
    }

    @Override
    public CompanyDomain create(CompanyDomain companyDomain) {
        if (companyDomain == null)  return null;
        boolean success = companyDB.add(companyDomain);
        return success ? companyDomain : null;
    }

    @Override
    public CompanyDomain read(String companyId) {
        return companyDB.stream().filter(c -> c.getCompanyId().equals(companyId))
                .findFirst().orElse(null);
    }

    @Override
    public CompanyDomain update(CompanyDomain companyDomain) {
        CompanyDomain oldCompanyDomain = read(companyDomain.getCompanyId());
        if (oldCompanyDomain != null){
            companyDB.remove(oldCompanyDomain);
            companyDB.add(companyDomain);
            return companyDomain;
        }
        return null;
    }

    @Override
    public boolean delete(String companyId) {
        CompanyDomain companyDomainToDelete = read(companyId);
        if (companyDomainToDelete == null) return false;
        return companyDB.remove(companyDomainToDelete);
    }

    @Override
    public Set<CompanyDomain> getAll() {
        return companyDB;
    }
}
