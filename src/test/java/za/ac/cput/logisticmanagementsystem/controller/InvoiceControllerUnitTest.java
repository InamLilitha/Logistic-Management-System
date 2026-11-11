package za.ac.cput.logisticmanagementsystem.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.service.InvoiceService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InvoiceControllerUnitTest {

    @Mock
    private InvoiceService invoiceService;

    @InjectMocks
    private InvoiceController invoiceController;

    @Test
    void getByShipmentIdDelegatesToService() {
        List<Invoice> invoices = List.of(new Invoice.Builder()
                .invoiceId("INV-001")
                .shipmentId("SHP-001")
                .build());
        when(invoiceService.getByShipmentId("SHP-001")).thenReturn(invoices);

        assertSame(invoices, invoiceController.getByShipmentId("SHP-001"));
        verify(invoiceService).getByShipmentId("SHP-001");
    }

    @Test
    void getByCompanyIdDelegatesToService() {
        List<Invoice> invoices = List.of(new Invoice.Builder()
                .invoiceId("INV-001")
                .companyId("CMP-001")
                .build());
        when(invoiceService.getByCompanyId("CMP-001")).thenReturn(invoices);

        assertSame(invoices, invoiceController.getByCompanyId("CMP-001"));
        verify(invoiceService).getByCompanyId("CMP-001");
    }

    @Test
    void getByPaymentStatusDelegatesToService() {
        List<Invoice> invoices = List.of(new Invoice.Builder()
                .invoiceId("INV-001")
                .paymentStatus("Unpaid")
                .build());
        when(invoiceService.getByPaymentStatus("Unpaid")).thenReturn(invoices);

        assertSame(invoices, invoiceController.getByPaymentStatus("Unpaid"));
        verify(invoiceService).getByPaymentStatus("Unpaid");
    }

    @Test
    void generateFromShipmentReturnsCreatedInvoice() {
        Invoice invoice = new Invoice.Builder().invoiceId("INV-001").build();
        when(invoiceService.generateFromShipment(
                "SHP-001", "CMP-001", "Freight", 14)).thenReturn(invoice);

        ResponseEntity<Invoice> response = invoiceController.generateFromShipment(
                "SHP-001", "CMP-001", "Freight", 14);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertSame(invoice, response.getBody());
        verify(invoiceService).generateFromShipment(
                "SHP-001", "CMP-001", "Freight", 14);
    }

    @Test
    void generateFromShipmentReturnsNotFoundWhenServiceCannotGenerate() {
        when(invoiceService.generateFromShipment(
                "missing", null, null, 30)).thenReturn(null);

        ResponseEntity<Invoice> response = invoiceController.generateFromShipment(
                "missing", null, null, 30);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(invoiceService).generateFromShipment("missing", null, null, 30);
    }
}
