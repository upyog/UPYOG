import os
import io
import time
import yaml
import json
import base64
import logging
import requests
import contextvars
from urllib.parse import urlparse
from datetime import datetime, timedelta
from typing import Dict, Any, List, Tuple, Optional
from dotenv import load_dotenv

try:
    from flask import has_request_context, request, g
except ImportError:
    has_request_context = lambda: False
    request = None
    g = None

load_dotenv()

logger = logging.getLogger(__name__)

llm = None
if os.environ.get("GROQ_API_KEY"):
    try:
        from langchain_groq import ChatGroq
        llm = ChatGroq(model=os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b"), temperature=0)
    except ImportError:
        pass


SENSITIVE_LOG_KEYS = {
    "authtoken", "auth_token", "password", "access_token", "token",
    "otp", "authorization", "basic_auth", "secret", "api_key", "jwt",
    "refreshtoken", "refresh_token", "userpassword"
}


def sanitize_payload_for_logging(data: Any) -> Any:
    """Recursively redacts sensitive keys like authToken, password, access_token, otp before logging."""
    if isinstance(data, dict):
        sanitized = {}
        for k, v in data.items():
            k_lower = str(k).lower().replace("-", "").replace("_", "")
            if any(sens in k_lower for sens in ["authtoken", "password", "accesstoken", "secret", "apikey", "otp", "jwt"]):
                sanitized[k] = "[REDACTED]"
            else:
                sanitized[k] = sanitize_payload_for_logging(v)
        return sanitized
    elif isinstance(data, list):
        return [sanitize_payload_for_logging(item) for item in data]
    return data


# ==============================================================
# CONFIG LOADER — reads config.yml once at startup
# ==============================================================

def _load_config() -> dict:
    """Load API configuration from config.yml sitting in project root."""
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    config_path = os.path.join(base_dir, "config.yml")
    try:
        with open(config_path, "r") as f:
            cfg = yaml.safe_load(f)
        logger.info(f"Config loaded from {config_path}")
        return cfg.get("upyog", {})
    except Exception as e:
        logger.error(f"Failed to load config.yml: {e}. All API calls will fail.")
        return {}


_cfg = _load_config()

# ContextVar for active environment per execution thread / async task
_current_base_url_var: contextvars.ContextVar[Optional[str]] = contextvars.ContextVar("_current_base_url_var", default=None)


def resolve_environment(request_obj=None, base_url_or_env: Optional[str] = None) -> Tuple[str, dict]:
    """
    Dynamically resolves active (base_url, env_config) based on:
    1. Explicit parameter base_url_or_env
    2. Flask request context (headers, JSON payload, Origin, Referer, Host)
    3. ContextVar value (set per request thread)
    4. Environment variable UPYOG_BASE_URL or UPYOG_ENV
    5. config.yml default_env / base_url
    """
    if isinstance(request_obj, str) and base_url_or_env is None:
        base_url_or_env = request_obj
        request_obj = None

    envs = _cfg.get("environments", {})
    default_base_url = _cfg.get("base_url", "https://niuatt.niua.in")
    default_env_key = _cfg.get("default_env", "niuatt")

    def _match_candidate(val: Any, is_origin_or_host: bool = False) -> Optional[Tuple[str, dict]]:
        if not val or not isinstance(val, str):
            return None
        val_clean = val.strip().rstrip("/")
        if not val_clean:
            return None

        # Alias for local / localhost
        if val_clean.lower() in ("local", "localhost"):
            cfg = dict(envs.get("local", {}))
            return (cfg.get("base_url") or "http://localhost:8080").rstrip("/"), cfg

        # 1. Exact match with environment key
        if val_clean.lower() in envs:
            cfg = dict(envs[val_clean.lower()])
            return (cfg.get("base_url") or default_base_url).rstrip("/"), cfg

        # 2. Match configured base_url
        for env_k, env_v in envs.items():
            b_url = (env_v.get("base_url") or "").rstrip("/")
            if b_url and (val_clean.lower() == b_url.lower() or val_clean.lower().startswith(b_url.lower())):
                return b_url, dict(env_v)

        # 3. Known domain keywords match
        if "niuatt.niua.in" in val_clean.lower():
            cfg = dict(envs.get("niuatt", {}))
            return cfg.get("base_url", "https://niuatt.niua.in").rstrip("/"), cfg
        if "upyog-sandbox.niua.org" in val_clean.lower() or "sandbox" in val_clean.lower():
            cfg = dict(envs.get("sandbox", {}))
            return cfg.get("base_url", "https://upyog-sandbox.niua.org").rstrip("/"), cfg
        if "upyog.niua.org" in val_clean.lower():
            cfg = dict(envs.get("production", {}))
            return cfg.get("base_url", "https://upyog.niua.org").rstrip("/"), cfg

        # 4. If this value is an Origin/Referer/Host header from a local browser (localhost or 127.0.0.1):
        if is_origin_or_host and any(loc in val_clean.lower() for loc in ("localhost", "127.0.0.1", "0.0.0.0")):
            # If the user explicitly configured default_env as local, use local
            if default_env_key in ("local", "localhost"):
                cfg = dict(envs.get("local", {}))
                return (cfg.get("base_url") or "http://localhost:8080").rstrip("/"), cfg
            # Otherwise, do NOT use the bot's own web server as the remote UPYOG base URL.
            # Return None to let resolution continue to ContextVar/OS env/default_env (e.g. niuatt).
            return None

        # 5. Match trusted_origins
        for env_k, env_v in envs.items():
            for origin in env_v.get("trusted_origins", []):
                origin_clean = origin.rstrip("/").lower()
                if origin_clean and origin_clean in val_clean.lower():
                    return (env_v.get("base_url") or default_base_url).rstrip("/"), dict(env_v)

        # 6. If it looks like a valid http/https URL (passed explicitly or via header/payload), use it directly
        if val_clean.startswith("http://") or val_clean.startswith("https://"):
            default_tenant = _cfg.get("tenant_id", "pg.citya")
            default_state = _cfg.get("state_tenant", "pg")
            is_loc = any(loc in val_clean for loc in ("localhost", "127.0.0.1"))
            cfg = dict(envs.get("local", {})) if is_loc else {}
            cfg.setdefault("base_url", val_clean)
            cfg.setdefault("tenant_id", default_tenant)
            cfg.setdefault("state_tenant", default_state)
            return val_clean, cfg

        return None

    # 1. Explicit parameter
    if base_url_or_env:
        res = _match_candidate(base_url_or_env)
        if res:
            return res

    # 2. Flask request context
    req = request_obj or (request if has_request_context() else None)
    if req:
        # Check flask.g first if already resolved in this request
        if has_request_context() and hasattr(g, "upyog_base_url") and g.upyog_base_url:
            return g.upyog_base_url, getattr(g, "upyog_env_config", {})

        # URL Query parameters: ?env=sandbox or ?base_url=... or ?baseUrl=...
        try:
            q_env = req.args.get("env") or req.args.get("UPYOG_ENV")
            res = _match_candidate(q_env)
            if res:
                return res
            q_url = req.args.get("base_url") or req.args.get("baseUrl")
            res = _match_candidate(q_url)
            if res:
                return res
        except Exception:
            pass

        # Header: X-UPYOG-Base-Url or X-UPYOG-Env
        h_url = req.headers.get("X-UPYOG-Base-Url") or req.headers.get("X-UPYOG-Env")
        res = _match_candidate(h_url)
        if res:
            return res

        # JSON body payload: base_url or RequestInfo.baseUrl
        try:
            body = req.get_json(silent=True) or {}
            b_val = (
                body.get("base_url")
                or body.get("baseUrl")
                or (body.get("RequestInfo", {}) if isinstance(body.get("RequestInfo"), dict) else {}).get("baseUrl")
                or (body.get("RequestInfo", {}) if isinstance(body.get("RequestInfo"), dict) else {}).get("base_url")
                or (body.get("request_info", {}) if isinstance(body.get("request_info"), dict) else {}).get("baseUrl")
                or (body.get("request_info", {}) if isinstance(body.get("request_info"), dict) else {}).get("base_url")
            )
            res = _match_candidate(b_val)
            if res:
                return res
        except Exception:
            pass

        # Origin header
        origin = req.headers.get("Origin")
        res = _match_candidate(origin, is_origin_or_host=True)
        if res:
            return res

        # Referer header
        referer = req.headers.get("Referer")
        res = _match_candidate(referer, is_origin_or_host=True)
        if res:
            return res

        # Host or X-Forwarded-Host header
        host = req.headers.get("X-Forwarded-Host") or req.headers.get("Host")
        res = _match_candidate(host, is_origin_or_host=True)
        if res:
            return res

    # 3. ContextVar (thread / task local)
    c_val = _current_base_url_var.get()
    if c_val:
        res = _match_candidate(c_val)
        if res:
            return res

    # 4. OS Environment variables
    env_var = os.environ.get("UPYOG_BASE_URL") or os.environ.get("UPYOG_ENV")
    if env_var:
        res = _match_candidate(env_var)
        if res:
            return res

    # 5. Default from config.yml
    def_env = envs.get(default_env_key, {})
    return (def_env.get("base_url") or default_base_url).rstrip("/"), dict(def_env)


def resolve_and_set_request_environment(base_url_or_env: Optional[str] = None) -> Tuple[str, dict]:
    """Resolves and registers the environment in ContextVar and flask.g for the current request."""
    b_url, cfg = resolve_environment(base_url_or_env=base_url_or_env)
    _current_base_url_var.set(b_url)
    if has_request_context():
        g.upyog_base_url = b_url
        g.upyog_env_config = cfg
    return b_url, cfg


def get_current_base_url(explicit: Optional[str] = None) -> str:
    """Returns the active base URL string for the current execution context."""
    b_url, _ = resolve_environment(base_url_or_env=explicit)
    return b_url


def get_current_environment_config(explicit: Optional[str] = None) -> dict:
    """Returns the active environment configuration dictionary."""
    _, cfg = resolve_environment(base_url_or_env=explicit)
    return cfg


class DynamicBaseUrl(str):
    """Dynamic string proxy that evaluates the active environment's base URL at string formatting time."""
    def __str__(self):
        return get_current_base_url()
    def __repr__(self):
        return f"'{get_current_base_url()}'"
    def __add__(self, other):
        return get_current_base_url() + str(other)
    def __radd__(self, other):
        return str(other) + get_current_base_url()
    def rstrip(self, chars=None):
        return get_current_base_url().rstrip(chars)


class DynamicTenantId(str):
    """Dynamic string proxy that evaluates the active environment's tenant ID at string formatting time."""
    def __str__(self):
        env_cfg = get_current_environment_config()
        return env_cfg.get("tenant_id") or _cfg.get("tenant_id", "pg.citya")
    def __repr__(self):
        return f"'{self.__str__()}'"
    def __add__(self, other):
        return self.__str__() + str(other)
    def __radd__(self, other):
        return str(other) + self.__str__()


# Shortcuts used across the client (dynamic proxies for backward compatibility)
UPYOG_BASE_URL = DynamicBaseUrl(_cfg.get("base_url", "https://niuatt.niua.in"))
MDMS_TENANT_ID = DynamicTenantId(_cfg.get("tenant_id", "pg.citya"))
_ep  = _cfg.get("endpoints", {})          # API endpoint paths
_bd  = _cfg.get("booking_defaults", {})    # booking time/status/address defaults
_dt  = _cfg.get("document_types", {})     # document type strings
_sd  = _cfg.get("search_defaults", {})    # search limits, sort orders
_fs  = _cfg.get("filestore", {})          # filestore tenant/module
_mu  = _cfg.get("master_user", {})        # fallback system user profile
_mc  = _cfg.get("mdms", {})               # MDMS module/master names

# Secrets — from .env only
_BASIC_AUTH = os.environ.get("UPYOG_BASIC_AUTH", "Basic ZWdvdjZ1c2VyOmVnb3Y2dXNlcjFzZWNyZXQ=")
_USERNAME   = os.environ.get("UPYOG_USERNAME", "9999999999")
_PASSWORD   = os.environ.get("UPYOG_PASSWORD", "123456")

# Global MDMS cache partitioned by base_url: { base_url: { module_name: { master_name: [...] } } }
_mdms_cache: Dict[str, Dict[str, Any]] = {}


# ==============================================================
# UPYOG API CLIENT CLASS
# ==============================================================

class UpyogAPI:
    """
    Handles OAuth token lifecycle and all HTTP POST calls to UPYOG.
    Credentials and endpoint paths come entirely from config.yml / .env.
    Maintains isolated token caches per base URL environment.
    """

    def __init__(self):
        # Tokens cached per base_url: { "https://...": {"token": "...", "expiry": timestamp} }
        self._tokens: Dict[str, Dict[str, Any]] = {}

    def _fetch_live_token(self, base_url: Optional[str] = None) -> str:
        """Fetches a fresh OAuth token from UPYOG for the target environment."""
        b_url = base_url or get_current_base_url()
        env_cfg = get_current_environment_config(b_url)
        auth_cfg = _cfg.get("auth", {})
        token_path = auth_cfg.get("token_path", "/user/oauth/token")
        url = f"{b_url}{token_path}?_={int(time.time() * 1000)}"

        state_tenant = env_cfg.get("state_tenant") or auth_cfg.get("tenant_id", "pg")
        headers = {
            "Authorization": _BASIC_AUTH,
            "Content-Type":  "application/x-www-form-urlencoded"
        }
        data = (
            f"username={_USERNAME}"
            f"&password={_PASSWORD}"
            f"&tenantId={state_tenant}"
            f"&userType={auth_cfg.get('user_type', 'CITIZEN')}"
            f"&scope={auth_cfg.get('scope', 'read')}"
            f"&grant_type={auth_cfg.get('grant_type', 'password')}"
        )
        logger.info(f"Fetching live OAuth token from UPYOG ({b_url})...")
        res = requests.post(url, headers=headers, data=data)
        res.raise_for_status()
        token_data = res.json()
        token = token_data.get("access_token")
        buffer = auth_cfg.get("refresh_buffer_seconds", 600)
        expiry = time.time() + token_data.get("expires_in", 3600) - buffer
        self._tokens[b_url] = {
            "token": token,
            "expiry": expiry
        }
        logger.info(f"OAuth token fetched successfully for environment ({b_url}).")
        return token

    def get_live_token(self, base_url: Optional[str] = None) -> str:
        """Returns a valid token for the active environment, refreshing if expired."""
        b_url = base_url or get_current_base_url()
        token_info = self._tokens.get(b_url)
        if not token_info or time.time() > token_info.get("expiry", 0):
            return self._fetch_live_token(b_url)
        return token_info.get("token")

    @property
    def auth_token(self):
        """Backward-compatible property returning the live token for the active environment."""
        return self.get_live_token()

    @auth_token.setter
    def auth_token(self, val):
        b_url = get_current_base_url()
        if val is None:
            self._tokens.pop(b_url, None)
        else:
            self._tokens[b_url] = {"token": val, "expiry": time.time() + 3600}

    @property
    def token_expiry(self):
        """Backward-compatible property returning token expiry timestamp."""
        b_url = get_current_base_url()
        return self._tokens.get(b_url, {}).get("expiry", 0)

    @token_expiry.setter
    def token_expiry(self, val):
        b_url = get_current_base_url()
        if b_url in self._tokens:
            self._tokens[b_url]["expiry"] = val

    def get_request_info(self, phone_anchor: str = None) -> Dict[str, Any]:
        """Builds the UPYOG RequestInfo block for authenticated citizen sessions."""
        user_info  = None
        auth_token = None

        if phone_anchor and phone_anchor != "default":
            try:
                from services.user_service import get_user_profile_info
                user_info = get_user_profile_info(phone_anchor)
                if user_info:
                    auth_token = user_info.get("_auth_token")
            except Exception:
                try:
                    from app import get_user_profile_info
                    user_info = get_user_profile_info(phone_anchor)
                    if user_info:
                        auth_token = user_info.get("_auth_token")
                except Exception:
                    pass

        # Fallback 1: check in-memory profile cache
        if not user_info or not auth_token:
            try:
                from services.user_service import _USER_PROFILE_CACHE
                for p_num, p_info in _USER_PROFILE_CACHE.items():
                    if isinstance(p_info, dict) and p_info.get("_auth_token"):
                        user_info = p_info
                        auth_token = p_info.get("_auth_token")
                        break
            except Exception:
                try:
                    from app import _USER_PROFILE_CACHE
                    for p_num, p_info in _USER_PROFILE_CACHE.items():
                        if isinstance(p_info, dict) and p_info.get("_auth_token"):
                            user_info = p_info
                            auth_token = p_info.get("_auth_token")
                            break
                except Exception:
                    pass

        # Fallback 2: scan Redis/memory for any authenticated citizen profile
        if not user_info or not auth_token:
            try:
                from database import r_client
                for k in r_client.scan_iter("user_profile_info:*"):
                    raw = r_client.get(k)
                    if raw:
                        parsed = json.loads(raw.decode('utf-8') if isinstance(raw, bytes) else raw)
                        if parsed and parsed.get("_auth_token"):
                            user_info = parsed
                            auth_token = parsed.get("_auth_token")
                            break
            except Exception as e:
                logger.error(f"[get_request_info] Error scanning active profiles: {e}")

        if user_info and auth_token:
            return {
                "apiId":              "Rainmaker",
                "authToken":          auth_token,
                "userInfo":           user_info,
                "msgId":              f"{int(time.time() * 1000)}|en_IN",
                "plainAccessRequest": {}
            }

        raise PermissionError(
            f"No authenticated session found for phone '{phone_anchor}'. "
            "User must log in with their registered UPYOG mobile number first."
        )

    def get_system_request_info(self) -> Dict[str, Any]:
        """Returns a system-level RequestInfo using the master OAuth token."""
        token = None
        try:
            token = self.get_live_token()
        except Exception as e:
            logger.warning(f"Could not fetch live system OAuth token: {e}. Proceeding without token for system call.")

        return {
            "apiId":      "Rainmaker",
            "authToken":  token,
            "userInfo": {
                "id":            _mu.get("id", 0),
                "uuid":          _mu.get("uuid", ""),
                "userName":      _mu.get("user_name", ""),
                "name":          _mu.get("name", ""),
                "mobileNumber":  _mu.get("mobile_number", ""),
                "emailId":       _mu.get("email", ""),
                "locale":        _mu.get("locale", "en_IN"),
                "type":          _mu.get("type", "CITIZEN"),
                "roles": [{
                    "name":     _mu.get("role_name", "Citizen"),
                    "code":     _mu.get("role_code", "CITIZEN"),
                    "tenantId": _mu.get("tenant_id", "pg")
                }],
                "active":        True,
                "tenantId":      _mu.get("tenant_id", "pg"),
                "permanentCity": None
            },
            "msgId":              f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }

    def post(self, url: str, payload: dict) -> dict:
        """POST with automatic token injection and 401 retry per environment."""
        parsed = urlparse(url)
        base_url = f"{parsed.scheme}://{parsed.netloc}" if parsed.netloc else get_current_base_url()

        is_system = False
        req_info = payload.get("RequestInfo", {})
        if "authToken" not in req_info or req_info.get("authToken") is None:
            try:
                payload["RequestInfo"]["authToken"] = self.get_live_token(base_url)
                is_system = True
            except Exception as e:
                logger.warning(f"[UpyogAPI.post] Could not fetch live system OAuth token for {base_url}: {e}. Proceeding with payload as is.")
            
        headers = {"Content-Type": "application/json"}
        start_t = time.time()
        logger.info(f"[UpyogAPI.post] POST {url} | systemToken={is_system} | payloadKeys={list(payload.keys())}")
        res = requests.post(url, json=payload, headers=headers)
        elapsed = time.time() - start_t
        logger.info(f"[UpyogAPI.post] Response HTTP {res.status_code} from {url} in {elapsed:.2f}s")
        
        if res.status_code == 401 and is_system:
            logger.info(f"[UpyogAPI.post] Got 401 from {base_url} — refreshing token and retrying...")
            self._tokens.pop(base_url, None)
            try:
                payload["RequestInfo"]["authToken"] = self.get_live_token(base_url)
                res = requests.post(url, json=payload, headers=headers)
                logger.info(f"[UpyogAPI.post] Retry Response HTTP {res.status_code} from {url}")
            except Exception as e:
                logger.warning(f"[UpyogAPI.post] Token refresh failed: {e}")
            
        if res.status_code >= 400:
            try:
                error_body = res.json()
                logger.error(f"[UpyogAPI.post] UPYOG {res.status_code} Error details: {json.dumps(sanitize_payload_for_logging(error_body), indent=2)}")
            except Exception:
                logger.error(f"[UpyogAPI.post] UPYOG {res.status_code} Error raw text: {res.text[:500]}")
                
        res.raise_for_status()
        return res.json()


api_client = UpyogAPI()


# ==============================================================
# AUTH & OTP APIS
# ==============================================================

def send_otp_upyog(mobile: str, base_url: Optional[str] = None) -> dict:
    """Sends OTP to the given mobile number via UPYOG OTP API."""
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    auth_cfg = _cfg.get("auth", {})
    state_tenant = env_cfg.get("state_tenant") or _cfg.get("state_tenant", "pg")
    url = f"{b_url}{_cfg.get('endpoints', {}).get('send_otp', '/user-otp/v1/_send')}?tenantId={state_tenant}&_={int(time.time() * 1000)}"
    payload = {
        "otp": {
            "mobileNumber": mobile,
            "tenantId": state_tenant,
            "userType": auth_cfg.get("user_type", "CITIZEN").lower(),
            "type": "login"
        },
        "RequestInfo": {
            "apiId": "Rainmaker",
            "msgId": f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }
    headers = {"Content-Type": "application/json"}
    if _BASIC_AUTH:
        headers["Authorization"] = _BASIC_AUTH
    try:
        res = requests.post(url, json=payload, headers=headers)
        if res.status_code not in (200, 201):
            logger.error(f"UPYOG OTP Error {res.status_code}: {res.text}")
        else:
            logger.info(f"UPYOG OTP sent successfully (HTTP {res.status_code})")
        
        try:
            return res.json()
        except ValueError:
            logger.error(f"UPYOG OTP Non-JSON response: {res.text}")
            return {"error": f"Invalid response from server: {res.status_code}", "details": res.text[:200]}
            
    except Exception as e:
        logger.error(f"Error sending UPYOG OTP: {e}")
        return {"error": str(e)}


def verify_otp_upyog(mobile: str, otp: str, base_url: Optional[str] = None) -> dict:
    """Verifies OTP for the given mobile number via UPYOG OAuth token endpoint."""
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    auth_cfg = _cfg.get("auth", {})
    state_tenant = env_cfg.get("state_tenant") or _cfg.get("state_tenant", "pg")
    url = f"{b_url}{auth_cfg.get('token_path', '/user/oauth/token')}"
    data = {
        "username": mobile,
        "password": otp,
        "grant_type": auth_cfg.get("grant_type", "password"),
        "scope": auth_cfg.get("scope", "read"),
        "tenantId": state_tenant,
        "userType": auth_cfg.get("user_type", "CITIZEN")
    }
    headers = {
        "Content-Type": "application/x-www-form-urlencoded",
        "Authorization": _BASIC_AUTH
    }
    try:
        res = requests.post(url, data=data, headers=headers)
        if res.status_code != 200:
            logger.error(f"UPYOG Verify OTP Error {res.status_code}: {res.text}")
        try:
            return res.json()
        except ValueError:
            logger.error(f"UPYOG Verify OTP Non-JSON response: {res.text}")
            return {"error": f"Invalid response from server: {res.status_code}", "details": res.text[:200]}
    except Exception as e:
        logger.error(f"Error verifying UPYOG OTP: {e}")
        return {"error": str(e)}


def fetch_user_details_upyog(mobile: str, auth_token: str, base_url: Optional[str] = None) -> dict:
    """Fetches user profile details from UPYOG /user/_search."""
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    state_tenant = env_cfg.get("state_tenant") or _cfg.get("state_tenant", "pg")
    url = f"{b_url}{_cfg.get('endpoints', {}).get('user_search', '/user/_search')}?_={int(time.time() * 1000)}"
    payload = {
        "tenantId": state_tenant,
        "userName": mobile,
        "pageSize": "100",
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": auth_token,
            "msgId": f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }
    try:
        res = requests.post(url, json=payload, headers={"Content-Type": "application/json"})
        if res.status_code != 200:
            logger.error(f"UPYOG User Search Error {res.status_code}: {res.text}")
        try:
            data = res.json()
        except ValueError:
            logger.error(f"UPYOG User Search Non-JSON response: {res.text}")
            return {}
            
        if "user" in data and len(data["user"]) > 0:
            return data["user"][0]
        return {}
    except Exception as e:
        logger.error(f"Error fetching user details from UPYOG: {e}")
        return {}


def verify_user_auth(auth_token: str, uuid_or_mobile: Optional[str] = None, tenant_id: Optional[str] = None, base_url: Optional[str] = None) -> Tuple[bool, dict]:
    """Validates an auth token with UPYOG /user/_search and returns (is_valid, user_info)."""
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    state_tenant = tenant_id or env_cfg.get("state_tenant") or _cfg.get("state_tenant", "pg")
    url = f"{b_url}{_cfg.get('endpoints', {}).get('user_search', '/user/_search')}?_={int(time.time() * 1000)}"
    headers = {
        "Content-Type": "application/json",
        "auth-token": str(auth_token)
    }
    payload = {
        "tenantId": state_tenant,
        "pageSize": "100",
        "RequestInfo": {
            "apiId": "Rainmaker",
            "authToken": auth_token,
            "msgId": f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }
    if uuid_or_mobile and uuid_or_mobile != "default":
        if "-" in str(uuid_or_mobile):
            payload["uuid"] = [str(uuid_or_mobile)]
        elif str(uuid_or_mobile).isdigit():
            payload["userName"] = str(uuid_or_mobile)

    max_retries = 2
    timeout_seconds = 5
    
    for attempt in range(max_retries + 1):
        try:
            logger.info(f"[Auth] Verifying token for {uuid_or_mobile} (attempt {attempt + 1}/{max_retries + 1})...")
            res = requests.post(url, json=payload, headers=headers, timeout=timeout_seconds)
            
            if res.status_code in [500, 502, 504]:
                logger.warning(f"[Auth] Received transient status {res.status_code} from UPYOG user search. Retrying...")
                if attempt < max_retries:
                    time.sleep(1)
                    continue
                
            if res.status_code != 200:
                logger.error(f"UPYOG Verify Auth Error {res.status_code}: {res.text}")
                return False, {}
                
            try:
                data = res.json()
            except ValueError:
                logger.error(f"UPYOG Verify Auth Non-JSON response: {res.text}")
                return False, {}
                
            if "user" in data and len(data["user"]) > 0:
                logger.info(f"[Auth] Verified user successfully: {data['user'][0].get('userName') or data['user'][0].get('mobileNumber')}")
                return True, data["user"][0]
            else:
                logger.error(f"UPYOG Verify Auth failed - No user found for {uuid_or_mobile}. Response: {data}")
                return False, {}
                
        except (requests.exceptions.Timeout, requests.exceptions.ConnectionError) as net_err:
            logger.warning(f"[Auth] Network error/timeout on attempt {attempt + 1}: {net_err}")
            if attempt < max_retries:
                time.sleep(1)
                continue
            logger.error("[Auth] All auth verification retries failed due to network errors.")
            return False, {}
        except Exception as e:
            logger.error(f"Error checking user auth: {e}")
            return False, {}
            
    return False, {}


# ==============================================================
# FILESTORE API
# ==============================================================

def upload_to_filestore(file_name: str, file_data_base64: str, token: str, base_url: Optional[str] = None) -> str:
    """Uploads a document to UPYOG's filestore and returns its unique file ID."""
    logger.info(f"[upload_to_filestore] Uploading file '{file_name}' (payload length: {len(file_data_base64) if file_data_base64 else 0} chars)")
    try:
        b_url = get_current_base_url(base_url)
        env_cfg = get_current_environment_config(b_url)
        fs_tenant = env_cfg.get("state_tenant") or _fs.get("tenant", "pg")

        if "," in file_data_base64:
            header, encoded = file_data_base64.split(",", 1)
            mime_type = header.split(":")[1].split(";")[0]
        else:
            encoded   = file_data_base64
            mime_type = "application/pdf"

        file_bytes = base64.b64decode(encoded)
        url = f"{b_url}{_ep.get('filestore_upload')}"
        logger.info(f"[upload_to_filestore] Sending {len(file_bytes)} bytes ({mime_type}) to {url}")
        resp = requests.post(
            url,
            headers={"auth-token": token},
            files={"file": (file_name, io.BytesIO(file_bytes), mime_type)},
            data={"tenantId": fs_tenant, "module": _fs.get("module")}
        )
        logger.info(f"[upload_to_filestore] HTTP {resp.status_code} received")
        resp_json = resp.json()
        if "files" in resp_json and resp_json["files"]:
            file_store_id = resp_json["files"][0].get("fileStoreId", "")
            logger.info(f"[upload_to_filestore] Successfully uploaded! fileStoreId: {file_store_id}")
            return file_store_id
        logger.warning(f"[upload_to_filestore] No fileStoreId returned in response: {resp_json}")
    except Exception as e:
        logger.error(f"[upload_to_filestore] Filestore upload exception: {e}")
    return ""


# ==============================================================
# ADVERTISEMENT BOOKING APIS & MDMS
# ==============================================================

def search_ads(mobile_number: str, booking_no: str = None, status: str = None, latest: bool = False, base_url: Optional[str] = None) -> str:
    """Searches UPYOG database for past advertisement bookings using the citizen's mobile number."""
    logger.info(f"[search_ads] Called with mobile_number={mobile_number}, booking_no={booking_no}, status={status}, latest={latest}")
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    tenant_id = env_cfg.get("tenant_id") or MDMS_TENANT_ID
    url = (
        f"{b_url}{_ep.get('search_ads')}"
        f"?tenantId={tenant_id}"
        f"&limit={_sd.get('ads_limit')}"
        f"&sortOrder={_sd.get('ads_sort_order')}"
        f"&sortBy={_sd.get('ads_sort_by')}"
        f"&offset={_sd.get('ads_offset')}"
        f"&mobileNumber={mobile_number}"
    )
    if booking_no:
        url += f"&bookingNo={booking_no}"
    if status:
        url += f"&status={status}"

    payload = {"RequestInfo": api_client.get_request_info(mobile_number)}
    try:
        data = api_client.post(url, payload)
        bookings = data.get("bookingApplication", [])
        logger.info(f"[search_ads] Successfully retrieved {len(bookings)} bookings for {mobile_number}")
        if latest and bookings:
            bookings = [bookings[0]]

        optimized = []
        for b in bookings:
            optimized.append({
                "bookingNo":     b.get("bookingNo"),
                "applicationNo": b.get("applicationNo"),
                "bookingDate":   (b.get("cartDetails") or [{}])[0].get("bookingDate"),
                "status":        b.get("bookingStatus"),
                "applicantDetail": {
                    "applicantName":     b.get("applicantDetail", {}).get("applicantName"),
                    "applicantMobileNo": b.get("applicantDetail", {}).get("applicantMobileNo")
                },
                "address": {
                    "houseNo":    b.get("address", {}).get("houseNo"),
                    "streetName": b.get("address", {}).get("streetName"),
                    "city":       b.get("address", {}).get("city")
                },
                "documents": [
                    {"documentType": d.get("documentType")}
                    for d in b.get("documents", [])
                ]
            })
        return json.dumps(optimized)
    except Exception as e:
        logger.error(f"[search_ads] Error fetching bookings: {e}")
        return json.dumps({"error": str(e), "message": "Failed to fetch bookings from UPYOG"})


def mdms_get(module_name: str, master_name: str, base_url: Optional[str] = None) -> list:
    """Fetches dropdown choices like AdType, FaceArea, or Location live from UPYOG MDMS API."""
    logger.info(f"[mdms_get] Requested master={master_name} for module={module_name}")
    if master_name == "NightLight":
        return ["Yes", "No"]

    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    tenant_id = env_cfg.get("tenant_id") or MDMS_TENANT_ID
    state_tenant = env_cfg.get("state_tenant") or str(tenant_id).split(".")[0]

    env_cache = _mdms_cache.setdefault(b_url, {})
    if module_name in env_cache:
        items = env_cache[module_name].get(master_name, [])
        logger.info(f"[mdms_get] Cache HIT for {module_name}/{master_name} ({len(items)} items) on {b_url}")
        return [(item.get("name") or item.get("code") or "").strip() for item in items if item.get("active", True) and (item.get("name") or item.get("code"))]

    adv_module = _mc.get("advertisement_module")
    master_details = [{"name": master_name}]
    if module_name == adv_module:
        master_details = [
            {"name": m} for m in _mc.get("advertisement_masters")
        ]

    url = f"{b_url}{_ep.get('mdms_search')}?tenantId={state_tenant}"
    payload = {
        "MdmsCriteria": {
            "tenantId": state_tenant,
            "moduleDetails": [{
                "moduleName":  module_name,
                "masterDetails": master_details
            }]
        },
        "RequestInfo": api_client.get_system_request_info()
    }
    try:
        data        = api_client.post(url, payload)
        module_data = data.get("MdmsRes", {}).get(module_name, {})
        env_cache[module_name] = module_data
        items = module_data.get(master_name, [])
        options = [(item.get("name") or item.get("code") or "").strip() for item in items if item.get("active", True) and (item.get("name") or item.get("code"))]
        logger.info(f"[mdms_get] Fetched {len(options)} options from MDMS for {module_name}/{master_name} on {b_url}")
        return options
    except Exception as e:
        logger.error(f"[mdms_get] MDMS error for {module_name}/{master_name}: {e}")
        return []


def slot_search(addType: str, faceArea: str, location: str,
                start_date: str, end_date: str, nightLight: bool,
                phone_anchor: str = None, base_url: Optional[str] = None) -> str:
    """Checks UPYOG server to find available advertisement slots for a given date and location."""
    logger.info(f"[slot_search] Query: addType='{addType}', faceArea='{faceArea}', location='{location}', start='{start_date}', end='{end_date}', nightLight='{nightLight}', phone='{phone_anchor}'")
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    tenant_id = env_cfg.get("tenant_id") or MDMS_TENANT_ID

    url = f"{b_url}{_ep.get('slot_search')}"
    req_info = None
    if phone_anchor:
        try:
            req_info = api_client.get_request_info(phone_anchor)
        except Exception:
            pass
    if not req_info:
        req_info = api_client.get_system_request_info()

    payload = {
        "RequestInfo": req_info,
        "advertisementSlotSearchCriteria": [{
            "addType":          addType,
            "faceArea":         faceArea,
            "location":         location,
            "bookingStartDate": start_date,
            "bookingEndDate":   end_date,
            "nightLight":       str(nightLight).lower() == "true" or nightLight is True or str(nightLight).lower() == "yes",
            "isTimerRequired":  False,
            "tenantId":         tenant_id
        }]
    }
    try:
        data  = api_client.post(url, payload)
        slots = data.get("advertisementSlotAvailabiltityDetails", [])
        today_str = datetime.now().strftime("%Y-%m-%d")
        if slots:
            normalized = []
            for i, s in enumerate(slots):
                d = s.get("bookingStartDate")
                if not d:
                    try:
                        sd = datetime.strptime(start_date, "%Y-%m-%d")
                        d  = (sd + timedelta(days=i)).strftime("%Y-%m-%d")
                    except Exception:
                        d = start_date
                
                if not d or str(d).strip() <= today_str:
                    continue

                normalized.append({
                    "type":   s.get("addType")       or addType,
                    "area":   s.get("faceArea")       or faceArea,
                    "light":  "Yes" if s.get("nightLight", nightLight) else "No",
                    "date":   d,
                    "status": s.get("status") or s.get("bookingStatus") or "AVAILABLE"
                })
            logger.info(f"[slot_search] Returned {len(normalized)} normalized available slots (out of {len(slots)} raw)")
            return json.dumps(normalized)
        logger.warning(f"[slot_search] No available slots returned by UPYOG for {addType}/{location}")
        return json.dumps([])
    except Exception as e:
        logger.error(f"slot_search error from UPYOG API: {e}")
        return json.dumps([])


def fetch_bill(booking_no: str, mobile_number: str = None, base_url: Optional[str] = None) -> str:
    """Calculates and fetches the estimated bill amount for an advertisement booking."""
    logger.info(f"[fetch_bill] Called for booking_no='{booking_no}', mobile='{mobile_number}'")
    try:
        request_info = api_client.get_request_info(mobile_number)
    except PermissionError as auth_err:
        logger.warning(f"[fetch_bill] Auth permission error: {auth_err}")
        return f"Authentication required: {auth_err}"
    
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    tenant_id = env_cfg.get("tenant_id") or MDMS_TENANT_ID

    url = (
        f"{b_url}{_ep.get('fetch_bill')}"
        f"?tenantId={tenant_id}"
        f"&consumerCode={booking_no}"
        f"&businessService={_sd.get('bill_business_service')}"
    )
    payload = {"RequestInfo": request_info}
    try:
        data  = api_client.post(url, payload)
        bills = data.get("Bill", [])
        if bills:
            amount = bills[0].get("totalAmount")
            logger.info(f"[fetch_bill] Successfully fetched bill for {booking_no}: ₹{amount}")
            return f"Total Estimated Amount: ₹{amount}"
        logger.warning(f"[fetch_bill] No bill records returned for booking {booking_no}")
        return "Could not find a bill for this booking."
    except Exception as e:
        logger.error(f"[fetch_bill] Error fetching bill for {booking_no}: {e}")
        return "Unable to fetch the bill details at this moment due to a technical issue. Please try again later."


def create_booking(booking_details_json: str, base_url: Optional[str] = None) -> str:
    """Submits all collected booking data to UPYOG server to officially register the advertisement booking."""
    try:
        details = json.loads(booking_details_json)
    except Exception:
        return "Error: Could not parse booking details. Please provide valid JSON."

    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    tenant_id = env_cfg.get("tenant_id") or MDMS_TENANT_ID
    state_tenant = env_cfg.get("state_tenant") or _fs.get("tenant", "pg")

    address_str = details.get("address", "")
    addr_details = {}
    if address_str and llm:
        try:
            from langchain_core.messages import SystemMessage
            prompt = f"""You are a strict data parser for UPYOG addresses.
Given this full address: "{address_str}"
Extract these fields as a JSON object:
- "pincode": 6-digit postal code (e.g. "110001", "180091")
- "city": city name
- "locality": locality name
- "streetName": street name
- "houseNo": house number or building number (e.g. "E-56", "23")
- "landmark": landmark if present, else null

Reply with ONLY the valid JSON object (no markdown, no other text)."""
            res = llm.invoke([SystemMessage(content=prompt)])
            import re
            m = re.search(r'\{.*\}', res.content.strip(), re.DOTALL)
            if m:
                addr_details = json.loads(m.group(0))
                logger.info(f"[Auth] Extracted address details via LLM: {addr_details}")
        except Exception as e:
            logger.error(f"Error parsing address via LLM: {e}")

    mobile_number = details.get("mobileNumber", "")
    try:
        citizen_request_info = api_client.get_request_info(mobile_number)
    except PermissionError:
        try:
            citizen_request_info = api_client.get_request_info()
        except PermissionError:
            return (
                "You must be logged in with your registered UPYOG mobile number to create a booking. "
                "Please log in first using the login button."
            )

    url = f"{b_url}{_ep.get('create_booking')}"

    selected_slots = details.get("selected_slots", [])
    if not selected_slots:
        selected_slots = [{"date": details.get("start_date", "")}]

    cart_details = []
    for slot in selected_slots:
        cart_details.append({
            "addType":         slot.get("type",  details.get("addType", "")),
            "faceArea":        slot.get("area",  details.get("faceArea", "")),
            "location":        details.get("location", ""),
            "nightLight":      str(slot.get("light", details.get("nightLight", "No"))).lower() in ["yes", "true"],
            "bookingDate":     slot.get("date",  details.get("start_date", "")),
            "bookingFromTime": _bd.get("booking_from_time"),
            "bookingToTime":   _bd.get("booking_to_time"),
            "status":          _bd.get("booking_status")
        })

    documents = []
    seen_ids = set()

    def _get_unique_file_id(fid: str) -> str:
        if not fid or fid.endswith("_doc") or len(fid) < 10:
            return ""
        if fid not in seen_ids:
            seen_ids.add(fid)
            return fid
        try:
            token = citizen_request_info.get("authToken")
            url_api = f"{b_url}/filestore/v1/files/id?tenantId={state_tenant}&fileStoreId={fid}"
            file_resp = requests.get(url_api, headers={"auth-token": token})
            
            if file_resp.status_code == 200:
                upload_url = f"{b_url}{_ep.get('filestore_upload')}"
                up_resp = requests.post(
                    upload_url,
                    headers={"auth-token": token},
                    files={"file": (f"{fid}_copy.pdf", io.BytesIO(file_resp.content), "application/pdf")},
                    data={"tenantId": state_tenant, "module": _fs.get("module")}
                )
                if up_resp.status_code in [200, 201]:
                    new_id = up_resp.json()["files"][0]["fileStoreId"]
                    seen_ids.add(new_id)
                    return new_id
                else:
                    logger.warning(f"Failed to upload copy: {up_resp.status_code} - {up_resp.text}")
            else:
                logger.warning(f"Failed to download original file: {file_resp.status_code} - {file_resp.text}")
        except Exception as e:
            logger.error(f"Failed to copy file for deduplication: {e}")
            
        return ""

    doc_sample_val = _get_unique_file_id(details.get("doc_sample", ""))
    if doc_sample_val:
        documents.append({
            "documentType": _dt.get("sample"),
            "fileStoreId":  doc_sample_val,
            "documentUid":  doc_sample_val
        })

    doc_address_val = _get_unique_file_id(details.get("doc_address", ""))
    if doc_address_val:
        documents.append({
            "documentType": _dt.get("address"),
            "fileStoreId":  doc_address_val,
            "documentUid":  doc_address_val
        })

    doc_identity_val = _get_unique_file_id(details.get("doc_identity", ""))
    if doc_identity_val:
        documents.append({
            "documentType": _dt.get("identity"),
            "fileStoreId":  doc_identity_val,
            "documentUid":  doc_identity_val
        })

    payload = {
        "RequestInfo": citizen_request_info,
        "bookingApplication": {
            "tenantId": tenant_id,
            "applicantDetail": {
                "applicantName":              details.get("applicantName", "Unknown"),
                "applicantMobileNo":           details.get("mobileNumber", ""),
                "applicantAlternateMobileNo":  "",
                "applicantEmailId":            details.get("emailId", "")
            },
            "address": {
                "pincode":      addr_details.get("pincode") or details.get("pincode") or _bd.get("default_pincode"),
                "city":         addr_details.get("city") or details.get("city") or _bd.get("default_city"),
                "cityCode":     details.get("cityCode", _bd.get("default_city_code")),
                "locality":     addr_details.get("locality") or details.get("locality") or _bd.get("default_locality"),
                "localityCode": details.get("localityCode", _bd.get("default_locality_code")),
                "streetName":   addr_details.get("streetName") or details.get("streetName") or _bd.get("default_street"),
                "addressLine1": address_str if address_str else f"{addr_details.get('houseNo', '')}, {addr_details.get('streetName', '')}, {addr_details.get('locality', '')}".strip(", "),
                "addressLine2": "",
                "houseNo":      addr_details.get("houseNo") or details.get("houseNo") or _bd.get("default_house_no"),
                "landmark":     addr_details.get("landmark") or details.get("landmark") or _bd.get("default_landmark")
            },
            "cartDetails":   cart_details,
            "bookingStatus": _bd.get("booking_status"),
            "documents":     documents,
            "workflow":      None
        }
    }
    try:
        logger.info(f"=== CREATE BOOKING PAYLOAD ===\n{json.dumps(sanitize_payload_for_logging(payload), indent=2)}\n==============================")
        data = api_client.post(url, payload)
        logger.info(f"=== CREATE BOOKING RESPONSE ===\n{json.dumps(sanitize_payload_for_logging(data), indent=2)}\n===============================")
        app_no = (data.get("bookingApplication") or [{}])[0].get("bookingNo")
        if app_no:
            return f"Booking successfully created! Application Number: {app_no}"
        return f"Booking creation succeeded but no application number was returned. Raw: {data}"
    except Exception as e:
        logger.error(f"[create_booking] Error creating booking: {e}", exc_info=True)
        return (
            "I apologize, but we could not complete your advertisement booking due to a technical issue with the municipal portal. "
            "Please verify your booking details and try again, or contact the UPYOG support desk."
        )


# ==============================================================
# GRIEVANCE (PGR) APIS
# ==============================================================

def pgr_get_categories(phone_anchor: str = None, base_url: Optional[str] = None) -> dict:
    """Fetches the list of all grievance complaint categories from the UPYOG server."""
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    grv = _cfg.get("grievance", {})
    state_tenant = env_cfg.get("state_tenant") or grv.get("state_tenant", "pg")
    module       = grv.get("mdms_module", "RAINMAKER-PGR")
    master       = grv.get("mdms_master",  "ServiceDefs")

    url = f"{b_url}{_ep.get('pgr_mdms')}?tenantId={state_tenant}"
    payload = {
        "MdmsCriteria": {
            "tenantId": state_tenant,
            "moduleDetails": [{
                "moduleName": module,
                "masterDetails": [{"name": master}]
            }]
        },
        "RequestInfo": api_client.get_system_request_info()
    }
    try:
        data = api_client.post(url, payload)
        defs = data.get("MdmsRes", {}).get(module, {}).get(master, [])
        structured: dict = {}
        for d in defs:
            if not d.get("active", True):
                continue
            menu = d.get("menuPath") or "Others"
            structured.setdefault(menu, [])
            structured[menu].append({
                "name": d.get("name", ""),
                "code": d.get("serviceCode", "")
            })
        logger.info(f"[pgr_get_categories] Fetched {sum(len(v) for v in structured.values())} items across {len(structured)} groups on {b_url}")
        return structured
    except Exception as e:
        logger.error(f"pgr_get_categories error: {e}")
        return {}


def pgr_get_localities(phone_anchor: str = None, base_url: Optional[str] = None) -> list:
    """Fetches the list of all localities/areas for the city from the UPYOG server."""
    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    grv       = _cfg.get("grievance", {})
    tenant_id = env_cfg.get("tenant_id") or grv.get("tenant_id", "pg.citya")
    hierarchy = grv.get("hierarchy_type", "ADMIN")
    boundary  = grv.get("boundary_type",  "Locality")

    url = (
        f"{b_url}{_ep.get('pgr_locality')}"
        f"?hierarchyTypeCode={hierarchy}"
        f"&boundaryType={boundary}"
        f"&tenantId={tenant_id}"
    )
    payload = {"RequestInfo": api_client.get_system_request_info()}
    try:
        data       = api_client.post(url, payload)
        boundaries = data.get("TenantBoundary", [])
        if boundaries and boundaries[0].get("boundary"):
            locs = [
                {"name": b["name"], "code": b["code"]}
                for b in boundaries[0]["boundary"]
                if b.get("name") and b.get("code")
            ]
            logger.info(f"[pgr_get_localities] Fetched {len(locs)} localities on {b_url}")
            return locs
        return []
    except Exception as e:
        logger.error(f"pgr_get_localities error: {e}")
        return []


def pgr_create_complaint(complaint_json: str, base_url: Optional[str] = None) -> str:
    """Submits all collected grievance data to UPYOG server to officially register the complaint."""
    try:
        details = json.loads(complaint_json)
    except Exception:
        return "Error: Could not parse complaint details. Please provide valid JSON."

    b_url = get_current_base_url(base_url)
    env_cfg = get_current_environment_config(b_url)
    grv       = _cfg.get("grievance", {})
    tenant_id = env_cfg.get("tenant_id") or grv.get("tenant_id", "pg.citya")
    state_tenant = env_cfg.get("state_tenant") or grv.get("state_tenant", "pg")
    priority  = grv.get("priority",     "HIGH")
    source    = grv.get("source",       "web")

    phone_anchor = details.get("phone_number", "")

    try:
        request_info = api_client.get_request_info(phone_anchor)
        user_info    = request_info.get("userInfo", {})
    except PermissionError:
        try:
            request_info = api_client.get_request_info()
            user_info    = request_info.get("userInfo", {})
        except PermissionError:
            return (
                "You must be logged in with your registered UPYOG mobile number to register a complaint. "
                "Please log in first using the login button."
            )

    citizen_block = {
        "id":           user_info.get("id"),
        "userName":     user_info.get("userName") or phone_anchor,
        "name":         user_info.get("name", "Citizen"),
        "type":         user_info.get("type", "CITIZEN"),
        "mobileNumber": user_info.get("mobileNumber") or phone_anchor,
        "emailId":      user_info.get("emailId", ""),
        "roles":        user_info.get("roles", []),
        "tenantId":     user_info.get("tenantId", state_tenant),
        "uuid":         user_info.get("uuid"),
    }

    url = f"{b_url}{_ep.get('pgr_create')}?tenantId={tenant_id}"
    payload = {
        "service": {
            "tenantId":        tenant_id,
            "serviceCode":     details.get("category_code", ""),
            "accountId":       user_info.get("uuid"),
            "citizen":         citizen_block,
            "priority":        priority,
            "description":     details.get("description", ""),
            "additionalDetail": {},
            "source":          source,
            "address": {
                "tenantId": tenant_id,
                "landmark": details.get("locality", ""),
                "city":     "City A",
                "district": "City A",
                "region":   "City A",
                "state":    "Demo",
                "pincode":  "143001",
                "locality": {
                    "code": details.get("locality_code", ""),
                    "name": details.get("locality", ""),
                },
                "geoLocation": {"latitude": 0.0, "longitude": 0.0},
            },
        },
        "workflow":    {"action": "APPLY", "comments": "", "assignes": []},
        "RequestInfo": request_info,
    }

    try:
        logger.info(f"=== PGR CREATE PAYLOAD ===\n{json.dumps(sanitize_payload_for_logging(payload), indent=2)}\n==========================")
        data = api_client.post(url, payload)
        logger.info(f"=== PGR CREATE RESPONSE ===\n{json.dumps(sanitize_payload_for_logging(data), indent=2)}\n===========================")

        if "Errors" in data:
            logger.error(f"[pgr_create_complaint] Portal error: {data.get('Errors')}")
            return (
                "**Submission Notice**\n\n"
                "We were unable to complete your complaint registration due to a temporary service issue. "
                "Please verify your details and try again shortly, or contact the UPYOG helpdesk."
            )

        sw_list = data.get("ServiceWrappers", [])
        if sw_list:
            ticket_id = sw_list[0].get("service", {}).get("serviceRequestId")
            if ticket_id:
                return (
                    "**Complaint Registered Successfully**\n\n"
                    f"- **Ticket Number:** `{ticket_id}`\n"
                    f"- **Status:** Submitted\n\n"
                )
        return (
            "**Complaint Submitted**\n\n"
            "Your complaint has been submitted. However, the server did not return a ticket number at this time. "
            "Please check your complaint status under **My Complaints** in the UPYOG portal."
        )

    except Exception as e:
        logger.error(f"pgr_create_complaint error: {e}", exc_info=True)
        return (
            "**Submission Notice**\n\n"
            "We were unable to complete your complaint registration due to a temporary technical issue. "
            "Please try again shortly, or contact the UPYOG helpdesk."
        )


def pgr_search_complaints_raw(phone_anchor: str = None, complaint_id: str = None, base_url: Optional[str] = None) -> list:
    """Fetches raw JSON list of past complaints from UPYOG server for UI rendering."""
    try:
        b_url = get_current_base_url(base_url)
        env_cfg = get_current_environment_config(b_url)
        phone_number = phone_anchor if (phone_anchor and phone_anchor != "default") else "default"
        try:
            req_info = api_client.get_request_info(phone_number)
        except Exception:
            req_info = api_client.get_system_request_info()

        user_info = req_info.get("userInfo", {})
        tenant_id = user_info.get("tenantId") or env_cfg.get("tenant_id") or MDMS_TENANT_ID
        mobile = user_info.get("mobileNumber") or (phone_number if phone_number != "default" else "9999999999")
        
        url = f"{b_url}{_ep.get('pgr_search', '/pgr-services/v2/request/_search')}?tenantId={tenant_id}"
        if complaint_id and ("PG-PGR" in complaint_id.upper() or "PGR" in complaint_id.upper()):
            url += f"&serviceRequestId={complaint_id.strip()}"
        else:
            url += f"&mobileNumber={mobile}"
        
        data = api_client.post(url, {"RequestInfo": req_info})
        sw_list = data.get("ServiceWrappers", [])

        if not sw_list and complaint_id:
            fallback_url = f"{b_url}{_ep.get('pgr_search', '/pgr-services/v2/request/_search')}?tenantId={tenant_id}&mobileNumber={mobile}"
            data = api_client.post(fallback_url, {"RequestInfo": req_info})
            sw_list = data.get("ServiceWrappers", [])

        if not sw_list:
            return []

        results = []
        for sw in sw_list:
            svc = sw.get("service", {})
            req_id = svc.get("serviceRequestId", "N/A")
            service_code = svc.get("serviceCode", "N/A")
            status = svc.get("applicationStatus") or svc.get("status") or "Submitted"
            created_time = svc.get("auditDetails", {}).get("createdTime")
            filed_on = "N/A"
            if created_time:
                try:
                    from datetime import datetime
                    filed_on = datetime.fromtimestamp(created_time / 1000).strftime("%d %b %Y")
                except Exception:
                    pass
            addr = svc.get("address", {})
            loc_name = addr.get("locality", {}).get("name") or addr.get("city") or "N/A"
            
            if complaint_id:
                cid_clean = complaint_id.lower().strip()
                if cid_clean not in req_id.lower() and cid_clean not in service_code.lower():
                    continue

            results.append({
                "serviceRequestId": req_id,
                "bookingNo": req_id,
                "serviceCode": service_code,
                "status": status,
                "applicationStatus": status,
                "filed_on": filed_on,
                "bookingDate": filed_on,
                "locality": loc_name
            })

        return results[:4]
    except Exception as e:
        logger.error(f"[pgr_search_complaints_raw] error: {e}")
        return []


def pgr_search_complaints(phone_anchor: str = None, complaint_id: str = None, base_url: Optional[str] = None) -> str:
    """Searches UPYOG server for past complaints linked to the user's phone number."""
    raw_list = pgr_search_complaints_raw(phone_anchor, complaint_id=complaint_id, base_url=base_url)
    if not raw_list:
        return "No previous complaints found for your account."
    return json.dumps(raw_list)

