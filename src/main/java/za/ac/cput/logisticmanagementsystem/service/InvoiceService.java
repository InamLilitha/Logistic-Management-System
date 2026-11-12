package za.ac.cput.logisticmanagementsystem.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.repository.IInvoiceRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * InvoiceService.java
 * Service implementation for Invoice business logic
 * Author: Tebogo Pii 230226442
 * Date: 27 July 2026 / Updated: November 2026
 */

@Service
public class InvoiceService implements IInvoiceService {

    private final IInvoiceRepository invoiceRepository;
    private final IShipmentRepository shipmentRepository;

    @Autowired
    public InvoiceService(IInvoiceRepository invoiceRepository,
                          IShipmentRepository shipmentRepository) {
        this.invoiceRepository  = invoiceRepository;
        this.shipmentRepository = shipmentRepository;
    }

    @Override
    public Invoice create(Invoice invoice) {
        if (invoice == null) return null;
        invoice.invoiceId = UUID.randomUUID().toString();
        return invoiceRepository.save(invoice);
    }

    @Override
    public Invoice read(String invoiceId) {
        if (invoiceId == null || invoiceId.isBlank()) return null;
        return invoiceRepository.findById(invoiceId).orElse(null);
    }

    @Override
    public Invoice update(Invoice invoice) {
        if (invoice == null || invoice.getInvoiceId() == null
                || invoice.getInvoiceId().isBlank()) return null;
        return invoiceRepository.save(invoice);
    }

    @Override
    public boolean delete(String invoiceId) {
        if (invoiceId == null || invoiceId.isBlank()) return false;
        if (invoiceRepository.existsById(invoiceId)) {
            invoiceRepository.deleteById(invoiceId);
            return true;
        }
        return false;
    }

    @Override
    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> getByShipmentId(String shipmentId) {
        return invoiceRepository.findByShipmentId(shipmentId);
    }

    @Override
    public List<Invoice> getByCompanyId(String companyId) {
        return invoiceRepository.findByCompanyId(companyId);
    }

    @Override
    public List<Invoice> getByPaymentStatus(String paymentStatus) {
        return invoiceRepository.findByPaymentStatus(paymentStatus);
    }

    @Override
    public List<Invoice> getByDateRange(Date from, Date to) {
        return invoiceRepository.findByDateIssuedBetween(from, to);
    }

    @Override
    public Invoice updatePaymentStatus(String invoiceId, String paymentStatus) {
        if (paymentStatus == null || paymentStatus.isBlank()) return null;
        Invoice invoice = read(invoiceId);
        if (invoice == null) return null;
        Invoice updated = new Invoice.Builder()
                .copy(invoice)
                .paymentStatus(paymentStatus)
                .build();
        return invoiceRepository.save(updated);
    }

    @Override
    public int markOverdueInvoices() {
        List<Invoice> overdue = invoiceRepository.findOverdueInvoices(new Date());
        int count = 0;
        for (Invoice inv : overdue) {
            Invoice updated = new Invoice.Builder()
                    .copy(inv)
                    .paymentStatus("Overdue")
                    .build();
            invoiceRepository.save(updated);
            count++;
        }
        return count;
    }

    @Override
    public Invoice generateFromShipment(String shipmentId, String companyId,
                                        String description, int paymentTermDays) {
        if (shipmentId == null || shipmentId.isBlank() || paymentTermDays < 0) {
            return null;
        }

        Shipment shipment = shipmentRepository.findById(shipmentId).orElse(null);
        if (shipment == null) return null;

        ShippingContract contract = shipment.getContract();
        if (contract == null) return null;

        if (invoiceRepository.existsByShipmentId(shipmentId)) return null;

        if (!Double.isFinite(shipment.getWeight()) || shipment.getWeight() <= 0
                || contract.getPricePerKg() == null
                || !Double.isFinite(contract.getPricePerKg())
                || contract.getPricePerKg() <= 0) {
            return null;
        }

        double totalAmount = contract.calculateTotalCost(shipment.getWeight());
        Date now     = new Date();
        Date dueDate = new Date(now.getTime() + ((long) paymentTermDays * 24 * 60 * 60 * 1000));

        Invoice invoice = new Invoice.Builder()
                .invoiceId(UUID.randomUUID().toString())
                .shipmentId(shipmentId)
                .companyId(companyId)
                .description(description != null ? description
                        : "Freight charges — shipment " + shipmentId)
                .total(totalAmount)
                .paymentStatus("Unpaid")
                .dateIssued(now)
                .dueDate(dueDate)
                .build();

        return invoiceRepository.save(invoice);
    }
}
