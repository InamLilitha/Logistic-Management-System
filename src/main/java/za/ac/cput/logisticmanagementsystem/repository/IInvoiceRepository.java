package za.ac.cput.logisticmanagementsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * IInvoiceRepository.java
 * Repository interface for Invoice entity
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026 / Updated: November 2026
 */

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, String> {

    List<Invoice> findByShipmentId(String shipmentId);

    Optional<Invoice> findTopByShipmentIdOrderByDateIssuedDesc(String shipmentId);

    List<Invoice> findByCompanyId(String companyId);

    List<Invoice> findByCompanyIdAndPaymentStatus(String companyId, String paymentStatus);

    List<Invoice> findByPaymentStatus(String paymentStatus);

    List<Invoice> findByDateIssuedBetween(Date from, Date to);

    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :cutoff AND i.paymentStatus <> 'Paid'")
    List<Invoice> findOverdueInvoices(@Param("cutoff") Date cutoff);

    boolean existsByShipmentId(String shipmentId);
}
