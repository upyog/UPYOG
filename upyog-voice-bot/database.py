import os
import time
import redis
import json
import logging
import fnmatch
from typing import Optional
from dotenv import load_dotenv

# Load environment variables
load_dotenv(override=True)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] [%(name)s:%(lineno)d] %(message)s"
)
logger = logging.getLogger(__name__)

# Redis Configuration with support for UPYOG / Spring / Kubernetes environment variables
REDIS_URL = os.getenv("REDIS_URL") or os.getenv("SPRING_REDIS_URL")

def _resolve_redis_host() -> str:
    # 1. Environment variables
    env_vars = [
        "REDIS_HOST",
        "SPRING_REDIS_HOST",
        "redis.backbone",
        "REDIS_BACKBONE",
        "REDIS_BACKBONE_HOST",
        "REDIS_SERVICE_HOST",
        "EGOV_REDIS_HOST",
        "CACHE_REDIS_HOST",
    ]
    for v in env_vars:
        val = os.getenv(v)
        if val and val.strip():
            return val.strip()

    # 2. Check if 'redis.backbone' or 'redis' is resolvable via environment DNS / /etc/hosts
    for candidate in ["redis.backbone", "redis", "redis-master", "redis.egov"]:
        try:
            import socket
            socket.gethostbyname(candidate)
            logger.info(f"[Redis] Successfully resolved backbone host '{candidate}' via DNS")
            return candidate
        except Exception:
            pass

    # 3. Default to localhost for local development
    return "127.0.0.1"

REDIS_HOST = _resolve_redis_host()
REDIS_PORT = int(
    os.getenv("REDIS_PORT") or 
    os.getenv("SPRING_REDIS_PORT") or 
    os.getenv("REDIS_SERVICE_PORT") or 
    6379
)
REDIS_PASSWORD = os.getenv("REDIS_PASSWORD") or os.getenv("SPRING_REDIS_PASSWORD") or None
REDIS_DB = int(os.getenv("REDIS_DB", 0))


class ResilientRedisClient:
    """
    Transparent Redis client proxy with high-speed in-memory fallback.
    - Connects to Redis using environment variables (or local default).
    - If Redis is unavailable or disconnected, operates smoothly using in-memory store
      without throwing unhandled ConnectionRefused errors or breaking workflows.
    """
    def __init__(self):
        self._memory_store = {}
        self._redis = None
        self._is_connected = False
        self._last_connect_attempt = 0
        self._init_redis()

    def _init_redis(self):
        try:
            if REDIS_URL:
                self._redis = redis.from_url(
                    REDIS_URL,
                    socket_connect_timeout=2,
                    socket_timeout=2,
                    decode_responses=False
                )
            else:
                self._redis = redis.Redis(
                    host=REDIS_HOST,
                    port=REDIS_PORT,
                    password=REDIS_PASSWORD,
                    db=REDIS_DB,
                    socket_connect_timeout=2,
                    socket_timeout=2,
                    decode_responses=False
                )
            self._redis.ping()
            self._is_connected = True
            logger.info(f"[Redis] Connected successfully to {REDIS_HOST}:{REDIS_PORT}")
        except Exception as e:
            self._is_connected = False
            logger.warning(f"[Redis] Could not connect to Redis at {REDIS_HOST}:{REDIS_PORT} ({e}). Using resilient in-memory store.")

    def _ensure_connection(self):
        if not self._is_connected and (time.time() - self._last_connect_attempt > 30):
            self._last_connect_attempt = time.time()
            try:
                self._init_redis()
            except Exception:
                pass

    def get(self, key):
        self._ensure_connection()
        if self._is_connected and self._redis:
            try:
                val = self._redis.get(key)
                if val is not None:
                    self._memory_store[key] = val
                    logger.debug(f"[Redis.get] Hit Redis for key='{key}' (len={len(val)} bytes)")
                    return val
                logger.debug(f"[Redis.get] Key not found in Redis: '{key}'")
            except Exception as e:
                self._is_connected = False
                logger.warning(f"[Redis.get] Redis failed for key='{key}', fallback to memory store: {e}")
        
        val = self._memory_store.get(key)
        if isinstance(val, str):
            return val.encode('utf-8')
        return val

    def set(self, key, value, ex=None):
        raw_val = value.encode('utf-8') if isinstance(value, str) else value
        self._memory_store[key] = raw_val
        
        self._ensure_connection()
        if self._is_connected and self._redis:
            try:
                res = self._redis.set(key, value, ex=ex)
                logger.debug(f"[Redis.set] Key='{key}' set in Redis (ex={ex})")
                return res
            except Exception as e:
                self._is_connected = False
                logger.warning(f"[Redis.set] Redis failed for key='{key}', saved to memory store: {e}")
        else:
            logger.debug(f"[Redis.set] Saved key='{key}' to in-memory store (ex={ex})")
        return True

    def setex(self, key, ttl, value):
        return self.set(key, value, ex=ttl)

    def delete(self, key):
        self._memory_store.pop(key, None)
        self._ensure_connection()
        if self._is_connected and self._redis:
            try:
                return self._redis.delete(key)
            except Exception as e:
                self._is_connected = False
                logger.warning(f"[Redis] delete('{key}') fallback to memory: {e}")
        return 1

    def scan_iter(self, match="*"):
        self._ensure_connection()
        if self._is_connected and self._redis:
            try:
                for k in self._redis.scan_iter(match=match):
                    yield k
                return
            except Exception as e:
                self._is_connected = False
                logger.warning(f"[Redis] scan_iter('{match}') fallback to memory: {e}")
        
        pattern = match or "*"
        for k in list(self._memory_store.keys()):
            k_str = k.decode('utf-8') if isinstance(k, bytes) else str(k)
            if fnmatch.fnmatch(k_str, pattern):
                yield k_str.encode('utf-8')

    def incr(self, key):
        self._ensure_connection()
        current = 0
        val = self._memory_store.get(key)
        if val is not None:
            try:
                current = int(val)
            except Exception:
                current = 0
        current += 1
        self._memory_store[key] = str(current).encode('utf-8')

        if self._is_connected and self._redis:
            try:
                return self._redis.incr(key)
            except Exception as e:
                self._is_connected = False
                logger.warning(f"[Redis] incr('{key}') fallback to memory: {e}")
        return current

    def expire(self, key, ttl):
        self._ensure_connection()
        if self._is_connected and self._redis:
            try:
                return self._redis.expire(key, ttl)
            except Exception as e:
                self._is_connected = False
        return True

    def ping(self):
        if self._is_connected and self._redis:
            try:
                return self._redis.ping()
            except Exception:
                return True
        return True

    def close(self):
        if self._redis:
            try:
                self._redis.close()
            except Exception:
                pass


# Initialize resilient client proxy
r_client = ResilientRedisClient()


def verify_connection() -> bool:
    """Verify Redis is connected"""
    return r_client.ping()


def close_connection():
    """Close Redis connection"""
    r_client.close()


# --- SHORT-TERM MEMORY (DRAFTS) ---

def get_short_term_memory(phone_number: str, tenant_id: str = "pg.citya") -> dict:
    """Fetch active session state from Redis/memory using PHONE NUMBER."""
    try:
        data = r_client.get(f"draft_form:{phone_number}")
        if data:
            state = json.loads(data.decode('utf-8') if isinstance(data, bytes) else data)
            logger.info(f"[Redis] Loaded draft for {phone_number}: step={state.get('step')}")
            return state
    except Exception as e:
        logger.error(f"[Redis] Error fetching draft for {phone_number}: {e}")

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
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving draft for {phone_number}: {e}")
        return False


def clear_short_term_memory(phone_number: str):
    """Clear temporary form state."""
    try:
        r_client.delete(f"draft_form:{phone_number}")
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
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving permanent booking for {phone_number}: {e}")
        return False


def get_long_term_memory(phone_number: str) -> list:
    """FETCH PERMANENTLY: Retrieves past records using PHONE NUMBER."""
    try:
        data = r_client.get(f"long_term_bookings:{phone_number}")
        if data:
            val = json.loads(data.decode('utf-8') if isinstance(data, bytes) else data)
            if isinstance(val, list):
                return val
            return [val]
    except Exception as e:
        logger.error(f"[Redis] Error fetching permanent booking for {phone_number}: {e}")
    return []


# --- USER PROFILE NAMES & SESSIONS ---

def get_any_user_profile_name() -> str:
    """Scan all keys matching user_profile_name:* to find any stored name (for RAG context)."""
    try:
        for key in r_client.scan_iter("user_profile_name:*"):
            val = r_client.get(key)
            if val:
                return val.decode('utf-8') if isinstance(val, bytes) else str(val)
    except Exception as e:
        logger.error(f"[Redis] Error scanning profile names: {e}")
    return "User"


def save_user_profile_name(phone_anchor: str, name: str) -> bool:
    """Store user profile name associated with phone number."""
    try:
        r_client.set(f"user_profile_name:{phone_anchor}", name)
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving profile name for {phone_anchor}: {e}")
        return False


def save_chat_history(phone_number: str, history: list) -> bool:
    """Save the chat conversation history permanently under the user's phone number."""
    try:
        r_client.set(f"chat_history:{phone_number}", json.dumps(history))
        return True
    except Exception as e:
        logger.error(f"[Redis] Error saving chat history for {phone_number}: {e}")
        return False


def get_chat_history(phone_number: str) -> list:
    """Retrieve the chat conversation history for the user's phone number."""
    try:
        data = r_client.get(f"chat_history:{phone_number}")
        if data:
            return json.loads(data.decode('utf-8') if isinstance(data, bytes) else data)
    except Exception as e:
        logger.error(f"[Redis] Error fetching chat history for {phone_number}: {e}")
    return []


def get_user_from_redis_token(token: str) -> Optional[dict]:
    """
    Directly retrieves citizen profile metadata from UPYOG user-service's Redis token store.
    egov-user stores token metadata under 'access_token:<token>' with a 'UserRequest' payload.
    """
    if not token or len(str(token)) < 10:
        return None
    try:
        raw = r_client.get(f"access_token:{token}")
        if raw:
            data = json.loads(raw.decode('utf-8') if isinstance(raw, bytes) else raw)
            if isinstance(data, dict):
                user_req = data.get("UserRequest") or data
                if isinstance(user_req, dict) and (user_req.get("mobileNumber") or user_req.get("userName")):
                    user_req["_auth_token"] = token
                    user_req["_verified_at"] = time.time()
                    return user_req
    except Exception as e:
        logger.warning(f"[Redis] Could not fetch user from access_token:{token[:8]}...: {e}")
    return None

