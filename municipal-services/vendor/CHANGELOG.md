
# Changelog
All notable changes to this module will be documented in this file.

## 1.3.0 - 2023-03-31

 - Introduced seperate tab for vendor and driver.
 - Seperated the Apis -Create, update and search for both vendor and driver.
 - linking and delinking  of driver with vendor introduced seperatly.

## 1.2.0 - 2022-08-04

- Vehicle logging at FSTP decoupled from FSM module 

## 1.0.3 - 2022-01-13

- Updated to log4j2 version 2.17.1

## 1.0.2

- Vendor creation validation is added for same mobile number.
- DSO roles are made configurable.
- Plain search service is added.

## 1.0.1

- Fixed security issue of untrusted data pass as user input.

## 1.0.0

- base version


## 1.4.0 - 2026-10-01

- Added `serviceType` support in `additionalDetails` for Vendor and Driver.
- Default `serviceType` to `"FSM"` if not provided during creation.

### Database Migration
If migrating existing legacy records to support `serviceType`, execute the following queries in `vendor_db`:

```sql
-- Vendor Table
UPDATE eg_vendor 
SET additionaldetails = '{"serviceType": "FSM"}'::jsonb 
WHERE additionaldetails IS NULL OR additionaldetails = 'null'::jsonb;

UPDATE eg_vendor 
SET additionaldetails = jsonb_set(additionaldetails, '{serviceType}', '"FSM"') 
WHERE additionaldetails->>'serviceType' IS NULL;

-- Vendor Auditlog Table
UPDATE eg_vendor_auditlog 
SET additionaldetails = '{"serviceType": "FSM"}'::jsonb 
WHERE additionaldetails IS NULL OR additionaldetails = 'null'::jsonb;

UPDATE eg_vendor_auditlog 
SET additionaldetails = jsonb_set(additionaldetails, '{serviceType}', '"FSM"') 
WHERE additionaldetails->>'serviceType' IS NULL;

-- Driver Table
UPDATE eg_driver 
SET additionaldetails = '{"serviceType": "FSM"}'::jsonb 
WHERE additionaldetails IS NULL OR additionaldetails = 'null'::jsonb;

UPDATE eg_driver 
SET additionaldetails = jsonb_set(additionaldetails, '{serviceType}', '"FSM"') 
WHERE additionaldetails->>'serviceType' IS NULL;

-- Driver Auditlog Table
UPDATE eg_driver_auditlog 
SET additionaldetails = '{"serviceType": "FSM"}'::jsonb 
WHERE additionaldetails IS NULL OR additionaldetails = 'null'::jsonb;

UPDATE eg_driver_auditlog 
SET additionaldetails = jsonb_set(additionaldetails, '{serviceType}', '"FSM"') 
WHERE additionaldetails->>'serviceType' IS NULL;


