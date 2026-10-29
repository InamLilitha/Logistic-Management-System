package za.ac.cput.logisticmanagementsystem.domain;
        /*
         * Shipment.java
         * Author: Inam Ngqokomashe
         * 222660155
         */
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "shipment")
public class Shipment {

    @Id
    @Column(name = "shipment_id", nullable = false)
    private String shipmentId;

    @Column(name = "weight", nullable = false)
    private double weight;

    @Column(name = "origin", nullable = false)
    private String origin;

    @Column(name = "destination", nullable = false)
    private String destination;

    @Column(name = "dispatch_date", nullable = false)
    private LocalDate dispatchDate;

    @Column(name = "estimated_delivery_date", nullable = false)
    private LocalDate estimatedDeliveryDate;

    @Column(name = "cargo_type", nullable = false)
    private String cargoType;

    @Column(name = "inventory_id")
    private String inventoryId;

    @Column(name = "contract_id")
    private String contractId;

    protected Shipment() {
        // Required by JPA
    }

    public Shipment(Builder builder) {
        this.shipmentId = builder.shipmentId;
        this.weight = builder.weight;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.dispatchDate = builder.dispatchDate;
        this.estimatedDeliveryDate = builder.estimatedDeliveryDate;
        this.cargoType = builder.cargoType;
        this.inventoryId = builder.inventoryId;
        this.contractId = builder.contractId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public double getWeight() {
        return weight;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDispatchDate() {
        return dispatchDate;
    }

    public LocalDate getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public String getCargoType() {
        return cargoType;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getContractId() {
        return contractId;
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId='" + shipmentId + '\'' +
                ", weight=" + weight +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", dispatchDate=" + dispatchDate +
                ", estimatedDeliveryDate=" + estimatedDeliveryDate +
                ", cargoType='" + cargoType + '\'' +
                ", inventoryId='" + inventoryId + '\'' +
                ", contractId='" + contractId + '\'' +
                '}';
    }

    public static class Builder {

        private String shipmentId;
        private double weight;
        private String origin;
        private String destination;
        private LocalDate dispatchDate;
        private LocalDate estimatedDeliveryDate;
        private String cargoType;
        private String inventoryId;
        private String contractId;

        public Builder shipmentId(String shipmentId) {
            this.shipmentId = shipmentId;
            return this;
        }

        public Builder weight(double weight) {
            this.weight = weight;
            return this;
        }

        public Builder origin(String origin) {
            this.origin = origin;
            return this;
        }

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder dispatchDate(LocalDate dispatchDate) {
            this.dispatchDate = dispatchDate;
            return this;
        }

        public Builder estimatedDeliveryDate(LocalDate estimatedDeliveryDate) {
            this.estimatedDeliveryDate = estimatedDeliveryDate;
            return this;
        }

        public Builder cargoType(String cargoType) {
            this.cargoType = cargoType;
            return this;
        }

        public Builder inventoryId(String inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public Builder contractId(String contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder copy(Shipment shipment) {
            this.shipmentId = shipment.shipmentId;
            this.weight = shipment.weight;
            this.origin = shipment.origin;
            this.destination = shipment.destination;
            this.dispatchDate = shipment.dispatchDate;
            this.estimatedDeliveryDate = shipment.estimatedDeliveryDate;
            this.cargoType = shipment.cargoType;
            this.inventoryId = shipment.inventoryId;
            this.contractId = shipment.contractId;
            return this;
        }

        public Shipment build() {
            return new Shipment(this);
        }
    }
}

