package za.ac.cput.logisticmanagementsystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;

@Entity
public class ShippingContract {

    @Id
    private String contractId;
    private String contractNumber;
    private Double pricePerKg;
    private LocalDate contractStartDate;
    private LocalDate contractExpiryDate;

    protected ShippingContract() {
    }

    private ShippingContract(Builder builder) {
        this.contractId = builder.contractId;
        this.contractNumber = builder.contractNumber;
        this.pricePerKg = builder.pricePerKg;
        this.contractStartDate = builder.contractStartDate;
        this.contractExpiryDate = builder.contractExpiryDate;
    }

    // Getters

    public String getContractId() {
        return contractId;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public Double getPricePerKg() {
        return pricePerKg;
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public LocalDate getContractExpiryDate() {
        return contractExpiryDate;
    }

    // Business Logic Methods

    public boolean isContractValid() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(contractStartDate) && !today.isAfter(contractExpiryDate);
    }

    public boolean isExpired() {
        return LocalDate.now().isAfter(contractExpiryDate);
    }

    public boolean isActive() {
        return isContractValid();
    }

    public double calculateTotalCost(double weightInKg) {
        if (weightInKg <= 0) {
            throw new IllegalArgumentException("Weight must be greater than 0");
        }
        return this.pricePerKg * weightInKg;
    }

    public boolean isWithinValidPeriod(LocalDate date) {
        return !date.isBefore(contractStartDate) && !date.isAfter(contractExpiryDate);
    }


    @Override
    public String toString() {
        return "ShippingContract{" +
                "contractId='" + contractId + '\'' +
                ", contractNumber='" + contractNumber + '\'' +
                ", pricePerKg=" + pricePerKg +
                ", contractStartDate=" + contractStartDate +
                ", contractExpiryDate=" + contractExpiryDate +
                '}';
    }

    // Builder Class
    public static class Builder {
        private String contractId;
        private String contractNumber;
        private Double pricePerKg;
        private LocalDate contractStartDate;
        private LocalDate contractExpiryDate;

        public Builder setContractId(String contractId) {
            this.contractId = contractId;
            return this;
        }

        public Builder setContractNumber(String contractNumber) {
            this.contractNumber = contractNumber;
            return this;
        }

        public Builder setPricePerKgString(Double pricePerKg) {
            this.pricePerKg = pricePerKg;
            return this;
        }

        public Builder setContractStartDate(LocalDate contractStartDate) {
            this.contractStartDate = contractStartDate;
            return this;
        }

        public Builder setContractExpiryDate(LocalDate contractExpiryDate) {
            this.contractExpiryDate = contractExpiryDate;
            return this;
        }

        public ShippingContract build() {

            // Validation before building
            if (this.pricePerKg == null || this.pricePerKg <= 0) {
                throw new IllegalArgumentException("Price per kg must be positive");
            }
            if (this.contractStartDate == null || this.contractExpiryDate == null) {
                throw new IllegalArgumentException("Start date and expiry date cannot be null");
            }
            if (this.contractStartDate.isAfter(this.contractExpiryDate)) {
                throw new IllegalArgumentException("Start date cannot be after expiry date");
            }
            if (this.contractNumber == null || this.contractNumber.isEmpty()) {
                throw new IllegalArgumentException("Contract number cannot be empty");
            }
            return new ShippingContract(this);
        }
    }
}


