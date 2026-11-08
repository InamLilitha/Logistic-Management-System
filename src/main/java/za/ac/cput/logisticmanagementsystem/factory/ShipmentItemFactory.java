package za.ac.cput.logisticmanagementsystem.factory;
/*
 * ShipmentItemFactory.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 05 November 2026
 */
import za.ac.cput.logisticmanagementsystem.domain.Inventory;
import za.ac.cput.logisticmanagementsystem.domain.Shipment;
import za.ac.cput.logisticmanagementsystem.domain.ShipmentItem;

import java.util.UUID;

public class ShipmentItemFactory {

    private ShipmentItemFactory() {
    }

    public static ShipmentItem createShipmentItem(
            Shipment shipment,
            Inventory inventory,
            int quantityShipped) {

        if (shipment == null) {
            throw new IllegalArgumentException(
                    "Shipment is required"
            );
        }

        if (inventory == null) {
            throw new IllegalArgumentException(
                    "Inventory is required"
            );
        }

        if (quantityShipped <= 0) {
            throw new IllegalArgumentException(
                    "Quantity shipped must be greater than zero"
            );
        }

        return new ShipmentItem.Builder()
                .shipmentItemId(generateShipmentItemId())
                .shipment(shipment)
                .inventory(inventory)
                .quantityShipped(quantityShipped)
                .build();
    }

    private static String generateShipmentItemId() {

        return "SI-" +
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();
    }
}