/*
 * 28/07/2026
 *ICompanyRepository.java
 *ICompanyRepository model interface in repository folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.repository;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import java.util.Set;

public interface ICompanyRepository extends IRepository<CompanyDomain, String>{
    Set<CompanyDomain> getAll();
}
