package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import za.ac.cput.logisticmanagementsystem.repository.IInvoiceRepository;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InvoiceServiceTest.java
 * Service implementation for Invoice business logic tests
 * Author: Tebogo Pii 230226442
 * Date: 29 July 2026
 */

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvoiceServiceTest {

    @Mock
    private IInvoiceRepository repository;

    @InjectMocks
    private InvoiceService service;

    private static final Invoice invoice = InvoiceFactory.buildInvoice(2500.50, "Pending", new Date());

    @Test
    @Order(1)
    void create() {
        Mockito.when(repository.save(invoice)).thenReturn(invoice);
        Invoice created = service.create(invoice);
        assertNotNull(created);
        assertEquals(invoice.getInvoiceId(), created.getInvoiceId());
        System.out.println("Created: " + created);
    }

    @Test
    @Order(2)
    void read() {
        Mockito.when(repository.findById(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        Invoice read = service.read(invoice.getInvoiceId());
        assertNotNull(read);
        assertEquals(2500.50, read.getTotal());
        System.out.println("Read: " + read);
    }

    @Test
    @Order(3)
    void update() {
        Invoice updatedInvoice = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId())
                .total(3000.00)
                .paymentStatus("Paid")
                .dateIssued(invoice.getDateIssued())
                .build();
        Mockito.when(repository.save(Mockito.any(Invoice.class))).thenReturn(updatedInvoice);
        Invoice updated = service.update(updatedInvoice);
        assertNotNull(updated);
        assertEquals(3000.00, updated.getTotal());
        System.out.println("Updated: " + updated);
    }

    @Test
    @Order(4)
    void updatePaymentStatus() {
        Invoice updatedInvoice = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId())
                .total(invoice.getTotal())
                .paymentStatus("Overdue")
                .dateIssued(invoice.getDateIssued())
                .build();
        Mockito.when(repository.findById(invoice.getInvoiceId())).thenReturn(Optional.of(invoice));
        Mockito.when(repository.save(Mockito.any(Invoice.class))).thenReturn(updatedInvoice);
        
        Invoice updated = service.updatePaymentStatus(invoice.getInvoiceId(), "Overdue");
        assertNotNull(updated);
        assertEquals("Overdue", updated.getPaymentStatus());
        System.out.println("Updated Payment Status: " + updated);
    }

    @Test
    @Order(5)
    void getAll() {
        Mockito.when(repository.findAll()).thenReturn(List.of(invoice));
        assertFalse(service.getAll().isEmpty());
        System.out.println("All invoices: " + service.getAll());
    }

    @Test
    @Order(6)
    void delete() {
        Mockito.when(repository.existsById(invoice.getInvoiceId())).thenReturn(true);
        Mockito.doNothing().when(repository).deleteById(invoice.getInvoiceId());
        boolean deleted = service.delete(invoice.getInvoiceId());
        assertTrue(deleted);
        
        Mockito.when(repository.findById(invoice.getInvoiceId())).thenReturn(Optional.empty());
        assertNull(service.read(invoice.getInvoiceId()));
        System.out.println("Deleted Successfully: " + invoice.getInvoiceId());
    }

    @Test
    void testCreateWithNullInvoice() {
        Invoice created = service.create(null);
        assertNull(created);
    }

    @Test
    void testUpdatePaymentStatusWithInvalidInput() {
        Invoice testInvoice = InvoiceFactory.buildInvoice(1200.00, "Pending", new Date());
        
        Mockito.when(repository.findById("invalid-id")).thenReturn(Optional.empty());
        assertNull(service.updatePaymentStatus("invalid-id", "Paid"));
        
        Mockito.when(repository.findById(testInvoice.getInvoiceId())).thenReturn(Optional.of(testInvoice));
        assertNull(service.updatePaymentStatus(testInvoice.getInvoiceId(), null));
        assertNull(service.updatePaymentStatus(testInvoice.getInvoiceId(), ""));
    }
}
