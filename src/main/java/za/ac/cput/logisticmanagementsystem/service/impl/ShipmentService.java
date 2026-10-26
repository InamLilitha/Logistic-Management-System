package za.ac.cput.logisticmanagementsystem.service.impl;

import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.factory.ShipmentFactory;
import za.ac.cput.logisticmanagementsystem.repository.IShipmentRepository;
import za.ac.cput.logisticmanagementsystem.service.IShipmentService;

import java.util.List;

@Service
public class ShipmentService implements IShipmentService {

    private final IShipmentRepository repository;

    public ShipmentService(IShipmentRepository shipmentRepository) {
        this.repository = shipmentRepository;
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
                .build();

        return repository.save(updatedShipment);
    }
    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}