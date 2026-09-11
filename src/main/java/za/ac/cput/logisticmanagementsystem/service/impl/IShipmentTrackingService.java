package za.ac.cput.logisticmanagementsystem.service.impl;

/**
 * Service interface for ShipmentTracking business logic
 * Author: Ryan Paledi 230969429
 * Date: 29 July 2026
 */

import za.ac.cput.logisticmanagementsystem.domain.ShipmentTracking;

import java.util.List;

public interface IShipmentTrackingService {

    ShipmentTracking create(ShipmentTracking shipmentTracking);

    ShipmentTracking read(String trackingId);

    ShipmentTracking update(ShipmentTracking shipmentTracking);

    boolean delete(String trackingId);

    List<ShipmentTracking> getAll();

    ShipmentTracking updateShipmentStatus(String trackingId, String shipmentStatus);
}