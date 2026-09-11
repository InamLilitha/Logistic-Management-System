package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;

/**
 * IInvoiceRepository.java
 * Repository interface for Invoice entity
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, String> {
}
