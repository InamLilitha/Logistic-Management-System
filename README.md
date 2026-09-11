# ADP Capstone Project
### Applications Development Practice 3 — Assignment 1

## Team Members
| Name | Student Number | Role/Focus |
| :--- | :--- | :--- |
| Siphamandla | 240256891 | |
| Ryan Paledi | 230969429 | |
| Maghdie Petersen | 230600204 | |
| Simphiwe | 221549323 | |
| Inam Ngqokomashe | 222660155 | |
| Tebogo Pii | 230226442 | Invoice |

## Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    Company ||--|| Inventory : "owns"
    Company ||--|| ShippingContract : "holds"
    Inventory ||--|| Shipment : "assigned to"
    ShippingContract ||--|| Shipment : "applies to"
    Shipment ||--|| ShipmentTracking : "tracked by"
    Shipment ||--|| Invoice : "generates"

    Company {
        String companyId PK
        String companyName
        String taxId
        String phoneNumber
        String emailAddress
        boolean isActive
    }

    Inventory {
        String inventoryId PK
        String itemName
        String sku
        int quantityAvailable
        double unitWeight
        String companyId FK
    }

    ShippingContract {
        String contractId PK
        String contractNumber
        double pricePerKg
        LocalDate contractStartDate
        LocalDate contractExpiryDate
        String companyId FK
    }

    Shipment {
        String shipmentId PK
        double weight
        String origin
        String destination
        LocalDate dispatchDate
        LocalDate estimatedDeliveryDate
        String inventoryId FK
        String contractId FK
    }

    ShipmentTracking {
        String trackingId PK
        String trackingNumber
        String currentLocation
        String shipmentStatus
        LocalDate dateCreated
        String shipmentId FK
    }

    Invoice {
        String invoiceId PK
        double total
        String paymentStatus
        LocalDate dateIssued
        String shipmentId FK
    }
