package za.ac.cput.logisticmanagementsystem.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.Date;

/**
 * InvoiceFactoryTest.java
 * Test class for InvoiceFactory
 * Author: Tebogo Pii 230226442
 * Date: 26-27 July 2026 / Updated: November 2026
 */

public class InvoiceFactoryTest {

    @Test
    void testBuildInvoiceSuccess() {
        Date dateIssued = new Date();
        Invoice invoice = InvoiceFactory.buildInvoice(1500.00, "Paid", dateIssued);
        assertNotNull(invoice);
        assertNotNull(invoice.getInvoiceId());
        assertEquals(1500.00, invoice.getTotal());
        assertEquals("Paid", invoice.getPaymentStatus());
    }

    @Test
    void testBuildInvoiceWithShipmentAndCompanySuccess() {
        Date dateIssued = new Date();
        Date dueDate = new Date(dateIssued.getTime() + (14L * 24 * 60 * 60 * 1000));
        Invoice invoice = InvoiceFactory.buildInvoice(
                "SHP-001",
                "CMP-001",
                "Acme Corp",
                "Freight charges for bulk shipment",
                4500.00,
                "Unpaid",
                dateIssued,
                dueDate
        );

        assertNotNull(invoice);
        assertNotNull(invoice.getInvoiceId());
        assertEquals("SHP-001", invoice.getShipmentId());
        assertEquals("CMP-001", invoice.getCompanyId());
        assertEquals("Acme Corp", invoice.getCustomerName());
        assertEquals("Freight charges for bulk shipment", invoice.getDescription());
        assertEquals(4500.00, invoice.getTotal());
        assertEquals(4500.00, invoice.getTotalAmount());
        assertEquals("Unpaid", invoice.getPaymentStatus());
        assertEquals(dateIssued, invoice.getDateIssued());
        assertEquals(dueDate, invoice.getDueDate());
    }

    @Test
    void testBuildInvoiceWithInvalidTotal() {
        Date dateIssued = new Date();
        Invoice invoice = InvoiceFactory.buildInvoice(-500.00, "Paid", dateIssued);
        assertNull(invoice);
    }

    @Test
    void testBuildInvoiceWithNullOrEmptyPaymentStatus() {
        Date dateIssued = new Date();
        assertNull(InvoiceFactory.buildInvoice(1500.00, null, dateIssued));
        assertNull(InvoiceFactory.buildInvoice(1500.00, "", dateIssued));
        assertNull(InvoiceFactory.buildInvoice(1500.00, "   ", dateIssued));
    }

    @Test
    void testBuildInvoiceWithNullDateIssued() {
        Invoice invoice = InvoiceFactory.buildInvoice(1500.00, "Paid", null);
        assertNull(invoice);
    }

    @Test
    void testBuildInvoiceGeneratesUniqueIds() {
        Date dateIssued = new Date();
        Invoice invoice1 = InvoiceFactory.buildInvoice(1500.00, "Paid", dateIssued);
        Invoice invoice2 = InvoiceFactory.buildInvoice(2000.00, "Pending", dateIssued);
        assertNotEquals(invoice1.getInvoiceId(), invoice2.getInvoiceId());
    }
}
