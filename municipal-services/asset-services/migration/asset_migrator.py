#!/usr/bin/env python3

import sys
import os
import logging
import json
from datetime import datetime
import argparse

from excel_reader import ExcelReader
from api_client import AssetAPIClient

class AssetMigrator:
    def __init__(self):
        self.setup_logging()
        self.excel_reader = ExcelReader()
        self.api_client = AssetAPIClient()
        self.migration_stats = {
            'total_assets': 0,
            'successful_assets': 0,
            'failed_assets': 0,
            'total_inventory': 0,
            'successful_inventory': 0,
            'failed_inventory': 0,
            'start_time': None,
            'end_time': None
        }
    
    def setup_logging(self):
        """Setup logging configuration"""
        log_filename = f"logs/migration_{datetime.now().strftime('%Y%m%d_%H%M%S')}.log"
        os.makedirs('logs', exist_ok=True)
        
        logging.basicConfig(
            level=logging.INFO,
            format='%(asctime)s - %(levelname)s - %(message)s',
            handlers=[
                logging.FileHandler(log_filename),
                logging.StreamHandler(sys.stdout)
            ]
        )
        
        logging.info("Asset Migration Started")
    
    def migrate_from_excel(self, excel_file_path, dry_run=False):
        """Main migration function"""
        self.migration_stats['start_time'] = datetime.now()
        
        logging.info(f"Starting migration from: {excel_file_path}")
        logging.info(f"Dry run mode: {dry_run}")
        
        # Read Excel data
        assets_data, inventory_data = self.excel_reader.read_excel(excel_file_path)
        
        if not assets_data:
            logging.error("No valid asset data found in Excel file")
            return False
        
        self.migration_stats['total_assets'] = len(assets_data)
        self.migration_stats['total_inventory'] = len(inventory_data)
        
        logging.info(f"Found {len(assets_data)} assets and {len(inventory_data)} inventory records")
        
        if dry_run:
            self._perform_dry_run(assets_data, inventory_data)
            return True
        
        # Migrate assets
        asset_results = self._migrate_assets(assets_data)
        
        # Migrate inventory with proper asset ID mapping
        inventory_results = []
        if inventory_data:
            inventory_results = self._migrate_inventory(inventory_data, asset_results)
        
        # Generate reports
        self._generate_migration_report(asset_results, inventory_results)
        
        self.migration_stats['end_time'] = datetime.now()
        self._print_summary()
        
        return True
    
    def _migrate_assets(self, assets_data):
        """Migrate assets via API"""
        logging.info("Starting asset migration...")
        
        results = self.api_client.create_assets_batch(assets_data)
        
        # Update stats
        for result in results:
            if result['status'] == 'SUCCESS':
                self.migration_stats['successful_assets'] += 1
            else:
                self.migration_stats['failed_assets'] += 1
        
        logging.info(f"Asset migration completed: {self.migration_stats['successful_assets']} success, {self.migration_stats['failed_assets']} failed")
        
        return results
    
    def _migrate_inventory(self, inventory_data, asset_results):
        """Migrate inventory via API with proper asset ID mapping"""
        logging.info("Starting inventory migration...")
        
        # Create asset ID mapping from migration results
        asset_id_mapping = {}
        for result in asset_results:
            if result['status'] == 'SUCCESS' and 'response' in result:
                # Extract actual assetId from API response
                response_data = result['response']
                if 'Asset' in response_data and 'assetId' in response_data['Asset']:
                    original_id = result['assetId']  # Excel asset ID
                    actual_id = response_data['Asset']['assetId']  # API generated ID
                    asset_id_mapping[original_id] = actual_id
                    logging.info(f"Asset ID mapping: {original_id} -> {actual_id}")
        
        # Update inventory data with actual asset IDs
        updated_inventory = []
        for inv in inventory_data:
            excel_asset_id = inv['assetId']
            if excel_asset_id in asset_id_mapping:
                inv['assetId'] = asset_id_mapping[excel_asset_id]
                updated_inventory.append(inv)
                logging.info(f"Updated inventory asset ID: {excel_asset_id} -> {inv['assetId']}")
            else:
                logging.warning(f"No asset found for inventory asset ID: {excel_asset_id}")
        
        if not updated_inventory:
            logging.warning("No valid inventory records to migrate")
            return []
        
        results = self.api_client.create_inventory_batch(updated_inventory)
        
        # Update stats
        for result in results:
            if result['status'] == 'SUCCESS':
                self.migration_stats['successful_inventory'] += 1
            else:
                self.migration_stats['failed_inventory'] += 1
        
        logging.info(f"Inventory migration completed: {self.migration_stats['successful_inventory']} success, {self.migration_stats['failed_inventory']} failed")
        
        return results
    
    def _perform_dry_run(self, assets_data, inventory_data):
        """Perform dry run validation"""
        logging.info("=== DRY RUN MODE ===")
        
        # Validate asset data structure
        for i, asset in enumerate(assets_data[:5]):  # Show first 5
            logging.info(f"Sample Asset {i+1}:")
            logging.info(f"  ID: {asset.get('tempAssetId', 'N/A')}")
            logging.info(f"  Name: {asset['assetName']}")
            logging.info(f"  Type: {asset['assetType']}")
            logging.info(f"  Category: {asset['assetCategory']}")
            if 'additionalDetails' in asset:
                logging.info(f"  Additional Details: {json.dumps(asset['additionalDetails'], indent=2)}")
        
        # Validate inventory data
        for i, inv in enumerate(inventory_data[:3]):  # Show first 3
            logging.info(f"Sample Inventory {i+1}:")
            logging.info(f"  Asset Reference: {inv['assetId']}")
            logging.info(f"  Quantity: {inv['quantity']}")
            logging.info(f"  Unit: {inv['unit']}")
        
        logging.info("=== DRY RUN COMPLETED ===")
    
    def _generate_migration_report(self, asset_results, inventory_results):
        """Generate detailed migration report"""
        report_filename = f"logs/migration_report_{datetime.now().strftime('%Y%m%d_%H%M%S')}.json"
        
        report = {
            'migration_summary': self.migration_stats,
            'asset_results': asset_results,
            'inventory_results': inventory_results,
            'failed_assets': [r for r in asset_results if r['status'] != 'SUCCESS'],
            'failed_inventory': [r for r in inventory_results if r['status'] != 'SUCCESS']
        }
        
        with open(report_filename, 'w') as f:
            json.dump(report, f, indent=2, default=str)
        
        logging.info(f"Migration report saved: {report_filename}")
        
        # Generate CSV for failed records
        if report['failed_assets']:
            self._generate_failed_csv(report['failed_assets'], 'failed_assets')
        
        if report['failed_inventory']:
            self._generate_failed_csv(report['failed_inventory'], 'failed_inventory')
    
    def _generate_failed_csv(self, failed_records, record_type):
        """Generate CSV file for failed records"""
        import pandas as pd
        
        csv_filename = f"logs/{record_type}_{datetime.now().strftime('%Y%m%d_%H%M%S')}.csv"
        
        df_data = []
        for record in failed_records:
            df_data.append({
                'Asset_ID': record['assetId'],
                'Status': record['status'],
                'Error': record.get('error', '')
            })
        
        df = pd.DataFrame(df_data)
        df.to_csv(csv_filename, index=False)
        
        logging.info(f"Failed {record_type} CSV saved: {csv_filename}")
    
    def _print_summary(self):
        """Print migration summary"""
        duration = self.migration_stats['end_time'] - self.migration_stats['start_time']
        
        print("\n" + "="*50)
        print("MIGRATION SUMMARY")
        print("="*50)
        print(f"Total Assets: {self.migration_stats['total_assets']}")
        print(f"Successful Assets: {self.migration_stats['successful_assets']}")
        print(f"Failed Assets: {self.migration_stats['failed_assets']}")
        print(f"Total Inventory: {self.migration_stats['total_inventory']}")
        print(f"Successful Inventory: {self.migration_stats['successful_inventory']}")
        print(f"Failed Inventory: {self.migration_stats['failed_inventory']}")
        print(f"Duration: {duration}")
        print("="*50)

def main():
    parser = argparse.ArgumentParser(description='Asset Migration Tool')
    parser.add_argument('excel_file', help='Path to Excel file containing asset data')
    parser.add_argument('--dry-run', action='store_true', help='Perform dry run without actual migration')
    parser.add_argument('--config', help='Path to config directory', default='config/')
    
    args = parser.parse_args()
    
    if not os.path.exists(args.excel_file):
        print(f"Error: Excel file not found: {args.excel_file}")
        sys.exit(1)
    
    migrator = AssetMigrator()
    
    try:
        success = migrator.migrate_from_excel(args.excel_file, args.dry_run)
        if success:
            print("Migration completed successfully!")
        else:
            print("Migration failed!")
            sys.exit(1)
    except Exception as e:
        logging.error(f"Migration failed with exception: {str(e)}")
        print(f"Migration failed: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    main()