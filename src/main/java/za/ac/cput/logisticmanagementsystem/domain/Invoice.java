package za.ac.cput.logisticmanagementsystem.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Invoice.java
 * Domain class for Invoice entity
 * Author: Tebogo Pii 230226442
 * Date: 25-26 July 2026 / Updated: November 2026
 */

@Entity
@Table(name = "invoice")
public class Invoice implements Serializable {

    @Id
    @Column(name = "invoice_id")
    public String invoiceId;

    @Column(name = "shipment_id")
    public String shipmentId;

    @Column(name = "company_id")
    public String companyId;

    public String customerName;
    public String description;

    @Column(name = "total_amount")
    public double total;

    public String paymentStatus;
    public Date dateIssued;
    public Date dueDate;

    public Invoice() {
    }

    private Invoice(Builder builder) {
        this.invoiceId = builder.invoiceId;
        this.shipmentId = builder.shipmentId;
        this.companyId = builder.companyId;
        this.customerName = builder.customerName;
        this.description = builder.description;
        this.total = builder.total;
        this.paymentStatus = builder.paymentStatus;
        this.dateIssued = builder.dateIssued;
        this.dueDate = builder.dueDate;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public String getShipmentId() {
        return shipmentId;
    }

    public String getCompanyId() {
        return companyId;
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

    public double getTotalAmount() {
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
        private String shipmentId;
        private String companyId;
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

        public Builder shipmentId(String shipmentId) {
            this.shipmentId = shipmentId;
            return this;
        }

        public Builder companyId(String companyId) {
            this.companyId = companyId;
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

        public Builder totalAmount(double totalAmount) {
            this.total = totalAmount;
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

        public Builder copy(Invoice invoice) {
            this.invoiceId = invoice.invoiceId;
            this.shipmentId = invoice.shipmentId;
            this.companyId = invoice.companyId;
            this.customerName = invoice.customerName;
            this.description = invoice.description;
            this.total = invoice.total;
            this.paymentStatus = invoice.paymentStatus;
            this.dateIssued = invoice.dateIssued;
            this.dueDate = invoice.dueDate;
            return this;
        }

        public Invoice build() {
            return new Invoice(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Invoice invoice = (Invoice) o;
        return Objects.equals(invoiceId, invoice.invoiceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(invoiceId);
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceId='" + invoiceId + '\'' +
                ", shipmentId='" + shipmentId + '\'' +
                ", companyId='" + companyId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", description='" + description + '\'' +
                ", total=" + total +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", dateIssued=" + dateIssued +
                ", dueDate=" + dueDate +
                '}';
    }
}
