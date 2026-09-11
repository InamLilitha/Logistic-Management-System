package za.ac.cput.logisticmanagementsystem.service.impl;

/**
 * ShipmentTrackingService.java
 * Service implementation for ShipmentTracking business logic
 * Author: Ryan Paledi 230969429
 */


import org.springframework.stereotype.Service;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentTracking;
import za.ac.cput.logisticmanagementsystem.repository.ShipmentTrackingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ShipmentTrackingService implements IShipmentTrackingService {

    private final ShipmentTrackingRepository repository;

    public ShipmentTrackingService(ShipmentTrackingRepository repository) {
        this.repository = repository;
    }

    @Override
    public ShipmentTracking create(ShipmentTracking shipmentTracking) {
        if (shipmentTracking == null) {
            return null;
        }
        return repository.save(shipmentTracking);
    }

    @Override
    public ShipmentTracking read(String trackingId) {
        Optional<ShipmentTracking> tracking = repository.findById(trackingId);
        return tracking.orElse(null);
    }

    @Override
    public ShipmentTracking update(ShipmentTracking shipmentTracking) {
        if (shipmentTracking == null) {
            return null;
        }
        return repository.save(shipmentTracking);
    }

    @Override
    public boolean delete(String trackingId) {
        try {
            repository.deleteById(trackingId);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<ShipmentTracking> getAll() {
        return repository.findAll();
    }

    @Override
    public ShipmentTracking updateShipmentStatus(String trackingId, String shipmentStatus) {

        ShipmentTracking tracking = read(trackingId);

        if (tracking != null) {

            ShipmentTracking updated = new ShipmentTracking.Builder()
                    .copy(tracking)
                    .setShipmentStatus(shipmentStatus)
                    .build();

            return repository.save(updated);
        }

        return null;
    }
}
