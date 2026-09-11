package za.ac.cput.logisticmanagementsystem.domain;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDate;
import java.util.Objects;

@Entity
public class ShipmentTracking {

    @Id
    private String trackingId;
    private String trackingNumber;
    private String currentLocation;
    private String shipmentStatus;
    private LocalDate dateCreated;

    // Relationship
    @OneToOne
    @JoinColumn(name = "shipment_id")
    private Shipment shipment;

    // Private constructor
    private ShipmentTracking(Builder builder) {
        this.trackingId = builder.trackingId;
        this.trackingNumber = builder.trackingNumber;
        this.currentLocation = builder.currentLocation;
        this.shipmentStatus = builder.shipmentStatus;
        this.dateCreated = builder.dateCreated;
        this.shipment = builder.shipment;
    }

    // Getters
    public String getTrackingId() {
        return trackingId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getShipmentStatus() {
        return shipmentStatus;
    }

    public LocalDate getDateCreated() {
        return dateCreated;
    }

    public Shipment getShipment() {
        return shipment;
    }

    // Builder Class
    public static class Builder {

        private String trackingId;
        private String trackingNumber;
        private String currentLocation;
        private String shipmentStatus;
        private LocalDate dateCreated;
        private Shipment shipment;

        public Builder setTrackingId(String trackingId) {
            this.trackingId = trackingId;
            return this;
        }

        public Builder setTrackingNumber(String trackingNumber) {
            this.trackingNumber = trackingNumber;
            return this;
        }

        public Builder setCurrentLocation(String currentLocation) {
            this.currentLocation = currentLocation;
            return this;
        }

        public Builder setShipmentStatus(String shipmentStatus) {
            this.shipmentStatus = shipmentStatus;
            return this;
        }

        public Builder setDateCreated(LocalDate dateCreated) {
            this.dateCreated = dateCreated;
            return this;
        }

        public Builder setShipment(Shipment shipment) {
            this.shipment = shipment;
            return this;
        }

        public Builder copy(ShipmentTracking shipmentTracking) {
            this.trackingId = shipmentTracking.trackingId;
            this.trackingNumber = shipmentTracking.trackingNumber;
            this.currentLocation = shipmentTracking.currentLocation;
            this.shipmentStatus = shipmentTracking.shipmentStatus;
            this.dateCreated = shipmentTracking.dateCreated;
            this.shipment = shipmentTracking.shipment;
            return this;
        }

        public ShipmentTracking build() {
            return new ShipmentTracking(this);
        }
    }

    @Override
    public String toString() {
        return "ShipmentTracking{" +
                "trackingId='" + trackingId + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", currentLocation='" + currentLocation + '\'' +
                ", shipmentStatus='" + shipmentStatus + '\'' +
                ", dateCreated=" + dateCreated +
                ", shipment=" + shipment +
                '}';
    }




}