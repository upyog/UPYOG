# Asset Services - Changes Log

## Version 1.3.0 - 2025-01-20

### Modified
- **Field Name Change**: Changed `originalBookValue` to `acquisitionCost` across all components
- **Schema Cleanup**: Removed `scheme` and `subScheme` fields from asset management
- **Database Schema**: Updated column name from `originalbookvalue` to `acquisitioncost`
- **API Contract**: Updated Asset model to use `acquisitionCost` instead of `originalBookValue`

### Database Changes
- **Column Rename**: `originalbookvalue` → `acquisitioncost` in `eg_asset_assetdetails` and `eg_asset_auditdetails`
- **Column Removal**: Removed `scheme` and `subscheme` columns from asset tables
- **Flyway Migration**: Updated V20250101000000__asset_management.sql

### Code Changes
- **Asset.java**: Updated field from `originalBookValue` to `acquisitionCost`
- **AssetRowMapper.java**: Fixed method call from `getString` to `getDouble` for acquisitionCost
- **Persister Config**: Updated JSON paths and column mappings
- **API Documentation**: Updated CURL examples and workflows

### Breaking Changes
- API requests must use `acquisitionCost` instead of `originalBookValue`
- `scheme` and `subScheme` fields removed from Asset model
- Database column names changed

### Migration Steps
1. **Run SQL Commands**:
   ```sql
   -- Rename columns
   ALTER TABLE public.eg_asset_assetdetails RENAME COLUMN originalbookvalue TO acquisitioncost;
   ALTER TABLE public.eg_asset_auditdetails RENAME COLUMN originalbookvalue TO acquisitioncost;
   
   -- Remove scheme columns
   ALTER TABLE public.eg_asset_assetdetails DROP COLUMN IF EXISTS scheme, DROP COLUMN IF EXISTS subscheme;
   ALTER TABLE public.eg_asset_auditdetails DROP COLUMN IF EXISTS scheme, DROP COLUMN IF EXISTS subscheme;
   ```

2. **Update Persister Config**: Deploy updated asset-persister.yml
3. **Restart Services**: Restart asset-services and persister
4. **Update API Calls**: Change `originalBookValue` to `acquisitionCost` in all API requests

## Version 1.2.0 - 2025-09-10

### Added
- **Division & District Fields**: Added division and district columns to asset management
- **MDMS Integration**: Support for fetching division/district from MDMS based on tenant
- **Migration Toolkit**: Complete Excel to database migration pipeline
- **Multi-Tenant Support**: Dynamic tenant ID support from Excel columns

## Version 1.1.0 - 2025-09-09

### Added
- **Workflow Integration**: Added workflow support for asset management
- **Persister Configuration**: Complete persister configs for all asset modules
- **Inventory Management**: Full inventory CRUD operations with database persistence
- **Procurement Requests**: Workflow-enabled procurement request management
- **Vendor Management**: Vendor registration and management APIs

### Modified
- **Workflow Service**: Removed workflow dependency from asset creation (configurable)
- **Database Schema**: Fixed column name mappings for PostgreSQL compatibility
- **Persister Configs**: Updated JSON paths for proper data mapping

### Configuration Changes
```properties
# Workflow can be enabled/disabled
is.workflow.enabled=true/false

# Persister config path
egov.persist.yml.repo.path=/Users/atul/Documents/persister
```

### Database Changes
- **New Columns Added**: `division` and `district` in `eg_asset_assetdetails`
- **Flyway Migration**: V20250110000000__add_division_district_columns.sql
- All existing tables compatible
- Updated persister configs for:
  - `eg_asset_assetdetails` (with division/district support)
  - `eg_asset_addressdetails` 
  - `eg_asset_inventory_procurement_request`
  - `eg_asset_inventory`
  - `eg_asset_vendor`

### API Endpoints
- ✅ `/asset-services/v1/assets/_create` - Asset creation (with acquisitionCost)
- ✅ `/asset-services/v1/assets/_update` - Asset updates
- ✅ `/asset-services/v1/assets/_search` - Asset search
- ✅ `/asset-services/inventory-procurement/v1/_create` - Procurement requests
- ✅ `/asset-services/inventory/v1/_create` - Inventory management
- ✅ `/asset-services/vendor/v1/_create` - Vendor management

### Migration Toolkit
- ✅ **Excel Template**: Complete template with acquisitionCost field
- ✅ **Data Validation**: Mandatory field validation including tenant ID
- ✅ **Batch Processing**: Bulk migration with retry logic
- ✅ **Asset ID Mapping**: Excel reference to actual asset ID mapping
- ✅ **Multi-Tenant**: Support for different tenants per row
- ✅ **MDMS Ready**: Prepared for division/district fetching from MDMS

### Deployment Notes
1. **Run Database Migration**: Execute column rename and removal scripts
2. **Update Persister Config**: Deploy updated asset-persister.yml
3. **Restart Services**: Restart persister and asset services
4. **Verify Database**: Check acquisitioncost column exists
5. **Test APIs**: Use updated CURL with acquisitionCost field
6. **Verify Kafka Topics**: Ensure all topics are created

### Rollback Plan
1. Revert database column names if needed
2. Revert persister config path
3. Restart persister service
4. Disable workflow if needed: `is.workflow.enabled=false`

## Version 1.4.0 - 2025-01-21

### Enhanced Search API
- **Complete Search Parameters**: Added all required search fields for asset management
- **Field Mapping**: Fixed division, district, and other field mappings in row mapper
- **Response Optimization**: Removed unnecessary assetAssignment from search response

### New Search Parameters Added
- `division` - Division search filter
- `district` - District search filter  
- `office` - Office/Tenant search filter
- `uain` - Asset Number (UAIN) search filter
- `disposedReason` - Disposed reason search filter
- `parentCategory` - Parent Category search filter
- `category` - Category search filter
- `assetName` - Asset name search filter
- `assetDescription` - Asset description search filter
- `acquisitionCost` - Exact acquisition cost filter
- `minAcquisitionCost` & `maxAcquisitionCost` - Cost range filters
- `acquisitionDate` - Acquisition date filter

### API Changes
- **Enhanced Search Response**: Now includes all required fields:
  - Division, District, Office (tenant)
  - UAIN (Asset No.), Status, Disposed reason
  - Parent Category, Category, Quantity (unitOfMeasurement)
  - Asset name, Asset Description, Acquisition Cost, Acquisition Date

### Database Query Updates
- **AssetQueryBuilder**: Added all new search criteria to both full and limited queries
- **AssetRowMapper**: Added division, district, unitOfMeasurement field mappings
- **Column Fixes**: Fixed acquisitioncost, bookrefno, unitofmeasurement column references

### Code Changes
- **AssetSearchCriteria.java**: Added all new search parameter fields
- **AssetSearchDTO.java**: Added response fields, removed assetAssignment
- **AssetService.java**: Removed assetAssignment mapping logic
- **AssetRepository.java**: Simplified to use full query for all searches
- **AssetQueryBuilder.java**: Enhanced with comprehensive search filters

### CURL Examples
```bash
# Search by multiple parameters
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&status=ACTIVE&parentCategory=VEHICLE&assetName=Test&minAcquisitionCost=10000&maxAcquisitionCost=100000" \
-H "Content-Type: application/json" \
-d '{"RequestInfo":{"apiId":"asset-services","ver":"1.0","ts":1640995200000}}'

# Search by division and district
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&division=North&district=Kamrup" \
-H "Content-Type: application/json" \
-d '{"RequestInfo":{"apiId":"asset-services","ver":"1.0","ts":1640995200000}}'
```

### Breaking Changes
- **AssetSearchDTO**: Removed `assetAssignment` field from search response
- **Search Logic**: Now uses full asset query for all searches (not limited data query)

### Migration Steps
1. **No Database Changes Required** - All columns already exist
2. **Restart asset-services** - Code changes only
3. **Test Search API** - Verify all fields are returned
4. **Update Frontend** - Remove assetAssignment handling from search results

### Deployment Notes
- ✅ **Backward Compatible**: Existing search APIs continue to work
- ✅ **Enhanced Response**: More comprehensive asset data in search results
- ✅ **Performance**: Optimized query logic for better performance
- ✅ **Field Coverage**: All required business fields now available in search