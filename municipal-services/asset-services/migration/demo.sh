#!/bin/bash

# Asset Migration Demo Script

echo "🚀 Asset Migration Toolkit Demo"
echo "================================"

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}Step 1: Setup Environment${NC}"
echo "Installing dependencies..."
pip3 install -r requirements.txt > /dev/null 2>&1
echo "✅ Dependencies installed"

echo ""
echo -e "${BLUE}Step 2: Generate Excel Template${NC}"
python3 create_template.py
echo "✅ Template created: templates/asset_migration_template.xlsx"

echo ""
echo -e "${BLUE}Step 3: Show Template Structure${NC}"
echo "📁 Excel Template contains:"
echo "  - Assets Sheet (with sample data)"
echo "  - Inventory Sheet (optional)"
echo "  - Instructions Sheet"

echo ""
echo -e "${BLUE}Step 4: Configuration Check${NC}"
echo "📋 API Configuration:"
cat config/api-config.json | head -5

echo ""
echo -e "${BLUE}Step 5: Dry Run Demo${NC}"
echo "Running dry run with sample data..."
python3 asset_migrator.py templates/asset_migration_template.xlsx --dry-run

echo ""
echo -e "${YELLOW}🎯 Next Steps:${NC}"
echo "1. Edit templates/asset_migration_template.xlsx with your data"
echo "2. Update config/api-config.json with your API URL"
echo "3. Run: ./run_migration.sh your_assets.xlsx --dry-run"
echo "4. Run: ./run_migration.sh your_assets.xlsx"

echo ""
echo -e "${GREEN}✅ Demo completed! Ready for migration.${NC}"