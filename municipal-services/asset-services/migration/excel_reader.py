import pandas as pd
import json
from datetime import datetime
import logging

class ExcelReader:
    def __init__(self, config_path="config/"):
        self.config_path = config_path
        self.field_mapping = self._load_config("field-mapping.json")
        self.validation_rules = self._load_config("validation-rules.json")
        
    def _load_config(self, filename):
        with open(f"{self.config_path}{filename}", 'r') as f:
            return json.load(f)
    
    def read_excel(self, file_path):
        """Read Excel file and return processed data"""
        try:
            # Read both sheets
            assets_df = pd.read_excel(file_path, sheet_name='Assets')
            inventory_df = pd.read_excel(file_path, sheet_name='Inventory', na_filter=False)
            
            logging.info(f"Read {len(assets_df)} assets and {len(inventory_df)} inventory records")
            
            # Process assets
            processed_assets = []
            for index, row in assets_df.iterrows():
                try:
                    asset_data = self._process_asset_row(row, index + 2)  # +2 for header
                    if asset_data:
                        processed_assets.append(asset_data)
                except Exception as e:
                    logging.error(f"Error processing asset row {index + 2}: {str(e)}")
            
            # Process inventory
            processed_inventory = []
            for index, row in inventory_df.iterrows():
                try:
                    inventory_data = self._process_inventory_row(row, index + 2)
                    if inventory_data:
                        processed_inventory.append(inventory_data)
                except Exception as e:
                    logging.error(f"Error processing inventory row {index + 2}: {str(e)}")
            
            return processed_assets, processed_inventory
            
        except Exception as e:
            logging.error(f"Error reading Excel file: {str(e)}")
            return [], []
    
    def _process_asset_row(self, row, row_num):
        """Process single asset row with correct API structure"""
        # Validate mandatory fields
        if not self._validate_mandatory_fields(row, row_num):
            return None
            
        asset_type = str(row.get('Asset Type', '')).upper()
        temp_asset_id = f"TEMP_AST_{row_num:04d}"
        
        # Build asset data based on working API payload
        asset_data = {
            'tenantId': str(row.get('Tenant ID', 'pg.citya')),  # From Excel column
            'tempAssetId': temp_asset_id,  # For Excel reference
            'applicationNo': f"PG-TESTING-2025-A-{row_num:06d}",  # Pre-generated ID
            'assetBookRefNo': f"REF_{temp_asset_id}",
            'assetName': str(row.get('Asset Name', '')),
            'description': str(row.get('Description', '')),
            'assetClassification': asset_type,
            'assetParentCategory': str(row.get('Asset Category', '')),
            'assetCategory': str(row.get('Asset Sub Category', '')),
            'department': str(row.get('Department', '')),
            'assetType': 'NA',
            'assetUsage': 'NA',
            'financialYear': '2025-26',
            'sourceOfFinance': 'OTHER_GRANTS',
            'modeOfPossessionOrAcquisition': 'AST_PURCHASED',
            'invoiceDate': self._parse_date_timestamp(row.get('Purchase Date')),
            'invoiceNumber': f"INV_{temp_asset_id}",
            'purchaseDate': self._parse_date_timestamp(row.get('Purchase Date')),
            'purchaseOrderNumber': f"PO_{temp_asset_id}",
            'assetAge': '0 MONTH',
            'lifeOfAsset': '5',
            'purchaseCost': self._parse_number(row.get('Purchase Cost')),
            'acquisitionCost': 0,
            'bookValue': self._parse_number(row.get('Current Value')),
            'originalBookValue': self._parse_number(row.get('Purchase Cost')),
            'islegacyData': False,
            'minimumValue': 0,
            'assetStatus': '1',
            'businessService': 'asset-create',
            'division': str(row.get('Division', '')),
            'district': str(row.get('District', ''))
        }
        
        # Build address (API format)
        address = {
            'city': str(row.get('City', 'Default City')),
            'locality': {
                'code': 'LOC001',
                'area': str(row.get('Locality', 'Default Area'))
            }
        }
        asset_data['addressDetails'] = address
        
        # Build additional details (API format)
        additional = {
            'assetParentCategory': str(row.get('Asset Category', '')),
            'modeOfPossessionOrAcquisition': {
                'i18nKey': 'AST_PURCHASED',
                'code': 'AST_PURCHASED',
                'name': 'modeOfPossessionOrAcquisition'
            }
        }
        
        if asset_type == 'MOVABLE':
            additional.update({
                'brand': str(row.get('Manufacturer', 'Unknown')),
                'manufacturer': str(row.get('Manufacturer', 'Unknown')),
                'warranty': {
                    'i18nKey': '2_YEAR',
                    'code': '2_YEAR', 
                    'name': 'warranty'
                },
                'serialNumber': str(row.get('Serial Number', '')),
                'model': str(row.get('Model', ''))
            })
        elif asset_type == 'IMMOVABLE':
            additional.update({
                'plotArea': str(row.get('Plot Area', '')),
                'builtUpArea': str(row.get('Built Up Area', '')),
                'propertyType': str(row.get('Property Type', ''))
            })
        
        asset_data['additionalDetails'] = additional
        
        # Skip workflow for testing
        # asset_data['workflow'] = {
        #     'action': 'INITIATE',
        #     'businessService': 'asset-create',
        #     'moduleName': 'asset-services'
        # }
            
        return asset_data
    
    def _process_inventory_row(self, row, row_num):
        """Process single inventory row"""
        asset_ref = row.get('Asset Reference')
        if pd.isna(asset_ref) or not str(asset_ref).strip():
            logging.warning(f"Row {row_num}: Missing Asset Reference for inventory")
            return None
            
        return {
            'assetId': str(asset_ref).strip(),  # This will be Excel row reference
            'quantity': self._parse_number(row.get('Quantity', 1)),
            'unit': str(row.get('Unit', 'NOS')),
            'supplier': str(row.get('Supplier', '')),
            'purchaseOrder': str(row.get('Purchase Order', '')),
            'invoiceNumber': str(row.get('Invoice Number', ''))
        }
    
    def _validate_mandatory_fields(self, row, row_num):
        """Validate mandatory fields"""
        mandatory = self.validation_rules['mandatory_fields']['common']
        asset_type = str(row.get('Asset Type', '')).upper()
        
        if asset_type == 'MOVABLE':
            mandatory.extend(self.validation_rules['mandatory_fields']['movable'])
        elif asset_type == 'IMMOVABLE':
            mandatory.extend(self.validation_rules['mandatory_fields']['immovable'])
        
        for field in mandatory:
            if pd.isna(row.get(field)) or not str(row.get(field, '')).strip():
                logging.error(f"Row {row_num}: Missing mandatory field '{field}'")
                return False
        return True
    
    def _parse_date_timestamp(self, date_value):
        """Parse date to timestamp for API"""
        if pd.isna(date_value):
            return int(datetime.now().timestamp() * 1000)  # Default to now
            
        if isinstance(date_value, datetime):
            return int(date_value.timestamp() * 1000)
        
        date_str = str(date_value).strip()
        for fmt in ['%d/%m/%Y', '%d-%m-%Y', '%Y-%m-%d']:
            try:
                dt = datetime.strptime(date_str, fmt)
                return int(dt.timestamp() * 1000)
            except:
                continue
        return int(datetime.now().timestamp() * 1000)
    
    def _parse_number(self, value):
        """Parse numeric value"""
        if pd.isna(value):
            return 0
        try:
            return float(str(value).replace(',', ''))
        except:
            return 0