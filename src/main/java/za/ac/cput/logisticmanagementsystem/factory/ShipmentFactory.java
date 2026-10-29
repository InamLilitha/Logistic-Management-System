package za.ac.cput.logisticmanagementsystem.factory;

import za.ac.cput.logisticmanagementsystem.domain.Shipment;

import java.time.LocalDate;
import java.util.UUID;

/**

 * ShipmentFactory.java
 * Author: Inam Ngqokomashe
 * 222660155
 */
public class ShipmentFactory {

    private ShipmentFactory() {

    }


    public static Shipment createShipment(
            double weight,
            String origin,
            String destination,
            LocalDate dispatchDate,
            String cargoType) {

        return createShipment(
                weight,
                origin,
                destination,
                dispatchDate,
                cargoType,
                null,
                null
        );
    }


    public static Shipment createShipment(
            double weight,
            String origin,
            String destination,
            LocalDate dispatchDate,
            String cargoType,
            String inventoryId,
            String contractId) {

        if (!Double.isFinite(weight) || weight <= 0) {
            throw new IllegalArgumentException(
                    "Weight has to be greater than zero"
            );
        }

        if (origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Origin has to be specified"
            );
        }

        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Destination has to be specified"
            );
        }

        String normalizedOrigin = origin.trim();
        String normalizedDestination = destination.trim();

        if (normalizedOrigin.equalsIgnoreCase(normalizedDestination)) {
            throw new IllegalArgumentException(
                    "Origin and destination cannot be the same"
            );
        }

        if (dispatchDate == null) {
            throw new IllegalArgumentException(
                    "Dispatch date is required"
            );
        }

        if (cargoType == null || cargoType.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Cargo type is required"
            );
        }

        String normalizedCargoType = normalizeCargoType(cargoType);

        String normalizedInventoryId = normalizeOptionalId(
                inventoryId,
                "Inventory ID"
        );

        String normalizedContractId = normalizeOptionalId(
                contractId,
                "Contract ID"
        );

        LocalDate estimatedDeliveryDate = dispatchDate.plusDays(12);

        return new Shipment.Builder()
                .shipmentId(generateShipmentId())
                .weight(weight)
                .origin(normalizedOrigin)
                .destination(normalizedDestination)
                .dispatchDate(dispatchDate)
                .estimatedDeliveryDate(estimatedDeliveryDate)
                .cargoType(normalizedCargoType)
                .inventoryId(normalizedInventoryId)
                .contractId(normalizedContractId)
                .build();
    }

    private static String normalizeCargoType(String cargoType) {
        String normalized = cargoType.trim();


        if (normalized.equalsIgnoreCase("General Cargo")) {
            return "General Cargo";
        }

        if (normalized.equalsIgnoreCase("Fragile")) {
            return "Fragile";
        }

        if (normalized.equalsIgnoreCase("Perishable Goods")) {
            return "Perishable Goods";
        }

        if (normalized.equalsIgnoreCase("Hazardous Materials")) {
            return "Hazardous Materials";
        }

        throw new IllegalArgumentException(
                "Invalid cargo type. Must be General Cargo, Fragile, " +
                        "Perishable Goods, or Hazardous Materials"
        );


    }

    private static String normalizeOptionalId(
            String id,
            String fieldName) {


        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        return id.trim();


    }

    private static String generateShipmentId() {
        return "SHIP-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }
}
