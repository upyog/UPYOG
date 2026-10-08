"""
services/user_service.py — User Profile, Session & Auth Cache Management
========================================================================
Handles user profile cache in-memory and in Redis, session phone extraction,
and citizen credential anchoring.
"""

import json
import logging
from typing import Dict, Any, Optional

logger = logging.getLogger(__name__)

# Global in-memory user profile cache
_USER_PROFILE_CACHE: Dict[str, dict] = {}


def extract_phone_from_session(session_id: str) -> str:
    """
    Extract phone number from session_id format.
    Handles: "user_9876543210" or "user-9876543210"
    Returns: "9876543210" or "default" if not found
    """
    if not session_id:
        return "default"

    # Normalize: convert hyphens to underscores
    session_clean = str(session_id).replace("-", "_")
    parts = session_clean.split("_")

    # Check if second part is a 10-digit number
    if len(parts) > 1:
        potential_phone = parts[1]
        if potential_phone.isdigit() and len(potential_phone) == 10:
            logger.info(f"Extracted phone {potential_phone} from session {session_id}")
            return potential_phone

    logger.warning(f"Could not extract phone from session_id: {session_id}")
    return "default"


def save_user_profile_info(phone_anchor: str, user_info: dict) -> bool:
    """
    Saves user profile information to in-memory cache and Redis.
    """
    try:
        _USER_PROFILE_CACHE[str(phone_anchor)] = dict(user_info)
        from database import r_client
        r_client.set(f"user_profile_info:{phone_anchor}", json.dumps(user_info))
        return True
    except Exception as e:
        logger.warning(f"Error saving user profile info: {e}")
        return True


def get_user_profile_info(phone_anchor: str) -> dict:
    """
    Retrieves user profile information from in-memory cache or Redis.
    """
    if str(phone_anchor) in _USER_PROFILE_CACHE:
        return _USER_PROFILE_CACHE[str(phone_anchor)]
    try:
        from database import r_client
        data = r_client.get(f"user_profile_info:{phone_anchor}")
        if data:
            parsed = json.loads(data.decode('utf-8') if isinstance(data, bytes) else data)
            _USER_PROFILE_CACHE[str(phone_anchor)] = parsed
            return parsed
    except Exception as e:
        logger.warning(f"Error getting user profile info: {e}")
    return _USER_PROFILE_CACHE.get(str(phone_anchor), {})
