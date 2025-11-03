#!/usr/bin/env python3

import json
from excel_reader import ExcelReader

# Debug payload generation
def debug_payload():
    reader = ExcelReader()
    assets, inventory = reader.read_excel("templates/asset_migration_template.xlsx")
    
    if assets:
        print("Generated Asset Payload:")
        print(json.dumps(assets[0], indent=2))
        
        # Create API payload
        api_payload = {
            "RequestInfo": {
                "apiId": "debug-test",
                "ver": "1.0",
                "ts": 1757483366785,
                "action": "create",
                "userInfo": {
                    "uuid": "test-user",
                    "userName": "test-migration"
                }
            },
            "Asset": assets[0]
        }
        
        # Remove tempAssetId for API
        if 'tempAssetId' in api_payload['Asset']:
            del api_payload['Asset']['tempAssetId']
            
        print("\nAPI Payload:")
        print(json.dumps(api_payload, indent=2))

if __name__ == "__main__":
    debug_payload()