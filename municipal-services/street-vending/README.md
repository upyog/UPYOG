# Street Vending Service (egov-street-vending)

## Overview
The **Street Vending Service** enables registration, management, and tracking of vendors operating in urban areas under the Street Vendors Act, 2014. It manages vendor details, vending zones, documents, and workflows, ensuring a streamlined process for ULBs.

---

## DB UML Diagram
- **Not Applicable (NA)**

---

## Service Dependencies
- **egov-user** (User Service)
- **egov-mdms** (Master Data Management Service)
- **egov-workflow** (Workflow Service)
- **egov-idgen** (ID Generation Service)
- **egov-filestore** (File Store Service)
- **egov-location** (Location Service)
- **egov-localization** (Localization Service)
- **egov-boundary** (Optional Boundary Service)

---

## Swagger API Contract
- [Click here for Swagger Contract](https://editor.swagger.io/?url=https://raw.githubusercontent.com/upyog/UPYOG/master/business-services/Docs/street-vending.yaml)

---

## Entities

### a) Vendor Details
- Stores personal and contact details.
- Includes unique **Vendor Code**, **Aadhaar**, **Phone Number**.

**Constraints:**
- Phone number & Vendor Code must be unique.
- Aadhaar or valid ID proof required.

---

### b) Vending Details
- Information about vending type (Stationary/Mobile).
- Includes vending zone, time, and location.

**Constraints:**
- Zones must be pre-configured via MDMS.
- One vending location per vendor at a time.

---

### c) Document Details
- Uploads ID proof, license, NOC, and supporting documents.

**Constraints:**
- Documents stored via **egov-filestore**.
- Mandatory documents can be configured in MDMS.

---

### d) Workflow Status
- Tracks vendor status through workflow states:
  - `APPLIED`, `VERIFIED`, `APPROVED`, `REJECTED`

**Constraints:**
- Workflow transitions must be configured in **egov-workflow**.
- Role-based access enforced.

---

## Configurable Properties

| Environment Variable                  | Description                                                             | Example Value                                      |
|--------------------------------------|-------------------------------------------------------------------------|---------------------------------------------------|
| `egov.sv.default.pagination.limit`   | Default search result page size                                         | `100`                                             |
| `egov.idgen.vendor.code.name`        | Key name used in IDGen for vendor codes                                 | `sv.vendor.code`                                  |
| `egov.idgen.vendor.code.format`      | Format to generate vendor code                                          | `VENDOR-[city]-[SEQ_SV_VENDOR_CODE]`              |
| `egov.sv.notification.enabled`       | Enables/disables SMS notifications                                      | `true`                                            |
| `egov.sv.portal.link`                | Portal login URL for vendors                                            | `https://upyog.niua.org/digit-ui/vendor/login`    |

---

## API Details

**BasePath:** `/egov-street-vending/vendors`

### a) Create Vendor
- **POST** `/egov-street-vending/vendors/_create`
- Registers a new vendor with all details.

---

### b) Update Vendor
- **POST** `/egov-street-vending/vendors/_update`
- Updates vendor details or progresses workflow status.

---

### c) Search Vendors
- **POST** `/egov-street-vending/vendors/_search`
- Search based on `ID`, `Name`, `Code`, `Status`, `Phone`, `Zone`.

---

### d) Count Vendors
- **POST** `/egov-street-vending/vendors/_count`
- Returns count of vendors by `status`.

---

## Kafka Topics

### Producer Topics
| Topic Name               | Purpose                                     |
|--------------------------|---------------------------------------------|
| `save-sv-vendor`         | Publishes vendor creation events            |
| `update-sv-vendor`       | Publishes vendor update events              |
| `egov.core.notification.sms` | Sends SMS notifications                  |

### Consumer Topics
- **None**

---

## Notification
- SMS notifications triggered on:
  - Successful registration
  - Application approval or rejection
- Templates configured via **egov-localization** service.

---

