# Asset Management System - Complete API Workflow Documentation

This document provides complete CURL commands for all APIs in the asset management workflow.

## 1. Vendor Management APIs

### Create Vendor
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/vendor/_create' \
--header 'Content-Type: application/json' \
--data '{
  "Vendor": {
    "accountId": "",
    "vendorNumber": "",
    "vendorName": "ABC Electronics Pvt Ltd",
    "contactPerson": "John Doe",
    "contactNumber": "9876543210",
    "contactEmail": "john.doe@abcelectronics.com",
    "gstin": "27ABCDE1234F1Z5",
    "pan": "ABCDE1234F",
    "vendorAddress": "123 Electronics Street, Tech City, State - 400001",
    "tenantId": "pg.citya",
    "status": "ACTIVE",
    "auditDetails": {
      "createdBy": "",
      "lastModifiedBy": "",
      "createdTime": "",
      "lastModifiedTime": ""
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "mobileNumber": "8448489439",
      "tenantId": "pg.citya"
    },
    "msgId": "1747725192315|en_IN"
  }
}'
```

### Update Vendor
```bash
curl --location 'http://localhost:8098/asset-services/v1/vendor/_update' \
--header 'Content-Type: application/json' \
--data '{
  "Vendor": {
    "vendorId": "vendor-123-456-789",
    "vendorNumber": "UVIN25012012345",
    "vendorName": "ABC Electronics Pvt Ltd - Updated",
    "contactPerson": "Jane Smith",
    "contactNumber": "9876543211",
    "contactEmail": "jane.smith@abcelectronics.com",
    "gstin": "27ABCDE1234F1Z6",
    "pan": "ABCDE1234G",
    "vendorAddress": "456 Updated Electronics Street, New Tech City, State - 400002",
    "tenantId": "pg.citya",
    "status": "ACTIVE",
    "auditDetails": {
      "createdBy": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "lastModifiedBy": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "createdTime": 1747725192315,
      "lastModifiedTime": 1747725192315
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Search Vendor
```bash
curl --location 'http://localhost:8098/asset-services/v1/vendor/_search' \
--header 'Content-Type: application/json' \
--data '{
  "Vendor": {
    "tenantId": "pg.citya",
    "vendorName": "ABC Electronics",
    "status": "ACTIVE"
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "tenantId": "pg.citya"
    }
  }
}'
```

## 2. Procurement Request Workflow APIs

### Create Procurement Request (Employee)
```bash
curl --location 'http://localhost:8098/asset-services/v1/procurement/_create' \
--header 'Content-Type: application/json' \
--data '{
  "ProcurementRequest": {
    "requestId": "",
    "item": "Dell Latitude 5520",
    "itemType": "Laptop",
    "quantity": 5,
    "assetApplicationNumber": "PG-1013-2025-S-001170",
    "tenantId": "pg.citya",
    "status": "PENDING",
    "auditDetails": {
      "createdBy": "",
      "lastModifiedBy": "",
      "createdTime": "",
      "lastModifiedTime": ""
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 908,
      "uuid": "employee-uuid-456",
      "userName": "EMPLOYEE",
      "name": "Employee User",
      "mobileNumber": "9876543210",
      "tenantId": "pg.citya"
    },
    "msgId": "1747725192315|en_IN"
  }
}'
```

### Search Procurement Requests (Admin)
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/procurement/_search' \
--header 'Content-Type: application/json' \
--data '{
  "ProcurementRequest": {
    "tenantId": "pg.citya",
    "status": "PENDING"
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Approve Procurement Request (Admin)
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/procurement/_update' \
--header 'Content-Type: application/json' \
--data '{
  "ProcurementRequest": {
    "requestId": "REQID25012012345",
    "item": "Dell Latitude 5520",
    "itemType": "Laptop",
    "quantity": 5,
    "assetApplicationNumber": "PG-1013-2025-S-001170",
    "tenantId": "pg.citya",
    "status": "APPROVED",
    "auditDetails": {
      "createdBy": "employee-uuid-456",
      "lastModifiedBy": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "createdTime": 1747725192315,
      "lastModifiedTime": 1747725192315
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Reject Procurement Request (Admin)
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/procurement/_update' \
--header 'Content-Type: application/json' \
--data '{
  "ProcurementRequest": {
    "requestId": "REQID25012012345",
    "item": "Dell Latitude 5520",
    "itemType": "Laptop",
    "quantity": 5,
    "assetApplicationNumber": "PG-1013-2025-S-001170",
    "tenantId": "pg.citya",
    "status": "REJECTED",
    "auditDetails": {
      "createdBy": "employee-uuid-456",
      "lastModifiedBy": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "createdTime": 1747725192315,
      "lastModifiedTime": 1747725192315
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

## 3. Asset Management APIs

### Create Asset
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/_create' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "",
    "tenantId": "pg.citya",
    "assetName": "Dell Latitude 5520 Laptop",
    "description": "High-performance business laptop for office use",
    "assetClassification": "MOVABLE",
    "assetParentCategory": "ELECTRONICS",
    "assetCategory": "COMPUTER",
    "assetSubCategory": "LAPTOP",
    "applicationNo": "",
    "approvalNo": "",
    "approvalDate": null,
    "applicationDate": 1747725192315,
    "status": "ACTIVE",
    "accountId": "ACC-001",
    "remarks": "New laptop for development team",
    "scheme": "IT_PROCUREMENT",
    "subScheme": "LAPTOP_SCHEME",
    "purchaseDate": 1747267200000,
    "oldCode": null,
    "unitOfMeasurement": 1,
    "assetType": "LAPTOP",
    "acquisitionCost": 125000.00,
    "minimumValue": "10000",
    "islegacyData": "false",
    "additionalDetails": {
      "processor": "Intel i7",
      "ram": "16GB",
      "storage": "512GB SSD"
    },
    "addressDetails": {
      "addressId": "",
      "tenantId": "pg.citya",
      "doorNo": "101",
      "addressLine1": "IT Department",
      "addressLine2": "Municipal Office Building",
      "landmark": "Near Main Gate",
      "city": "Test City",
      "pincode": "400001",
      "locality_code": "LOC001",
      "locality_name": "Central Area"
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 908,
      "uuid": "employee-uuid-456",
      "userName": "EMPLOYEE",
      "name": "Employee User",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Update Asset
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/_update' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
    "tenantId": "pg.citya",
    "assetName": "Dell Latitude 5520 Laptop - Updated",
    "description": "High-performance business laptop for office use - Updated specs",
    "assetClassification": "MOVABLE",
    "assetParentCategory": "ELECTRONICS",
    "assetCategory": "COMPUTER",
    "assetSubCategory": "LAPTOP",
    "applicationNo": "PG-1013-2025-S-001170",
    "approvalNo": "APV-2025-001",
    "approvalDate": 1747725192315,
    "applicationDate": 1747725192315,
    "status": "ACTIVE",
    "accountId": "ACC-001",
    "remarks": "Updated laptop specifications",
    "scheme": "IT_PROCUREMENT",
    "subScheme": "LAPTOP_SCHEME",
    "purchaseDate": 1747267200000,
    "oldCode": null,
    "unitOfMeasurement": 1,
    "assetType": "LAPTOP",
    "acquisitionCost": 130000.00,
    "minimumValue": "12000",
    "islegacyData": "false"
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Search Asset
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/_search' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "tenantId": "pg.citya",
    "applicationNo": "PG-1013-2025-S-001170",
    "status": "ACTIVE"
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "tenantId": "pg.citya"
    }
  }
}'
```

## 4. Inventory Management APIs

### Create Inventory (After Approved Procurement Request)
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/inventory/_create' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
    "tenantId": "pg.citya",
    "applicationNo": "PG-1013-2025-S-001170",
    "assetInventory": {
      "inventoryId": "",
      "assetId": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
      "tenantId": "pg.citya",
      "purchaseDate": 1747267200000,
      "purchaseMode": "DIRECT_PURCHASE",
      "vendorId": "vendor-123-456-789",
      "vendorNumber": "UVIN25012012345",
      "deliveryDate": 1747353600000,
      "endOfLife": 1778803200000,
      "endOfSupport": 1762819200000,
      "quantity": 5,
      "unitPrice": 25000.50,
      "totalPrice": 125002.50,
      "inventoryStatus": "ACTIVE",
      "procurementRequestId": "REQID25012012345",
      "insuranceApplicability": "YES",
      "auditDetails": {
        "createdBy": "",
        "lastModifiedBy": "",
        "createdTime": "",
        "lastModifiedTime": ""
      }
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 908,
      "uuid": "employee-uuid-456",
      "userName": "EMPLOYEE",
      "name": "Employee User",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Update Inventory
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/inventory/_update' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
    "tenantId": "pg.citya",
    "applicationNo": "PG-1013-2025-S-001170",
    "assetInventory": {
      "inventoryId": "fe747222-41cd-4a37-b7fc-f5df7862ab0f",
      "assetId": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
      "tenantId": "pg.citya",
      "purchaseDate": 1747267200000,
      "purchaseMode": "DIRECT_PURCHASE",
      "vendorId": "vendor-789-012",
      "vendorNumber": "UVIN25012012345",
      "deliveryDate": 1747353600000,
      "endOfLife": 1778803200000,
      "endOfSupport": 1762819200000,
      "quantity": 10,
      "unitPrice": 30000.00,
      "totalPrice": 300000.00,
      "inventoryStatus": "INACTIVE",
      "procurementRequestId": "REQID25012012345",
      "insuranceApplicability": "NO",
      "auditDetails": {
        "createdBy": "employee-uuid-456",
        "lastModifiedBy": "491381fb-858c-4582-a8cd-24ea08394bb8",
        "createdTime": 1747725192315,
        "lastModifiedTime": 1747725192315
      }
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Search Inventory
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/inventory/_search' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
    "tenantId": "pg.citya",
    "assetInventory": {
      "inventoryStatus": "ACTIVE",
      "vendorNumber": "UVIN25012012345"
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "tenantId": "pg.citya"
    }
  }
}'
```

## 5. Asset Assignment APIs

### Create Asset Assignment
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/assignment/_create' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
    "tenantId": "pg.citya",
    "assetAssignment": {
      "assignmentId": "",
      "applicationNo": "PG-1013-2025-S-001170",
      "tenantId": "pg.citya",
      "assignedUserName": "John Developer",
      "designation": "Senior Developer",
      "department": "IT Department",
      "assignedDate": 1747725192315,
      "returnDate": null,
      "assetId": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
      "isAssigned": true,
      "employeeCode": "EMP001"
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Update Asset Assignment (Return Asset)
```bash
curl --location 'http://localhost:8098/asset-services/v1/assets/assignment/_update' \
--header 'Content-Type: application/json' \
--data '{
  "Asset": {
    "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
    "tenantId": "pg.citya",
    "assetAssignment": {
      "assignmentId": "assignment-123-456",
      "applicationNo": "PG-1013-2025-S-001170",
      "tenantId": "pg.citya",
      "assignedUserName": "John Developer",
      "designation": "Senior Developer",
      "department": "IT Department",
      "assignedDate": 1747725192315,
      "returnDate": 1747811592315,
      "assetId": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
      "isAssigned": false,
      "employeeCode": "EMP001"
    }
  },
  "RequestInfo": {
    "apiId": "Rainmaker",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

## Complete Workflow Process

### Step-by-Step Workflow Execution

1. **Create Vendor** (Use vendor creation CURL above)
2. **Create Asset** (Use asset creation CURL above)  
3. **Employee creates procurement request** (Use procurement creation CURL above)
4. **Admin searches and approves request** (Use search and approve CURLs above)
5. **Employee creates inventory from approved request** (Use inventory creation CURL above)
6. **Admin assigns asset to employee** (Use assignment creation CURL above)

### Validation Points

- **Vendor Number**: Must exist in vendor table before creating inventory
- **Procurement Request**: Must be APPROVED before creating inventory
- **Insurance Applicability**: Only 'YES' or 'NO' values allowed
- **Asset Status**: Must be ACTIVE for assignment
- **Employee Code**: Must be valid for assignment

### Error Scenarios

- Invalid vendor number → "Vendor number not found" error
- Non-approved procurement request → "Procurement request not approved" error
- Invalid insurance value → Database constraint violation
- Missing required fields → Validation error with field details

## 6. Workflow Configuration

### Global Workflow Setting
```properties
# Enable/Disable workflow for all modules
is.workflow.enabled=true
```

### Workflow Request Format

#### Asset Workflow (Optional)
```json
{
  "workflow": {
    "action": "APPLY",
    "businessService": "ASSET",
    "moduleName": "asset-services",
    "comment": "Asset registration request"
  }
}
```

#### Procurement Workflow (Enabled)
```json
{
  "workflow": {
    "action": "SUBMIT", 
    "businessService": "PROCUREMENT_REQUEST",
    "moduleName": "asset-services",
    "comment": "Procurement request for approval"
  }
}
```

### Updated API Endpoints (v1.1.0)

- ✅ `/asset-services/api/v1/_create` - Asset creation (with optional workflow)
- ✅ `/asset-services/api/v1/_update` - Asset updates
- ✅ `/asset-services/api/v1/_search` - Asset search
- ✅ `/asset-services/inventory-procurement/v1/_create` - Procurement requests (with workflow)
- ✅ `/asset-services/inventory-procurement/v1/_update` - Procurement approval/rejection
- ✅ `/asset-services/inventory/v1/_create` - Inventory management
- ✅ `/asset-services/vendor/v1/_create` - Vendor management
- ✅ `/asset-services/assignment/v1/_create` - Asset assignment

### Production Deployment Notes

1. **Persister Configuration**: Update persister config path
2. **Workflow Service**: Ensure workflow service is running if enabled
3. **Database**: No schema changes required - all backward compatible
4. **Kafka Topics**: Verify all topics are created and accessible
5. **Testing**: Test all APIs in staging before production deployment

### Rollback Plan

1. Revert persister config path: `egov.persist.yml.repo.path=/original/path`
2. Restart persister service
3. Disable workflow if needed: `is.workflow.enabled=false`
4. Monitor logs for any errors

### Monitoring Commands

```bash
# Check persister logs
tail -f persister.log | grep -i asset

# Check Kafka topics
kafka-topics --list --bootstrap-server localhost:9092 | grep asset

# Check database connections
psql -U atul -d UP -c "SELECT COUNT(*) FROM eg_asset_assetdetails;"

# Test API health
curl -s http://localhost:8098/asset-services/health
```
## 7. Enhanced Asset Search API (v1.4.0)

### Complete Search with All Parameters
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&status=ACTIVE&division=North&district=Kamrup&parentCategory=VEHICLE&category=MCWG&assetName=Dell&minAcquisitionCost=10000&maxAcquisitionCost=200000&offset=0&limit=10" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000,
    "action": "search",
    "did": "1",
    "key": "",
    "msgId": "search",
    "authToken": "ae03fae7-86f0-4d76-8f66-fd98400d32c6",
    "userInfo": {
      "id": 909,
      "uuid": "491381fb-858c-4582-a8cd-24ea08394bb8",
      "userName": "ASSET_APPROVER",
      "name": "Asset Approver",
      "tenantId": "pg.citya"
    }
  }
}'
```

### Search by Division and District
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&division=North&district=Kamrup" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000
  }
}'
```

### Search by Asset Categories
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&parentCategory=ELECTRONICS&category=COMPUTER" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0", 
    "ts": 1640995200000
  }
}'
```

### Search by Asset Name and Description
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&assetName=Laptop&assetDescription=Dell" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000
  }
}'
```

### Search by Acquisition Cost Range
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&minAcquisitionCost=50000&maxAcquisitionCost=150000" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000
  }
}'
```

### Search by UAIN (Asset Number)
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&uain=PG-1013-2025" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000
  }
}'
```

### Search by Status and Disposed Reason
```bash
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&status=DISPOSED&disposedReason=End of Life" \
-H "Content-Type: application/json" \
-d '{
  "RequestInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000
  }
}'
```

### Enhanced Search Response Format
```json
{
  "ResponseInfo": {
    "apiId": "asset-services",
    "ver": "1.0",
    "ts": 1640995200000,
    "resMsgId": "uief87324",
    "msgId": "search",
    "status": "successful"
  },
  "Assets": [
    {
      "id": "bc42cab1-453c-4ccb-9553-abbeba23a9a6",
      "tenantId": "pg.citya",
      "assetBookRefNo": "BRN-2025-001",
      "assetName": "Dell Latitude 5520 Laptop",
      "description": "High-performance business laptop for office use",
      "assetClassification": "MOVABLE",
      "assetParentCategory": "ELECTRONICS",
      "assetCategory": "COMPUTER",
      "assetSubCategory": "LAPTOP",
      "department": "IT Department",
      "applicationNo": "PG-1013-2025-E-000011",
      "applicationDate": 1747725192315,
      "status": "ACTIVE",
      "location": "IT Department, Floor 2",
      "division": "North Division",
      "district": "Kamrup District",
      "disposedReason": null,
      "acquisitionCost": 125000,
      "acquisitionDate": 1747267200000,
      "quantity": 1,
      "unitOfMeasurement": 1
    }
  ]
}
```

### Available Search Parameters (v1.4.0)

| Parameter | Type | Description | Example |
|-----------|------|-------------|---------|
| `tenantId` | String | Office/Tenant ID | `pg.citya` |
| `division` | String | Division filter | `North` |
| `district` | String | District filter | `Kamrup` |
| `uain` | String | Asset Number (partial match) | `PG-1013-2025` |
| `status` | String | Asset status | `ACTIVE`, `DISPOSED` |
| `disposedReason` | String | Disposal reason (partial match) | `End of Life` |
| `parentCategory` | String | Parent category | `ELECTRONICS` |
| `category` | String | Category | `COMPUTER` |
| `assetName` | String | Asset name (partial match) | `Laptop` |
| `assetDescription` | String | Description (partial match) | `Dell` |
| `acquisitionCost` | Double | Exact cost | `125000.00` |
| `minAcquisitionCost` | Double | Minimum cost | `50000.00` |
| `maxAcquisitionCost` | Double | Maximum cost | `200000.00` |
| `acquisitionDate` | Long | Exact date (timestamp) | `1747267200000` |
| `offset` | Integer | Pagination offset | `0` |
| `limit` | Integer | Results per page | `10` |

### Response Fields (v1.4.0)

All search responses now include these comprehensive fields:

- **Basic Info**: `id`, `tenantId`, `applicationNo`, `status`
- **Asset Details**: `assetName`, `description`, `assetClassification`
- **Categories**: `assetParentCategory`, `assetCategory`, `assetSubCategory`
- **Location**: `division`, `district`, `location`, `department`
- **Financial**: `acquisitionCost`, `acquisitionDate`
- **Quantity**: `quantity`, `unitOfMeasurement`
- **Reference**: `assetBookRefNo`, `disposedReason`
- **Timestamps**: `applicationDate`

### Performance Notes

- **Optimized Queries**: Uses single full query for all searches
- **Indexed Fields**: Ensure database indexes on frequently searched fields
- **Pagination**: Default limit is 10, maximum is 50
- **Partial Matching**: Uses ILIKE for text searches (case-insensitive)
- **Range Queries**: Supports BETWEEN for cost ranges

### Migration from Previous Versions

- **No Breaking Changes**: All existing search APIs continue to work
- **Enhanced Response**: More fields now available in response
- **Removed Field**: `assetAssignment` no longer in search response
- **New Parameters**: Additional search filters available

### Testing Commands

```bash
# Test basic search
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya" \
-H "Content-Type: application/json" \
-d '{"RequestInfo":{"apiId":"asset-services","ver":"1.0","ts":1640995200000}}'

# Test with all parameters
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&status=ACTIVE&assetName=Test&minAcquisitionCost=1000&maxAcquisitionCost=500000" \
-H "Content-Type: application/json" \
-d '{"RequestInfo":{"apiId":"asset-services","ver":"1.0","ts":1640995200000}}'

# Test pagination
curl -X POST "http://localhost:8098/asset-services/v1/assets/_search?tenantId=pg.citya&offset=0&limit=5" \
-H "Content-Type: application/json" \
-d '{"RequestInfo":{"apiId":"asset-services","ver":"1.0","ts":1640995200000}}'
```