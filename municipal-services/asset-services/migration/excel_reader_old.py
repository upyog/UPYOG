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
        """Process single asset row"""
        # Validate mandatory fields
        if not self._validate_mandatory_fields(row, row_num):
            return None
            
        asset_type = str(row.get('Asset Type', '')).upper()
        
        # Generate temporary asset ID for Excel reference
        temp_asset_id = f"TEMP_AST_{row_num:04d}"
        
        # Build basic asset data
        asset_data = {
            'tenantId': 'pg.testing',
            'tempAssetId': temp_asset_id,  # For Excel reference
            'assetName': str(row.get('Asset Name', '')),
            'assetCategory': str(row.get('Asset Category', '')),
            'assetSubCategory': str(row.get('Asset Sub Category', '')),
            'description': str(row.get('Description', '')),
            'purchaseDate': self._parse_date_timestamp(row.get('Purchase Date')),
            'originalBookValue': self._parse_number(row.get('Purchase Cost')),
            'assetType': asset_type
        }
        
        # Build address
        address = self._build_address(row)
        if address:
            asset_data['addressDetails'] = address
        
        # Build additional details based on asset type
        additional_details = self._build_additional_details(row, asset_type)
        if additional_details:
            asset_data['additionalDetails'] = additional_details
            
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
    
    def _build_address(self, row):
        """Build address object"""
        address_fields = self.field_mapping['address_fields']
        address = {}
        
        for excel_field, api_field in address_fields.items():
            value = row.get(excel_field)
            if not pd.isna(value) and str(value).strip():
                address[api_field] = str(value).strip()
        
        return address if address else None
    
    def _build_additional_details(self, row, asset_type):
        """Build additional details JSON based on asset type"""
        additional = {}
        
        if asset_type == 'MOVABLE':
            fields = self.field_mapping['movable_additional']
        elif asset_type == 'IMMOVABLE':
            fields = self.field_mapping['immovable_additional']
        else:
            return None
        
        for excel_field, json_field in fields.items():
            value = row.get(excel_field)
            if not pd.isna(value) and str(value).strip():
                if excel_field in ['Warranty Expiry']:
                    additional[json_field] = self._parse_date(value)
                else:
                    additional[json_field] = str(value).strip()
        
        return additional if additional else None
    
    def _parse_date(self, date_value):
        """Parse date from various formats"""
        if pd.isna(date_value):
            return None
            
        if isinstance(date_value, datetime):
            return date_value.strftime('%Y-%m-%d')
        
        date_str = str(date_value).strip()
        for fmt in ['%d/%m/%Y', '%d-%m-%Y', '%Y-%m-%d']:
            try:
                return datetime.strptime(date_str, fmt).strftime('%Y-%m-%d')
            except:
                continue
        return None
    
    def _parse_date_timestamp(self, date_value):
        """Parse date to timestamp for API"""
        if pd.isna(date_value):
            return None
            
        if isinstance(date_value, datetime):
            return int(date_value.timestamp() * 1000)
        
        date_str = str(date_value).strip()
        for fmt in ['%d/%m/%Y', '%d-%m-%Y', '%Y-%m-%d']:
            try:
                dt = datetime.strptime(date_str, fmt)
                return int(dt.timestamp() * 1000)
            except:
                continue
        return None
    
    def _parse_number(self, value):
        """Parse numeric value"""
        if pd.isna(value):
            return 0
        try:
            return float(str(value).replace(',', ''))
        except:
            return 0