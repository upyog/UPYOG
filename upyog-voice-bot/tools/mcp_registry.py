"""
tools/mcp_registry.py — FastMCP Tool Registry
=============================================
Registers UPYOG REST tools onto the FastMCP server.
"""

import logging
from fastmcp import FastMCP
from clients.upyog_client import (
    search_ads,
    mdms_get,
    slot_search,
    fetch_bill,
    create_booking,
    upload_to_filestore,
    verify_user_auth,
    pgr_get_categories,
    pgr_get_localities,
    pgr_create_complaint,
    pgr_search_complaints_raw,
    pgr_search_complaints,
)

logger = logging.getLogger(__name__)

mcp = FastMCP("UPYOG-Voice-Bot")

# Register tools on FastMCP server
mcp.tool()(search_ads)
mcp.tool()(mdms_get)
mcp.tool()(slot_search)
mcp.tool()(fetch_bill)
mcp.tool()(create_booking)
mcp.tool()(upload_to_filestore)
mcp.tool()(verify_user_auth)
mcp.tool()(pgr_get_categories)
mcp.tool()(pgr_get_localities)
mcp.tool()(pgr_create_complaint)
mcp.tool()(pgr_search_complaints_raw)
mcp.tool()(pgr_search_complaints)

if __name__ == "__main__":
    logger.info("Starting UPYOG FastMCP Server...")
    mcp.run()
