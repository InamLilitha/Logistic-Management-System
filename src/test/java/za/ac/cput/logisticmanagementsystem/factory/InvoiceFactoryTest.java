package za.ac.cput.logisticmanagementsystem.factory;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import za.ac.cput.logisticmanagementsystem.domain.Invoice;

import java.util.Date;

/**
 * InvoiceFactoryTest.java
 * Test class for InvoiceFactory
 * Author: Tebogo Pii 230226442
 * Date: 26-27 July 2026
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


