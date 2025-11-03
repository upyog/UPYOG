# Asset Migration Tool

Complete toolkit for migrating asset data from Excel to UPYOG Asset Service.

## Features

- ✅ **Excel to API Migration**: Direct migration from Excel to Asset Service APIs
- ✅ **Movable/Immovable Support**: Different handling for movable and immovable assets
- ✅ **Additional Details JSON**: Dynamic mapping of additional fields to JSON
- ✅ **Inventory Integration**: Auto-create inventory records for assets
- ✅ **Batch Processing**: Efficient batch processing with retry logic
- ✅ **Validation**: Comprehensive data validation before migration
- ✅ **Error Handling**: Detailed error logging and failed record tracking
- ✅ **Dry Run Mode**: Test migration without actual API calls

## Quick Start

### 1. Setup Environment
```bash
cd migration/
pip install -r requirements.txt
```

### 2. Generate Excel Template
```bash
python create_template.py
```

### 3. Configure API Settings
Edit `config/api-config.json`:
```json
{
  "base_url": "http://localhost:8098",
  "tenant_id": "pg.testing"
}
```

### 4. Prepare Excel Data
- Use `templates/asset_migration_template.xlsx`
- Fill **Assets** sheet with asset data
- Fill **Inventory** sheet (optional)

### 5. Run Migration

**Dry Run (Recommended First):**
```bash
python asset_migrator.py path/to/your/assets.xlsx --dry-run
```

**Actual Migration:**
```bash
python asset_migrator.py path/to/your/assets.xlsx
```

## Excel Template Structure

### Assets Sheet (Mandatory)
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| Asset Name | Text | ✅ | Name of the asset |
| Asset Category | Text | ✅ | Category (IT Equipment, Infrastructure, etc.) |
| Asset Sub Category | Text | ✅ | Sub-category |
| Department | Text | ✅ | Owning department |
| Asset Type | MOVABLE/IMMOVABLE | ✅ | Asset type |
| Purchase Date | Date | ❌ | DD/MM/YYYY format |
| Purchase Cost | Number | ❌ | Cost in rupees |
| Current Value | Number | ❌ | Current valuation |

### Additional Fields by Asset Type

**Movable Assets:**
- Serial Number ✅
- Model ✅  
- Manufacturer
- Warranty Expiry
- Condition (EXCELLENT/GOOD/FAIR/POOR)

**Immovable Assets:**
- Plot Area ✅
- Built Up Area
- Floor Number
- Room Number
- Property Type ✅
- Construction Year

### Inventory Sheet (Optional)
| Field | Required | Description |
|-------|----------|-------------|
| Asset ID | ✅ | Reference to created asset |
| Quantity | ❌ | Quantity (default: 1) |
| Unit | ❌ | Unit (NOS, KG, etc.) |
| Supplier | ❌ | Supplier name |

## Configuration Files

### Field Mapping (`config/field-mapping.json`)
Maps Excel columns to API fields and defines additional details structure.

### Validation Rules (`config/validation-rules.json`)
Defines mandatory fields, data types, and allowed values.

### API Configuration (`config/api-config.json`)
API endpoints, batch size, retry settings.

## Migration Process

1. **Excel Parsing**: Read and validate Excel data
2. **Data Transformation**: Convert to API payload format
3. **Additional Details**: Build JSON based on asset type
4. **Asset Creation**: Create assets via `/asset-services/api/v1/_create`
5. **Inventory Creation**: Create inventory via `/asset-services/inventory/v1/_create`
6. **Report Generation**: Generate success/failure reports

## Output Files

### Logs Directory
- `migration_YYYYMMDD_HHMMSS.log` - Detailed migration log
- `migration_report_YYYYMMDD_HHMMSS.json` - Complete migration report
- `failed_assets_YYYYMMDD_HHMMSS.csv` - Failed asset records
- `failed_inventory_YYYYMMDD_HHMMSS.csv` - Failed inventory records

## Error Handling

- **Validation Errors**: Missing mandatory fields, invalid formats
- **API Errors**: HTTP errors, timeout issues
- **Data Errors**: Invalid asset types, malformed data
- **Retry Logic**: Automatic retry with exponential backoff

## Advanced Usage

### Custom Configuration
```bash
python asset_migrator.py assets.xlsx --config /path/to/custom/config/
```

### Batch Size Tuning
Edit `config/api-config.json`:
```json
{
  "batch_size": 25,
  "retry_attempts": 5,
  "timeout": 60
}
```

## Troubleshooting

### Common Issues

1. **Missing Mandatory Fields**
   - Check validation rules in `config/validation-rules.json`
   - Ensure all required fields are filled

2. **API Connection Errors**
   - Verify `base_url` in `config/api-config.json`
   - Check if asset service is running

3. **Date Format Issues**
   - Use DD/MM/YYYY, DD-MM-YYYY, or YYYY-MM-DD
   - Ensure Excel cells are formatted as dates

4. **Asset Type Validation**
   - Use only "MOVABLE" or "IMMOVABLE"
   - Check case sensitivity

### Debug Mode
Add more logging by editing the script:
```python
logging.basicConfig(level=logging.DEBUG)
```

## Sample Migration

```bash
# 1. Create template
python create_template.py

# 2. Fill template with your data
# Edit templates/asset_migration_template.xlsx

# 3. Test with dry run
python asset_migrator.py templates/asset_migration_template.xlsx --dry-run

# 4. Run actual migration
python asset_migrator.py templates/asset_migration_template.xlsx

# 5. Check results
cat logs/migration_*.log
```

## Production Deployment

1. **Backup Database**: Take database backup before migration
2. **Test Environment**: Run complete test in staging
3. **Batch Migration**: Migrate in smaller batches for large datasets
4. **Monitor Resources**: Watch CPU, memory, and database connections
5. **Rollback Plan**: Keep rollback scripts ready

## Support

For issues or questions:
1. Check logs in `logs/` directory
2. Review failed records CSV files
3. Verify API configuration
4. Test with smaller dataset first