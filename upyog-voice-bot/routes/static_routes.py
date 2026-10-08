import os
import logging
from flask import Blueprint, send_from_directory, Response

logger = logging.getLogger(__name__)

static_bp = Blueprint("static_routes", __name__)

BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

import base64
import json

_cached_bundled_html = None


def get_bundled_index():
    """
    Inlines assets (CSS, JS, Avatar video data) directly into index.html to prevent
    API Gateway / Zuul / Ingress from returning 401 Unauthorized on unwhitelisted
    /assets/* sub-requests on testing/production.
    """
    global _cached_bundled_html
    if _cached_bundled_html is not None:
        return _cached_bundled_html

    index_path = os.path.join(BASE_DIR, 'index.html')
    if not os.path.exists(index_path):
        return ""
    with open(index_path, 'r', encoding='utf-8') as f:
        html = f.read()

    assets_dir = os.path.join(BASE_DIR, 'assets')

    # Inline CSS
    css_path = os.path.join(assets_dir, 'styles.css')
    if os.path.exists(css_path):
        with open(css_path, 'r', encoding='utf-8') as f:
            css_content = f.read()
        html = html.replace('<link rel="stylesheet" href="assets/styles.css">', f'<style>\n{css_content}\n</style>')

    # Pre-encode avatar videos to Base64 to bypass Zuul/Ingress 401 on /assets/avatar/*.mp4
    avatar_dir = os.path.join(assets_dir, 'avatar')
    avatar_video_data = {}
    if os.path.exists(avatar_dir):
        for emotion in ['idle', 'listening', 'thinking', 'talking']:
            v_path = os.path.join(avatar_dir, f"{emotion}.mp4")
            if os.path.exists(v_path):
                try:
                    with open(v_path, 'rb') as f:
                        avatar_video_data[emotion] = base64.b64encode(f.read()).decode('utf-8')
                except Exception as err:
                    logger.error("Error reading avatar video %s: %s", v_path, err)

    avatar_b64_script = ""
    if avatar_video_data:
        avatar_b64_script = f"<script>\nwindow.AVATAR_VIDEO_BASE64 = {json.dumps(avatar_video_data)};\n</script>\n"

    # Inline JS files in exact required dependency order
    for js_file in ['constants.js', 'icons.js', 'chat.js', 'avatar.js', 'audio.js']:
        js_path = os.path.join(assets_dir, js_file)
        if os.path.exists(js_path):
            with open(js_path, 'r', encoding='utf-8') as f:
                js_content = f.read()
            if js_file == 'avatar.js' and avatar_b64_script:
                html = html.replace(f'<script src="assets/{js_file}"></script>', f'{avatar_b64_script}<script>\n{js_content}\n</script>')
            else:
                html = html.replace(f'<script src="assets/{js_file}"></script>', f'<script>\n{js_content}\n</script>')

    _cached_bundled_html = html
    return html


@static_bp.route("/")
@static_bp.route("/upyog-voice-bot", strict_slashes=False)
@static_bp.route("/upyog-voice-bot/")
def index_page():
    """
    Serves the chatbot UI (index.html).
    Route '/' handles direct local access at localhost:8090.
    Route '/upyog-voice-bot' handles requests routed through niautt's EKS ingress
    at niautt.niua.in/upyog-voice-bot.
    strict_slashes=False accepts both trailing-slash and non-trailing-slash URLs.
    """
    try:
        bundled_html = get_bundled_index()
        return Response(bundled_html, mimetype='text/html')
    except Exception as e:
        logger.error("Error generating bundled index.html: %s", e)
        return send_from_directory(BASE_DIR, 'index.html')


@static_bp.route("/assets/<path:filename>")
@static_bp.route("/upyog-voice-bot/assets/<path:filename>")
@static_bp.route("/upyog-voice/assets/<path:filename>")
def serve_assets(filename):
    """
    Serves static files from the assets/ folder (styles.css, constants.js, icons.js, chat.js, audio.js).
    Three route aliases match all deployment paths — local dev, EKS ingress, and
    the production VM nginx proxy.
    """
    return send_from_directory(os.path.join(BASE_DIR, 'assets'), filename)


def register_static_routes(app):
    """Registers static routes Blueprint with the Flask app."""
    app.register_blueprint(static_bp)
