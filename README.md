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

An interactive viewer is available at [`erd.html`](erd.html) and a standalone high-resolution vector image at [`erd.svg`](erd.svg).

![Logistic Management System ERD](erd.svg)

```mermaid
erDiagram
    STAFF_PROFILES ||--o{ PASSWORD_RESET_REQUESTS : "initiates"
    STAFF_PROFILES ||--o{ SHIPMENT : "manages"
    COMPANY_DOMAIN ||--o{ INVENTORY : "owns_supplies"
    COMPANY_DOMAIN ||--o{ SHIPPING_CONTRACT : "holds"
    COMPANY_DOMAIN ||--o{ SHIPMENT : "client_or_carrier"
    SHIPPING_CONTRACT ||--o{ SHIPMENT : "governs"
    SHIPMENT ||--o{ SHIPMENT_ITEM : "contains"
    INVENTORY ||--o{ SHIPMENT_ITEM : "allocated_to"
    SHIPMENT ||--o| SHIPMENT_TRACKING : "tracked_by"
    SHIPMENT ||--o{ INVOICE : "billed_as"

    COMPANY_DOMAIN {
        string company_id PK
        string company_name
        string company_type "Client, Supplier, Carrier"
        string tax_id
        string phone_number
        string email_address
        boolean is_active
    }

    INVENTORY {
        string inventory_id PK
        string company_id FK
        string item_name
        string sku
        int quantity_available
        double unit_weight
    }

    SHIPPING_CONTRACT {
        string contract_id PK
        string company_id FK
        string contract_number
        double price_per_kg
        date contract_start_date
        date contract_expiry_date
    }

    SHIPMENT {
        string shipment_id PK
        string company_id FK
        string contract_id FK
        bigint created_by_staff_id FK
        double total_weight
        string origin
        string destination
        date dispatch_date
        date estimated_delivery_date
        string cargo_type
    }

    SHIPMENT_ITEM {
        string shipment_item_id PK
        string shipment_id FK
        string inventory_id FK
        int quantity_shipped
    }

    SHIPMENT_TRACKING {
        string tracking_id PK
        string shipment_id FK
        string tracking_number
        string current_location
        string shipment_status
        datetime date_created
    }

    INVOICE {
        string invoice_id PK
        string shipment_id FK
        string company_id FK
        string description
        double total_amount
        string payment_status
        datetime date_issued
        datetime due_date
    }

    STAFF_PROFILES {
        bigint id PK
        string username UK
        string email UK
        string name
        string password_hash
        string role
        boolean active
    }

    PASSWORD_RESET_REQUESTS {
        bigint id PK
        bigint staff_profile_id FK
        uuid token UK
        string status
        datetime created_at
        datetime expires_at
        datetime reviewed_at
        datetime completed_at
    }
```

## Inventory and Shipment Stock

The inventory page flags items with 10 or fewer units, separately calls out
zero-stock items, and supports searching and filtering by SKU and stock status.
Inventory CSV exports include `inventoryId`, `itemName`, `sku`,
`quantityAvailable`, `unitWeight`, and `companyId`. CSV imports require item
name, SKU, quantity, and unit weight; provide a valid company ID for new SKUs.
Matching SKUs are updated, and new SKUs are created.

Shipment creation can include inventory item IDs and quantities. Stock is
checked and deducted transactionally when the shipment is created; a request
that exceeds available stock is rejected without creating the shipment or
partially deducting stock.

## How to Run the Project

### Prerequisites

Install the following software:

- Java 17 or later
- MySQL 8 or later
- Node.js and npm

### 1. Configure MySQL

Start MySQL and create the database:

```sql
CREATE DATABASE logistics;
```

The backend is configured to connect to `localhost:3306/logistics` using the
credentials in
[`src/main/resources/application.properties`](src/main/resources/application.properties).
Update the datasource username and password in that file to match your local
MySQL account. Do not commit real passwords or email credentials to the
repository.

The application uses Hibernate's `update` setting, so the required tables are
created or updated automatically when the backend starts.

### 2. Start the Backend

From the project root, run:

**Windows:**

```powershell
.\mvnw.cmd spring-boot:run
```

**macOS/Linux:**

```bash
./mvnw spring-boot:run
```

The backend runs at [http://localhost:8080](http://localhost:8080).

To run the backend tests instead:

```powershell
.\mvnw.cmd test
```

### 3. Start the Frontend

Open a second terminal and run:

```powershell
cd frontend
npm install
npm run dev
```

The frontend runs at [http://localhost:5173](http://localhost:5173) and
communicates with the backend at `http://localhost:8080`.

Open the frontend URL in a browser after both the backend and MySQL are
running.

### Troubleshooting

- **Database connection failed:** Confirm that MySQL is running, the
  `logistics` database exists, and the credentials in
  `application.properties` are correct.
- **Port already in use:** Stop the process using port `8080` or `5173`, or
  change the corresponding application configuration.
- **Frontend dependencies are missing:** Run `npm install` from the
  `frontend` directory.
- **Mail features are unavailable:** Set the `MAIL_USERNAME` and corresponding
  mail credential environment variables before using email functionality.
