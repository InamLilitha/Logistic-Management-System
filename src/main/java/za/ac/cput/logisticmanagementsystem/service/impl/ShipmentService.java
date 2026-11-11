package za.ac.cput.logisticmanagementsystem.service.impl;
/*
 * ShipmentService.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 27 July 2026
 * Date Upated: November 2026
 */
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import za.ac.cput.logisticmanagementsystem.domain.CompanyDomain;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.domain.StaffProfile;

import za.ac.cput.logisticmanagementsystem.factory.ShipmentFactory;

import za.ac.cput.logisticmanagementsystem.repository.ICompanyRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;
import za.ac.cput.logisticmanagementsystem.repository.StaffProfileRepository;

import za.ac.cput.logisticmanagementsystem.service.IShipmentService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
public class ShipmentService implements IShipmentService {

    private final IShipmentRepository repository;
    private final ICompanyRepository companyRepository;
    private final ShippingContractRepository contractRepository;
    private final StaffProfileRepository staffRepository;
    private final IInventoryRepository inventoryRepository;

    public ShipmentService(
            IShipmentRepository repository,
            ICompanyRepository companyRepository,
            ShippingContractRepository contractRepository,
            StaffProfileRepository staffRepository,
            IInventoryRepository inventoryRepository) {

        this.repository = repository;
        this.companyRepository = companyRepository;
        this.contractRepository = contractRepository;
        this.staffRepository = staffRepository;
        this.inventoryRepository = inventoryRepository;
    }



    @Override
    public Shipment create(Shipment request) {

        if (request == null) {
            throw new IllegalArgumentException("Shipment cannot be null");
        }

        CompanyDomain company = resolveCompany(request.getCompany());

        ShippingContract contract =
                resolveContract(request.getContract());

        if (request.getCreatedByStaff() == null ||
                request.getCreatedByStaff().getId() == null) {

            throw new IllegalArgumentException(
                    "Staff member who created the shipment is required"
            );
        }

        Long staffId = request.getCreatedByStaff().getId();

        StaffProfile staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Staff member not found: " + staffId
                ));

        validateContract(contract, request.getDispatchDate());

        Shipment shipment = ShipmentFactory.createShipment(
                request.getWeight(),
                request.getOrigin(),
                request.getDestination(),
                request.getDispatchDate(),
                request.getCargoType(),
                company,
                contract,
                staff
        );


        List<ShipmentItem> shipmentItems =
                buildShipmentItems(
                        request.getShipmentItems(),
                        shipment,
                        company
                );



        applyInventoryChanges(
                new ArrayList<>(),
                shipmentItems
        );

        shipment.setShipmentItems(shipmentItems);

        return repository.save(shipment);
    }


    @Override
    public Shipment read(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Shipment ID is required"
            );
        }

        return repository.findById(id).orElse(null);
    }


    @Override
    public List<Shipment> readAll() {

        return repository.findAll();
    }



    @Override
    public Shipment update(Shipment shipment) {

        if (shipment == null ||
                shipment.getShipmentId() == null) {

            throw new IllegalArgumentException(
                    "Shipment and shipment ID are required"
            );
        }

        Shipment existingShipment = repository
                .findById(shipment.getShipmentId())
                .orElse(null);

        if (existingShipment == null) {
            return null;
        }

        CompanyDomain company =
                resolveCompany(shipment.getCompany());

        ShippingContract contract =
                resolveContract(shipment.getContract());

        validateContract(contract, shipment.getDispatchDate());

        StaffProfile staff = existingShipment.getCreatedByStaff();


        List<ShipmentItem> existingItems =
                existingShipment.getShipmentItems();

        List<ShipmentItem> oldItems =
                new ArrayList<>(existingItems);

        Shipment updatedShipment = new Shipment.Builder()
                .shipmentId(existingShipment.getShipmentId())
                .weight(shipment.getWeight())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .dispatchDate(shipment.getDispatchDate())
                .estimatedDeliveryDate(
                        existingShipment.getEstimatedDeliveryDate()
                )
                .cargoType(shipment.getCargoType())
                .company(company)
                .contract(contract)
                .createdByStaff(staff)
                .shipmentItems(existingItems)
                .build();

        List<ShipmentItem> newItems =
                buildShipmentItems(
                        shipment.getShipmentItems(),
                        updatedShipment,
                        company
                );

        applyInventoryChanges(oldItems, newItems);
        existingItems.clear();
        existingItems.addAll(newItems);

        return repository.save(updatedShipment);
    }

    @Override
    public void delete(String id) {

        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Shipment ID is required"
            );
        }

        Shipment shipment = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shipment not found: " + id
                ));

        applyInventoryChanges(
                new ArrayList<>(shipment.getShipmentItems()),
                new ArrayList<>()
        );

        repository.delete(shipment);
    }


    private CompanyDomain resolveCompany(CompanyDomain requestedCompany) {

        if (requestedCompany == null ||
                requestedCompany.getCompanyId() == null ||
                requestedCompany.getCompanyId().isBlank()) {

            throw new IllegalArgumentException(
                    "A company must be selected for the shipment"
            );
        }

        String companyId = requestedCompany.getCompanyId();

        return companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Company not found: " + companyId
                ));
    }


    private ShippingContract resolveContract(
            ShippingContract requestedContract) {

        if (requestedContract == null) {
            return null;
        }

        String contractId = requestedContract.getContractId();

        if (contractId == null || contractId.isBlank()) {
            throw new IllegalArgumentException(
                    "Invalid shipping contract ID"
            );
        }

        return contractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shipping contract not found: " + contractId
                ));
    }


    private List<ShipmentItem> buildShipmentItems(
            List<ShipmentItem> requestedItems,
            Shipment shipment,
            CompanyDomain company) {

        if (requestedItems == null || requestedItems.isEmpty()) {

            throw new IllegalArgumentException(
                    "At least one inventory item must be selected"
            );
        }

        List<ShipmentItem> shipmentItems = new ArrayList<>();

        for (ShipmentItem requestedItem : requestedItems) {

            if (requestedItem == null ||
                    requestedItem.getInventory() == null ||
                    requestedItem.getInventory().getInventoryId() == null) {

                throw new IllegalArgumentException(
                        "Every shipment item must have an inventory item"
                );
            }

            String inventoryId =
                    requestedItem.getInventory().getInventoryId();

            int quantity = requestedItem.getQuantityShipped();

            if (quantity <= 0) {
                throw new IllegalArgumentException(
                        "Quantity shipped must be greater than zero"
                );
            }

            Inventory inventory = inventoryRepository.findById(inventoryId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory not found: " + inventoryId
                    ));


            if (!company.getCompanyId().equals(inventory.getCompanyId())) {

                throw new IllegalArgumentException(
                        "Inventory item does not belong to the selected company: "
                                + inventory.getItemName()
                );
            }

            ShipmentItem shipmentItem = new ShipmentItem.Builder()
                    .shipmentItemId(UUID.randomUUID().toString())
                    .shipment(shipment)
                    .inventory(inventory)
                    .quantityShipped(quantity)
                    .build();

            shipmentItems.add(shipmentItem);
        }

        return shipmentItems;
    }


    private void applyInventoryChanges(
            List<ShipmentItem> oldItems,
            List<ShipmentItem> newItems) {

        Map<String, Integer> quantityChanges = new HashMap<>();


        for (ShipmentItem item : oldItems) {

            String inventoryId =
                    item.getInventory().getInventoryId();

            quantityChanges.merge(
                    inventoryId,
                    -item.getQuantityShipped(),
                    Integer::sum
            );
        }

        for (ShipmentItem item : newItems) {

            String inventoryId =
                    item.getInventory().getInventoryId();

            quantityChanges.merge(
                    inventoryId,
                    item.getQuantityShipped(),
                    Integer::sum
            );
        }

        for (Map.Entry<String, Integer> entry :
                quantityChanges.entrySet()) {

            String inventoryId = entry.getKey();

            int quantityChange = entry.getValue();

            Inventory inventory = inventoryRepository
                    .findById(inventoryId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory not found: " + inventoryId
                    ));

            int updatedQuantity =
                    inventory.getQuantityAvailable() - quantityChange;

            if (updatedQuantity < 0) {

                throw new IllegalArgumentException(
                        "Insufficient inventory for "
                                + inventory.getItemName()
                                + ". Available: "
                                + inventory.getQuantityAvailable()
                );
            }

            Inventory updatedInventory =
                    new Inventory.Builder()
                            .copy(inventory)
                            .setQuantityAvailable(updatedQuantity)
                            .build();

            inventoryRepository.save(updatedInventory);
        }
    }


    private void validateContract(
            ShippingContract contract,
            LocalDate dispatchDate) {

        if (dispatchDate == null) {
            throw new IllegalArgumentException(
                    "Dispatch date is required"
            );
        }

        if (contract != null &&
                !contract.isWithinValidPeriod(dispatchDate)) {

            throw new IllegalArgumentException(
                    "The shipping contract is not valid on the dispatch date"
            );
        }
    }
}