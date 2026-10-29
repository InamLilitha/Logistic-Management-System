package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShippingContract;
import za.ac.cput.logisticmanagementsystem.factory.ShipmentFactory;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.repository.InventoryRepository;
import za.ac.cput.logisticmanagementsystem.repository.ShippingContractRepository;
import za.ac.cput.logisticmanagementsystem.service.IShipmentService;

import java.time.LocalDate;
import java.util.List;

@Service
public class ShipmentService implements IShipmentService {


    private final IShipmentRepository repository;
    private final InventoryRepository inventoryRepository;
    private final ShippingContractRepository contractRepository;

    public ShipmentService(
            IShipmentRepository repository,
            InventoryRepository inventoryRepository,
            ShippingContractRepository contractRepository) {

        this.repository = repository;
        this.inventoryRepository = inventoryRepository;
        this.contractRepository = contractRepository;
    }

    @Override
    public Shipment create(Shipment request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Shipment cannot be null"
            );
        }

        validateAssociations(
                request.getInventoryId(),
                request.getContractId(),
                request.getDispatchDate()
        );

        Shipment shipment = ShipmentFactory.createShipment(
                request.getWeight(),
                request.getOrigin(),
                request.getDestination(),
                request.getDispatchDate(),
                request.getCargoType(),
                request.getInventoryId(),
                request.getContractId()
        );

        return repository.save(shipment);
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

        validateAssociations(
                shipment.getInventoryId(),
                shipment.getContractId(),
                shipment.getDispatchDate()
        );

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
                .inventoryId(shipment.getInventoryId())
                .contractId(shipment.getContractId())
                .build();

        return repository.save(updatedShipment);
    }

    private void validateAssociations(
            String inventoryId,
            String contractId,
            LocalDate dispatchDate) {

        if (inventoryId != null && !inventoryId.isBlank()) {

            Inventory inventory = inventoryRepository
                    .findById(inventoryId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Inventory not found: " + inventoryId
                    ));
        }

        if (contractId != null && !contractId.isBlank()) {

            ShippingContract contract = contractRepository
                    .findById(contractId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Shipping contract not found: " + contractId
                    ));

            if (dispatchDate != null &&
                    !contract.isWithinValidPeriod(dispatchDate)) {

                throw new IllegalArgumentException(
                        "The shipping contract is not valid on the dispatch date"
                );
            }
        }
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }


}
