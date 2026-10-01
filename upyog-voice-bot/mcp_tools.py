"""
mcp_tools.py — UPYOG MCP Tool Definitions (Facade / Compatibility Layer)
========================================================================
This module re-exports tools and client functions from `clients.upyog_client`
and `tools.mcp_registry` to preserve 100% backward compatibility with
existing workflows and plugins.
"""

import logging
from tools.mcp_registry import mcp
from clients.upyog_client import (
    api_client,
    UpyogAPI,
    UPYOG_BASE_URL,
    MDMS_TENANT_ID,
    SENSITIVE_LOG_KEYS,
    sanitize_payload_for_logging,
    send_otp_upyog,
    verify_otp_upyog,
    fetch_user_details_upyog,
    verify_user_auth,
    upload_to_filestore,
    search_ads,
    mdms_get,
    slot_search,
    fetch_bill,
    create_booking,
    pgr_get_categories,
    pgr_get_localities,
    pgr_create_complaint,
    pgr_search_complaints_raw,
    pgr_search_complaints,
    get_current_base_url,
    get_current_environment_config,
    resolve_environment,
    llm,
)

logger = logging.getLogger(__name__)

__all__ = [
    "mcp",
    "api_client",
    "UpyogAPI",
    "UPYOG_BASE_URL",
    "MDMS_TENANT_ID",
    "SENSITIVE_LOG_KEYS",
    "sanitize_payload_for_logging",
    "send_otp_upyog",
    "verify_otp_upyog",
    "fetch_user_details_upyog",
    "verify_user_auth",
    "upload_to_filestore",
    "search_ads",
    "mdms_get",
    "slot_search",
    "fetch_bill",
    "create_booking",
    "pgr_get_categories",
    "pgr_get_localities",
    "pgr_create_complaint",
    "pgr_search_complaints_raw",
    "pgr_search_complaints",
    "get_current_base_url",
    "get_current_environment_config",
    "resolve_environment",
    "llm",
]

if __name__ == "__main__":
    logger.info("Starting UPYOG FastMCP Server from mcp_tools...")
    mcp.run()
