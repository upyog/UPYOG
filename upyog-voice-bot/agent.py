"""
UPYOG Advertisement Agent Service — agent.py
============================================
Handles step-by-step form filling, backed by Redis.
ANCHORED ENTIRELY TO PHONE NUMBER for true persistent memory.
"""

# Import standard library, network, logging, and FastAPI modules
from fastapi import FastAPI, Request, logger
import uvicorn
import json
import re
import random
import time
import requests
import logging 
import os
from dotenv import load_dotenv

# Import Redis memory persistence helpers from local database utility module
from database import (
    verify_connection,
    close_connection,
    get_short_term_memory,
    save_short_term_memory,
    clear_short_term_memory,
    get_long_term_memory,
    save_long_term_memory
)

# Load global environment variables from root-level .env file
load_dotenv()

# Initialize FastAPI application instance on agent microservice
app = FastAPI()

# Set up runtime logger configuration to print server logs to output console
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


# Core UPYOG integration constants and environment endpoints
ADV_API_BASE = "https://niuatt.niua.in/adv-services"
ADV_TENANT_ID = "pg.citya"

# ============== STARTUP EVENT: Verify Redis Connection ==============

@app.on_event("startup")
async def startup_event():
    """Verify Redis is connected on application startup; crashes app immediately if connection fails"""
    if not verify_connection():
        raise RuntimeError("Redis connection FAILED")


@app.on_event("shutdown")
async def shutdown_event():
    """Safely closes active Redis connection pool on server shutdown"""
    close_connection()

# ============== END STARTUP EVENT ==============

# --- CODES AND MAPPINGS UTILITIES ---

# Maps conversational advertisement type inputs to UPYOG master database keys
AD_TYPE_MAP = {
    "digital screen": "DIGITAL_SCREEN",
    "hoarding": "HOARDING",
    "poster": "POSTER",
    "billboard": "BILLBOARD",
    "kiosk pole": "KIOSK_POLE",
    "unipolar": "UNIPOLAR"
}

# Maps conversational dimension inputs to UPYOG faceArea database keys
FACE_AREA_MAP = {
    "public utility 20 x 10": "PUBLIC_UTILITY_20_X_10",
    "decorative kiosk pole 4 x 2.5": "DECORATIVE_KIOSK",
    "kiosk poles 30 x 40 (both side)": "KIOSK_POLES_30_X_40_BOTH_SIDE",
    "direction board 20 x 6": "DIRECTION_BOARD_20_X_6",
    "unipole 20 x 10": "UNIPOLE_20_X_10",
    "unipole 18 x 8": "UNIPOLE_18_X_8",
    "unipole 12 x 8": "UNIPOLE_12_X_8"
}

# Maps conversational location inputs to UPYOG location database keys
LOCATION_MAP = {
    "test saket": "Test_Saket",
    "jor bagh": "JOR_BAGH",
    "hauz khas": "HAUZ_KHAS",
    "green park": "GREEN_PARK"
}

# --- FUZZY MAPPING HELPERS ---
# These functions translate natural language inputs (typed or spoken) to official database codes.

def map_ad_type(val):
    """Fuzzy-matches spoken/typed ad type to database advertisement type code (e.g. Kiosk Pole -> KIOSK_POLE)"""
    if not val:
        return ""
    val_lower = val.lower().strip()
    for key, code in AD_TYPE_MAP.items():
        if key in val_lower or val_lower in key:
            return code
    return val.upper().replace(" ", "_")

def map_face_area(val):
    """Fuzzy-matches face area size text to database faceArea code (e.g. public utility -> PUBLIC_UTILITY_20_X_10)"""
    if not val:
        return ""
    val_lower = val.lower().strip()
    for key, code in FACE_AREA_MAP.items():
        if key in val_lower or val_lower in key:
            return code
    return val.upper().replace(" ", "_")

def map_location(val):
    """Fuzzy-matches location text to database location code (e.g. Saket -> Test_Saket)"""
    if not val:
        return ""
    val_lower = val.lower().strip()
    for key, code in LOCATION_MAP.items():
        if key in val_lower or val_lower in key:
            return code
    return val.upper().replace(" ", "_")

# --- DATE STANDARDIZER ---
# Normalizes multi-format inputs (DD-MM-YYYY, DD/MM/YYYY) to standard ISO format (YYYY-MM-DD)

def parse_and_format_date(date_str: str) -> str:
    """Uses regular expression groups to standardize date format to YYYY-MM-DD required by UPYOG APIs"""
    match = re.match(r'(\d{1,2})[-/.](\d{1,2})[-/.](\d{4})', date_str.strip())
    if match:
        day, month, year = match.groups()
        return f"{year}-{int(month):02d}-{int(day):02d}"
    return date_str.strip()

# --- UPYOG API INTEGRATION HELPERS ---

def execute_slot_search(session_data, auth_token):
    """Checks advertisement slot availability by posting criteria to UPYOG /booking/v1/_slot-search"""
    url = f"{ADV_API_BASE}/booking/v1/_slot-search"
    payload = {
        "advertisementSlotSearchCriteria": [session_data],
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": auth_token,
            "msgId": f"{int(time.time() * 1000)}|en_IN"
        }
    }
    headers = {"Content-Type": "application/json"}
    try:
        res = requests.post(url, json=payload, headers=headers)
        return res.json()
    except Exception as e:
        return {"error": str(e)}

def fetch_bill_for_booking(booking_no, auth_token, tenant_id="pg.citya"):
    """Fetches total billing amount for bookingNo from UPYOG billing service fetchbill endpoint"""
    url = f"https://niuatt.niua.in/billing-service/bill/v2/_fetchbill?tenantId={tenant_id}&consumerCode={booking_no}&businessService=adv-services&_={int(time.time() * 1000)}"
    payload = {
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": auth_token,
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
    }
    try:
        res = requests.post(url, json=payload, headers={"Content-Type": "application/json"})
        data = res.json()
        if isinstance(data, dict) and "Bill" in data and len(data["Bill"]) > 0:
            bill = data["Bill"][0]
            amount = bill.get("totalAmount")
            if amount is not None:
                return amount
        return None
    except Exception as e:
        print(f"Error fetching bill: {e}")
        return None

def execute_booking_search(auth_token, mobile_number="9999999999"):
    """Queries UPYOG booking service database by mobile number to search for user's past applications"""
    url = f"https://niuatt.niua.in/adv-services/booking/v1/_search?_={int(time.time() * 1000)}"
    payload = {
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": auth_token,
            "userInfo": {
                "id": 893,
                "uuid": "1d19970b-e1c7-46dd-a554-d2353a2c2965",
                "userName": mobile_number,
                "name": "ABC",
                "mobileNumber": mobile_number,
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
    }
    try:
        res = requests.post(url, json=payload, headers={"Content-Type": "application/json"})
        data = res.json()
        if isinstance(data, dict) and "bookingApplication" in data:
            return data["bookingApplication"]
        return []
    except Exception as e:
        logger.error(f"Error searching booking: {e}")
        return []

def upload_to_filestore(file_name, base64_data, auth_token, tenant_id="pg"):
    """Decodes base64 file data and uploads it directly to UPYOG FileStore API, returning fileStoreId"""
    url = f"https://niuatt.niua.in/filestore/v1/files"
    if not base64_data: return None
    if base64_data.startswith("data:"): base64_data = base64_data.split(",")[1]
    import base64
    try:
        file_bytes = base64.b64decode(base64_data)
        files = {"file": (file_name, file_bytes, "application/pdf")}
        data = {"tenantId": tenant_id, "module": "ADS"}
        headers = {"auth-token": auth_token}
        res = requests.post(url, files=files, data=data, headers=headers)
        res_data = res.json()
        if "files" in res_data and len(res_data["files"]) > 0:
            return res_data["files"][0].get("fileStoreId")
    except Exception as e:
        print(f"Error uploading file: {e}")
    return None

def execute_create_booking(session_data, auth_token):
    """Compiles the Redis draft details and submits the booking application JSON to UPYOG _create endpoint"""
    import uuid
    url = f"{ADV_API_BASE}/booking/v1/_create"
    payload = {
        "bookingApplication": {
            "tenantId": "pg.citya",
            "draftId": str(uuid.uuid4()),
            "applicantDetail": {
                "applicantName": session_data.get("applicantName") or "John Doe",
                "applicantMobileNo": session_data.get("applicantMobileNo") or "9999999999",
                "applicantAlternateMobileNo": session_data.get("applicantAlternateMobileNo") or "9090909090",
                "applicantEmailId": session_data.get("applicantEmailId") or "johndoe@gmail.com"
            },
            "address": {
                "pincode": session_data.get("pincode", "110001"),
                "city": "City A",
                "cityCode": "1013",
                "locality": session_data.get("locality", "Preet Nagar"),
                "localityCode": "JLC478",
                "streetName": session_data.get("streetName", "Atul Grove Road"),
                "addressLine1": session_data.get("streetName", "Atul Lane"),
                "addressLine2": "",
                "houseNo": session_data.get("houseNo", "T-27"),
                "landmark": session_data.get("landmark", "Janpath")
            },
            "documents": [
                {"documentType": "APPLICANT.ADVERTISEMENT.SAMPLE.DOC", "fileStoreId": session_data.get("adSampleFileStoreId", "1c2e5ff3-615a-4881-aca0-477c02640489"), "documentUid": session_data.get("adSampleFileStoreId", "1c2e5ff3-615a-4881-aca0-477c02640489")},
                {"documentType": "APPLICANT.ADDRESSPROOF.ELECTRICITYBILL", "fileStoreId": session_data.get("addressProofFileStoreId", "a9bcb487-d9d7-4134-81af-680baf30d4cb"), "documentUid": session_data.get("addressProofFileStoreId", "a9bcb487-d9d7-4134-81af-680baf30d4cb")},
                {"documentType": "APPLICANT.IDENTITYPROOF.AADHAAR", "fileStoreId": session_data.get("identityProofFileStoreId", "ed0a0f99-be64-4ff2-b8a6-e55fe85187fa"), "documentUid": session_data.get("identityProofFileStoreId", "ed0a0f99-be64-4ff2-b8a6-e55fe85187fa")}
            ],
            "bookingStatus": "BOOKING_CREATED",
            "cartDetails": [
                {
                    "addType": map_ad_type(session_data.get("addType")),
                    "faceArea": map_face_area(session_data.get("faceArea")),
                    "location": map_location(session_data.get("location", "GREEN_PARK")),
                    "nightLight": session_data.get("nightLight") == "true",
                    "bookingDate": session_data.get("bookingStartDate"),
                    "bookingFromTime": "06:00",
                    "bookingToTime": "05:59",
                    "status": "BOOKING_CREATED"
                }
            ],
            "workflow": None
        },
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": auth_token,
            "msgId": f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }
    headers = {"Content-Type": "application/json", "auth-token": auth_token}
    try:
        res = requests.post(url, json=payload, headers=headers)
        data = res.json()
        logger.info(f"[Create Booking] Status: {res.status_code}, Response: {data}")
        if isinstance(data, dict) and "bookingApplication" in data:
            return True, data["bookingApplication"][0].get("bookingNo") or data["bookingApplication"][0].get("applicationNo")
        else:
            if isinstance(data, dict) and "Errors" in data and len(data["Errors"]) > 0:
                err_msg = data["Errors"][0].get("message") or data["Errors"][0].get("description") or str(data["Errors"][0])
                return False, err_msg
            return False, f"Server returned status {res.status_code}"
    except Exception as e:
        logger.error(f"Error creating booking: {e}")
        return False, str(e)

# --- CORE ADVISORY AGENT ENDPOINT ---

@app.post("/adv_agent")
async def chat_agent(request: Request):
    """Processes conversational turns, coordinates form step transitions, and persists state in Redis"""
    load_dotenv(override=True)
    req_data = await request.json()
    user_input = req_data.get("message", "").strip()
    phone_number = req_data.get("phone_number", "default")
    auth_token = req_data.get("token") or os.getenv("PORTAL_AUTH_TOKEN")
    workflow = req_data.get("workflow", "booking")
    reset_param = req_data.get("reset", False)
    file_name = req_data.get("file_name")
    file_data = req_data.get("file_data")
    
    logger.info(f"[Agent] Received: phone={phone_number}, workflow={workflow}, reset={reset_param}, input_len={len(user_input)}")
    if phone_number == "default":
        logger.warning("[Agent] Received default phone_number! Check app.py session_id extraction")
        
    # Load session state from Redis short-term draft database
    state = get_short_term_memory(phone_number)
    logger.info(f"[Redis] Loaded state for {phone_number}: {state.get('step')}")
    step = state.get("step")
    
    # --- RESET TRIGGER ---
    user_input_lower = user_input.lower()
    import re
    if reset_param or re.search(r'\b(hello|hi|hey|restart|reset|fresh|new booking|start booking|book ad|book a new ad)\b', user_input_lower):
        clear_short_term_memory(phone_number)
        state = get_short_term_memory(phone_number)
        step = state.get("step")

    response_payload = {"status": "active"}

    # --- THANK YOU / EXIT TRIGGER ---
    if re.search(r'\b(thank you|thanks|dhanyawad|shukriya|thank u)\b', user_input_lower):
        clear_short_term_memory(phone_number)
        return {
            "status": "completed",
            "response": "You're very welcome! I'm glad I could help you with your advertisement booking. Let me know if you need anything else! Goodbye!",
            "input_type": "text"
        }

    # --- MEMORY RETRIEVAL & SEARCH ---
    user_input_lower = user_input.lower()
    if step == "AWAITING_AD_TYPE" and any(word in user_input_lower for word in ["my booking", "previous booking", "details", "remember", "filled", "search"]):
        # 1. Check Permanent Bookings Cache for status check context
        long_term_data = get_long_term_memory(phone_number)
        
        if long_term_data and isinstance(long_term_data, list) and len(long_term_data) > 0:
            matching_bookings = []
            for b in long_term_data:
                b_str = json.dumps(b).lower()
                match = False
                for term in user_input_lower.split():
                    if len(term) > 3 and term in b_str:
                        match = True
                        break
                if match:
                    matching_bookings.append(b)
            
            if not matching_bookings:
                matching_bookings = [long_term_data[-1]]
                
            latest = matching_bookings[-1]
            if latest.get("bookingNo"):
                status = latest.get("status") or "PENDING_PAYMENT"
                response_payload["response"] = (
                    f"I found a matching booking! Application ID: {latest['bookingNo']}. "
                    f"Details: Type: {latest.get('addType')}, Location: {latest.get('location')}, "
                    f"Dates: {latest.get('bookingStartDate')} to {latest.get('bookingEndDate')}. "
                    f"Status: {status}."
                )
                response_payload["input_type"] = "choice"
                response_payload["options"] = ["Pay Now", "View My Bookings"] if status != "PAID" else ["View My Bookings"]
                response_payload["redirect_url"] = "https://niuatt.niua.in/upyog-ui/citizen/ads-home"
                return response_payload
        
        # 2. Check Active Draft to allow user to continue form filling seamlessly
        mem_data = state.get("data", {})
        if mem_data.get("addType"):
            memory_summary = (
                f"You are currently filling a draft. Here is what I have so far: "
                f"Type: {mem_data.get('addType') or 'Pending'}, "
                f"Location: {mem_data.get('location') or 'Pending'}, "
                f"Start Date: {mem_data.get('bookingStartDate') or 'Pending'}, "
                f"End Date: {mem_data.get('bookingEndDate') or 'Pending'}."
            )
            response_payload["response"] = memory_summary + " Please continue where we left off."
            return response_payload
        
        response_payload["response"] = f"I couldn't find any advertisement bookings associated with your phone number ({phone_number})."
        return response_payload

    # --- STATUS CHECK WORKFLOW ---
    if workflow == "status":
        bookings = execute_booking_search(auth_token, phone_number)
        options = ["View My Bookings"]
        if bookings and len(bookings) > 0:
            latest = bookings[0]
            booking_no = latest.get("bookingNo")
            status = latest.get("bookingStatus") or "PENDING_PAYMENT"
            if booking_no:
                msg = f"Your active advertisement application ID is {booking_no}. Status is {status}."
                if status != "PAID" and status != "APPROVED":
                    options = ["Pay Now", "View My Bookings"]
            else:
                msg = "Navigating you to your main advertisement dashboard to view your booking history."
        else:
            msg = "Navigating you to your main advertisement dashboard to view your booking history."
        
        return {
            "status": "completed",
            "response": msg,
            "input_type": "choice",
            "options": options,
            "show_button": "View My Bookings",
            "redirect_url": "https://niuatt.niua.in/upyog-ui/citizen/ads-home"
        }

    # --- FORM FILLING STATE MACHINE ---
    
    # Step 1: Validates choice and triggers transition to location step
    if step == "AWAITING_AD_TYPE":
        valid_types = ["Digital Screen", "Hoarding", "Poster", "BillBoard", "Kiosk Pole", "Unipolar"]
        user_input_matched = next((t for t in valid_types if t.lower() == user_input.lower() or map_ad_type(user_input) == map_ad_type(t)), None)
        if user_input_matched:
            state["data"]["addType"] = user_input_matched
            state["step"] = "AWAITING_LOCATION"
            response_payload["response"] = "Please select the location for your advertisement."
            response_payload["input_type"] = "choice"
            response_payload["options"] = ["Test Saket", "Jor Bagh", "Hauz Khas", "Green Park"]
        else:
            response_payload["response"] = "What type of advertisement do you want to book?"
            response_payload["input_type"] = "choice"
            response_payload["options"] = valid_types

    # Step 2: Captures location details and transitions to dimensions step
    elif step == "AWAITING_LOCATION":
        state["data"]["location"] = user_input
        state["step"] = "AWAITING_FACE_AREA"
        response_payload["response"] = "Please select the Face Area dimensions."
        response_payload["input_type"] = "choice"
        response_payload["options"] = [
            "Public Utility 20 X 10",
            "Decorative Kiosk Pole 4 X 2.5",
            "Kiosk Poles 30 X 40 (Both Side)",
            "Direction Board 20 X 6",
            "Unipole 20 X 10",
            "Unipole 18 X 8",
            "Unipole 12 X 8"
        ]

    # Step 3: Captures face area dimensions and prompts user for booking start date
    elif step == "AWAITING_FACE_AREA":
        state["data"]["faceArea"] = user_input
        state["step"] = "AWAITING_FROM_DATE"
        response_payload["response"] = "Enter the start date for the booking."
        response_payload["input_type"] = "date"
        import datetime
        response_payload["min_date"] = datetime.date.today().strftime("%Y-%m-%d")

    # Step 4: Normalizes and saves start date, then prompts user for end date
    elif step == "AWAITING_FROM_DATE":
        state["data"]["bookingStartDate"] = parse_and_format_date(user_input)
        state["step"] = "AWAITING_TO_DATE"
        response_payload["response"] = "Enter the end date for the booking."
        response_payload["input_type"] = "date"
        response_payload["min_date"] = state["data"]["bookingStartDate"]

    # Step 5: Normalizes and saves end date, then asks night lighting requirements
    elif step == "AWAITING_TO_DATE":
        state["data"]["bookingEndDate"] = parse_and_format_date(user_input)
        state["step"] = "AWAITING_NIGHT_LIGHT"
        response_payload["response"] = "Do you need Advertisement With Night Light?"
        response_payload["input_type"] = "choice"
        response_payload["options"] = ["Yes", "No"]

    # Step 6: Captures night lighting choice, calls slots check, and shows available dates
    elif step == "AWAITING_NIGHT_LIGHT":
        state["data"]["nightLight"] = "true" if user_input.lower() == "yes" else "false"
        
        # INLINE SLOT SEARCH VERIFICATION
        import uuid
        api_add_type = map_ad_type(state["data"]["addType"])
        api_face_area = map_face_area(state["data"]["faceArea"])
        api_location = map_location(state["data"]["location"])
        
        search_payload = {
            "bookingId": str(uuid.uuid4()),
            "addType": api_add_type,
            "bookingStartDate": state["data"]["bookingStartDate"],
            "bookingEndDate": state["data"]["bookingEndDate"],
            "tenantId": ADV_TENANT_ID,
            "location": api_location,
            "faceArea": api_face_area,
            "nightLight": state["data"]["nightLight"] == "true",
            "isTimerRequired": True
        }
        res_data = execute_slot_search(search_payload, auth_token)
        
        available_slots = []
        avail_details = []
        if isinstance(res_data, dict):
            avail_details = res_data.get("advertisementSlotAvailabiltityDetails") or res_data.get("advertisementSlotAvailabilityDetails") or res_data.get("advertisementSlotSearchCriteria") or []
            
        if avail_details:
            for slot in avail_details:
                status = slot.get("slotStaus") or slot.get("slotStatus") or "AVAILABLE"
                if status.upper() == "AVAILABLE":
                    date_str = slot.get("bookingDate") or slot.get("bookingStartDate")
                    if date_str:
                        type_label = state["data"]["addType"]
                        face_label = state["data"]["faceArea"]
                        nl_label = "Night Light: Yes" if state["data"]["nightLight"] == "true" else "Night Light: No"
                        option_text = f"{date_str} ({type_label} | {face_label} | {nl_label} | Status: {status.capitalize()})"
                        if option_text not in available_slots:
                            available_slots.append(option_text)
                

        if available_slots:
            state["step"] = "AWAITING_SLOT_SELECTION"
            response_payload["response"] = "Here are the available slots based on your search. Please select the date you want to add to your cart."
            response_payload["input_type"] = "choice"
            response_payload["options"] = available_slots
        else:
            state["step"] = "AWAITING_FROM_DATE"
            response_payload["response"] = "Sorry, that slot is already booked for those dates. Please try entering a different Start Date."
            response_payload["input_type"] = "date"
            
    # Step 7: Citizen locks in a date, bot triggers transition to profiling step
    elif step == "AWAITING_SLOT_SELECTION":
        selected_date = user_input.split()[0].strip()
        state["data"]["selectedSlotDate"] = selected_date
        state["data"]["bookingStartDate"] = selected_date
        state["data"]["bookingEndDate"] = selected_date
        
        state["step"] = "AWAITING_APPLICANT_CHOICE"
        response_payload["response"] = f"Added {selected_date} to cart. Let's proceed with the applicant details. Would you like to use your registered profile details, or enter new details manually?"
        response_payload["input_type"] = "choice"
        response_payload["options"] = ["Use Profile Details", "Enter Manually"]

    # Step 8: Decides between loading existing profile coordinates vs. collecting manually
    elif step == "AWAITING_APPLICANT_CHOICE":
        if "profile" in user_input.lower():
            state["step"] = "AWAITING_PINCODE"
            try:
                from database import get_user_profile_info
                info = get_user_profile_info(phone_number)
                if info:
                    state["data"]["applicantName"] = info.get("name") or "ABC"
                    state["data"]["applicantMobileNo"] = info.get("mobileNumber") or phone_number
                    state["data"]["applicantEmailId"] = info.get("emailId") or "abc@gmail.com"
            except Exception as e:
                logger.error(f"Error loading profile info in applicant choice: {e}")
                
            response_payload["response"] = "Excellent! Let's proceed with your address. Please enter your 6-digit Pincode."
            response_payload["input_type"] = "number"
        else:
            state["step"] = "AWAITING_APPLICANT_NAME"
            response_payload["response"] = "Please enter the Applicant's Name."
            response_payload["input_type"] = "text"
            
    # Step 9 (Manual Flow): Saves name, prompts user for mobile number
    elif step == "AWAITING_APPLICANT_NAME":
        state["data"]["applicantName"] = user_input
        state["step"] = "AWAITING_APPLICANT_MOBILE"
        response_payload["response"] = "Please enter the Applicant's 10-digit Mobile Number."
        response_payload["input_type"] = "number"
        
    # Step 10 (Manual Flow): Saves mobile number, prompts user for alternate mobile number
    elif step == "AWAITING_APPLICANT_MOBILE":
        state["data"]["applicantMobileNo"] = re.sub(r'\D', '', user_input)
        state["step"] = "AWAITING_ALT_MOBILE"
        response_payload["response"] = "Please enter an Alternate Mobile Number."
        response_payload["input_type"] = "number"
        
    # Step 11 (Manual Flow): Saves alternate mobile number, prompts user for email address
    elif step == "AWAITING_ALT_MOBILE":
        state["data"]["applicantAlternateMobileNo"] = re.sub(r'\D', '', user_input)
        state["step"] = "AWAITING_APPLICANT_EMAIL"
        response_payload["response"] = "Please enter the Applicant's Email ID."
        response_payload["input_type"] = "text"
        
    # Step 12 (Manual Flow): Saves email address and redirects to pincode collection step
    elif step == "AWAITING_APPLICANT_EMAIL":
        state["data"]["applicantEmailId"] = user_input
        state["step"] = "AWAITING_PINCODE"
        response_payload["response"] = "Thank you. Let's proceed with your address details. Please enter your 6-digit Pincode."
        response_payload["input_type"] = "number"

    # Step 13 (Address Flow): Saves pincode, prompts user for locality
    elif step == "AWAITING_PINCODE":
        state["data"]["pincode"] = re.sub(r'\D', '', user_input)
        state["step"] = "AWAITING_LOCALITY"
        response_payload["response"] = "Please enter your Locality."
        response_payload["input_type"] = "text"

    # Step 14 (Address Flow): Saves locality, prompts user for house number
    elif step == "AWAITING_LOCALITY":
        state["data"]["locality"] = user_input
        state["step"] = "AWAITING_HOUSE_NO"
        response_payload["response"] = "Please enter your House Number."
        response_payload["input_type"] = "text"

    # Step 15 (Address Flow): Saves house number, prompts user for street name
    elif step == "AWAITING_HOUSE_NO":
        state["data"]["houseNo"] = user_input
        state["step"] = "AWAITING_STREET_NAME"
        response_payload["response"] = "Please enter your Street Name."
        response_payload["input_type"] = "text"

    # Step 16 (Address Flow): Saves street name, prompts user for landmark details
    elif step == "AWAITING_STREET_NAME":
        state["data"]["streetName"] = user_input
        state["step"] = "AWAITING_LANDMARK"
        response_payload["response"] = "Please enter a nearby Landmark."
        response_payload["input_type"] = "text"

    # Step 17 (Address Flow): Saves landmark details, prompts user to upload ad layout file
    elif step == "AWAITING_LANDMARK":
        state["data"]["landmark"] = user_input
        state["step"] = "AWAITING_AD_SAMPLE_DOC"
        response_payload["response"] = "Address details captured. Now, let's upload your documents. Please upload your Advertisement Sample Document (image or PDF)."
        response_payload["input_type"] = "file"

    # Step 18 (Upload Flow): Uploads ad sample file via FileStore and transitions to address proof step
    elif step == "AWAITING_AD_SAMPLE_DOC":
        if file_name and file_data:
            fid = upload_to_filestore(file_name, file_data, auth_token)
            if fid: state["data"]["adSampleFileStoreId"] = fid
            
        state["step"] = "AWAITING_ADDRESS_PROOF_DOC"
        response_payload["response"] = "Advertisement Sample Document uploaded. Next, please upload your Applicant Address Proof (electricity bill, etc.)."
        response_payload["input_type"] = "file"

    # Step 19 (Upload Flow): Uploads address proof file via FileStore and transitions to identity proof step
    elif step == "AWAITING_ADDRESS_PROOF_DOC":
        if file_name and file_data:
            fid = upload_to_filestore(file_name, file_data, auth_token)
            if fid: state["data"]["addressProofFileStoreId"] = fid
            
        state["step"] = "AWAITING_IDENTITY_PROOF_DOC"
        response_payload["response"] = "Address Proof uploaded. Finally, please upload your Applicant Identity Proof (Aadhaar, Voter ID, etc.)."
        response_payload["input_type"] = "file"

    # Step 20 (Upload Flow): Uploads identity proof file via FileStore and transitions to confirmation step
    elif step == "AWAITING_IDENTITY_PROOF_DOC":
        if file_name and file_data:
            fid = upload_to_filestore(file_name, file_data, auth_token)
            if fid: state["data"]["identityProofFileStoreId"] = fid
            
        state["step"] = "CONFIRMATION"
        response_payload["response"] = "All documents uploaded successfully. Click 'Submit' below to finalize the booking application."
        response_payload["input_type"] = "choice"
        response_payload["options"] = ["Submit"]

    # Step 21 (Confirmation): Runs booking creation API, retrieves tax invoice, prompts payment method
    elif step == "CONFIRMATION":
        if user_input.lower() == "submit":
            is_created, app_id_or_err = execute_create_booking(state["data"], auth_token)
            
            if is_created:
                state["data"]["bookingNo"] = app_id_or_err
                state["data"]["status"] = "BOOKING_CREATED"
                save_long_term_memory(phone_number, state["data"])
                import time
                time.sleep(1.5)  # Allow UPYOG backend to generate the bill
                amount = fetch_bill_for_booking(app_id_or_err, auth_token)
                bill_text = f" Your total bill amount is ₹{amount}." if amount is not None else ""
                
                state["step"] = "PAYMENT_METHOD"
                response_payload["response"] = f"Booking Created successfully (Application ID: {app_id_or_err})!{bill_text} How would you like to make the payment?"
                response_payload["input_type"] = "choice"
                response_payload["options"] = ["Credit Card", "Debit Card", "UPI"]
            else:
                response_payload["response"] = f"Sorry, booking creation failed: {app_id_or_err}. Please try again."
                response_payload["input_type"] = "choice"
                response_payload["options"] = ["Submit"]
        else:
            response_payload["response"] = "Please click 'Submit' to proceed."
            response_payload["input_type"] = "choice"
            response_payload["options"] = ["Submit"]
            
    # Step 22 (Payment Flow): Saves payment method and asks user card number/UPI credentials
    elif step == "PAYMENT_METHOD":
        state["data"]["paymentMethod"] = user_input
        if "card" in user_input.lower():
            state["step"] = "AWAITING_CARD_NUMBER"
            response_payload["response"] = "Please enter your 16-digit Card Number."
            response_payload["input_type"] = "number"
        else:
            state["step"] = "PAYMENT_COMPLETE"
            response_payload["response"] = "Please enter your UPI ID."
            response_payload["input_type"] = "text"
            
    # Step 23 (Card Flow): Saves card number and asks card expiration details
    elif step == "AWAITING_CARD_NUMBER":
        state["step"] = "AWAITING_CARD_EXPIRY"
        response_payload["response"] = "Please enter the Expiry Date (MM/YY)."
        response_payload["input_type"] = "text"
        
    # Step 24 (Card Flow): Saves card expiration date and asks 3-digit CVV number
    elif step == "AWAITING_CARD_EXPIRY":
        state["step"] = "AWAITING_CARD_CVV"
        response_payload["response"] = "Please enter your 3-digit CVV."
        response_payload["input_type"] = "number"
        
    # Step 25 (Payment Finalization): Marks status PAID, deletes draft from Redis, redirects to homepage
    elif step == "AWAITING_CARD_CVV" or step == "PAYMENT_COMPLETE":
        # Update the long term memory booking status to PAID!
        long_term_bookings = get_long_term_memory(phone_number)
        if long_term_bookings:
            curr_booking_no = state["data"].get("bookingNo")
            for b in long_term_bookings:
                if b.get("bookingNo") == curr_booking_no or b.get("applicationNo") == curr_booking_no:
                    b["status"] = "PAID"
                    save_long_term_memory(phone_number, b)
                    break
                    
        clear_short_term_memory(phone_number)
        response_payload["response"] = "Payment processed successfully! Your advertisement booking is confirmed and has been reflected on your UPYOG Dashboard. Redirecting..."
        response_payload["status"] = "completed"
        response_payload["redirect_url"] = "https://niuatt.niua.in/upyog-ui/citizen/ads-home"
        return response_payload

    # Persist the updated state dictionary in Redis and send message payload back to gateway
    save_short_term_memory(phone_number, state)
    return response_payload

if __name__ == "__main__":
    print("Starting UPYOG Agent Service on port 8080...")
    uvicorn.run(app, host="0.0.0.0", port=8080)
