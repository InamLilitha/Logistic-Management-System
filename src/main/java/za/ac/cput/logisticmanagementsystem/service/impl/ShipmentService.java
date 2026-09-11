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
        Shipment shipment = ShipmentFactory.createShipment(request.getWeight(), request.getOrigin(), request.getDestination(), request.getDispatchDate(), request.getEstimatedDeliveryDate());
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
        if(shipment == null) {
            return null;
        }
        if(!repository.existsById(shipment.getShipmentId())) {
            return null;
        }
        return repository.save(shipment);
    }

    @Override
    public void delete(String id) {
        repository.deleteById(id);
    }
}
