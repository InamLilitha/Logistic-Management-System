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

    public double total;
    public String paymentStatus;
    public Date dateIssued;

    private Invoice(Builder builder) {
        this.invoiceId = builder.invoiceId;
        this.total = builder.total;
        this.paymentStatus = builder.paymentStatus;
        this.dateIssued = builder.dateIssued;
    }

    public Invoice() {
    }


    public String getInvoiceId() {
        return invoiceId;
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

    public static class Builder {
        private String invoiceId;
        private double total;
        private String paymentStatus;
        private Date dateIssued;

        public Builder invoiceId(String invoiceId) {
            this.invoiceId = invoiceId;
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
