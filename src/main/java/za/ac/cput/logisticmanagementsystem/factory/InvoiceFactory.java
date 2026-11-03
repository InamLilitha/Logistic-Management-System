package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.Date;
import java.util.UUID;

/**
 * InvoiceFactory.java
 * Factory for Invoice entity using internal validation
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026 / Updated: November 2026
 */

public class InvoiceFactory {

    /**
     * Legacy factory method for backwards compatibility
     */
    public static Invoice buildInvoice(double total, String paymentStatus, Date dateIssued) {
        if (paymentStatus == null || paymentStatus.trim().isEmpty() ||
                dateIssued == null || total < 0) {
            return null;
        }

        return new Invoice.Builder()
                .invoiceId(UUID.randomUUID().toString())
                .total(total)
                .paymentStatus(paymentStatus)
                .dateIssued(dateIssued)
                .build();
    }

    /**
     * Overloaded factory method supporting shipment_id, company_id, and due date
     */
    public static Invoice buildInvoice(String shipmentId, String companyId, String customerName,
                                       String description, double total, String paymentStatus,
                                       Date dateIssued, Date dueDate) {

        if (paymentStatus == null || paymentStatus.trim().isEmpty() ||
                dateIssued == null || total < 0) {
            return null;
        }

        return new Invoice.Builder()
                .invoiceId(UUID.randomUUID().toString())
                .shipmentId(shipmentId)
                .companyId(companyId)
                .customerName(customerName)
                .description(description)
                .total(total)
                .paymentStatus(paymentStatus)
                .dateIssued(dateIssued)
                .dueDate(dueDate)
                .build();
    }

    /**
     * Overloaded factory method for shipment-linked invoice generation
     */
    public static Invoice buildInvoice(String shipmentId, String companyId, String description,
                                       double totalAmount, String paymentStatus,
                                       Date dateIssued, Date dueDate) {
        return buildInvoice(shipmentId, companyId, null, description, totalAmount, paymentStatus, dateIssued, dueDate);
    }
}
