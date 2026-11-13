package za.ac.cput.logisticmanagementsystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Invoice;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentTracking;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;
import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInvoiceRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentItemRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.ShipmentTrackingRepository;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Configuration
public class DemoDataInitializer {

    @Bean
    @Order(2)
    CommandLineRunner seedDemoData(
            ICompanyRepository companyRepository,
            IInventoryRepository inventoryRepository,
            ShippingContractRepository contractRepository,
            IShipmentRepository shipmentRepository,
            IShipmentItemRepository shipmentItemRepository,
            ShipmentTrackingRepository trackingRepository,
            IInvoiceRepository invoiceRepository,
            StaffProfileRepository staffRepository) {
        return args -> {
            CompanyDomain company = seedCompany(companyRepository);
            Inventory inventory = seedInventory(inventoryRepository, company.getCompanyId());
            ShippingContract contract = seedContract(contractRepository);
            StaffProfile demoUser = staffRepository
                    .findByUsernameAndActiveTrue("demo")
                    .orElseThrow();

            Shipment shipment = seedShipment(
                    shipmentRepository, company, contract, demoUser);
            seedShipmentItem(shipmentItemRepository, shipment, inventory);
            seedTracking(trackingRepository, shipment);
            seedInvoice(invoiceRepository, shipment, company);
        };
    }

    private CompanyDomain seedCompany(ICompanyRepository repository) {
        return repository.findById("DEMO-COMPANY").orElseGet(() ->
                repository.save(new CompanyDomain.Builder()
                        .setCompanyId("DEMO-COMPANY")
                        .setCompanyName("Cape Logistics Demo")
                        .setCompanyType("Freight and distribution")
                        .setTaxId("DEMO-TAX-001")
                        .setPhoneNumber("0215550100")
                        .setEmailAddress("demo@capelogistics.example")
                        .setIsActive(true)
                        .build()));
    }

    private Inventory seedInventory(
            IInventoryRepository repository, String companyId) {
        return repository.findById("DEMO-INVENTORY-001").orElseGet(() ->
                repository.save(new Inventory.Builder()
                        .setInventoryId("DEMO-INVENTORY-001")
                        .setItemName("Demo Office Equipment")
                        .setSku("DEMO-OFFICE-001")
                        .setQuantityAvailable(25)
                        .setUnitWeight(12.5)
                        .setCompanyId(companyId)
                        .build()));
    }

    private ShippingContract seedContract(
            ShippingContractRepository repository) {
        return repository.findById("DEMO-CONTRACT-001").orElseGet(() ->
                repository.save(new ShippingContract.Builder()
                        .setContractId("DEMO-CONTRACT-001")
                        .setContractNumber("LMS-DEMO-2026-001")
                        .setPricePerKgString(18.50)
                        .setContractStartDate(LocalDate.now().minusDays(30))
                        .setContractExpiryDate(LocalDate.now().plusDays(335))
                        .build()));
    }

    private Shipment seedShipment(
            IShipmentRepository repository,
            CompanyDomain company,
            ShippingContract contract,
            StaffProfile createdBy) {
        return repository.findById("DEMO-SHIPMENT-001").orElseGet(() ->
                repository.save(new Shipment.Builder()
                        .shipmentId("DEMO-SHIPMENT-001")
                        .weight(12.5)
                        .origin("Cape Town")
                        .destination("Johannesburg")
                        .dispatchDate(LocalDate.now().minusDays(1))
                        .estimatedDeliveryDate(LocalDate.now().plusDays(2))
                        .cargoType("Office equipment")
                        .company(company)
                        .contract(contract)
                        .createdByStaff(createdBy)
                        .build()));
    }

    private void seedShipmentItem(
            IShipmentItemRepository repository,
            Shipment shipment,
            Inventory inventory) {
        if (!repository.existsById("DEMO-SHIPMENT-ITEM-001")) {
            repository.save(new ShipmentItem.Builder()
                    .shipmentItemId("DEMO-SHIPMENT-ITEM-001")
                    .shipment(shipment)
                    .inventory(inventory)
                    .quantityShipped(1)
                    .build());
        }
    }

    private void seedTracking(
            ShipmentTrackingRepository repository, Shipment shipment) {
        if (!repository.existsById("DEMO-TRACKING-001")) {
            repository.save(new ShipmentTracking.Builder()
                    .setTrackingId("DEMO-TRACKING-001")
                    .setTrackingNumber("LMS-DEMO-TRACK-001")
                    .setCurrentLocation("Cape Town depot")
                    .setShipmentStatus("In Transit")
                    .setDateCreated(LocalDate.now().minusDays(1))
                    .setShipment(shipment)
                    .build());
        }
    }

    private void seedInvoice(
            IInvoiceRepository repository,
            Shipment shipment,
            CompanyDomain company) {
        if (!repository.existsById("DEMO-INVOICE-001")) {
            Date issued = new Date();
            repository.save(new Invoice.Builder()
                    .invoiceId("DEMO-INVOICE-001")
                    .shipmentId(shipment.getShipmentId())
                    .companyId(company.getCompanyId())
                    .customerName("Cape Logistics Demo")
                    .description("Demo freight shipment")
                    .total(231.25)
                    .paymentStatus("Unpaid")
                    .dateIssued(issued)
                    .dueDate(new Date(issued.getTime() + 30L * 24 * 60 * 60 * 1000))
                    .build());
        }
    }
}
