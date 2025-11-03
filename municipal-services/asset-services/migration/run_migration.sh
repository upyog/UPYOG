#!/bin/bash

# Asset Migration Runner Script

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}=== Asset Migration Tool ===${NC}"

# Check if Python is available
if ! command -v python3 &> /dev/null; then
    echo -e "${RED}Error: python3 not found${NC}"
    exit 1
fi

# Check if Excel file is provided
if [ $# -eq 0 ]; then
    echo -e "${YELLOW}Usage: $0 <excel_file> [--dry-run]${NC}"
    echo ""
    echo "Examples:"
    echo "  $0 assets.xlsx --dry-run    # Test migration"
    echo "  $0 assets.xlsx              # Actual migration"
    echo ""
    echo "Available templates:"
    ls -la templates/*.xlsx 2>/dev/null || echo "  No templates found"
    exit 1
fi

EXCEL_FILE="$1"
DRY_RUN_FLAG="$2"

# Check if Excel file exists
if [ ! -f "$EXCEL_FILE" ]; then
    echo -e "${RED}Error: Excel file not found: $EXCEL_FILE${NC}"
    exit 1
fi

# Install dependencies if needed
if [ ! -d "venv" ]; then
    echo -e "${YELLOW}Setting up virtual environment...${NC}"
    python3 -m venv venv
    source venv/bin/activate
    pip install -r requirements.txt
else
    source venv/bin/activate
fi

# Create logs directory
mkdir -p logs

echo -e "${GREEN}Starting migration...${NC}"
echo "Excel file: $EXCEL_FILE"
echo "Dry run: ${DRY_RUN_FLAG:-false}"
echo ""

# Run migration
if [ "$DRY_RUN_FLAG" = "--dry-run" ]; then
    python3 asset_migrator.py "$EXCEL_FILE" --dry-run
else
    python3 asset_migrator.py "$EXCEL_FILE"
fi

echo ""
echo -e "${GREEN}Migration completed!${NC}"
echo ""
echo "Check logs directory for detailed reports:"
ls -la logs/ | tail -5