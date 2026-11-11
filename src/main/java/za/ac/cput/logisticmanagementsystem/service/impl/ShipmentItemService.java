package za.ac.cput.logisticmanagementsystem.service.impl;
/*
 * ShipmentItemService.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 05 November 2026
 */
import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;
import za.ac.cput.logisticmanagementsystem.factory.ShipmentItemFactory;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentItemRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.service.IShipmentItemService;

import java.util.List;

@Service
public class ShipmentItemService implements IShipmentItemService {

    private final IShipmentItemRepository repository;
    private final IShipmentRepository shipmentRepository;
    private final IInventoryRepository inventoryRepository;

    public ShipmentItemService(
            IShipmentItemRepository repository,
            IShipmentRepository shipmentRepository,
            IInventoryRepository inventoryRepository) {

        this.repository = repository;
        this.shipmentRepository = shipmentRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public ShipmentItem create(ShipmentItem request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Shipment item cannot be null"
            );
        }

        if (request.getShipment() == null ||
                request.getShipment().getShipmentId() == null) {

            throw new IllegalArgumentException(
                    "Shipment is required"
            );
        }

        if (request.getInventory() == null ||
                request.getInventory().getInventoryId() == null) {

            throw new IllegalArgumentException(
                    "Inventory is required"
            );
        }

        if (request.getQuantityShipped() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity shipped must be greater than zero"
            );
        }

        Shipment shipment = shipmentRepository
                .findById(
                        request.getShipment().getShipmentId()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Shipment not found: " +
                                request.getShipment().getShipmentId()
                ));

        Inventory inventory = inventoryRepository
                .findById(
                        request.getInventory().getInventoryId()
                )
                .orElseThrow(() -> new IllegalArgumentException(
                        "Inventory not found: " +
                                request.getInventory()
                                        .getInventoryId()
                ));

        if (request.getQuantityShipped() >
                inventory.getQuantityAvailable()) {

            throw new IllegalArgumentException(
                    "Insufficient inventory. Available: " +
                            inventory.getQuantityAvailable()
            );
        }

        ShipmentItem shipmentItem =
                ShipmentItemFactory.createShipmentItem(
                        shipment,
                        inventory,
                        request.getQuantityShipped()
                );

        return repository.save(shipmentItem);
    }

    @Override
    public ShipmentItem read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<ShipmentItem> readAll() {
        return repository.findAll();
    }

    @Override
    public List<ShipmentItem> readByShipmentId(
            String shipmentId) {

        return repository.findByShipment_ShipmentId(
                shipmentId
        );
    }

    @Override
    public ShipmentItem update(
            ShipmentItem shipmentItem) {

        if (shipmentItem == null ||
                shipmentItem.getShipmentItemId() == null) {

            throw new IllegalArgumentException(
                    "Shipment item and ID are required"
            );
        }

        ShipmentItem existing =
                repository.findById(
                        shipmentItem.getShipmentItemId()
                ).orElse(null);

        if (existing == null) {
            return null;
        }

        if (shipmentItem.getShipment() == null) {
            throw new IllegalArgumentException(
                    "Shipment is required"
            );
        }

        if (shipmentItem.getInventory() == null) {
            throw new IllegalArgumentException(
                    "Inventory is required"
            );
        }

        if (shipmentItem.getQuantityShipped() <= 0) {
            throw new IllegalArgumentException(
                    "Quantity shipped must be greater than zero"
            );
        }

        return repository.save(shipmentItem);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}