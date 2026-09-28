"""
UPYOG Voice Assistant v2 - Telephone Call Model
=================================================
A project that adapts the UPYOG voice bot with continuous listening,
barge-in support, and streaming responses.

Port: 8090
"""

import logging
from flask import Flask
from flask_cors import CORS
from dotenv import load_dotenv

load_dotenv()

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] [%(name)s:%(lineno)d] %(message)s"
)
logger = logging.getLogger(__name__)

# Initialize Flask app
app = Flask(__name__)
CORS(app)

from clients.upyog_client import resolve_and_set_request_environment

@app.before_request
def setup_request_environment():
    """Dynamically detects the parent UPYOG environment from request headers/origin/payload."""
    resolve_and_set_request_environment()

# Register Route Blueprints
from routes.static_routes import static_bp
app.register_blueprint(static_bp)

from routes.chat_routes import chat_bp
app.register_blueprint(chat_bp)

from routes.auth_routes import auth_bp
app.register_blueprint(auth_bp)

# User service re-exports for backward compatibility
from services.user_service import (
    _USER_PROFILE_CACHE,
    extract_phone_from_session,
    save_user_profile_info,
    get_user_profile_info,
)

if __name__ == "__main__":
    from memory_manager import init_collections
    from services.intent_service import load_plugins
    from services.rag_service import load_resources
    try:
        init_collections()
        load_plugins()
        load_resources()
        logger.info("Starting UPYOG Voice Assistant v2 on port 8090...")
        app.run(host='0.0.0.0', port=8090)
    except Exception as e:
        logger.error(f"Error starting Flask application: {e}")
        raise
