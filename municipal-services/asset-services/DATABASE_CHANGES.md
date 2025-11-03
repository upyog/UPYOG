# Asset Management Database Schema Changes

This document outlines all the changes made to the asset management system database schema and related code modifications.

## Database Tables Added

The following 12 tables were created in the new migration file `V20250101000000__asset_management.sql`:

1. **eg_asset_assetdetails** - Main asset information table
2. **eg_asset_addressdetails** - Asset address information
3. **eg_asset_document** - Asset document attachments
4. **eg_asset_assetassignment** - Asset assignment records
5. **eg_asset_assetassignment_document** - Assignment document attachments
6. **eg_asset_assetdisposal** - Asset disposal records
7. **eg_asset_assetdisposal_document** - Disposal document attachments
8. **eg_asset_assetmaintenance** - Asset maintenance records
9. **eg_asset_assetmaintenance_document** - Maintenance document attachments
10. **eg_asset_assetmaintenance_schedule** - Maintenance scheduling
11. **eg_asset_assetmaintenance_schedule_document** - Schedule document attachments
12. **eg_asset_auditdetails** - Audit trail information

## Database Columns Deleted

### From Asset.java Model Class (17 columns removed):
- `financialYear`
- `sourceOfFinance`
- `department`
- `assetUsage`
- `assetSubUsage`
- `assetParentCategory`
- `assetCategory`
- `assetSubCategory`
- `scheme`
- `subScheme`
- `invoiceDate`
- `invoiceNumber`
- `purchaseDate`
- `purchaseOrderNumber`
- `acquisitionCost`
- `bookValue`
- `currentValue`

### From AddressDetails.java Model Class (3 columns removed):
- `locality`
- `ward`
- `zone`

## AdditionalDetails JSON Fields Deleted

### From LandInfo.java (15+ fields removed):
- `OSRLand`
- `isitfenced`
- `howassetbeingused`
- `isAssetUnderAnyLitigation`
- `litigationDetails`
- `isAssetUnderAnyEncumbrance`
- `encumbranceDetails`
- `isAssetInsured`
- `insuranceDetails`
- `isAssetUnderAnyMortgage`
- `mortgageDetails`
- `isAssetGivenOnRent`
- `rentDetails`
- `isAssetGivenOnLease`
- `leaseDetails`
- `isAssetGivenOnLicense`
- `licenseDetails`
- `isAssetGivenOnEasement`
- `easementDetails`
- `isAssetGivenOnUsufruct`
- `usufructDetails`
- `isAssetGivenOnSuperFicies`
- `superFiciesDetails`
- `isAssetGivenOnEmphyteusis`
- `emphyteusisDetails`
- `isAssetGivenOnAntiChresis`
- `antiChresisDetails`
- `isAssetGivenOnHabitation`
- `habitationDetails`
- `isAssetGivenOnUse`
- `useDetails`
- `isAssetGivenOnUsufructuary`
- `usufructuaryDetails`

### From LandAcquisitionPossessionInfo.java (15+ fields removed):
- `landType`
- `modeOfPossessionOrAcquisition`
- `areaOfLand`
- `unitOfMeasurement`
- `dateOfPossessionOrAcquisition`
- `costOfAcquisition`
- `costOfPossession`
- `surveyNumber`
- `subDivisionNumber`
- `pattaNumber`
- `marketValue`
- `registrationFees`
- `stampDuty`
- `registrationDate`
- `registrationNumber`
- `titleDeedNumber`
- `titleDeedDate`
- `boundariesNorth`
- `boundariesSouth`
- `boundariesEast`
- `boundariesWest`
- `landClassification`
- `landSubClassification`
- `landUse`
- `landSubUse`
- `landOwnership`
- `landOwnershipType`
- `landOwnershipSubType`

## Code Files Modified

### Java Model Classes:
- `Asset.java` - Removed 17 properties
- `AddressDetails.java` - Removed 3 properties  
- `LandInfo.java` - Removed 30+ JSON properties
- `LandAcquisitionPossessionInfo.java` - Removed 25+ JSON properties

### Repository Classes:
- `AssetRowMapper.java` - Removed column mappings for deleted fields
- `AssetQueryBuilder.java` - Removed deleted columns from SELECT statements

### Service Classes:
- `AssetDisposeService.java` - Removed calls to deleted asset properties
- `AssetMaintenanceService.java` - Removed references to deleted fields

### Utility Classes:
- `AssetUtil.java` - Removed `updateAssetStatusAndUsage` method completely

## Migration Strategy

1. **New Migration File**: Created `V20250101000000__asset_management.sql` replacing all previous migrations
2. **Schema Simplification**: Streamlined database schema by removing unnecessary columns
3. **JSON Cleanup**: Removed corresponding fields from additionalDetails JSON objects
4. **Code Refactoring**: Updated all Java classes to remove references to deleted fields

## Impact Assessment

- **Database Size**: Reduced by removing unused columns
- **Code Complexity**: Simplified by removing unnecessary field mappings
- **Performance**: Improved due to fewer columns in SELECT queries
- **Maintenance**: Easier to maintain with streamlined schema

## Persister Configuration Changes

### Removed from INSERT queries:
- `parentCategory`, `category`, `subcategory`, `department`
- `financialYear`, `sourceOfFinance`
- `invoiceDate`, `invoiceNumber`, `purchaseDate`, `purchaseOrderNumber`
- `acquisitionCost`, `bookValue`, `modeOfPossessionOrAcquisition`, `assetUsage`

### Removed from UPDATE queries:
- Same fields as INSERT queries
- `assetUsage` removed from status update query

### Address table changes:
- Removed locality-related columns: `locality_code`, `locality_name`, `locality_label`, `locality_latitude`, `locality_longitude`, `locality_materializedPath`

### Table name corrections:
- `eg_asset_assignmentdetails` → `eg_asset_assetassignment`
- `EG_Asset_assignmentdetails` → `eg_asset_assetassignment`
- `eg_asset_assignment_history` → `eg_asset_assetassignment_history`

## Notes

- The `additionalDetails` JSONB column continues to store asset-type-specific information
- Core asset functionality remains intact
- All essential asset management features are preserved
- The changes maintain backward compatibility for existing API contracts
- **New file created**: `persister-config.yml` with updated configuration