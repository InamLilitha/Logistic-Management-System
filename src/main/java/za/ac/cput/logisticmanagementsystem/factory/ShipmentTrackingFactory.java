package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentTracking;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class ShipmentTrackingFactory {

    public static ShipmentTracking createShipmentTracking(
            String currentLocation,
            String shipmentStatus,
            Shipment shipment) {

        return new ShipmentTracking.Builder()
                .setTrackingId(UUID.randomUUID().toString())
                .setTrackingNumber("TRK-" + System.currentTimeMillis())
                .setCurrentLocation(currentLocation)
                .setShipmentStatus(shipmentStatus)
                .setDateCreated(LocalDate.from(LocalDateTime.now()))
                .setShipment(shipment)
                .build();
    }
}