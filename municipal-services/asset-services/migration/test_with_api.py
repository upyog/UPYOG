#!/usr/bin/env python3

import json
import requests
from datetime import datetime

# Test asset creation with actual API call
def test_asset_api():
    """Test asset creation API"""
    
    # Sample asset payload
    asset_payload = {
        "RequestInfo": {
            "apiId": "asset-migration-test",
            "ver": "1.0",
            "ts": int(datetime.now().timestamp() * 1000),
            "action": "create",
            "did": "migration-test",
            "key": "",
            "msgId": f"test_{int(datetime.now().timestamp())}",
            "authToken": "",
            "userInfo": {
                "uuid": "test-user",
                "userName": "test-migration",
                "name": "Test Migration User",
                "type": "SYSTEM"
            }
        },
        "Asset": {
            "tenantId": "pg.testing",
            "assetName": "Test Laptop Migration",
            "assetCategory": "IT Equipment",
            "assetSubCategory": "Laptop",
            "department": "IT Department",
            "description": "Test laptop for migration validation",
            "purchaseDate": "2023-01-15",
            "purchaseCost": 45000,
            "currentValue": 40000,
            "assetType": "MOVABLE",
            "addressDetails": {
                "addressLine1": "123 Test Street",
                "city": "Test City",
                "pincode": "411001",
                "ward": "Ward 1",
                "zone": "Zone A"
            },
            "additionalDetails": {
                "serialNumber": "TEST123456",
                "model": "Dell Test Model",
                "manufacturer": "Dell Inc",
                "condition": "GOOD",
                "maintenanceSchedule": "Quarterly"
            }
        }
    }
    
    try:
        # Make API call
        url = "http://localhost:8098/asset-services/v1/assets/_create"
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        print("🔄 Testing Asset Creation API...")
        print(f"URL: {url}")
        print(f"Payload: {json.dumps(asset_payload, indent=2)}")
        
        response = requests.post(url, json=asset_payload, headers=headers, timeout=30)
        
        print(f"\n📊 Response Status: {response.status_code}")
        print(f"📊 Response Headers: {dict(response.headers)}")
        
        if response.status_code == 200:
            response_data = response.json()
            print(f"✅ Success! Asset created:")
            print(json.dumps(response_data, indent=2))
            
            # Extract asset ID for inventory test
            if 'Asset' in response_data and 'assetId' in response_data['Asset']:
                asset_id = response_data['Asset']['assetId']
                print(f"\n🎯 Created Asset ID: {asset_id}")
                
                # Test inventory creation
                test_inventory_api(asset_id)
            
        else:
            print(f"❌ Failed: {response.status_code}")
            print(f"Error: {response.text}")
            
    except requests.exceptions.ConnectionError:
        print("❌ Connection Error: Asset service not running on localhost:8098")
        print("💡 Start asset service first or update API URL in config/api-config.json")
    except Exception as e:
        print(f"❌ Error: {str(e)}")

def test_inventory_api(asset_id):
    """Test inventory creation API"""
    
    inventory_payload = {
        "RequestInfo": {
            "apiId": "inventory-migration-test",
            "ver": "1.0",
            "ts": int(datetime.now().timestamp() * 1000),
            "action": "create",
            "did": "migration-test",
            "key": "",
            "msgId": f"inv_test_{int(datetime.now().timestamp())}",
            "authToken": "",
            "userInfo": {
                "uuid": "test-user",
                "userName": "test-migration",
                "name": "Test Migration User",
                "type": "SYSTEM"
            }
        },
        "AssetInventory": {
            "assetId": asset_id,
            "quantity": 1,
            "unit": "NOS",
            "supplier": "Test Supplier",
            "purchaseOrder": "PO_TEST_001",
            "invoiceNumber": "INV_TEST_001"
        }
    }
    
    try:
        url = "http://localhost:8098/asset-services/inventory/v1/_create"
        headers = {
            "Content-Type": "application/json",
            "Accept": "application/json"
        }
        
        print(f"\n🔄 Testing Inventory Creation API...")
        print(f"URL: {url}")
        
        response = requests.post(url, json=inventory_payload, headers=headers, timeout=30)
        
        print(f"\n📊 Inventory Response Status: {response.status_code}")
        
        if response.status_code == 200:
            response_data = response.json()
            print(f"✅ Success! Inventory created:")
            print(json.dumps(response_data, indent=2))
        else:
            print(f"❌ Inventory Failed: {response.status_code}")
            print(f"Error: {response.text}")
            
    except Exception as e:
        print(f"❌ Inventory Error: {str(e)}")

if __name__ == "__main__":
    print("🧪 Asset Migration API Test")
    print("=" * 40)
    test_asset_api()
    print("\n" + "=" * 40)
    print("🏁 Test completed!")