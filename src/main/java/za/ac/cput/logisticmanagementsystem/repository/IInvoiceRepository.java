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
 * Update: Added derived query finders for shipment, company, and payment status lookups
 */

@Repository
public interface IInvoiceRepository extends JpaRepository<Invoice, String> {

    // -------------------------------------------------------------------------
    // Shipment-based lookups
    // -------------------------------------------------------------------------

    /** Find all invoices linked to a specific shipment */
    List<Invoice> findByShipmentId(String shipmentId);

    /** Find the most recent invoice for a shipment (useful when only one is expected) */
    Optional<Invoice> findTopByShipmentIdOrderByDateIssuedDesc(String shipmentId);

    // -------------------------------------------------------------------------
    // Company-based lookups
    // -------------------------------------------------------------------------

    /** Find all invoices billed to a specific company */
    List<Invoice> findByCompanyId(String companyId);

    /** Find invoices for a company filtered by payment status */
    List<Invoice> findByCompanyIdAndPaymentStatus(String companyId, String paymentStatus);

    // -------------------------------------------------------------------------
    // Payment status lookups
    // -------------------------------------------------------------------------

    /** Find all invoices by payment status (e.g. "Unpaid", "Paid", "Overdue") */
    List<Invoice> findByPaymentStatus(String paymentStatus);

    // -------------------------------------------------------------------------
    // Date-range lookups
    // -------------------------------------------------------------------------

    /** Find all invoices issued between two dates */
    List<Invoice> findByDateIssuedBetween(Date from, Date to);

    /** Find invoices whose due date is before a given date and are not yet Paid — overdue detection */
    @Query("SELECT i FROM Invoice i WHERE i.dueDate < :cutoff AND i.paymentStatus <> 'Paid'")
    List<Invoice> findOverdueInvoices(@Param("cutoff") Date cutoff);

    // -------------------------------------------------------------------------
    // Combined lookups
    // -------------------------------------------------------------------------

    /** Check whether an invoice already exists for a given shipment */
    boolean existsByShipmentId(String shipmentId);
}
