package za.ac.cput.logisticmanagementsystem.service;

import org.junit.jupiter.api.BeforeEach;
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
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;
import za.ac.cput.logisticmanagementsystem.repository.IInvoiceRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

/**
 * InvoiceServiceTest.java
 * Service implementation for Invoice business logic tests
 * Author: Tebogo Pii 230226442
 * Date: 29 July 2026 / Updated: November 2026
 * Update: Added tests for generateFromShipment, markOverdueInvoices,
 *         getByShipmentId, getByCompanyId, getByPaymentStatus, getByDateRange
 */

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InvoiceServiceTest {

    @Mock private IInvoiceRepository  invoiceRepository;
    @Mock private IShipmentRepository shipmentRepository;
    @Mock private ShippingContractRepository contractRepository;

    @InjectMocks
    private InvoiceService service;

    // ---- shared test data --------------------------------------------------
    private static final String SHIPMENT_ID  = "SHP-001";
    private static final String CONTRACT_ID  = "CON-001";
    private static final String COMPANY_ID   = "CMP-001";
    private static final double WEIGHT_KG    = 100.0;
    private static final double PRICE_PER_KG = 15.0;
    private static final double EXPECTED_TOTAL = WEIGHT_KG * PRICE_PER_KG; // 1500.0

    private Invoice sampleInvoice;
    private Shipment sampleShipment;
    private ShippingContract sampleContract;

    @BeforeEach
    void setUp() {
        sampleInvoice = InvoiceFactory.buildInvoice(2500.50, "Unpaid", new Date());

        sampleContract = new ShippingContract.Builder()
                .setContractId(CONTRACT_ID)
                .setContractNumber("CON-2026-001")
                .setPricePerKgString(PRICE_PER_KG)
                .setContractStartDate(LocalDate.now().minusDays(30))
                .setContractExpiryDate(LocalDate.now().plusDays(335))
                .build();

        sampleShipment = new Shipment.Builder()
                .shipmentId(SHIPMENT_ID)
                .weight(WEIGHT_KG)
                .contract(sampleContract)
                .origin("Cape Town")
                .destination("Johannesburg")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(3))
                .cargoType("General Cargo")
                .build();
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    @Test @Order(1)
    void create_assignsNewId() {
        String originalId = sampleInvoice.getInvoiceId();
        Mockito.when(invoiceRepository.save(sampleInvoice)).thenReturn(sampleInvoice);

        Invoice created = service.create(sampleInvoice);

        assertNotNull(created);
        assertNotNull(created.getInvoiceId());
        // service overwrites the id with a new UUID
        assertNotEquals(originalId, created.getInvoiceId());
        System.out.println("create: " + created);
    }

    @Test @Order(2)
    void create_returnsNull_forNullInput() {
        assertNull(service.create(null));
    }

    @Test @Order(3)
    void read_returnsInvoice_whenFound() {
        Mockito.when(invoiceRepository.findById(sampleInvoice.getInvoiceId()))
               .thenReturn(Optional.of(sampleInvoice));

        Invoice found = service.read(sampleInvoice.getInvoiceId());

        assertNotNull(found);
        assertEquals(2500.50, found.getTotal());
        System.out.println("read: " + found);
    }

    @Test @Order(4)
    void read_returnsNull_whenNotFound() {
        Mockito.when(invoiceRepository.findById("bad-id")).thenReturn(Optional.empty());
        assertNull(service.read("bad-id"));
    }

    @Test @Order(5)
    void update_savesAndReturns() {
        Invoice updated = new Invoice.Builder()
                .copy(sampleInvoice)
                .total(3000.00)
                .paymentStatus("Paid")
                .build();
        Mockito.when(invoiceRepository.save(updated)).thenReturn(updated);

        Invoice result = service.update(updated);

        assertNotNull(result);
        assertEquals(3000.00, result.getTotal());
        assertEquals("Paid", result.getPaymentStatus());
        System.out.println("update: " + result);
    }

    @Test @Order(6)
    void update_returnsNull_forNullInput() {
        assertNull(service.update(null));
    }

    @Test @Order(7)
    void delete_returnsTrue_whenExists() {
        Mockito.when(invoiceRepository.existsById(sampleInvoice.getInvoiceId())).thenReturn(true);
        Mockito.doNothing().when(invoiceRepository).deleteById(sampleInvoice.getInvoiceId());

        assertTrue(service.delete(sampleInvoice.getInvoiceId()));
    }

    @Test @Order(8)
    void delete_returnsFalse_whenNotFound() {
        Mockito.when(invoiceRepository.existsById("ghost-id")).thenReturn(false);
        assertFalse(service.delete("ghost-id"));
    }

    // -------------------------------------------------------------------------
    // Listing
    // -------------------------------------------------------------------------

    @Test @Order(9)
    void getAll_returnsList() {
        Mockito.when(invoiceRepository.findAll()).thenReturn(List.of(sampleInvoice));
        assertFalse(service.getAll().isEmpty());
        System.out.println("getAll: " + service.getAll());
    }

    @Test @Order(10)
    void getByShipmentId_returnsList() {
        Invoice linked = new Invoice.Builder().copy(sampleInvoice).shipmentId(SHIPMENT_ID).build();
        Mockito.when(invoiceRepository.findByShipmentId(SHIPMENT_ID)).thenReturn(List.of(linked));

        List<Invoice> results = service.getByShipmentId(SHIPMENT_ID);

        assertFalse(results.isEmpty());
        assertEquals(SHIPMENT_ID, results.get(0).getShipmentId());
        System.out.println("getByShipmentId: " + results);
    }

    @Test @Order(11)
    void getByCompanyId_returnsList() {
        Invoice linked = new Invoice.Builder().copy(sampleInvoice).companyId(COMPANY_ID).build();
        Mockito.when(invoiceRepository.findByCompanyId(COMPANY_ID)).thenReturn(List.of(linked));

        List<Invoice> results = service.getByCompanyId(COMPANY_ID);

        assertFalse(results.isEmpty());
        assertEquals(COMPANY_ID, results.get(0).getCompanyId());
        System.out.println("getByCompanyId: " + results);
    }

    @Test @Order(12)
    void getByPaymentStatus_returnsList() {
        Mockito.when(invoiceRepository.findByPaymentStatus("Unpaid")).thenReturn(List.of(sampleInvoice));

        List<Invoice> results = service.getByPaymentStatus("Unpaid");

        assertFalse(results.isEmpty());
        System.out.println("getByPaymentStatus: " + results);
    }

    @Test @Order(13)
    void getByDateRange_returnsList() {
        Date from = new Date(System.currentTimeMillis() - 60_000);
        Date to   = new Date(System.currentTimeMillis() + 60_000);
        Mockito.when(invoiceRepository.findByDateIssuedBetween(from, to)).thenReturn(List.of(sampleInvoice));

        List<Invoice> results = service.getByDateRange(from, to);

        assertFalse(results.isEmpty());
        System.out.println("getByDateRange: " + results);
    }

    // -------------------------------------------------------------------------
    // Status management
    // -------------------------------------------------------------------------

    @Test @Order(14)
    void updatePaymentStatus_updatesAndReturns() {
        Invoice overdueVersion = new Invoice.Builder()
                .copy(sampleInvoice).paymentStatus("Overdue").build();

        Mockito.when(invoiceRepository.findById(sampleInvoice.getInvoiceId()))
               .thenReturn(Optional.of(sampleInvoice));
        Mockito.when(invoiceRepository.save(any(Invoice.class))).thenReturn(overdueVersion);

        Invoice result = service.updatePaymentStatus(sampleInvoice.getInvoiceId(), "Overdue");

        assertNotNull(result);
        assertEquals("Overdue", result.getPaymentStatus());
        System.out.println("updatePaymentStatus: " + result);
    }

    @Test @Order(15)
    void updatePaymentStatus_returnsNull_forBlankStatus() {
        assertNull(service.updatePaymentStatus(sampleInvoice.getInvoiceId(), ""));
        assertNull(service.updatePaymentStatus(sampleInvoice.getInvoiceId(), null));
    }

    @Test @Order(16)
    void updatePaymentStatus_returnsNull_whenInvoiceNotFound() {
        Mockito.when(invoiceRepository.findById("missing")).thenReturn(Optional.empty());
        assertNull(service.updatePaymentStatus("missing", "Paid"));
    }

    @Test @Order(17)
    void markOverdueInvoices_updatesAllPastDue() {
        // Two past-due unpaid invoices
        Invoice inv1 = InvoiceFactory.buildInvoice(500.0, "Unpaid", new Date());
        Invoice inv2 = InvoiceFactory.buildInvoice(750.0, "Unpaid", new Date());

        Mockito.when(invoiceRepository.findOverdueInvoices(any(Date.class)))
               .thenReturn(List.of(inv1, inv2));
        Mockito.when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        int updated = service.markOverdueInvoices();

        assertEquals(2, updated);
        System.out.println("markOverdueInvoices: updated " + updated + " record(s)");
    }

    @Test @Order(18)
    void markOverdueInvoices_returnsZero_whenNoneFound() {
        Mockito.when(invoiceRepository.findOverdueInvoices(any(Date.class))).thenReturn(List.of());
        assertEquals(0, service.markOverdueInvoices());
    }

    // -------------------------------------------------------------------------
    // Auto-billing — generateFromShipment
    // -------------------------------------------------------------------------

    @Test @Order(19)
    void generateFromShipment_calculatesTotal_correctly() {
        Mockito.when(shipmentRepository.findById(SHIPMENT_ID)).thenReturn(Optional.of(sampleShipment));
        Mockito.when(contractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(sampleContract));
        Mockito.when(invoiceRepository.existsByShipmentId(SHIPMENT_ID)).thenReturn(false);
        Mockito.when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        Invoice result = service.generateFromShipment(SHIPMENT_ID, COMPANY_ID,
                "Freight — bulk cargo", 30);

        assertNotNull(result);
        assertEquals(EXPECTED_TOTAL, result.getTotal(), 0.001);
        assertEquals(SHIPMENT_ID,  result.getShipmentId());
        assertEquals(COMPANY_ID,   result.getCompanyId());
        assertEquals("Unpaid",     result.getPaymentStatus());
        assertNotNull(result.getDueDate());
        System.out.println("generateFromShipment: " + result);
    }

    @Test @Order(20)
    void generateFromShipment_returnsNull_whenShipmentNotFound() {
        Mockito.when(shipmentRepository.findById(anyString())).thenReturn(Optional.empty());
        assertNull(service.generateFromShipment("BAD-SHP", COMPANY_ID, "desc", 30));
    }

    @Test @Order(21)
    void generateFromShipment_returnsNull_whenContractNotFound() {
        // Shipment with no contract id
        Shipment noContract = new Shipment.Builder()
                .shipmentId(SHIPMENT_ID).weight(50)
                .origin("CPT").destination("JHB")
                .dispatchDate(LocalDate.now())
                .estimatedDeliveryDate(LocalDate.now().plusDays(2))
                .cargoType("General Cargo")
                .build();

        Mockito.when(shipmentRepository.findById(SHIPMENT_ID)).thenReturn(Optional.of(noContract));
        assertNull(service.generateFromShipment(SHIPMENT_ID, COMPANY_ID, "desc", 30));
    }

    @Test @Order(22)
    void generateFromShipment_returnsNull_whenDuplicateDetected() {
        Mockito.when(shipmentRepository.findById(SHIPMENT_ID)).thenReturn(Optional.of(sampleShipment));
        Mockito.when(contractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(sampleContract));
        // Invoice already exists for this shipment
        Mockito.when(invoiceRepository.existsByShipmentId(SHIPMENT_ID)).thenReturn(true);

        Invoice result = service.generateFromShipment(SHIPMENT_ID, COMPANY_ID, "desc", 30);

        assertNull(result, "Should not create a duplicate invoice for the same shipment");
        System.out.println("generateFromShipment duplicate guard: OK");
    }

    @Test @Order(23)
    void generateFromShipment_usesDefaultDescription_whenNullPassed() {
        Mockito.when(shipmentRepository.findById(SHIPMENT_ID)).thenReturn(Optional.of(sampleShipment));
        Mockito.when(contractRepository.findById(CONTRACT_ID)).thenReturn(Optional.of(sampleContract));
        Mockito.when(invoiceRepository.existsByShipmentId(SHIPMENT_ID)).thenReturn(false);
        Mockito.when(invoiceRepository.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        Invoice result = service.generateFromShipment(SHIPMENT_ID, COMPANY_ID, null, 14);

        assertNotNull(result);
        assertTrue(result.getDescription().contains(SHIPMENT_ID));
        System.out.println("generateFromShipment default description: " + result.getDescription());
    }
}
