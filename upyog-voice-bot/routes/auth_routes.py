import time
import logging
from flask import Blueprint, request, jsonify

from clients.upyog_client import (
    send_otp_upyog,
    verify_otp_upyog,
    fetch_user_details_upyog
)

logger = logging.getLogger(__name__)

auth_bp = Blueprint("auth_routes", __name__)


@auth_bp.route("/api/send-otp", methods=["POST"])
@auth_bp.route("/upyog-voice-bot/api/send-otp", methods=["POST"])
@auth_bp.route("/upyog-voice/api/send-otp", methods=["POST"])
def api_send_otp():
    """Endpoint to send OTP to citizen mobile number with rate limiting."""
    req_data = request.json or {}
    mobile = req_data.get("mobile")
    base_url = req_data.get("base_url")
    if not mobile or len(mobile) != 10:
        return jsonify({"error": "Invalid mobile number"}), 400

    client_ip = request.remote_addr or "unknown"
    rate_key = f"otp_ratelimit:{mobile}:{client_ip}"
    try:
        from database import r_client
        attempts = r_client.incr(rate_key)
        if attempts == 1:
            r_client.expire(rate_key, 600)  # 10 minutes rate limit window
        if attempts > 5:
            logger.warning(f"[RateLimit] Excessive OTP requests for mobile={mobile} from IP={client_ip}")
            return jsonify({"error": "Too many OTP requests. Please wait 10 minutes before requesting again."}), 429
    except Exception as rate_err:
        logger.error(f"[RateLimit] Rate limit check error: {rate_err}")

    res = send_otp_upyog(mobile, base_url=base_url)
    return jsonify(res)


@auth_bp.route("/api/verify-otp", methods=["POST"])
@auth_bp.route("/upyog-voice-bot/api/verify-otp", methods=["POST"])
@auth_bp.route("/upyog-voice/api/verify-otp", methods=["POST"])
def api_verify_otp():
    """Endpoint to verify OTP and cache user session profile."""
    from services.user_service import save_user_profile_info
    
    req_data = request.json or {}
    mobile = req_data.get("mobile")
    otp = req_data.get("otp")
    base_url = req_data.get("base_url")
    if not mobile or not otp:
        return jsonify({"error": "Mobile and OTP are required"}), 400

    # 1. Verify OTP
    verify_res = verify_otp_upyog(mobile, otp, base_url=base_url)
    if "access_token" not in verify_res:
        logger.warning(f"[api_verify_otp] Verification failed for mobile={mobile}: {verify_res}")
        return jsonify({"error": "Invalid OTP. Please check the code sent to your phone and try again."}), 400
    
    # 2. Get user info (either from verify response or search)
    user_info = verify_res.get("UserRequest", verify_res.get("userInfo", {}))
    if not user_info or not user_info.get("uuid"):
        # fallback search
        search_res = fetch_user_details_upyog(mobile, verify_res["access_token"], base_url=base_url)
        if search_res:
            user_info = search_res

    # 3. Save verified user profile & auth token into Redis cache
    if user_info:
        user_info["_auth_token"] = verify_res["access_token"]
        user_info["_verified_at"] = time.time()
        save_user_profile_info(mobile, user_info)
        logger.info(f"[Auth] Logged in and cached user profile info in Redis for mobile={mobile}")

    return jsonify({
        "access_token": verify_res["access_token"],
        "user_info": user_info
    })


def register_auth_routes(app):
    """Registers auth Blueprint with the Flask app."""
    app.register_blueprint(auth_bp)
