package za.ac.cput.logisticmanagementsystem.service;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.List;

/**
 * IInvoiceService.java
 * Service interface for Invoice business logic
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

public interface IInvoiceService {

    Invoice create(Invoice invoice);
    Invoice read(String invoiceId);
    Invoice update(Invoice invoice);
    boolean delete(String invoiceId);

    List<Invoice> getAll();

    Invoice updatePaymentStatus(String invoiceId, String paymentStatus);
}

