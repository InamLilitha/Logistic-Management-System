package za.ac.cput.logisticmanagementsystem.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

/**
 * Invoice.java
 * Domain class for Invoice entity
 * Author: Tebogo Pii 230226442
 * Date: 25-26 July 2026
 */

@Entity
public class Invoice{

    @Id
    public String invoiceId;

    public String customerName;
    public String description;
    public double total;
    public String paymentStatus;
    public Date dateIssued;
    public Date dueDate;

    private Invoice(Builder builder) {
        this.invoiceId = builder.invoiceId;
        this.customerName = builder.customerName;
        this.description = builder.description;
        this.total = builder.total;
        this.paymentStatus = builder.paymentStatus;
        this.dateIssued = builder.dateIssued;
        this.dueDate = builder.dueDate;
    }

    public Invoice() {
    }


    public String getInvoiceId() {
        return invoiceId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDescription() {
        return description;
    }

    public double getTotal() {
        return total;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public Date getDateIssued() {
        return dateIssued;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public static class Builder {
        private String invoiceId;
        private String customerName;
        private String description;
        private double total;
        private String paymentStatus;
        private Date dateIssued;
        private Date dueDate;

        public Builder invoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
            return this;
        }

        public Builder customerName(String customerName) {
            this.customerName = customerName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder total(double total) {
            this.total = total;
            return this;
        }

        public Builder paymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
            return this;
        }

        public Builder dateIssued(Date dateIssued) {
            this.dateIssued = dateIssued;
            return this;
        }

        public Builder dueDate(Date dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", total=" + total +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", dateIssued=" + dateIssued +
                '}';
    }
}
