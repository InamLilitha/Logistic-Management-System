package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.Date;
import java.util.List;

/**
 * IInvoiceService.java
 * Service interface for Invoice business logic
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026 / Updated: November 2026
 */

public interface IInvoiceService {

    Invoice create(Invoice invoice);
    Invoice read(String invoiceId);
    Invoice update(Invoice invoice);
    boolean delete(String invoiceId);

    List<Invoice> getAll();
    List<Invoice> getByShipmentId(String shipmentId);
    List<Invoice> getByCompanyId(String companyId);
    List<Invoice> getByPaymentStatus(String paymentStatus);
    List<Invoice> getByDateRange(Date from, Date to);

    Invoice updatePaymentStatus(String invoiceId, String paymentStatus);
    int markOverdueInvoices();

    Invoice generateFromShipment(String shipmentId, String companyId,
                                 String description, int paymentTermDays);
}
