/*
 * 28/07/2026
 *ICompanyService.java
 *ICompanyService model interface in service folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import java.util.Set;

public interface ICompanyService {

    CompanyDomain create(CompanyDomain companyDomain);
    CompanyDomain read(String companyId);
    CompanyDomain update(CompanyDomain companyDomain);
    boolean delete(String companyId);
    Set<CompanyDomain> getAll();

    CompanyDomain activateCompanyDomain(String companyId);
    CompanyDomain deactivateCompanyDomain(String companyId);
}
