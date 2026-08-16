import os
import redis
import json
import logging
from typing import Optional
from dotenv import load_dotenv

# Load environment variables
load_dotenv(override=True)

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Configuration
REDIS_HOST = os.getenv("REDIS_HOST", "127.0.0.1")
REDIS_PORT = int(os.getenv("REDIS_PORT", 6379))
REDIS_PASSWORD = os.getenv("REDIS_PASSWORD", None)

# Initialize Redis client with password support
r_client = redis.Redis(
    host=REDIS_HOST,
    port=REDIS_PORT,
    password=REDIS_PASSWORD if REDIS_PASSWORD else None,
    decode_responses=False
)

def verify_connection() -> bool:
    """Verify Redis is connected"""
    try:
        r_client.ping()
        logger.info(f"Redis connection verified successfully at {REDIS_HOST}:{REDIS_PORT}!")
        return True
    except redis.ConnectionError as e:
        logger.critical(f"Redis connection FAILED: {e}")
        return False

def close_connection():
    """Close Redis connection"""
    try:
        r_client.close()
        logger.info("Redis connection closed")
    except Exception as e:
        logger.error(f"Error closing Redis: {e}")

# --- SHORT-TERM MEMORY (DRAFTS) ---

def get_short_term_memory(phone_number: str, tenant_id: str = "pg.citya") -> dict:
    """Fetch active session state from Redis using PHONE NUMBER."""
    try:
        data = r_client.get(f"draft_form:{phone_number}")
        if data:
            state = json.loads(data.decode('utf-8'))
            logger.info(f"[Redis]  Loaded draft for {phone_number}: step={state.get('step')}")
            return state
        else:
            logger.info(f"[Redis] No draft found for {phone_number}, creating new")
    except Exception as e:
        logger.error(f"[Redis] Error fetching draft for {phone_number}: {e}")

    # Return fresh state
    return {
        "active": True,
        "step": "AWAITING_AD_TYPE",
        "data": {
            "addType": "",
            "location": "",
            "faceArea": "",
            "bookingStartDate": "",
            "bookingEndDate": "",
            "nightLight": "false",
            "tenantId": tenant_id,
            "isTimerRequired": False,
            "bookingNo": ""
        }
    }

def save_short_term_memory(phone_number: str, state: dict, ttl: int = 3600) -> bool:
    """Save temporary form-filling state (Lives for 1 hour)."""
    try:
        r_client.setex(f"draft_form:{phone_number}", ttl, json.dumps(state))
        logger.info(f"[Redis] Saved draft for {phone_number}: step={state.get('step')} (TTL: {ttl}s)")
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving draft for {phone_number}: {e}")
        return False

def clear_short_term_memory(phone_number: str):
    """Clear temporary form state."""
    try:
        r_client.delete(f"draft_form:{phone_number}")
        logger.info(f"[Redis] Cleared draft for {phone_number}")
    except Exception as e:
        logger.error(f"[Redis] Error clearing draft for {phone_number}: {e}")

# --- LONG-TERM MEMORY (BOOKINGS) ---

def save_long_term_memory(phone_number: str, booking_data: dict) -> bool:
    """SAVE PERMANENTLY: Anchors completed booking data to PHONE NUMBER."""
    try:
        existing = get_long_term_memory(phone_number) or []
        booking_no = booking_data.get("bookingNo")
        if booking_no:
            existing = [b for b in existing if b.get("bookingNo") != booking_no]
        existing.append(booking_data)
        r_client.set(f"long_term_bookings:{phone_number}", json.dumps(existing))
        logger.info(f"[Redis] PERMANENT saved for {phone_number}: booking_id={booking_no}")
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving permanent booking for {phone_number}: {e}")
        return False

def get_long_term_memory(phone_number: str) -> list:
    """FETCH PERMANENTLY: Retrieves past records using PHONE NUMBER."""
    try:
        data = r_client.get(f"long_term_bookings:{phone_number}")
        if data:
            val = json.loads(data.decode('utf-8'))
            if isinstance(val, list):
                return val
            return [val]
    except Exception as e:
        logger.error(f"[Redis] Error fetching permanent booking for {phone_number}: {e}")
    return []

# --- USER PROFILE NAMES ---

def get_any_user_profile_name() -> str:
    """Scan all keys matching user_profile_name:* to find any stored name (for RAG context)."""
    try:
        for key in r_client.scan_iter("user_profile_name:*"):
            val = r_client.get(key)
            if val:
                return val.decode('utf-8')
    except Exception as e:
        logger.error(f"[Redis] Error scanning profile names: {e}")
    return "User"

def save_user_profile_name(phone_anchor: str, name: str) -> bool:
    """Store user profile name associated with phone number."""
    try:
        r_client.set(f"user_profile_name:{phone_anchor}", name.encode('utf-8'))
        logger.info(f"[Redis] Saved profile name for {phone_anchor}: {name}")
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving profile name for {phone_anchor}: {e}")
        return False

def save_chat_history(phone_number: str, history: list) -> bool:
    """Save the chat conversation history permanently in Redis under the user's phone number."""
    try:
        r_client.set(f"chat_history:{phone_number}", json.dumps(history))
        logger.info(f"[Redis] Chat history saved for {phone_number}: {len(history)} turns")
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving chat history for {phone_number}: {e}")
        return False

def get_chat_history(phone_number: str) -> list:
    """Retrieve the chat conversation history from Redis for the user's phone number."""
    try:
        data = r_client.get(f"chat_history:{phone_number}")
        if data:
            return json.loads(data.decode('utf-8'))
    except Exception as e:
        logger.error(f"[Redis] Error fetching chat history for {phone_number}: {e}")
    return []
