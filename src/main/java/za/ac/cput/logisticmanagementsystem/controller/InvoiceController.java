package za.ac.cput.logisticmanagementsystem.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.service.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping("/create")
    public Invoice create(@RequestBody Invoice invoice) {
        return invoiceService.create(invoice);
    }

    @GetMapping("/read/{id}")
    public ResponseEntity<Invoice> read(@PathVariable String id) {
        Invoice invoice = invoiceService.read(id);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/update")
    public Invoice update(@RequestBody Invoice invoice) {
        return invoiceService.update(invoice);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/getall")
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }
}
