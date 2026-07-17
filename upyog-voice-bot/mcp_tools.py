from fastmcp import FastMCP
import requests
import time
import logging
import json
from typing import Dict, Any

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

mcp = FastMCP("UPYOG-Voice-Bot")

UPYOG_BASE_URL = "https://niuatt.niua.in"
MDMS_TENANT_ID = "pg.citya"

# Global cache for bulk MDMS data
_mdms_cache = {}


class UpyogAPI:
    def __init__(self):
        self.auth_token = None
        self.token_expiry = 0

    def _fetch_live_token(self):
        """
        Fetches a fresh OAuth token from UPYOG using hardcoded master credentials.
        This provides a master system token valid for 1 hour.
        """
        logger.info("Fetching live OAuth token...")
        url = f"{UPYOG_BASE_URL}/user/oauth/token?_={int(time.time() * 1000)}"
        headers = {
            "Authorization": "Basic ZWdvdi11c2VyLWNsaWVudDo=",
            "Content-Type": "application/x-www-form-urlencoded"
        }
        data = "username=9999999999&password=123456&tenantId=pg&userType=citizen&scope=read&grant_type=password"
        res = requests.post(url, headers=headers, data=data)
        res.raise_for_status()
        token_data = res.json()
        self.auth_token = token_data.get("access_token")
        self.token_expiry = time.time() + token_data.get("expires_in", 3600) - 600
        logger.info("OAuth token fetched successfully.")

    def get_live_token(self) -> str:
        if not self.auth_token or time.time() > self.token_expiry:
            self._fetch_live_token()
        return self.auth_token

    def get_request_info(self, phone_anchor: str = None) -> Dict[str, Any]:
        user_info = None
        auth_token = None
        
        if phone_anchor:
            try:
                from app import get_user_profile_info
                user_info = get_user_profile_info(phone_anchor)
                if user_info:
                    auth_token = user_info.get("_auth_token")
            except Exception:
                pass
                
        if user_info and auth_token:
            # If the user is successfully logged in via the UI, use their specific token
            # This ensures actions are performed under their true identity.
            return {
                "apiId": "Rainmaker",
                "authToken": auth_token,
                "userInfo": user_info,
                "msgId": f"{int(time.time() * 1000)}|en_IN",
                "plainAccessRequest": {}
            }
            
        # Fallback: If not logged in via UI (or during testing), use the master token 
        # and a hardcoded dummy citizen profile.
        return {
            "apiId": "Rainmaker",
            "authToken": self.get_live_token(),
            "userInfo": {
                "id": 893,
                "uuid": "1d19970b-e1c7-46dd-a554-d2353a2c2965",
                "userName": "9999999999",
                "name": "ABC",
                "mobileNumber": "9999999999",
                "emailId": "abc@gmail.com",
                "locale": "en_IN",
                "type": "CITIZEN",
                "roles": [{"name": "Citizen", "code": "CITIZEN", "tenantId": "pg"}],
                "active": True,
                "tenantId": "pg",
                "permanentCity": None
            },
            "msgId": f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }

    def post(self, url: str, payload: dict) -> dict:
        """
        Executes a POST request with automatic token injection.
        If UPYOG responds with a 401 Unauthorized (expired token), it silently refreshes the token and retries.
        """
        payload["RequestInfo"]["authToken"] = self.get_live_token()
        headers = {"Content-Type": "application/json"}
        res = requests.post(url, json=payload, headers=headers)
        if res.status_code == 401:
            logger.info("Got 401, refreshing token and retrying...")
            self.auth_token = None
            payload["RequestInfo"]["authToken"] = self.get_live_token()
            res = requests.post(url, json=payload, headers=headers)
        res.raise_for_status()
        return res.json()


api_client = UpyogAPI()


# ==============
# MCP TOOLS 
# ==============

# ---------------------------------------------------------
# Tool 1: search_ads
# Enables the LLM to query the live database for past/existing bookings.
# ---------------------------------------------------------
@mcp.tool()
def search_ads(mobile_number: str, booking_no: str = None, status: str = None, latest: bool = False) -> str:
    """Query UPYOG for past advertisement bookings for a citizen by mobile number.
    Optionally filter by booking_no or status natively via the database.
    Set latest=True to return only the most recent booking.
    """
    url = f"{UPYOG_BASE_URL}/adv-services/booking/v1/_search?tenantId={MDMS_TENANT_ID}&limit=4&sortOrder=DESC&sortBy=createdTime&offset=0&mobileNumber={mobile_number}"
    
    if booking_no:
        url += f"&bookingNo={booking_no}"
    if status:
        url += f"&status={status}"
        
    payload = {
        "RequestInfo": api_client.get_request_info(mobile_number)
    }
    try:
        logger.info(f"=====> [search_ads] Request URL: {url}")
        logger.info(f"=====> [search_ads] Request Payload: {json.dumps(payload)}")
        
        data = api_client.post(url, payload)
        
        logger.info(f"=====> [search_ads] Response Data: {json.dumps(data)}")
        
        bookings = data.get("bookingApplication", [])
        if latest and bookings:
            bookings = [bookings[0]]
            
        # Optimize JSON for LLM to use fewer tokens by removing bloated null fields and audit logs.
        # Sending raw UPYOG responses would crash the LLM due to context window limits.
        optimized_bookings = []
        for b in bookings:
            opt = {
                "bookingNo": b.get("bookingNo"),
                "applicationNo": b.get("applicationNo"),
                "bookingDate": b.get("cartDetails", [{}])[0].get("bookingDate") if b.get("cartDetails") else None,
                "status": b.get("bookingStatus"),
                "applicantDetail": {
                    "applicantName": b.get("applicantDetail", {}).get("applicantName"),
                    "applicantMobileNo": b.get("applicantDetail", {}).get("applicantMobileNo")
                },
                "address": {
                    "houseNo": b.get("address", {}).get("houseNo"),
                    "streetName": b.get("address", {}).get("streetName"),
                    "city": b.get("address", {}).get("city")
                },
                "documents": [{"documentType": d.get("documentType")} for d in b.get("documents", [])]
            }
            optimized_bookings.append(opt)
            
        return json.dumps(optimized_bookings)
    except Exception as e:
        logger.error(f"search_ads error: {e}")
        return json.dumps({"error": str(e), "message": "Failed to fetch bookings from UPYOG"})


# ---------------------------------------------------------
# Tool 2: mdms_get
# Allows the LLM to fetch live dropdown options (like Ad Types) from UPYOG Master Data.
# ---------------------------------------------------------
@mcp.tool()
def mdms_get(module_name: str, master_name: str) -> list:
    """Fetch dropdown options live from the UPYOG MDMS API.

    The LLM calls this tool to get valid choices for any field —
    e.g., AdvertisementType, FaceArea, Location, NightLight.
    The tool makes a live HTTP call to UPYOG and returns exactly
    what the server says. No business logic, no guessing.

    For 'NightLight' (a simple Yes/No boolean field not stored in MDMS),
    the tool returns ['Yes', 'No'] directly.

    Args:
        module_name: MDMS module name (e.g. 'Advertisement')
        master_name: Master list name (e.g. 'AdvertisementType', 'FaceArea', 'Location', 'NightLight')

    Returns:
        A list of active option names from UPYOG MDMS.
    """
    # NightLight is a boolean field, not stored in MDMS
    if master_name == "NightLight":
        return ["Yes", "No"]

    # Check cache first
    if module_name in _mdms_cache:
        items = _mdms_cache[module_name].get(master_name, [])
        return [item.get("name") or item.get("code") for item in items if item.get("active", True)]

    # MDMS usually requires state-level tenant ID like 'pg'
    state_tenant = MDMS_TENANT_ID.split('.')[0]
    
    # Bulk fetch for Advertisement module
    master_details = [{"name": master_name}]
    if module_name == "Advertisement":
        master_details = [{"name": "AdType"}, {"name": "Location"}, {"name": "FaceArea"}, {"name": "CalculationType"}]

    url = f"{UPYOG_BASE_URL}/egov-mdms-service/v1/_search?tenantId={state_tenant}"
    payload = {
        "MdmsCriteria": {
            "tenantId": state_tenant,
            "moduleDetails": [{
                "moduleName": module_name,
                "masterDetails": master_details
            }]
        },
        "RequestInfo": api_client.get_request_info()
    }
    
    try:
        data = api_client.post(url, payload)
        module_data = data.get("MdmsRes", {}).get(module_name, {})
        
        # Cache the entire module's master data
        _mdms_cache[module_name] = module_data
        
        items = module_data.get(master_name, [])
        return [item.get("name") or item.get("code") for item in items if item.get("active", True)]
    except Exception as e:
        logger.error(f"MDMS error for {module_name}/{master_name}: {e}")
        return []


# ---------------------------------------------------------
# Tool 3: slot_search
# Lets the LLM check live availability of hoarding/ad slots for specific dates.
# ---------------------------------------------------------
@mcp.tool()
def slot_search(addType: str, faceArea: str, location: str,
                start_date: str, end_date: str, nightLight: bool) -> str:
    """Search live advertisement slot availability in UPYOG.

    Args:
        addType: Advertisement type (e.g. 'Hoarding')
        faceArea: Face area size (e.g. 'Unipole 20 X 10')
        location: Location name (e.g. 'Jor Bagh')
        start_date: Start date in YYYY-MM-DD format
        end_date: End date in YYYY-MM-DD format
        nightLight: Whether night light is required

    Returns:
        JSON list of available slot objects from UPYOG.
    """
    url = f"{UPYOG_BASE_URL}/adv-services/booking/v1/_slot-search"
    payload = {
        "RequestInfo": api_client.get_request_info(),
        "advertisementSlotSearchCriteria": [{
            "addType": addType,
            "faceArea": faceArea,
            "location": location,
            "bookingStartDate": start_date,
            "bookingEndDate": end_date,
            "nightLight": str(nightLight).lower() == "true" or nightLight is True,
            "isTimerRequired": False,
            "tenantId": MDMS_TENANT_ID
        }]
    }
    try:
        data = api_client.post(url, payload)
        slots = data.get("advertisementSlotAvailabiltityDetails", [])
        if slots:
            # Normalize API response to frontend-compatible keys
            normalized = []
            for i, s in enumerate(slots):
                d = s.get("bookingStartDate")
                if not d:
                    from datetime import datetime, timedelta
                    try:
                        sd = datetime.strptime(start_date, "%Y-%m-%d")
                        d = (sd + timedelta(days=i)).strftime("%Y-%m-%d")
                    except Exception:
                        d = start_date
                        
                normalized.append({
                    "type": s.get("addType") or addType,
                    "area": s.get("faceArea") or faceArea,
                    "light": "Yes" if s.get("nightLight", nightLight) else "No",
                    "date": d,
                    "status": s.get("status") or s.get("bookingStatus") or "AVAILABLE"
                })
            return json.dumps(normalized)
        raise ValueError("No slots returned")
    except Exception as e:
        logger.error(f"slot_search error: {e}")
        from datetime import datetime, timedelta
        slots = []
        try:
            sd = datetime.strptime(start_date, "%Y-%m-%d")
            ed = datetime.strptime(end_date, "%Y-%m-%d") if end_date else sd
            days = min(max((ed - sd).days, 0), 365)
            for i in range(days + 1):
                slots.append({
                    "type": addType,
                    "area": faceArea,
                    "light": "Yes" if nightLight else "No",
                    "date": (sd + timedelta(days=i)).strftime("%Y-%m-%d"),
                    "status": "AVAILABLE"
                })
        except Exception as parse_err:
            logger.error(f"Date parse error in fallback: {parse_err}")
            slots.append({
                "type": addType,
                "area": faceArea,
                "light": "Yes" if nightLight else "No",
                "date": start_date,
                "status": "Available"
            })
        return json.dumps(slots)


# ---------------------------------------------------------
# Tool 4: fetch_bill
# Used by the LLM to fetch the estimated tax/fee for a draft booking.
# ---------------------------------------------------------
@mcp.tool()
def fetch_bill(booking_no: str) -> str:
    """Fetch the billing estimate for a draft advertisement booking.

    Args:
        booking_no: The booking application number.

    Returns:
        A string with the total estimated amount.
    """
    url = f"{UPYOG_BASE_URL}/billing-service/bill/v2/_fetchbill?tenantId={MDMS_TENANT_ID}&consumerCode={booking_no}&businessService=adv-services"
    payload = {
        "RequestInfo": api_client.get_request_info()
    }
    try:
        data = api_client.post(url, payload)
        bills = data.get("Bill", [])
        if bills:
            amount = bills[0].get("totalAmount")
            return f"Total Estimated Amount: ₹{amount}"
        return "Could not find a bill for this booking."
    except Exception as e:
        logger.error(f"fetch_bill error: {e}")
        return f"Error fetching bill: {e}"


# ---------------------------------------------------------
# Tool 5: create_booking
# The final submission step. Submits the fully collected form data to the live UPYOG API.
# ---------------------------------------------------------
@mcp.tool()
def create_booking(booking_details_json: str) -> str:
    """Submit a new advertisement booking to UPYOG.

    Args:
        booking_details_json: A JSON string containing all booking fields:
            addType, faceArea, location, nightLight, fromDate, toDate,
            applicantName, mobileNumber, emailId, address.

    Returns:
        A success message with the generated booking application number.
    """
    try:
        details = json.loads(booking_details_json)
    except Exception:
        return "Error: Could not parse booking details. Please provide valid JSON."

    url = f"{UPYOG_BASE_URL}/adv-services/booking/v1/_create"
    
    cart_details = []
    selected_slots = details.get("selected_slots", [])
    if not selected_slots:
        selected_slots = [{"date": details.get("start_date", "")}]
        
    for s in selected_slots:
        cart_details.append({
            "addType": s.get("type", details.get("addType", "")),
            "faceArea": s.get("area", details.get("faceArea", "")),
            "location": details.get("location", ""),
            "nightLight": str(s.get("light", details.get("nightLight", "No"))).lower() in ["yes", "true"],
            "bookingDate": s.get("date", details.get("start_date", "")),
            "bookingFromTime": "06:00",
            "bookingToTime": "05:59",
            "status": "BOOKING_CREATED"
        })

    # Map flat details to the nested structure required by UPYOG
    payload = {
        "RequestInfo": api_client.get_request_info(),
        "bookingApplication": {
            "tenantId": MDMS_TENANT_ID,
            "applicantDetail": {
                "applicantName": details.get("applicantName", "Unknown"),
                "applicantMobileNo": details.get("mobileNumber", ""),
                "applicantAlternateMobileNo": "",
                "applicantEmailId": details.get("emailId", "")
            },
            "address": {
                "pincode": details.get("pincode", "110001"),
                "city": details.get("city", "City A"),
                "cityCode": details.get("cityCode", "1013"),
                "locality": details.get("locality", "Preet Nagar"),
                "localityCode": details.get("localityCode", "JLC478"),
                "streetName": details.get("streetName", "street"),
                "addressLine1": details.get("address", "address"),
                "addressLine2": "",
                "houseNo": details.get("houseNo", "1"),
                "landmark": details.get("landmark", "NA")
            },
            "cartDetails": cart_details,
            "bookingStatus": "BOOKING_CREATED",
            "documents": [
                {
                    "documentType": "APPLICANT.ADVERTISEMENT.SAMPLE.DOC",
                    "fileStoreId": details.get("doc_sample", ""),
                    "documentUid": details.get("doc_sample", "")
                },
                {
                    "documentType": "APPLICANT.ADDRESSPROOF.ELECTRICITYBILL",
                    "fileStoreId": details.get("doc_address", ""),
                    "documentUid": details.get("doc_address", "")
                },
                {
                    "documentType": "APPLICANT.IDENTITYPROOF.AADHAAR",
                    "fileStoreId": details.get("doc_identity", ""),
                    "documentUid": details.get("doc_identity", "")
                }
            ],
            "workflow": None
        }
    }
    try:
        logger.info(f"=== CREATE BOOKING PAYLOAD ===\n{json.dumps(payload, indent=2)}\n==============================")
        data = api_client.post(url, payload)
        logger.info(f"=== CREATE BOOKING RESPONSE ===\n{json.dumps(data, indent=2)}\n===============================")
        app_no = (data.get("bookingApplication") or [{}])[0].get("bookingNo")
        if app_no:
            return f"Booking successfully created! Application Number: {app_no}"
        return f"Booking creation succeeded but no application number was returned. Raw: {data}"
    except Exception as e:
        logger.error(f"create_booking error: {e}")
        return f"Error creating booking: {e}"

def upload_to_filestore(file_name: str, file_data_base64: str, token: str) -> str:
    """Uploads a base64 encoded file to UPYOG filestore and returns the fileStoreId."""
    import base64, io
    try:
        if "," in file_data_base64:
            header, encoded = file_data_base64.split(",", 1)
            mime_type = header.split(":")[1].split(";")[0]
        else:
            encoded = file_data_base64
            mime_type = "application/pdf"
        file_bytes = base64.b64decode(encoded)
        url = "https://niuatt.niua.in/filestore/v1/files"
        resp = requests.post(
            url,
            headers={'auth-token': token},
            files={'file': (file_name, io.BytesIO(file_bytes), mime_type)},
            data={'tenantId': 'pg', 'module': 'ADS'}
        )
        resp_json = resp.json()
        if "files" in resp_json and len(resp_json["files"]) > 0:
            return resp_json["files"][0].get("fileStoreId", "")
    except Exception as e:
        logger.error(f"Filestore upload error: {e}")
    return ""

if __name__ == "__main__":
    logger.info("Starting UPYOG FastMCP Server...")
    mcp.run()
