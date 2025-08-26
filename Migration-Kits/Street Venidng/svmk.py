import pandas as pd
import requests
import time
import logging
import sqlite3
import json
from datetime import datetime

# Configure logging - minimal for performance
logging.basicConfig(
    level=logging.ERROR, # Only errors
    format='%(message)s',
    handlers=[logging.StreamHandler()]
) 

# Configuration
# IMPORTANT: Update these paths and URLs as needed
EXCEL_PATH = "/Users/atul/Desktop/migrate.xlsx" # <-- Verify this path
CSV_STATUS_PATH = "/Users/atul/Desktop/migrate_status.csv" # Real-time status file
DB_PATH = "migration.db"

# API Endpoints
USER_CREATE_API = "http://localhost:8099/user/users/_createnovalidate"
CREATE_API = "http://localhost:8033/sv-services/street-vending/_create" # <-- Verify this URL
UPDATE_API = "http://localhost:8033/sv-services/street-vending/_update" # <-- Verify this URL
BILL_FETCH_API = "http://localhost:8044/billing-service/bill/v2/_fetchbill" # <-- Verify this URL - **Will use POST method**
PAYMENT_API = "http://localhost:8074/collection-services/payments/_create" # <-- Verify this URL


# IMPORTANT: Define the Authorization Bearer token here
AUTH_TOKEN = "a00d5ebb-9559-404f-a0ef-aadc3019510c" # <-- Use the provided token
USER_AUTH_TOKEN = "9d78b340-4ebc-4c66-bff7-53a764b8263d" # User service token

# IMPORTANT: Use the exact user details from your successful API calls

# User details for the RequestInfo in the update, bill fetch, and payment calls (Employee User)
EMPLOYEE_REQUEST_USER_INFO = {
    "id": 832,
    "uuid": "7f4fd980-628e-4266-a992-40fa629e2f05",
    "userName": "SVEMP",
    "name": "SV",
    "mobileNumber": "9509935418", # Employee's mobile
    "emailId": None,
    "locale": None,
    "type": "EMPLOYEE",
    "roles": [
      {
        "name": "Inspection Officer",
        "code": "INSPECTIONOFFICER",
        "tenantId": "pg.citya"
      },
      {
        "name": "TVC Employee",
        "code": "TVCEMPLOYEE",
        "tenantId": "pg.citya"
      },
      {
        "name": "SV CEMP ",
        "code": "SVCEMP",
        "tenantId": "pg.citya"
      }
    ],
    "active": True,
    "tenantId": "pg.citya", # Ensure this matches your tenant
    "permanentCity": None
}

# User details for the RequestInfo in the create call (Citizen User)
CITIZEN_REQUEST_USER_INFO = {
    "id": 790,
    "uuid": "7e1ebe9e-d040-413f-896a-5460def381e9",
    "userName": "9999009999",
    "name": "Shivank",
    "mobileNumber": "9999009999",
    "emailId": None,
    "locale": None,
    "type": "CITIZEN",
    "roles": [
        {
            "name": "Citizen",
            "code": "CITIZEN",
            "tenantId": "pg" # Note: Tenant ID is "pg" for the citizen user
        }
    ],
    "active": True,
    "tenantId": "pg", # Ensure this matches your tenant
    "permanentCity": None
}

# Admin user for user creation
ADMIN_USER_INFO = {
    "id": 23287,
    "uuid": "4632c941-cb1e-4b83-b2d4-200022c1a137",
    "userName": "PalashS",
    "name": "Palash S",
    "mobileNumber": "9949032246",
    "type": "EMPLOYEE",
    "roles": [{"name": "superuser", "code": "SUPERUSER", "tenantId": "pg.citya"}],
    "tenantId": "pg.citya"
}

# Define common headers. Add more headers if needed based on UI network traffic analysis.
HEADERS = {
    "accept": "application/json",
    "content-type": "application/json",
    "authorization": f"Bearer {AUTH_TOKEN}",
    # Potentially add other headers observed in successful UI calls if the fix below isn't enough:
    # "Origin": "http://localhost:3000", # Example: if UI is on port 3000
    # "Referer": "http://localhost:3000/some-page", # Example: specific page
    # "User-Agent": "Mozilla/5.0...", # Example: Mimic a browser
    # "Accept-Language": "en-US,en;q=0.9,en;q=0.8", # Example: Language preference from your curl
    # "Accept-Encoding": "gzip, deflate, br", # Example: Encoding preference from your curl
    # "Connection": "keep-alive", # Example: Connection type from your curl
    # "sec-ch-ua": '"Chromium";v="136", "Google Chrome";v="136", "Not.A/Brand";v="99"', # Example from your curl
    # "sec-ch-ua-mobile": "?0", # Example from your curl
    # "sec-ch-ua-platform": '"macOS"', # Example from your curl
    # "sec-fetch-dest": "empty", # Example from your curl
    # "sec-fetch-mode": "cors", # Example from your curl
    # "sec-fetch-site": "same-origin", # Example from your curl
}


def init_db():
    """Initialize database with correct schema"""
    try:
        with sqlite3.connect(DB_PATH) as conn:
            # Drop existing table if it exists
            conn.execute('DROP TABLE IF EXISTS migration_log')
            # Create new table with correct schema
            conn.execute('''
                CREATE TABLE migration_log (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    vendor_name TEXT,
                    mobile_no TEXT,
                    tenant_id TEXT,
                    user_create_status TEXT DEFAULT 'NOT_ATTEMPTED',
                    create_status TEXT DEFAULT 'NOT_ATTEMPTED',
                    update_status TEXT DEFAULT 'NOT_ATTEMPTED',
                    bill_fetch_status TEXT DEFAULT 'NOT_ATTEMPTED',
                    payment_status TEXT DEFAULT 'NOT_ATTEMPTED',
                    application_id TEXT,
                    application_no TEXT,
                    bill_id TEXT,
                    total_amount_due REAL,
                    error TEXT,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    application_date INTEGER -- Store application date for bill fetch
                )
            ''')
            conn.commit()
            logging.info("Database initialized with fresh schema")
    except Exception as e:
        logging.error(f"Error initializing database: {e}")
        raise # Re-raise the exception to stop execution if DB is critical


def log_to_db(vendor_name, mobile_no, tenant_id, create_status='NOT_ATTEMPTED', update_status='NOT_ATTEMPTED', bill_fetch_status='NOT_ATTEMPTED', payment_status='NOT_ATTEMPTED', application_id=None, application_no=None, bill_id=None, total_amount_due=None, error=None, application_date=None):
    """Log migration attempt with proper column names"""
    try:
        with sqlite3.connect(DB_PATH) as conn:
            conn.execute('''
                INSERT INTO migration_log
                (vendor_name, mobile_no, tenant_id, create_status, update_status, bill_fetch_status, payment_status, application_id, application_no, bill_id, total_amount_due, error, application_date)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ''', (str(vendor_name), str(mobile_no), str(tenant_id), str(create_status), str(update_status),
                  str(bill_fetch_status), str(payment_status), str(application_id) if application_id is not None else None,
                  str(application_no) if application_no is not None else None, str(bill_id) if bill_id is not None else None,
                  float(total_amount_due) if total_amount_due is not None else None, str(error) if error is not None else None,
                  int(application_date) if application_date is not None else None))
            conn.commit()
    except Exception as db_err:
        logging.error(f"Failed to log to database for {vendor_name}/{mobile_no}: {db_err}")

def update_log_in_db(mobile_no, tenant_id, **kwargs):
    """Updates an existing log entry based on mobile_no and tenant_id."""
    try:
        with sqlite3.connect(DB_PATH) as conn:
            cursor = conn.cursor()
            update_fields = ', '.join([f"{key} = ?" for key in kwargs.keys()])
            values = list(kwargs.values()) + [str(mobile_no), str(tenant_id)]
            cursor.execute(f'''
                UPDATE migration_log
                SET {update_fields}
                WHERE mobile_no = ? AND tenant_id = ?
            ''', values)
            conn.commit()
    except Exception as db_err:
        logging.error(f"Failed to update log in database for {mobile_no}/{tenant_id}: {db_err}")


def update_excel_status(mobile_no, status, error_msg=None):
    """Update migration status in Excel file"""
    try:
        df = pd.read_excel(EXCEL_PATH)
        
        # Ensure status columns exist
        if 'migrationStatus' not in df.columns:
            df['migrationStatus'] = 'NOT_STARTED'
        if 'errorDetails' not in df.columns:
            df['errorDetails'] = ''
            
        # Convert to object type to avoid dtype warnings
        df['migrationStatus'] = df['migrationStatus'].astype('object')
        df['errorDetails'] = df['errorDetails'].astype('object')
        
        # Find matching row
        try:
            mobile_int = int(mobile_no)
            mask = df['mobileNo'] == mobile_int
        except:
            mask = df['mobileNo'].astype(str) == str(mobile_no)
            
        if mask.any():
            df.loc[mask, 'migrationStatus'] = str(status)
            if error_msg:
                df.loc[mask, 'errorDetails'] = str(error_msg)[:100]
            df.to_excel(EXCEL_PATH, index=False)
            # Also save as CSV for real-time viewing
            csv_path = EXCEL_PATH.replace('.xlsx', '_status.csv')
            df[['name', 'mobileNo', 'migrationStatus', 'errorDetails']].to_csv(csv_path, index=False)
            print(f"✓ {mobile_no}: {status}")
        else:
            print(f"Mobile {mobile_no} not found in Excel")
    except Exception as e:
        print(f"Excel update error: {e}")

def create_user(name, mobile):
    """Create user using user migration kit logic"""
    
    payload = {
        "requestInfo": {
            "apiId": "Rainmaker",
            "ver": ".01",
            "ts": int(time.time() * 1000),
            "action": "_update",
            "authToken": USER_AUTH_TOKEN,
            "userInfo": ADMIN_USER_INFO,
            "msgId": f"{int(time.time())}|en_IN"
        },
        "user": {
            "userName": mobile,
            "name": name,
            "mobileNumber": mobile,
            "type": "CITIZEN",
            "active": True,
            "password": "eGov@123",
            "roles": [{"code": "CITIZEN", "name": "Citizen", "tenantId": "pg.citya"}],
            "tenantId": "pg.citya"
        }
    }
    
    try:
        response = requests.post(USER_CREATE_API, headers={"Content-Type": "application/json"}, json=payload, timeout=7)
        if response.status_code == 200:
            return True, "Success"
        else:
            return False, f"HTTP {response.status_code}: {response.text[:100]}"
    except Exception as e:
        return False, str(e), f"Error: {str(e)[:50]}"

def create_base_payload(record):
    """Create payload matching the exact API requirements for the _create endpoint"""
    current_timestamp = int(time.time() * 1000)
    payload = {
        "streetVendingDetail": {
            # These lists/objects will be populated from the Excel record
            "addressDetails": [],
            "bankDetail": {},
            "documentDetails": [],
            "vendingOperationTimeDetails": [],
            "vendorDetail": [],
            "benificiaryOfSocialSchemes": [],

            # Fields populated from Excel record
            "disabilityStatus": str(record.get('disabilityStatus', 'NONE')),
            "draftId": str(record.get('draftId', '')),
            "localAuthorityName": str(record.get('localAuthorityName', '')),
            "vendingActivity": str(record.get('vendingActivity', 'STATIONARY')),
            "vendingArea": str(record.get('vendingArea', '100')),
            "vendingZone": str(record.get('vendingZone', 'TEST_VALUE_ONE')),
            "enrollmentId": str(record.get('enrollmentId', '')),
            "locality": str(record.get('vendingLocality', '')),
            "applicationCreatedBy": str(record.get('applicationCreatedBy', 'citizen')).upper(),

            # Fields expected to be generated by API or set initially
            "applicationDate": current_timestamp, # Set initially, API will generate
            "applicationId": None,
            "applicationNo": None,
            "oldApplicationNo": None,
            "applicationStatus": "APPLIED",
            "approvalDate": "0",
            "certificateNo": None,
            "cartLatitude": float(str(record.get('cartLatitude', 0)).strip()),
            "cartLongitude": float(str(record.get('cartLongitude', 0)).strip()),
            "vendingLicenseCertificateId": "",
            "paymentReceiptId": None,
            "vendingLicenseId": None,
            "validityDate": None,
            "validityDateForPersisterDate": None,
            "expireFlag": False,
            "renewalStatus": None,
            "issuedDate": None,
            "financialYear": str(record.get('financialYear', '')),
            "validFrom": str(record.get('validFrom', 'NA')),
            "validTo": str(record.get('validTo', 'NA')),
            "tradeLicenseNo": str(record.get('tradeLicenseNo', '')),

            # Audit details for the main object
            "auditDetails": None,

            "termsAndCondition": str(record.get('termsAndCondition', 'Y')),

            "workflow": {
                "action": "APPLY",
                "comments": "Created via migration script",
                "businessService": "street-vending",
                "moduleName": "sv-services",
                "varificationDocuments": []
            },
            "tenantId": "pg.citya" # Ensure this matches your tenant
        },
        "draftApplication": False,
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": AUTH_TOKEN,
            "userInfo": CITIZEN_REQUEST_USER_INFO, # Use Citizen User Info for Create
            "msgId": f"{int(time.time()*1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }

    # --- Map data from Excel record into the lists/objects ---
    # Use .get() with default values to handle missing columns gracefully

    # Address Details (Assuming PERMANENT and CORRESPONDENCE addresses are required and potentially the same)
    address_types = ["PERMANENT", "CORRESPONDENCE"]
    is_address_same = str(record.get('isAddressSame', '')).lower() == 'true'

    for i, addr_type in enumerate(address_types):
         prefix = 'corr' if addr_type == 'CORRESPONDENCE' and not is_address_same else ''
         if addr_type == 'CORRESPONDENCE' and is_address_same:
             address_data = {
                "addressId": None,
                "addressLine1": str(record.get('addressLine1', '')),
                "addressLine2": str(record.get('addressLine2', '')),
                "addressType": addr_type,
                "city": str(record.get('city', 'New Delhi')),
                "cityCode": str(record.get('cityCode', 'pg.citya')),
                "doorNo": str(record.get('doorNo', '')),
                "houseNo": str(record.get('houseNo', '')),
                "landmark": str(record.get('landmark', '')),
                "locality": str(record.get('locality', '')),
                "localityCode": str(record.get('localityCode', '')),
                "pincode": str(record.get('pincode', '')),
                "streetName": str(record.get('streetName', '')),
                "vendorId": None,
                "isAddressSame": True,
                "auditDetails": None
             }
         else:
            address_data = {
                "addressId": None,
                "addressLine1": str(record.get(f'{prefix}addressLine1', '')),
                "addressLine2": str(record.get(f'{prefix}addressLine2', '')),
                "addressType": addr_type,
                "city": str(record.get(f'{prefix}city', 'New Delhi')),
                "cityCode": str(record.get(f'{prefix}cityCode', 'pg.citya')),
                "doorNo": str(record.get(f'{prefix}doorNo', '')),
                "houseNo": str(record.get(f'{prefix}houseNo', '')),
                "landmark": str(record.get(f'{prefix}landmark', '')),
                "locality": str(record.get(f'{prefix}locality', '')),
                "localityCode": str(record.get(f'{prefix}localityCode', '')),
                "pincode": str(record.get(f'{prefix}pincode', '')),
                "streetName": str(record.get(f'{prefix}streetName', '')),
                "vendorId": None,
                "isAddressSame": is_address_same if addr_type == 'CORRESPONDENCE' else False,
                "auditDetails": None
            }
         # Add condition to not add empty addresses - check for at least one key field
         if any(address_data.get(key) for key in ['addressLine1', 'houseNo', 'localityCode', 'pincode']):
              payload['streetVendingDetail']['addressDetails'].append(address_data)


    # Bank Details - Only add if account number is provided
    if str(record.get('accountNumber', '')).strip():
        payload['streetVendingDetail']['bankDetail'] = {
            "accountHolderName": str(record.get('accountHolderName', '')),
            "accountNumber": str(record.get('accountNumber', '')),
            "bankBranchName": str(record.get('bankBranchName', '')),
            "bankName": str(record.get('bankName', '')),
            "ifscCode": str(record.get('ifscCode', '')),
            "applicationId": None,
            "id": None,
            "refundStatus": None,
            "refundType": None,
            "auditDetails": None
        }

    # Vendor Details
    vendor_main = {
        "applicationId": None,
        "auditDetails": None,
        "dob": str(record.get('dob_vendor', '')),
        "userCategory": str(record.get('userCategory', 'GEN')),
        "emailId": str(record.get('email', '')),
        "fatherName": str(record.get('fatherName', '')),
        "specialCategory": str(record.get('specialCategory_vendor', 'NONE')),
        "gender": str(record.get('gender', 'M')),
        "id": None,
        "isInvolved": str(record.get('isVendorInvolved', 'True')).lower() == 'true',
        "mobileNo": str(record.get('mobileNo', '')),
        "name": str(record.get('name', '')),
        "relationshipType": "VENDOR",
        "vendorId": None,
        "vendorPaymentFrequency": str(record.get('vendorPaymentFrequency', 'MONTHLY'))
    }
    if vendor_main.get('name') and vendor_main.get('mobileNo'):
        payload['streetVendingDetail']['vendorDetail'].append(vendor_main)
    else:
        logging.warning(f"Skipping main vendor for record {record.get('name')}/{record.get('mobileNo')} due to missing name or mobile.")


    spouse_name = str(record.get('spouseName', '')).strip()
    if spouse_name:
        vendor_spouse = {
            "applicationId": None,
            "auditDetails": None,
            "dob": str(record.get('dob_spouse', '')),
            "userCategory": str(record.get('userCategory_spouse', record.get('userCategory', 'GEN'))),
            "emailId": str(record.get('email_spouse', '')),
            "specialCategory": str(record.get('specialCategory_spouse', 'NONE')),
            "isInvolved": str(record.get('isSpouseInvolved', 'True')).lower() == 'true',
            "fatherName": str(record.get('fatherName_spouse', '')),
            "gender": str(record.get('gender_spouse', 'O')),
            "id": None,
            "mobileNo": str(record.get('mobileNo_spouse', '')), # Use spouse mobile if provided
            "name": spouse_name,
            "relationshipType": "SPOUSE",
            "vendorId": None,
            "vendorPaymentFrequency": None
        }
        payload['streetVendingDetail']['vendorDetail'].append(vendor_spouse)

    dependent_name = str(record.get('dependentName', '')).strip()
    if dependent_name:
        vendor_dependent = {
            "applicationId": None,
            "auditDetails": None,
            "dob": str(record.get('dob_dependent', '')),
            "userCategory": str(record.get('userCategory_dependent', record.get('userCategory', 'GEN'))),
            "emailId": str(record.get('email_dependent', '')),
            "specialCategory": str(record.get('specialCategory_dependent', 'NONE')),
            "isInvolved": str(record.get('isDependentInvolved', 'True')).lower() == 'true',
            "fatherName": str(record.get('fatherName_dependent', '')),
            "gender": str(record.get('gender_dependent', 'M')),
            "id": None,
            "mobileNo": str(record.get('mobileNo_dependent', '')), # Use dependent mobile if provided
            "name": dependent_name,
            "relationshipType": "DEPENDENT",
            "vendorId": None,
            "vendorPaymentFrequency": None
        }
        payload['streetVendingDetail']['vendorDetail'].append(vendor_dependent)


    # Document Details
    document_types_map = {
        'photoFileStoreId': "FAMILY.PHOTO.PHOTOGRAPH",
        'voterIdFileStoreId': "PROOF.RESIDENCE.VOTERID",
        'setupPhotoFileStoreId': "PHOTOGRAPH.VENDINGSETUP.PHOTO",
        'aadhaarFileStoreId': "IDENTITYPROOF.AADHAAR",
        # Add other document types and their corresponding Excel column names here
        # Example: 'panFileStoreId': "IDENTITYPROOF.PAN"
        # Example: 'disabilityCertificateFileStoreId': "DOCUMENT.SUPPORTING.DISABILITYCERTIFICATE"
        # Example: 'socialCategoryFileStoreId': "DOCUMENT.SUPPORTING.SOCIALCATEGORY"
        # Example: 'tvcResolutionFileStoreId': "DOCUMENT.SUPPORTING.TVCRESOLUTION"
        # Example: 'shopEstablishmentFileStoreId': "DOCUMENT.SUPPORTING.SHOPESTABLISHMENT"
        # Example: 'policeVerificationFileStoreId': "DOCUMENT.SUPPORTING.POLICEVERIFICATION"
    }

    for col_name, doc_type in document_types_map.items():
        file_store_id = str(record.get(col_name, '')).strip()
        if file_store_id:
            payload['streetVendingDetail']['documentDetails'].append({
                "applicationId": None,
                "documentType": doc_type,
                "fileStoreId": file_store_id,
                "documentDetailId": file_store_id, # As per your example
                "auditDetails": None
            })

    # Operation Times (all days)
    days = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"]
    start_time = str(record.get('operationStartTime', '08:00')).strip()
    end_time = str(record.get('operationEndTime', '20:00')).strip()
    if start_time and end_time and ':' in start_time and ':' in end_time:
        payload['streetVendingDetail']['vendingOperationTimeDetails'] = [
            {
                "applicationId": None,
                "auditDetails": None,
                "dayOfWeek": day,
                "fromTime": start_time,
                "toTime": end_time,
                "id": None
            } for day in days
        ]

    # Beneficiary of Social Schemes
    social_schemes = []
    for i in range(1, 5):
        scheme_name = str(record.get(f'socialScheme{i}Name', '')).strip()
        enrollment_id = str(record.get(f'socialScheme{i}EnrollmentId', '')).strip()

        if scheme_name and enrollment_id:
             try:
                 # Try to convert to int, keep as string if it fails
                 enrollment_id_val = int(enrollment_id)
             except ValueError:
                 enrollment_id_val = enrollment_id

             social_schemes.append({
                 "schemeName": scheme_name,
                 "enrollmentId": enrollment_id_val
             })
    if social_schemes:
         payload['streetVendingDetail']['benificiaryOfSocialSchemes'] = social_schemes


    # Ensure vendingArea is an integer
    try:
        vending_area_val = str(record.get('vendingArea', '0')).strip()
        payload['streetVendingDetail']['vendingArea'] = int(vending_area_val)
    except ValueError:
        logging.warning(f"Could not convert vendingArea '{record.get('vendingArea')}' to int for {record.get('name')}. Using default 0.")
        payload['streetVendingDetail']['vendingArea'] = 0


    return payload

def create_update_payload(sv_detail):
    """
    Create update payload using the streetVendingDetail object obtained from the create response.
    Modifies the object for the "APPROVE" action.
    """
    current_timestamp = int(time.time() * 1000)

    if not sv_detail or not isinstance(sv_detail, dict):
        logging.error("Invalid sv_detail object provided for update payload creation.")
        return None

    # Create the update payload by modifying the object from the create response
    update_payload = {
        "streetVendingDetail": sv_detail,
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": AUTH_TOKEN,
            "userInfo": EMPLOYEE_REQUEST_USER_INFO, # Use Employee User Info for Update
            "msgId": f"{int(time.time()*1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }

    # --- Modify the object for the "APPROVE" action ---
    # Set status back to APPLIED for the transition - This might be necessary depending on API logic
    update_payload['streetVendingDetail']['applicationStatus'] = "APPLIED"

    # Update audit details for the main object
    if update_payload['streetVendingDetail'].get('auditDetails') is None:
         update_payload['streetVendingDetail']['auditDetails'] = {}

    update_payload['streetVendingDetail']['auditDetails']['lastModifiedBy'] = str(EMPLOYEE_REQUEST_USER_INFO["uuid"]) # Ensure string
    update_payload['streetVendingDetail']['auditDetails']['lastModifiedTime'] = current_timestamp

    # Update audit details for sub-objects
    # Filter out None entries from the list of sub-objects to audit
    sub_objects_to_audit = [
        update_payload['streetVendingDetail'].get('bankDetail')
    ]
    sub_objects_to_audit = [item for item in sub_objects_to_audit if isinstance(item, dict)] # Filter None/non-dicts

    sub_lists_to_audit = [
        update_payload['streetVendingDetail'].get('addressDetails', []),
        update_payload['streetVendingDetail'].get('documentDetails', []),
        update_payload['streetVendingDetail'].get('vendorDetail', []),
        update_payload['streetVendingDetail'].get('vendingOperationTimeDetails', []),
        update_payload['streetVendingDetail'].get('benificiaryOfSocialSchemes', [])
    ]

    for item in sub_objects_to_audit:
        if item.get('auditDetails') is None:
            item['auditDetails'] = {}
        item['auditDetails']['lastModifiedBy'] = str(EMPLOYEE_REQUEST_USER_INFO["uuid"])
        item['auditDetails']['lastModifiedTime'] = current_timestamp

    for obj_list in sub_lists_to_audit:
        if isinstance(obj_list, list):
            for item in obj_list:
                if isinstance(item, dict):
                     if item.get('auditDetails') is None:
                         item['auditDetails'] = {}
                     item['auditDetails']['lastModifiedBy'] = str(EMPLOYEE_REQUEST_USER_INFO["uuid"])
                     item['auditDetails']['lastModifiedTime'] = current_timestamp


    # Set the workflow action for approval
    update_payload['streetVendingDetail']['workflow'] = {
        "action": "APPROVE",
        "comments": "Auto-approved by migration script",
        "businessService": "street-vending",
        "moduleName": "sv-services",
        "assignes": None,
        "varificationDocuments": []
    }

    # Ensure vendingArea is an integer
    try:
        vending_area_val = update_payload['streetVendingDetail'].get('vendingArea')
        update_payload['streetVendingDetail']['vendingArea'] = int(str(vending_area_val).strip())
    except (ValueError, TypeError):
        logging.warning(f"Could not convert vendingArea '{vending_area_val}' to int during update payload creation. Using default 0.")
        update_payload['streetVendingDetail']['vendingArea'] = 0

    # Ensure latitude/longitude are floats
    try:
         update_payload['streetVendingDetail']['cartLatitude'] = float(str(update_payload['streetVendingDetail'].get('cartLatitude', 0)).strip())
    except ValueError:
         logging.warning(f"Could not convert cartLatitude '{update_payload['streetVendingDetail'].get('cartLatitude')}' to float during update payload creation. Using default 0.")
         update_payload['streetVendingDetail']['cartLatitude'] = 0.0
    try:
         update_payload['streetVendingDetail']['cartLongitude'] = float(str(update_payload['streetVendingDetail'].get('cartLongitude', 0)).strip())
    except ValueError:
         logging.warning(f"Could not convert cartLongitude '{update_payload['streetVendingDetail'].get('cartLongitude')}' to float during update payload creation. Using default 0.")
         update_payload['streetVendingDetail']['cartLongitude'] = 0.0

    return update_payload

def fetch_bill_details(application_no, tenant_id, application_date):
    """
    Fetches bill details for the given application number.
    Returns bill_id and total_amount_due.

    **FIX:** Changed to POST method and added '_' query parameter.
    **FIX:** Corrected parsing of the response body ("Bill" singular).
    """
    logging.info(f"Attempting to fetch bill for Application No: {application_no}")

    bill_fetch_url = BILL_FETCH_API

    # Query parameters for the POST request
    query_params = {
        "tenantId": tenant_id,
        "consumerCode": application_no,
        "businessService": "sv-services",
        "_": application_date # Add the application date as '_' parameter
    }

    # Request body for the POST request
    request_body = {
        "RequestInfo": {
             "apiId": "Rainmaker",
             "authToken": AUTH_TOKEN,
             "userInfo": EMPLOYEE_REQUEST_USER_INFO, # Use Employee User Info
             "msgId": f"{int(time.time()*1000)}|en_IN",
             "plainAccessRequest": {}
        },
        "generateBillCriteria": { # Add generateBillCriteria as per 400 error
            "tenantId": tenant_id,
            "consumerCode": application_no,
            "businessService": "sv-services"
        }
    }

    try:
        # Use POST method instead of GET
        response = requests.post(
             bill_fetch_url,
             headers=HEADERS,
             params=query_params,
             json=request_body,
             timeout=7
        )
        response.raise_for_status()
        bill_data = response.json()

        logging.debug(f"Bill Fetch Response for {application_no}:\n{json.dumps(bill_data, indent=2)}")

        # --- Parse the response to find billId and totalAmount ---
        # FIX: Look for "Bill" (singular) instead of "Bills" (plural)
        bills = bill_data.get('Bill', [])
        if bills:
            bill = bills[0] # Assuming the first bill is the relevant one
            bill_id = bill.get('id')
            # Check for different possible keys for the total amount
            total_amount_due = bill.get('totalAmount')
            if total_amount_due is None:
                 total_amount_due = bill.get('totalAmountDue')

            if bill_id is not None and total_amount_due is not None:
                 try:
                     total_amount_due = float(total_amount_due)
                 except (ValueError, TypeError):
                     logging.error(f"Could not convert totalAmount '{total_amount_due}' to float for {application_no}.")
                     return None, None

                 logging.info(f"Successfully fetched bill details for {application_no}: Bill ID {bill_id}, Amount {total_amount_due}")
                 return bill_id, total_amount_due
            else:
                 logging.error(f"Bill fetch successful for {application_no}, but could not find bill ID or total amount in response.")
                 return None, None
        else:
            logging.warning(f"Bill fetch successful for {application_no}, but no bills found in response.")
            return None, None

    except requests.exceptions.Timeout:
        print(f"BILL ERROR: {application_no} - Request timed out")
        logging.error(f"Bill fetch API request timed out for {application_no}.")
        return None, None
    except requests.exceptions.RequestException as e:
        error_message = f"Bill fetch API Request Error for {application_no}: {str(e)}"
        if hasattr(e, 'response') and e.response is not None:
            try:
                 error_response_text = e.response.text
                 error_message += f" | Response: {error_response_text}"
                 print(f"BILL ERROR: {application_no} - HTTP {e.response.status_code}: {error_response_text[:200]}")
                 logging.error(f"Bill Fetch Failure Details: {error_response_text}")
            except:
                error_message += " | Could not parse error response text."
                print(f"BILL ERROR: {application_no} - {str(e)}")
        else:
            print(f"BILL ERROR: {application_no} - {str(e)}")
        logging.error(error_message)
        return None, None
    except json.JSONDecodeError:
         logging.error(f"Bill fetch API success, but response is not valid JSON for {application_no}.")
         return None, None
    except Exception as e:
         logging.error(f"Unexpected error during bill fetch for {application_no}: {str(e)}", exc_info=True)
         return None, None


def collect_payment(application_no, bill_id, total_amount_due, tenant_id, vendor_name, vendor_mobile):
    """Calls the Payment Collection API."""
    logging.info(f"Attempting to collect payment for Application No: {application_no}, Bill ID: {bill_id}, Amount: {total_amount_due}")

    # Use the Employee User Info for the collector details (in RequestInfo)
    collector_mobile = EMPLOYEE_REQUEST_USER_INFO.get("mobileNumber", "Unknown")
    collector_name = EMPLOYEE_REQUEST_USER_INFO.get("name", "Unknown")

    # Use the vendor's details from Excel for the payer and the main mobileNumber field
    payer_name = vendor_name # Use vendor's name from Excel
    payer_mobile = vendor_mobile # Use vendor's mobile from Excel


    payment_payload = {
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": AUTH_TOKEN,
            "userInfo": EMPLOYEE_REQUEST_USER_INFO, # Use Employee User Info
            "msgId": f"{int(time.time()*1000)}|en_IN",
            "plainAccessRequest": {}
        },
        "Payment": { # Note: The curl payload uses a single "Payment" object, not a list
            "tenantId": tenant_id,
            # FIX: Use the vendor's mobile number from the Excel for the main mobileNumber field
            "mobileNumber": payer_mobile, # Use vendor's mobile number from Excel
            "totalAmountReceived": total_amount_due, # Match totalAmountPaid
            "totalAmountPaid": total_amount_due, # Assuming full payment
            "paymentMode": "CASH", # Or other mode like "ONLINE" if applicable
            "paidBy": "OWNER", # FIX: Set paidBy to "OWNER" as seen in your curl
            "payerName": payer_name, # Use vendor's name
            "payerEmail": None, # Can be None
            "payerAddress": None, # Can be None
            # FIX: Set payerMobileNumber to the vendor's mobile as well (even if curl showed empty)
            "payerMobileNumber": payer_mobile, # Use vendor's mobile number from Excel
            "paymentDetails": [
                 {
                     "businessService":"sv-services",
                     "billId": bill_id,
                     "totalDue": total_amount_due, # Include totalDue
                     "totalAmountPaid": total_amount_due # Amount paid for this specific bill item
                 }
            ],
            "billId": bill_id, # Redundant with paymentDetails but included in curl
            "additionalDetails": {
                "manualReceiptDate": int(time.time()*1000), # Often required for CASH
                # Use a unique manual receipt number per payment attempt
                "manualReceiptNumber": f"MIG-{int(time.time())}-{application_no}",
                "manualCollectionCenter": "MigrationScript", # Or a relevant center code
                "manualInstrumentType": "CASH"
            }, # Optional
            "paymentStatus": "PAID", # Assuming successful payment after call
            "transactionNumber": None, # Generated by system for CASH
            "transactionDate": int(time.time()*1000), # Current timestamp in milliseconds
            "receiptNumber": None, # Generated by system
            "receiptDate": int(time.time()*1000), # Current timestamp in milliseconds
            "auditDetails": None, # Will be populated by system
            "onlinePayment": False,
            "instrumentType": {"code": "CASH"}, # Or other code
            # taxAndPayments is NOT present in your curl payload, so omitting it.
            # If your system requires it, you'll need to fetch bill details and extract taxHeadCodes and amounts.
            "consumerCode": application_no,
            "businessService": "sv-services",
        }
    }


    logging.debug("Payment Payload:\n" + json.dumps(payment_payload, indent=2))

    try:
        # Pass tenantId as a query parameter as seen in your curl
        response = requests.post(PAYMENT_API, headers=HEADERS, json=payment_payload, params={"tenantId": tenant_id}, timeout=7)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.Timeout:
        print(f"PAYMENT ERROR: {application_no} - Request timed out")
        logging.error("Payment API request timed out.")
        raise
    except requests.exceptions.RequestException as e:
        error_message = f"Payment API Request Error: {str(e)}"
        if hasattr(e, 'response') and e.response is not None:
            try:
                 error_response_text = e.response.text
                 error_message += f" | Response: {error_response_text}"
                 print(f"PAYMENT ERROR: {application_no} - HTTP {e.response.status_code}: {error_response_text[:200]}")
                 logging.error(f"Payment Failure Details: {error_response_text}")
            except:
                 error_message += " | Could not parse error response text."
                 print(f"PAYMENT ERROR: {application_no} - {str(e)}")
        else:
            print(f"PAYMENT ERROR: {application_no} - {str(e)}")
        raise requests.exceptions.RequestException(error_message) from e


def process_excel():
    """Main processing function"""
    try:
        df = pd.read_excel(EXCEL_PATH, dtype=str)
        if df.empty:
            logging.info("Excel file is empty. No records to process.")
            return
        # Replace NaN with empty strings for consistency
        df = df.fillna('')
    except FileNotFoundError:
        logging.critical(f"Excel file not found at {EXCEL_PATH}")
        return
    except Exception as e:
        logging.critical(f"Error reading Excel file: {e}")
        return

    # Initialize DB at the start of processing
    init_db()

    for index, record_series in df.iterrows():
        record = record_series.to_dict()

        name = record.get('name', f'Unnamed Vendor {index + 1}')
        mobile = record.get('mobileNo', f'No Mobile {index + 1}')
        tenant_id = record.get('tenantId', 'pg.citya') # Get tenant from Excel or use default

        # Validate essential fields
        main_vendor_name = str(record.get('name', '')).strip()
        main_vendor_mobile = str(record.get('mobileNo', '')).strip()
        if not main_vendor_name or not main_vendor_mobile:
            error_message = "Skipped due to missing main vendor name or mobile number in Excel."
            logging.warning(f"Skipping record {index + 1} ({name}/{mobile}): {error_message}")
            log_to_db(name, mobile, tenant_id, create_status="SKIPPED_MISSING_VENDOR_INFO", error=error_message)
            continue

        logging.info(f"Processing record {index + 1}: {name} ({mobile}) | Tenant: {tenant_id}")
        
        # Update status at start
        update_excel_status(mobile, "STARTING")
        
        # Step 0: Create User first
        user_success, user_error = create_user(name, mobile)
        if user_success:
            print(f"User created: {name} ({mobile})")
            update_excel_status(mobile, "USER_CREATED")
        else:
            print(f"User creation failed: {name} ({mobile}) - {user_error} - skipping to next record")
            update_excel_status(mobile, "USER_FAILED", str(user_error)[:100])
            continue  # Skip to next record if user creation fails

        # Check if record already exists in log for resuming
        conn = sqlite3.connect(DB_PATH)
        cursor = conn.cursor()
        # Include application_date in the select
        cursor.execute("SELECT create_status, update_status, bill_fetch_status, payment_status, application_id, application_no, bill_id, total_amount_due, error, application_date FROM migration_log WHERE mobile_no = ? AND tenant_id = ?", (mobile, tenant_id))
        existing_log = cursor.fetchone()
        conn.close()

        application_id = None
        application_no = None
        bill_id = None
        total_amount_due = None
        create_status = 'NOT_ATTEMPTED'
        update_status = 'NOT_ATTEMPTED'
        bill_fetch_status = 'NOT_ATTEMPTED'
        payment_status = 'NOT_ATTEMPTED'
        error_message = None
        sv_detail = None # To store the SVDetail object from the create response
        application_date = None # To store the application date for bill fetch

        if existing_log:
            create_status, update_status, bill_fetch_status, payment_status, application_id, application_no, bill_id, total_amount_due, error_message, application_date = existing_log
            logging.info(f"Record {name} ({mobile}) already logged. Status: Create={create_status}, Update={update_status}, Bill Fetch={bill_fetch_status}, Payment={payment_status}. Resuming...")

            # If payment was already successful, skip this record
            if payment_status == 'PAYMENT_SUCCESS':
                logging.info(f"Record {name} ({mobile}) already fully migrated. Skipping.")
                continue
            # If update was successful, but bill fetch was not, we might need to fetch SVDetail again
            elif update_status == 'UPDATE_SUCCESS' and bill_fetch_status == 'NOT_ATTEMPTED':
                 # Attempt to fetch SVDetail using application_no for the update payload if needed later
                 # This requires a Search API, which is not in the script yet.
                 # For now, we'll proceed assuming we *might* not need the full SVDetail object for subsequent steps
                 # if the API only needs application_no/ID.
                 pass # Continue to bill fetch step
            # If create was successful, but update was not
            elif create_status == 'CREATE_SUCCESS' and update_status == 'NOT_ATTEMPTED':
                 # The sv_detail object is needed for the update payload.
                 # Since we lost it from the previous run, we cannot resume the update step correctly.
                 # A robust resume would require a search API call here to get the SVDetail object.
                 logging.warning(f"Cannot resume update for {name} ({mobile}): SVDetail object not available from log. Need Search API for robust resume. Skipping update and subsequent steps for this record.")
                 update_log_in_db(mobile, tenant_id, update_status='RESUME_SKIPPED', bill_fetch_status='RESUME_SKIPPED', payment_status='RESUME_SKIPPED', error="SVDetail object needed for update not available during resume.")
                 continue # Skip to the next record
            # If any step before create failed or was not attempted, we will retry the create step below.
            else:
                 # This covers cases where create failed, or it's the first attempt.
                 # Log the initial state for a fresh attempt if it wasn't already logged with initial statuses.
                 if create_status == 'NOT_ATTEMPTED' and update_status == 'NOT_ATTEMPTED' and bill_fetch_status == 'NOT_ATTEMPTED' and payment_status == 'NOT_ATTEMPTED':
                      log_to_db(name, mobile, tenant_id)
                 pass # Proceed to create step


        # --- Step 1: Create Street Vendor Application (after user creation) ---
        if create_status == 'NOT_ATTEMPTED':
            try:
                create_payload = create_base_payload(record)
                create_response = requests.post(CREATE_API, headers=HEADERS, json=create_payload, timeout=7)
                create_response.raise_for_status()
                create_response_data = create_response.json()

                sv_detail = create_response_data.get("SVDetail") # Capture SVDetail for the next step

                if sv_detail and isinstance(sv_detail, dict):
                    application_id = sv_detail.get('applicationId', '')
                    application_no = sv_detail.get('applicationNo', '')
                    application_date = sv_detail.get('applicationDate') # Capture application date

                    if application_id and application_no and application_date is not None:
                        create_status = 'CREATE_SUCCESS'
                        logging.info(f"CREATE_SUCCESS for {name} | App ID: {application_id}, App No: {application_no}")
                        # Update log with success status, IDs, and application_date
                        update_log_in_db(mobile, tenant_id, create_status=create_status, application_id=application_id, application_no=application_no, application_date=application_date)
                        update_excel_status(mobile, "CREATED")
                    else:
                        create_status = 'CREATE_SUCCESS_NO_ID'
                        error_message = "Create API success, but application ID, No, or Date missing in SVDetail object."
                        logging.error(f"{create_status} for {name} | Mobile: {mobile}: {error_message}")
                        update_log_in_db(mobile, tenant_id, create_status=create_status, error=error_message)
                        update_excel_status(mobile, "CREATE_FAILED", error_message)
                        continue # Move to next record
                else:
                    create_status = 'CREATE_SUCCESS_NO_SVDETAIL'
                    error_message = "Create API success, but SVDetail object missing or not a dictionary in response."
                    logging.error(f"{create_status} for {name} | Mobile: {mobile}: {error_message}")
                    update_log_in_db(mobile, tenant_id, create_status=create_status, error=error_message)
                    update_excel_status(mobile, "CREATE_FAILED", error_message)
                    continue # Move to next record

            except requests.exceptions.Timeout:
                 create_status = 'CREATE_TIMEOUT'
                 error_message = "Create API request timed out."
                 print(f"CREATE ERROR: {name} ({mobile}) - {error_message}")
                 logging.error(f"{create_status} for {name} | Mobile: {mobile}: {error_message}")
                 update_log_in_db(mobile, tenant_id, create_status=create_status, error=error_message)
                 update_excel_status(mobile, "CREATE_FAILED", error_message[:100])
                 continue # Move to next record
            except requests.exceptions.RequestException as e:
                create_status = 'CREATE_FAILED'
                error_message = f"Create API Request Error: {str(e)}"
                if hasattr(e, 'response') and e.response is not None:
                    try:
                        error_response_text = e.response.text
                        error_message += f" | Response: {error_response_text}"
                        print(f"CREATE ERROR: {name} ({mobile}) - HTTP {e.response.status_code}: {error_response_text[:200]}")
                        logging.error(f"Create Failure Details: {error_response_text}")
                    except:
                        error_message += " | Could not parse error response text."
                        print(f"CREATE ERROR: {name} ({mobile}) - {str(e)}")
                else:
                    print(f"CREATE ERROR: {name} ({mobile}) - {str(e)}")
                logging.error(f"{create_status} for {name} | Mobile: {mobile}: {error_message}")
                update_log_in_db(mobile, tenant_id, create_status=create_status, error=error_message)
                continue # Move to next record
            except json.JSONDecodeError:
                 create_status = 'CREATE_SUCCESS_INVALID_JSON'
                 error_message = "Create API success, but response is not valid JSON."
                 logging.error(f"{create_status} for {name} | Mobile: {mobile}: {error_message}")
                 update_log_in_db(mobile, tenant_id, create_status=create_status, error=error_message)
                 continue # Move to next record
            except Exception as e:
                 create_status = 'CREATE_FAILED'
                 error_message = f"Unexpected error during create: {str(e)}"
                 logging.error(f"{create_status} for {name} | Mobile: {mobile}: {error_message}", exc_info=True)
                 update_log_in_db(mobile, tenant_id, create_status=create_status, error=error_message)
                 continue # Move to next record


        # --- Step 2: Update Street Vendor Application (e.g., Approve) ---
        # Only attempt if creation was successful (or was successful in a previous run)
        if create_status == 'CREATE_SUCCESS' and update_status == 'NOT_ATTEMPTED' and application_id and application_no:
            # If resuming, we need to fetch the sv_detail object first if we don't have it from the create step
            # This requires a Search API call. For now, we only proceed if sv_detail was captured from the create response.
            if sv_detail is None:
                 logging.warning(f"Skipping update for {name} ({mobile}) as SVDetail object is not available. Need Search API for robust resume.")
                 update_log_in_db(mobile, tenant_id, update_status='RESUME_SKIPPED', error="SVDetail object needed for update not available during resume.")
                 update_status = 'RESUME_SKIPPED' # Update status locally for the rest of the loop
                 # Continue to the next step if it doesn't require sv_detail (Bill Fetch/Payment might only need application_no)
            else: # We have the sv_detail object
                try:
                    # Remove delay for speed
                    pass

                    update_payload = create_update_payload(sv_detail)

                    if update_payload is None:
                        update_status = "UPDATE_PAYLOAD_ERROR"
                        error_message = "Failed to create update payload from SVDetail object."
                        logging.error(f"{update_status} for {name} | Mobile: {mobile}: {error_message}")
                        update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                        continue

                    logging.debug(f"Record {index + 1} - Update Payload:\n{json.dumps(update_payload, indent=2)}")

                    update_response = requests.post(UPDATE_API, headers=HEADERS, json=update_payload, timeout=7)
                    update_response.raise_for_status()
                    update_response_data = update_response.json()

                    logging.debug(f"Record {index + 1} - Update Response:\n{json.dumps(update_response_data, indent=2)}")

                    # Verify the status in the response
                    # FIX: Look for "SVDetail" key, not "streetVendingDetail"
                    updated_sv_details = update_response_data.get('SVDetail')

                    if updated_sv_details and isinstance(updated_sv_details, dict):
                        updated_status = updated_sv_details.get('applicationStatus')
                        if updated_status == 'APPROVED': # Check the resulting status
                             update_status = 'UPDATE_SUCCESS'
                             logging.info(f"UPDATE_SUCCESS (Approved) for {name} | App No: {application_no}")
                             update_log_in_db(mobile, tenant_id, update_status=update_status)
                             update_excel_status(mobile, "APPROVED")
                        else:
                             update_status = 'UPDATE_FAILED'
                             error_message = f"Update API success, but application status is '{updated_status}' instead of 'APPROVED'."
                             logging.error(f"{update_status} for {name} | App No: {application_no}: {error_message}")
                             update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                             update_excel_status(mobile, "UPDATE_FAILED", error_message)
                             continue # Continue to next record if update failed, payment won't work
                    else:
                        update_status = 'UPDATE_FAILED'
                        # FIX: Update the error message to reflect the correct key being looked for
                        error_message = "Update API success, but 'SVDetail' object missing or not a dictionary in response."
                        logging.error(f"{update_status} for {name} | App No: {application_no}: {error_message}")
                        update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                        update_excel_status(mobile, "UPDATE_FAILED", error_message)
                        continue

                except requests.exceptions.Timeout:
                    update_status = 'UPDATE_TIMEOUT'
                    error_message = "Update API request timed out."
                    print(f"UPDATE ERROR: {name} ({mobile}) - {error_message}")
                    logging.error(f"{update_status} for {name} | App No: {application_no}: {error_message}")
                    update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                    continue # Move to next record
                except requests.exceptions.RequestException as e:
                    update_status = 'UPDATE_FAILED'
                    error_message = f"Update API Request Error: {str(e)}"
                    if hasattr(e, 'response') and e.response is not None:
                        try:
                             error_response_text = e.response.text
                             error_message += f" | Response: {error_response_text}"
                             print(f"UPDATE ERROR: {name} ({mobile}) - HTTP {e.response.status_code}: {error_response_text[:200]}")
                             logging.error(f"Update Failure Details: {error_response_text}")
                        except:
                            error_message += " | Could not parse error response text."
                            print(f"UPDATE ERROR: {name} ({mobile}) - {str(e)}")
                    else:
                        print(f"UPDATE ERROR: {name} ({mobile}) - {str(e)}")
                    logging.error(f"{update_status} for {name} | App No: {application_no}: {error_message}")
                    update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                    continue # Move to next record
                except json.JSONDecodeError:
                     update_status = 'UPDATE_SUCCESS_INVALID_JSON'
                     error_message = "Update API success, but response is not valid JSON."
                     logging.error(f"{update_status} for {name} | App No: {application_no}: {error_message}")
                     update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                     continue # Move to next record
                except Exception as e:
                     update_status = 'UPDATE_FAILED'
                     error_message = f"Unexpected error during update: {str(e)}"
                     logging.error(f"{update_status} for {name} | App No: {application_no}: {error_message}", exc_info=True)
                     update_log_in_db(mobile, tenant_id, update_status=update_status, error=error_message)
                     continue # Move to next record


        # --- Step 3: Fetch Bill Details ---
        # Only attempt if update was successful (or was successful in a previous run)
        # Also check if bill_fetch_status is NOT_ATTEMPTED (for resuming)
        if update_status == 'UPDATE_SUCCESS' and bill_fetch_status == 'NOT_ATTEMPTED' and application_no and application_date is not None:
            bill_id, total_amount_due = fetch_bill_details(application_no, tenant_id, application_date)
            if bill_id is None or total_amount_due is None:
                bill_fetch_status = 'BILL_FETCH_FAILED'
                # fetch_bill_details logs errors internally
                update_log_in_db(mobile, tenant_id, bill_fetch_status=bill_fetch_status, error="Failed to fetch bill details.")
                update_excel_status(mobile, "BILL_FAILED", "Failed to fetch bill details")
                continue # Cannot proceed without bill details
            else:
                bill_fetch_status = 'BILL_FETCH_SUCCESS'
                update_log_in_db(mobile, tenant_id, bill_fetch_status=bill_fetch_status, bill_id=bill_id, total_amount_due=total_amount_due)
                update_excel_status(mobile, "BILL_READY")
        elif update_status != 'UPDATE_SUCCESS' and bill_fetch_status == 'NOT_ATTEMPTED':
             logging.debug(f"Skipping bill fetch for {name} ({mobile}) as update was not successful (status: {update_status}).")
        # If bill_fetch_status is already successful, bill_id and total_amount_due are loaded from the database log.


        # --- Step 4: Collect Payment ---
        # Only attempt if bill fetch was successful (or was successful in a previous run)
        # Also check if payment_status is NOT_ATTEMPTED (for resuming)
        if bill_fetch_status == 'BILL_FETCH_SUCCESS' and payment_status == 'NOT_ATTEMPTED' and application_no and bill_id and total_amount_due is not None:
             try:
                 payment_response = collect_payment(application_no, bill_id, total_amount_due, tenant_id, name, mobile)
                 # Payment API call successful - treat as completed
                 payment_status = 'PAYMENT_SUCCESS'
                 print(f"Migration completed: {name} - App No: {application_no}")
                 update_log_in_db(mobile, tenant_id, payment_status=payment_status)
                 update_excel_status(mobile, "SUCCESS")

             except requests.exceptions.Timeout:
                 payment_status = 'PAYMENT_TIMEOUT'
                 error_message = "Payment API request timed out."
                 logging.error(f"{payment_status} for {name} | App No: {application_no}, Bill ID: {bill_id}: {error_message}")
                 update_log_in_db(mobile, tenant_id, payment_status=payment_status, error=error_message)
                 update_excel_status(mobile, "PAYMENT_FAILED", error_message)
             except requests.exceptions.RequestException as e:
                 payment_status = 'PAYMENT_FAILED'
                 error_message = f"Payment API Request Error: {str(e)}"
                 if hasattr(e, 'response') and e.response is not None:
                     try:
                          error_response_text = e.response.text
                          error_message += f" | Response: {error_response_text}"
                          logging.error(f"Payment Failure Details: {error_response_text}")
                     except:
                          error_message += " | Could not parse error response text."
                 logging.error(f"{payment_status} for {name} | App No: {application_no}, Bill ID: {bill_id}: {error_message}")
                 update_log_in_db(mobile, tenant_id, payment_status=payment_status, error=error_message)
                 update_excel_status(mobile, "PAYMENT_FAILED", error_message[:100])
             except Exception as e:
                  payment_status = 'PAYMENT_FAILED'
                  error_message = f"Unexpected error during payment: {str(e)}"
                  logging.error(f"{payment_status} for {name} | App No: {application_no}, Bill ID: {bill_id}: {error_message}", exc_info=True)
                  update_log_in_db(mobile, tenant_id, payment_status=payment_status, error=error_message)
                  update_excel_status(mobile, "PAYMENT_FAILED", error_message[:100])

        elif bill_fetch_status == 'BILL_FETCH_SUCCESS' and payment_status == 'NOT_ATTEMPTED':
             logging.warning(f"Skipping payment for {name} ({mobile}) due to missing application_no, bill ID or amount (should not happen if bill fetch was successful).")
             update_log_in_db(mobile, tenant_id, payment_status='PAYMENT_SKIPPED', error="Missing data for payment after bill fetch.")
        elif bill_fetch_status != 'BILL_FETCH_SUCCESS' and payment_status == 'NOT_ATTEMPTED':
             logging.debug(f"Skipping payment for {name} ({mobile}) as bill fetch was not successful (status: {bill_fetch_status}).")


        # Remove delay for speed
        pass


def verify_api_connectivity(url, method="POST"): # Default to POST
    """Tests connectivity to an API endpoint using a specified method."""
    logging.debug(f"Testing connectivity to: {url} ({method})")
    try:
        # Use a simple request with a small timeout
        # Always send a basic RequestInfo payload in the body for these APIs
        test_payload = {
           "RequestInfo": {
                "apiId": "Rainmaker",
                "authToken": AUTH_TOKEN,
                "userInfo": EMPLOYEE_REQUEST_USER_INFO, # Use Employee User Info for connectivity test
                "msgId": f"{int(time.time()*1000)}|en_IN",
                "plainAccessRequest": {}
           }
        }

        if method.upper() == "POST":
             # Add basic payload structure for POST requests where required
             if "billing-service/bill/v2/_fetchbill" in url:
                  test_payload["generateBillCriteria"] = {
                       "tenantId": "pg.citya", # Use a placeholder tenant
                       "businessService": "sv-services" # Use a placeholder service
                  }
             elif "collection-services/payments/_create" in url:
                  test_payload["Payment"] = {
                       "tenantId": "pg.citya", # Use a placeholder tenant
                       "totalAmountPaid": 1, # Use a small placeholder amount
                       "paymentDetails": [{"businessService": "sv-services", "billId": "placeholder", "totalAmountPaid": 1}]
                  }

             response = requests.post(url, headers=HEADERS, json=test_payload, timeout=7)
        elif method.upper() == "GET":
             # For GET, include some basic query params if possible, otherwise just send body
             query_params = {"tenantId": "pg.citya"} # Example query param
             response = requests.get(url, headers=HEADERS, params=query_params, json=test_payload, timeout=7)
        else:
            logging.warning(f"Unsupported method '{method}' for connectivity test.")
            return False


        logging.info(f"API connection test to {url} ({method}): Status {response.status_code}")
        # Consider 2xx, 3xx (redirects), 401/403/400 (likely reachable but auth/payload issue), or 404 (reachable but path issue) as reachable
        # For connectivity, we are looking for a response, even if it's an error due to missing parameters (like 400).
        if 200 <= response.status_code < 500: # Includes 4xx errors which indicate reachability
             logging.info("API appears to be reachable and responding.")
             # For 4xx errors, still log the response body for debugging
             if 400 <= response.status_code < 500:
                  try:
                      logging.warning(f"Connectivity test to {url} ({method}) returned {response.status_code}. Response body: {response.text}")
                  except:
                      logging.warning(f"Connectivity test to {url} ({method}) returned {response.status_code}. Could not read response body.")

             return True
        else:
             logging.error(f"API connection test to {url} ({method}) failed with status {response.status_code}.")
             if response.status_code >= 500:
                  logging.error(f"Server Error (5xx) during connectivity test for {url}. Check server logs.")
             return False
    except requests.exceptions.ConnectionError:
        logging.error(f"API connection test to {url} ({method}) failed: Connection Error.")
        return False
    except requests.exceptions.Timeout:
        logging.error(f"API connection test to {url} ({method}) failed: Timeout.")
        return False
    except Exception as e:
        logging.error(f"API connection test to {url} ({method}) failed: Unexpected Error - {str(e)}")
        return False


if __name__ == "__main__":
    print("=== Street Vendor Migration ===")
    start = time.time()

    # Skip API connectivity check for speed
    pass



    try:
        process_excel()
    except Exception as e:
        logging.critical(f"Fatal error during migration process: {str(e)}", exc_info=True)
    finally:
        end = time.time()
        print(f"\nCompleted in {end - start:.2f}s")
        print(f"Database log file: {DB_PATH}")
        print(f"Debug log file: migration_debug.log")

        try:
            with sqlite3.connect(DB_PATH) as conn:
                cursor = conn.cursor()

                cursor.execute("SELECT create_status, COUNT(*) FROM migration_log GROUP BY create_status")
                create_stats = cursor.fetchall()

                cursor.execute("SELECT update_status, COUNT(*) FROM migration_log WHERE update_status != 'NOT_ATTEMPTED' GROUP BY update_status")
                update_stats = cursor.fetchall()

                cursor.execute("SELECT bill_fetch_status, COUNT(*) FROM migration_log WHERE bill_fetch_status != 'NOT_ATTEMPTED' GROUP BY bill_fetch_status")
                bill_fetch_stats = cursor.fetchall()

                cursor.execute("SELECT payment_status, COUNT(*) FROM migration_log WHERE payment_status != 'NOT_ATTEMPTED' GROUP BY payment_status")
                payment_stats = cursor.fetchall()


                print("\nMigration Summary:")
                print("=================")
                print("Create API Statuses:")
                if create_stats:
                    for status, count in create_stats:
                        print(f"  {status}: {count}")
                else:
                    print("  No create attempts logged.")

                print("\nUpdate API Statuses:")
                if update_stats:
                    for status, count in update_stats:
                        print(f"  {status}: {count}")
                else:
                     print("  No update attempts logged.")

                print("\nBill Fetch API Statuses:")
                if bill_fetch_stats:
                    for status, count in bill_fetch_stats:
                        print(f"  {status}: {count}")
                else:
                     print("  No bill fetch attempts logged.")

                print("\nPayment API Statuses:")
                if payment_stats:
                    for status, count in payment_stats:
                        print(f"  {status}: {count}")
                else:
                     print("  No payment attempts logged.")


                cursor.execute("SELECT vendor_name, mobile_no, tenant_id, create_status, update_status, bill_fetch_status, payment_status, application_no, bill_id, total_amount_due, error FROM migration_log WHERE error IS NOT NULL OR payment_status != 'PAYMENT_SUCCESS'")
                incomplete_or_error_records = cursor.fetchall()
                if incomplete_or_error_records:
                    print("\nRecords with Errors or Incomplete Migration:")
                    print("==============================================")
                    for name, mobile, tenant, c_status, u_status, bf_status, p_status, app_no, bill_id_log, amount, err in incomplete_or_error_records:
                        print(f"- {name} ({mobile}) | Tenant: {tenant} | AppNo: {app_no} | BillId: {bill_id_log} | Amount: {amount} | Create: {c_status} | Update: {u_status} | Bill Fetch: {bf_status} | Payment: {p_status} | Error: {err if err else 'None'}")

        except Exception as e:
            print(f"\nCould not generate summary from database: {str(e)}")
            if incomplete_or_error_records:
                    print("\nRecords with Errors or Incomplete Migration:")
                    print("==============================================")
                    for name, mobile, tenant, c_status, u_status, bf_status, p_status, app_no, bill_id_log, amount, err in incomplete_or_error_records:
                        print(f"- {name} ({mobile}) | Tenant: {tenant} | AppNo: {app_no} | BillId: {bill_id_log} | Amount: {amount} | Create: {c_status} | Update: {u_status} | Bill Fetch: {bf_status} | Payment: {p_status} | Error: {err if err else 'None'}")

        except Exception as e:
            print(f"\nCould not generate summary from database: {str(e)}")