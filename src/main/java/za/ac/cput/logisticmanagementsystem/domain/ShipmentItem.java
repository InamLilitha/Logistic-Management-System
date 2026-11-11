package za.ac.cput.logisticmanagementsystem.domain;

/*
 * ShipmentItem.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 05 November 2026
 */

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "shipment_item")
public class ShipmentItem {

    @Id
    @Column(name = "shipment_item_id", nullable = false)
    private String shipmentItemId;

    @JsonBackReference("shipment-items")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "quantity_shipped", nullable = false)
    private int quantityShipped;

    protected ShipmentItem() {
    }

    public ShipmentItem(Builder builder) {

        this.shipmentItemId = builder.shipmentItemId;
        this.shipment = builder.shipment;
        this.inventory = builder.inventory;
        this.quantityShipped = builder.quantityShipped;
    }

    public String getShipmentItemId() {
        return shipmentItemId;
    }

    public Shipment getShipment() {
        return shipment;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getQuantityShipped() {
        return quantityShipped;
    }

    @Override
    public String toString() {
        return "ShipmentItem{" +
                "shipmentItemId='" + shipmentItemId + '\'' +
                ", inventory=" +
                inventory +
                ", quantityShipped=" +
                quantityShipped +
                '}';
    }

    public static class Builder {

        private String shipmentItemId;
        private Shipment shipment;
        private Inventory inventory;
        private int quantityShipped;

        public Builder shipmentItemId(String shipmentItemId) {
            this.shipmentItemId = shipmentItemId;
            return this;
        }

        public Builder shipment(Shipment shipment) {
            this.shipment = shipment;
            return this;
        }

        public Builder inventory(Inventory inventory) {
            this.inventory = inventory;
            return this;
        }

        public Builder quantityShipped(int quantityShipped) {
            this.quantityShipped = quantityShipped;
            return this;
        }

        public ShipmentItem build() {
            return new ShipmentItem(this);
        }
    }
}
