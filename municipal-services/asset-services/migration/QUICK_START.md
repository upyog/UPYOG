# 🚀 Asset Migration - Quick Start Guide

## Step 1: Setup (One Time)
```bash
cd /Users/atul/Documents/UPASSET/Keshav-UPYOG-NIUA/municipal-services/asset-services/migration/

# Install dependencies
pip3 install -r requirements.txt
```

## Step 2: Get Excel Template
```bash
# Generate fresh template
python3 create_template.py

# Template created at: templates/asset_migration_template.xlsx
```

## Step 3: Fill Excel Data

### Open: `templates/asset_migration_template.xlsx`

**Assets Sheet:**
- Fill mandatory fields: Asset Name, Category, Department, Asset Type
- For MOVABLE: Add Serial Number, Model
- For IMMOVABLE: Add Plot Area, Property Type

**Inventory Sheet (Optional):**
- Asset Reference: Use TEMP_AST_0002, TEMP_AST_0003 format
- Add Quantity, Unit, Supplier details

## Step 4: Configure API
Edit `config/api-config.json`:
```json
{
  "base_url": "http://localhost:8098",
  "tenant_id": "pg.testing"
}
```

## Step 5: Test Migration (DRY RUN)
```bash
./run_migration.sh templates/asset_migration_template.xlsx --dry-run
```

## Step 6: Run Actual Migration
```bash
./run_migration.sh templates/asset_migration_template.xlsx
```

## Step 7: Check Results
```bash
# View logs
ls -la logs/

# Check migration report
cat logs/migration_report_*.json

# View failed records (if any)
cat logs/failed_*.csv
```

---

## 📋 Excel Template Fields

### Mandatory Fields:
- ✅ Asset Name
- ✅ Asset Category  
- ✅ Department
- ✅ Asset Type (MOVABLE/IMMOVABLE)

### Movable Assets:
- ✅ Serial Number
- ✅ Model
- Manufacturer, Warranty, Condition

### Immovable Assets:
- ✅ Plot Area
- ✅ Property Type
- Floor Number, Room Number

---

## 🔧 Common Issues

**1. API Connection Error:**
```bash
# Check if asset service is running
curl http://localhost:8098/asset-services/health
```

**2. Missing Fields:**
```
Error: Missing mandatory field 'Asset Name'
→ Fill all mandatory fields in Excel
```

**3. Date Format:**
```
Use: DD/MM/YYYY or DD-MM-YYYY
Example: 15/01/2023
```

---

## 📊 Sample Data

**Assets Sheet:**
| Asset Name | Category | Department | Asset Type | Serial Number |
|------------|----------|------------|------------|---------------|
| Laptop Dell | IT Equipment | IT Dept | MOVABLE | DL123456 |
| Office Building | Infrastructure | Admin | IMMOVABLE | - |

**Inventory Sheet:**
| Asset Reference | Quantity | Unit | Supplier |
|-----------------|----------|------|----------|
| TEMP_AST_0002 | 1 | NOS | Dell India |

---

## 🎯 Quick Commands

```bash
# Full migration in one go
./run_migration.sh your_assets.xlsx

# Test first, then migrate
./run_migration.sh your_assets.xlsx --dry-run
./run_migration.sh your_assets.xlsx

# Check logs
tail -f logs/migration_*.log
```