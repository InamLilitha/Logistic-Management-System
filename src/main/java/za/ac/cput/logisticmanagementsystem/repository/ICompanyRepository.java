/*
 * 28/07/2026
 *ICompanyRepository.java
 *ICompanyRepository model interface in repository folder
 *Maghdie Petersen 230600204
 *  */

package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;

public interface ICompanyRepository extends JpaRepository<CompanyDomain, String> {
}
