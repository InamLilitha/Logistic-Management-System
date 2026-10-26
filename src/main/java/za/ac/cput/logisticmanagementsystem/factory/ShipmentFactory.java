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

    public static Shipment createShipment(
            double weight,
            String origin,
            String destination,
            LocalDate dispatchDate,
            String cargoType) {


        if(weight <=0 ){
        throw new IllegalArgumentException("Weight has to be greater than zero");
        }

        if(origin == null || origin.trim().isEmpty()) {
            throw new IllegalArgumentException("Origin has to be specified");
        }
        if(destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination has to be specified");
        }
        if(origin.trim().equalsIgnoreCase(destination.trim())) {
            throw new IllegalArgumentException("Origin and destination cannot be the same");
        }
        if(dispatchDate == null) {
            throw new IllegalArgumentException("Dispatch date is required");
        }
        LocalDate estimatedDeliveryDate = dispatchDate.plusDays(12);

        if(cargoType == null || cargoType.trim().isEmpty()) {
            throw new IllegalArgumentException("Cargo type is required");
        }
        String normalizedCargoType = cargoType.trim().toUpperCase();
        if (!normalizedCargoType.equalsIgnoreCase("General Cargo")
                && !normalizedCargoType.equalsIgnoreCase("Fragile")
                && !normalizedCargoType.equalsIgnoreCase("Perishable Goods")
                && !normalizedCargoType.equalsIgnoreCase("Hazardous Materials")) {

            throw new IllegalArgumentException(
                    "Invalid cargo type. Must be General Cargo, Fragile, Perishable Goods, or Hazardous Materials"
            );
        }



        return new Shipment.Builder()
                .shipmentId(generateShipmentId())
                .weight(weight)
                .origin(origin.trim())
                .destination(destination.trim())
                .dispatchDate(dispatchDate)
                .estimatedDeliveryDate(estimatedDeliveryDate)
                .cargoType(normalizedCargoType)
                .build();

    }

    private static String generateShipmentId() {
        return "SHIP-" + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 8)
                .toUpperCase();
    }

}
