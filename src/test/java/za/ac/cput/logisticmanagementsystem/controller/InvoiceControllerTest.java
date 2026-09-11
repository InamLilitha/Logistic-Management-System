package za.ac.cput.logisticmanagementsystem.controller;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.factory.InvoiceFactory;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class InvoiceControllerTest {

    @Autowired
    private InvoiceController controller;

    private static Invoice invoice;

    @Test
    @Order(1)
    void create() {
        invoice = InvoiceFactory.buildInvoice(2500.50, "Pending", new Date());

        Invoice response = controller.create(invoice);

        assertNotNull(response);

        Invoice savedInvoice = response;
        assertNotNull(savedInvoice);
        assertEquals(invoice.getInvoiceId(), savedInvoice.getInvoiceId());
        System.out.println("Saved data: " + savedInvoice);
    }

    @Test
    @Order(2)
    void read() {
        ResponseEntity<Invoice> response = controller.read(invoice.getInvoiceId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(invoice.getInvoiceId(), response.getBody().getInvoiceId());
        System.out.println("Read: " + response.getBody());
    }

    @Test
    @Order(3)
    void update() {
        Invoice updated = new Invoice.Builder()
                .invoiceId(invoice.getInvoiceId())
                .total(3000.00)
                .paymentStatus("Paid")
                .dateIssued(invoice.getDateIssued())
                .build();

        Invoice response = controller.update(updated);

        assertNotNull(response);
        assertEquals("Paid", response.getPaymentStatus());
        System.out.println("Updated: " + response);
    }

    @Test
    @Order(4)
    void deleteTest() {
        controller.delete(invoice.getInvoiceId());
        System.out.println("Delete executed.");
    }

    @Test
    @Order(5)
    void getAll() {
        List<Invoice> response = controller.getAll();

        assertNotNull(response);
        System.out.println("Get All successful.");
    }
}