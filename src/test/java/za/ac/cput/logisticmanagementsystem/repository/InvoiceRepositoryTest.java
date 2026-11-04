package za.ac.cput.logisticmanagementsystem.repository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * InvoiceRepositoryTest.java
 * Test class for IInvoiceRepository — covers CRUD and all derived finders
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026 / Updated: November 2026
 * Update: Added tests for findByShipmentId, findByCompanyId,
 *         findByPaymentStatus, findOverdueInvoices, and existsByShipmentId
 */

@SpringBootTest
@TestMethodOrder(MethodOrderer.MethodName.class)
class InvoiceRepositoryTest {

    @Autowired
    private IInvoiceRepository repository;

    // Static so test state flows through alphabetical test order
    private static final String SHIPMENT_ID = "SHP-TEST-001";
    private static final String COMPANY_ID  = "CMP-TEST-001";

    private static Invoice invoice;

    // -------------------------------------------------------------------------
    // SETUP
    // -------------------------------------------------------------------------

    @BeforeAll
    static void setup() {
        Date dateIssued = new Date();
        // Build a fully-linked invoice using the new factory overload
        invoice = InvoiceFactory.buildInvoice(
                SHIPMENT_ID,
                COMPANY_ID,
                "Test Customer",
                "Freight charges for test shipment SHP-TEST-001",
                1250.00,
                "Unpaid",
                dateIssued,
                new Date(dateIssued.getTime() + (30L * 24 * 60 * 60 * 1000))
        );
        assertNotNull(invoice);
    }

    // -------------------------------------------------------------------------
    // A — CREATE
    // -------------------------------------------------------------------------

    @Test
    void a_createInvoice() {
        Invoice saved = repository.save(invoice);
        assertNotNull(saved);
        assertEquals(invoice.getInvoiceId(), saved.getInvoiceId());
        assertEquals(SHIPMENT_ID, saved.getShipmentId());
        assertEquals(COMPANY_ID, saved.getCompanyId());
        assertEquals("Unpaid", saved.getPaymentStatus());
        System.out.println("Created: " + saved);
    }

    // -------------------------------------------------------------------------
    // B — READ by primary key
    // -------------------------------------------------------------------------

    @Test
    void b_readInvoice() {
        Optional<Invoice> found = repository.findById(invoice.getInvoiceId());
        assertTrue(found.isPresent());
        assertEquals(invoice.getInvoiceId(), found.get().getInvoiceId());
        assertEquals(1250.00, found.get().getTotal());
        System.out.println("Read: " + found.get());
    }

    // -------------------------------------------------------------------------
    // C — UPDATE payment status
    // -------------------------------------------------------------------------

    @Test
    void c_updateInvoice() {
        Invoice updated = new Invoice.Builder()
                .copy(invoice)
                .paymentStatus("Paid")
                .total(1250.00)
                .build();

        Invoice saved = repository.save(updated);
        assertNotNull(saved);
        assertEquals("Paid", saved.getPaymentStatus());
        System.out.println("Updated: " + saved);
        invoice = saved;
    }

    // -------------------------------------------------------------------------
    // D — FIND by shipment ID
    // -------------------------------------------------------------------------

    @Test
    void d_findByShipmentId() {
        List<Invoice> results = repository.findByShipmentId(SHIPMENT_ID);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(i -> SHIPMENT_ID.equals(i.getShipmentId())));
        System.out.println("findByShipmentId: " + results);
    }

    // -------------------------------------------------------------------------
    // E — FIND latest invoice for shipment
    // -------------------------------------------------------------------------

    @Test
    void e_findTopByShipmentIdOrderByDateIssuedDesc() {
        Optional<Invoice> latest = repository.findTopByShipmentIdOrderByDateIssuedDesc(SHIPMENT_ID);
        assertTrue(latest.isPresent());
        assertEquals(SHIPMENT_ID, latest.get().getShipmentId());
        System.out.println("Latest for shipment: " + latest.get());
    }

    // -------------------------------------------------------------------------
    // F — FIND by company ID
    // -------------------------------------------------------------------------

    @Test
    void f_findByCompanyId() {
        List<Invoice> results = repository.findByCompanyId(COMPANY_ID);
        assertFalse(results.isEmpty());
        assertTrue(results.stream().allMatch(i -> COMPANY_ID.equals(i.getCompanyId())));
        System.out.println("findByCompanyId: " + results);
    }

    // -------------------------------------------------------------------------
    // G — FIND by company ID and payment status
    // -------------------------------------------------------------------------

    @Test
    void g_findByCompanyIdAndPaymentStatus() {
        List<Invoice> results = repository.findByCompanyIdAndPaymentStatus(COMPANY_ID, "Paid");
        assertFalse(results.isEmpty());
        results.forEach(i -> {
            assertEquals(COMPANY_ID, i.getCompanyId());
            assertEquals("Paid", i.getPaymentStatus());
        });
        System.out.println("findByCompanyIdAndPaymentStatus (Paid): " + results);
    }

    // -------------------------------------------------------------------------
    // H — FIND by payment status
    // -------------------------------------------------------------------------

    @Test
    void h_findByPaymentStatus() {
        List<Invoice> results = repository.findByPaymentStatus("Paid");
        assertFalse(results.isEmpty());
        results.forEach(i -> assertEquals("Paid", i.getPaymentStatus()));
        System.out.println("findByPaymentStatus (Paid): " + results);
    }

    // -------------------------------------------------------------------------
    // I — FIND by date range
    // -------------------------------------------------------------------------

    @Test
    void i_findByDateIssuedBetween() {
        Date from = new Date(System.currentTimeMillis() - (60L * 1000));  // 1 minute ago
        Date to   = new Date(System.currentTimeMillis() + (60L * 1000));  // 1 minute from now
        List<Invoice> results = repository.findByDateIssuedBetween(from, to);
        assertFalse(results.isEmpty());
        System.out.println("findByDateIssuedBetween: " + results);
    }

    // -------------------------------------------------------------------------
    // J — FIND overdue invoices (JPQL query)
    // -------------------------------------------------------------------------

    @Test
    void j_findOverdueInvoices() {
        // Save a past-due "Unpaid" invoice to trigger the overdue check
        Date pastDate = new Date(System.currentTimeMillis() - (60L * 1000));
        Invoice overdueInvoice = InvoiceFactory.buildInvoice(
                "SHP-OVERDUE-001", COMPANY_ID,
                "Overdue freight charges", 800.00, "Unpaid", pastDate, pastDate
        );
        repository.save(overdueInvoice);

        List<Invoice> overdue = repository.findOverdueInvoices(new Date());
        assertFalse(overdue.isEmpty(), "Expected at least one overdue invoice");
        overdue.forEach(i -> assertNotEquals("Paid", i.getPaymentStatus()));
        System.out.println("findOverdueInvoices: " + overdue);

        // Clean up the extra record
        repository.deleteById(overdueInvoice.getInvoiceId());
    }

    // -------------------------------------------------------------------------
    // K — EXISTS by shipment ID
    // -------------------------------------------------------------------------

    @Test
    void k_existsByShipmentId() {
        assertTrue(repository.existsByShipmentId(SHIPMENT_ID));
        assertFalse(repository.existsByShipmentId("SHP-DOES-NOT-EXIST"));
        System.out.println("existsByShipmentId: true for " + SHIPMENT_ID);
    }

    // -------------------------------------------------------------------------
    // L — GET ALL
    // -------------------------------------------------------------------------

    @Test
    void l_getAllInvoices() {
        List<Invoice> all = repository.findAll();
        assertFalse(all.isEmpty());
        System.out.println("All invoices: " + all);
    }

    // -------------------------------------------------------------------------
    // Z — DELETE (runs last)
    // -------------------------------------------------------------------------

    @Test
    void z_deleteInvoice() {
        repository.deleteById(invoice.getInvoiceId());
        assertFalse(repository.existsById(invoice.getInvoiceId()));
        System.out.println("Deleted: " + invoice.getInvoiceId());
    }
}
