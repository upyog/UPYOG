import requests
import json
import time
import logging
from datetime import datetime

class AssetAPIClient:
    def __init__(self, config_path="config/api-config.json"):
        with open(config_path, 'r') as f:
            self.config = json.load(f)
        
        self.base_url = self.config['base_url']
        self.headers = self.config['headers']
        self.batch_size = self.config['batch_size']
        self.retry_attempts = self.config['retry_attempts']
        self.timeout = self.config['timeout']
        
    def create_asset(self, asset_data):
        """Create single asset via API"""
        # Remove tempAssetId before sending to API
        clean_asset = asset_data.copy()
        if 'tempAssetId' in clean_asset:
            del clean_asset['tempAssetId']
            
        payload = {
            "RequestInfo": self._get_request_info(),
            "Asset": clean_asset
        }
        
        url = f"{self.base_url}{self.config['endpoints']['asset_create']}"
        return self._make_request(url, payload)
    
    def create_inventory(self, inventory_data):
        """Create inventory via API"""
        payload = {
            "RequestInfo": self._get_request_info(),
            "AssetInventory": inventory_data
        }
        
        url = f"{self.base_url}{self.config['endpoints']['inventory_create']}"
        return self._make_request(url, payload)
    
    def create_assets_batch(self, assets_list):
        """Create multiple assets in batches"""
        results = []
        total = len(assets_list)
        
        for i in range(0, total, self.batch_size):
            batch = assets_list[i:i + self.batch_size]
            batch_num = (i // self.batch_size) + 1
            
            logging.info(f"Processing batch {batch_num}: {len(batch)} assets")
            
            for asset in batch:
                try:
                    result = self.create_asset(asset)
                    temp_id = asset.get('tempAssetId', asset.get('assetId', 'unknown'))
                    
                    if result['success']:
                        logging.info(f"Created asset: {temp_id}")
                        results.append({
                            'assetId': temp_id,  # Excel reference ID
                            'status': 'SUCCESS',
                            'response': result['data']
                        })
                    else:
                        logging.error(f"Failed to create asset {temp_id}: {result['error']}")
                        results.append({
                            'assetId': temp_id,
                            'status': 'FAILED',
                            'error': result['error']
                        })
                except Exception as e:
                    temp_id = asset.get('tempAssetId', asset.get('assetId', 'unknown'))
                    logging.error(f"Exception creating asset {temp_id}: {str(e)}")
                    results.append({
                        'assetId': temp_id,
                        'status': 'ERROR',
                        'error': str(e)
                    })
            
            # Small delay between batches
            time.sleep(1)
        
        return results
    
    def create_inventory_batch(self, inventory_list):
        """Create multiple inventory records in batches"""
        results = []
        
        for inventory in inventory_list:
            try:
                result = self.create_inventory(inventory)
                if result['success']:
                    logging.info(f"Created inventory for asset: {inventory['assetId']}")
                    results.append({
                        'assetId': inventory['assetId'],
                        'status': 'SUCCESS',
                        'response': result['data']
                    })
                else:
                    logging.error(f"Failed to create inventory for {inventory['assetId']}: {result['error']}")
                    results.append({
                        'assetId': inventory['assetId'],
                        'status': 'FAILED',
                        'error': result['error']
                    })
            except Exception as e:
                logging.error(f"Exception creating inventory for {inventory['assetId']}: {str(e)}")
                results.append({
                    'assetId': inventory['assetId'],
                    'status': 'ERROR',
                    'error': str(e)
                })
        
        return results
    
    def _make_request(self, url, payload):
        """Make HTTP request with retry logic"""
        for attempt in range(self.retry_attempts):
            try:
                response = requests.post(
                    url, 
                    json=payload, 
                    headers=self.headers,
                    timeout=self.timeout
                )
                
                if response.status_code == 200:
                    return {
                        'success': True,
                        'data': response.json()
                    }
                else:
                    error_msg = f"HTTP {response.status_code}: {response.text}"
                    if attempt == self.retry_attempts - 1:
                        return {
                            'success': False,
                            'error': error_msg
                        }
                    else:
                        logging.warning(f"Attempt {attempt + 1} failed: {error_msg}")
                        time.sleep(2 ** attempt)  # Exponential backoff
                        
            except requests.exceptions.RequestException as e:
                error_msg = f"Request exception: {str(e)}"
                if attempt == self.retry_attempts - 1:
                    return {
                        'success': False,
                        'error': error_msg
                    }
                else:
                    logging.warning(f"Attempt {attempt + 1} failed: {error_msg}")
                    time.sleep(2 ** attempt)
        
        return {
            'success': False,
            'error': 'Max retry attempts exceeded'
        }
    
    def _get_request_info(self):
        """Generate RequestInfo object"""
        return {
            "apiId": "asset-migration",
            "ver": "1.0",
            "ts": int(datetime.now().timestamp() * 1000),
            "action": "create",
            "did": "migration-script",
            "key": "",
            "msgId": f"migration_{int(datetime.now().timestamp())}",
            "authToken": "",
            "userInfo": self.config['user_info']
        }