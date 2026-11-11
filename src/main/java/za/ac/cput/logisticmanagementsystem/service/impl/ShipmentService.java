package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;
import za.ac.cput.logisticmanagementsystem.factory.ShipmentFactory;
import za.ac.cput.logisticmanagementsystem.repository.IInventoryRepository;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.service.IShipmentService;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Transactional
public class ShipmentService implements IShipmentService {

    private final IShipmentRepository repository;
    private final IInventoryRepository inventoryRepository;

    @Autowired
    public ShipmentService(IShipmentRepository shipmentRepository, IInventoryRepository inventoryRepository) {
        this.repository = shipmentRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Override
    public Shipment create(Shipment request) {

        if (request == null) {
            return null;
        }

        Shipment shipment = ShipmentFactory.createShipment(
                request.getWeight(),
                request.getOrigin(),
                request.getDestination(),
                request.getDispatchDate(),
                request.getCargoType()
        );

        Map<String, Integer> quantitiesByInventory = new TreeMap<>();
        for (ShipmentItem item : request.getItems()) {
            if (item == null || item.getInventoryId() == null || item.getInventoryId().isBlank()
                    || item.getQuantity() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Each shipment item needs an inventory ID and a quantity greater than zero.");
            }
            try {
                quantitiesByInventory.merge(item.getInventoryId(), item.getQuantity(), Math::addExact);
            } catch (ArithmeticException overflow) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The combined quantity for an inventory item is too large.");
            }
        }

        Map<String, Inventory> inventoryById = new TreeMap<>();
        for (Map.Entry<String, Integer> entry : quantitiesByInventory.entrySet()) {
            Inventory inventory = inventoryRepository.findByIdForUpdate(entry.getKey())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Inventory item " + entry.getKey() + " was not found."));
            if (inventory.getQuantityAvailable() < entry.getValue()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Insufficient stock for SKU " + inventory.getSku() + ". Available: "
                                + inventory.getQuantityAvailable() + ", requested: " + entry.getValue() + ".");
            }
            inventoryById.put(entry.getKey(), inventory);
        }

        List<ShipmentItem> shipmentItems = request.getItems().stream()
                .map(item -> {
                    Inventory inventory = inventoryById.get(item.getInventoryId());
                    return new ShipmentItem(item.getInventoryId(), inventory.getSku(), item.getQuantity());
                })
                .toList();
        Shipment shipmentWithItems = new Shipment.Builder()
                .shipmentId(shipment.getShipmentId())
                .weight(shipment.getWeight())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .dispatchDate(shipment.getDispatchDate())
                .estimatedDeliveryDate(shipment.getEstimatedDeliveryDate())
                .cargoType(shipment.getCargoType())
                .items(shipmentItems)
                .build();

        for (Map.Entry<String, Integer> entry : quantitiesByInventory.entrySet()) {
            Inventory inventory = inventoryById.get(entry.getKey());
            Inventory updated = new Inventory.Builder()
                    .copy(inventory)
                    .setQuantityAvailable(inventory.getQuantityAvailable() - entry.getValue())
                    .build();
            inventoryRepository.save(updated);
        }
        return repository.save(shipmentWithItems);
    }

    @Override
    public Shipment read(String id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public List<Shipment> readAll() {
        return repository.findAll();
    }

    @Override
    public Shipment update(Shipment shipment) {

        if (shipment == null || shipment.getShipmentId() == null) {
            return null;
        }

        Shipment existingShipment = repository
                .findById(shipment.getShipmentId())
                .orElse(null);

        if (existingShipment == null) {
            return null;
        }

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
                .items(existingShipment.getItems())
                .build();

        return repository.save(updatedShipment);
    }
    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}