package za.ac.cput.logisticmanagementsystem.domain;

/*
 * Shipment.java
 * Author: Inam Ngqokomashe
 * 222660155
 * Date: 26 July 2026
 * Date Upated: November 2026
 */

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private CompanyDomain company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "contract_id")
    private ShippingContract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_staff_id", nullable = false)
    private StaffProfile createdByStaff;

    @JsonManagedReference("shipment-items")
    @OneToMany(
            mappedBy = "shipment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ShipmentItem> shipmentItems = new ArrayList<>();

    protected Shipment() {
    }

    public Shipment(Builder builder) {
        this.shipmentId = builder.shipmentId;
        this.weight = builder.weight;
        this.origin = builder.origin;
        this.destination = builder.destination;
        this.dispatchDate = builder.dispatchDate;
        this.estimatedDeliveryDate = builder.estimatedDeliveryDate;
        this.cargoType = builder.cargoType;
        this.company = builder.company;
        this.contract = builder.contract;
        this.createdByStaff = builder.createdByStaff;
        this.shipmentItems = builder.shipmentItems;
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

    public CompanyDomain getCompany() {
        return company;
    }

    public ShippingContract getContract() {
        return contract;
    }

    public StaffProfile getCreatedByStaff() {
        return createdByStaff;
    }

    public List<ShipmentItem> getShipmentItems() {
        return shipmentItems;
    }

    public void setShipmentItems(List<ShipmentItem> shipmentItems) {
        this.shipmentItems = shipmentItems;
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
                ", company=" + (company != null ? company.getCompanyId() : null) +
                ", contract=" + (contract != null ? contract.getContractId() : null) +
                ", createdByStaff=" + (createdByStaff != null ? createdByStaff.getId() : null) +
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
        private CompanyDomain company;
        private ShippingContract contract;
        private StaffProfile createdByStaff;
        private List<ShipmentItem> shipmentItems = new ArrayList<>();

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

        public Builder company(CompanyDomain company) {
            this.company = company;
            return this;
        }

        public Builder contract(ShippingContract contract) {
            this.contract = contract;
            return this;
        }

        public Builder createdByStaff(StaffProfile createdByStaff) {
            this.createdByStaff = createdByStaff;
            return this;
        }

        public Builder shipmentItems(List<ShipmentItem> shipmentItems) {
            this.shipmentItems = shipmentItems;
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
            this.company = shipment.company;
            this.contract = shipment.contract;
            this.createdByStaff = shipment.createdByStaff;
            this.shipmentItems = shipment.shipmentItems;

            return this;
        }

        public Shipment build() {
            return new Shipment(this);
        }
    }
}