package za.ac.cput.logisticmanagementsystem.factory;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import java.util.Date;
import java.util.UUID;

/**
 * InvoiceFactory.java
 * Factory for Invoice entity using internal validation
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

public class InvoiceFactory {

    public static Invoice buildInvoice(double total, String paymentStatus, Date dateIssued) {

        if (paymentStatus == null || paymentStatus.trim().isEmpty() ||
                dateIssued == null || total < 0) {
            return null;
        }

        return new Invoice.Builder()
                .invoiceId(String.valueOf(UUID.randomUUID()))
                .total(total)
                .paymentStatus(paymentStatus)
                .dateIssued(dateIssued)
                .build();
    }
}
