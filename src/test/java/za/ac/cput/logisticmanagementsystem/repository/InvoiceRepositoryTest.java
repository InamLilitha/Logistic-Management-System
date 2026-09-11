package za.ac.cput.logisticmanagementsystem.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InvoiceRepositoryTest.java
 * Test class for InvoiceRepository
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class InvoiceRepositoryTest {

    @Autowired
    private IInvoiceRepository repository;
    private static Invoice invoice = InvoiceFactory.buildInvoice(1000.0, "Pending", new Date());

    @Test
    void createInvoice() {
        Invoice created = repository.save(invoice);
        assertNotNull(created);
        assertEquals(invoice.getInvoiceId(), created.getInvoiceId());
        System.out.println("Created: " + created);
    }

    @Test
    void readInvoice() {
        Invoice read = repository.findById(invoice.getInvoiceId()).orElse(null);
        assertNotNull(read);
        assertEquals(invoice.getTotal(), read.getTotal());
        System.out.println("Read: " + read);
    }

    @Test
    void updateInvoice() {
        Invoice updatedInvoice = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId()) // Use existing ID
                .total(2000.0)
                .paymentStatus("Paid")
                .dateIssued(invoice.getDateIssued())
                .build();

        Invoice updated = repository.save(updatedInvoice);
        assertNotNull(updated);
        assertEquals("Paid", updated.getPaymentStatus());
        assertEquals(2000.0, updated.getTotal());
        System.out.println("Updated: " + updated);
        invoice = updated; // Update the static invoice variable for subsequent tests
    }

    @Test
    void getAllInvoice() {
        assertFalse(repository.findAll().isEmpty());
        System.out.println("All invoices: " + repository.findAll());
    }

    @Test
    void deleteInvoice() {
        repository.deleteById(invoice.getInvoiceId());
        assertFalse(repository.existsById(invoice.getInvoiceId()));
        System.out.println("Deleted Successfully: " + invoice.getInvoiceId());
    }
}

