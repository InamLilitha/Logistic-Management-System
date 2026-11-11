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

## Inventory and shipment stock

The inventory page flags items with 10 or fewer units, separately calls out zero-stock items, and supports searching and filtering by SKU and stock status. Inventory CSV exports include `inventoryId`, `itemName`, `sku`, `quantityAvailable`, `unitWeight`, and `companyId`. CSV imports require item name, SKU, quantity, and unit weight; provide a valid company ID for new SKUs. Matching SKUs are updated, and new SKUs are created.

Shipment creation can include inventory item IDs and quantities. Stock is checked and deducted transactionally when the shipment is created; a request that exceeds available stock is rejected without creating the shipment or partially deducting stock.mermaid
erDiagram
    Company ||--|| Inventory : "owns"
    Company ||--|| ShippingContract : "holds"
    Inventory ||--o{ ShipmentItem : "selected for"
    Shipment ||--|{ ShipmentItem : "contains"
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
        String contractId FK
    }

    ShipmentItem {
        String shipmentId FK
        String inventoryId FK
        String sku
        int quantity
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
