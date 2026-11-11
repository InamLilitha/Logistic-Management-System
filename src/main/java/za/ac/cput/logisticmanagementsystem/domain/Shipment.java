package za.ac.cput.logisticmanagementsystem.domain;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Shipment.java
 * Author: Inam Ngqokomashe
 * 222660155
 */
@Entity
@Table(name = "shipment")

public class Shipment {
    @Id
    private String shipmentId;

    @Column(nullable = false)
    private double weight;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDate dispatchDate;

    @Column(nullable = false)
    private LocalDate estimatedDeliveryDate;

    @Column(nullable = false)
    private String cargoType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shipment_items", joinColumns = @JoinColumn(name = "shipment_id"))
    private List<ShipmentItem> items = new ArrayList<>();

    protected Shipment(){
    }


    public Shipment(Builder builder) {
        this.shipmentId = builder.shipmentId;
        this.weight = builder.weight;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.dispatchDate = builder.dispatchDate;
        this.estimatedDeliveryDate = builder.estimatedDeliveryDate;
        this.cargoType = builder.cargoType;
        this.items = new ArrayList<>(builder.items);
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

    @JsonProperty("items")
    public List<ShipmentItem> getItems() {
        return items == null ? List.of() : new ArrayList<>(items);
    }

    @JsonProperty("items")
    public void setItems(List<ShipmentItem> items) {
        this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return "Shipment{" +
                "shipmentId='" + shipmentId + '\'' +
                ", weight=" + weight + " Kg" +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", dispatchDate=" + dispatchDate +
                ", estimatedDeliveryDate=" + estimatedDeliveryDate +
                ", cargoType='" + cargoType + '\'' +
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
        private List<ShipmentItem> items = new ArrayList<>();


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
        public Builder items(List<ShipmentItem> items) {
            this.items = items == null ? new ArrayList<>() : new ArrayList<>(items);
            return this;
        }
        public Shipment build() {
            return new Shipment(this);
        }
    }
}
