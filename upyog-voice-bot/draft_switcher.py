"""
src/workflow/draft_switcher.py — Dynamic Multi-Draft Switcher Engine
======================================================================
Detects active drafts for a citizen across all UPYOG modules (Qdrant DB),
renders interactive multi-draft selection menus, and manages switching 
between active form contexts seamlessly.
"""

import os
import yaml
import logging
from typing import List, Dict, Any, Optional
from memory_manager import MemoryManager

logger = logging.getLogger(__name__)

def _load_services_config() -> dict:
    """Loads services registry from config.yml to get localized module names."""
    config_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "config.yml")
    try:
        if os.path.exists(config_path):
            with open(config_path, "r", encoding="utf-8") as f:
                cfg = yaml.safe_load(f)
            return {s["key"]: s for s in cfg.get("services", [])}
    except Exception as e:
        logger.error(f"Failed to load services config in draft switcher: {e}")
    return {}

_SERVICES_MAP = _load_services_config()

class MultiDraftSwitcher:
    """
    Manages multi-draft discovery, interactive selection menu rendering,
    and workflow context switching.
    """

    @staticmethod
    def inspect_and_render_switcher(phone_number: str, user_input: str = "", user_language: str = "en") -> Dict[str, Any]:
        """
        Inspects active drafts for a phone number.
        Returns draft count, single draft payload, or formatted interactive menu in English or Hinglish.
        """
        logger.info(f"[MultiDraftSwitcher.inspect_and_render_switcher] Inspecting drafts for phone={phone_number}, lang={user_language}")
        drafts = MemoryManager.get_all_draft_states(phone_number)
        
        if not drafts:
            logger.info(f"[MultiDraftSwitcher.inspect_and_render_switcher] 0 active drafts found for {phone_number}")
            return {
                "has_drafts": False,
                "count": 0,
                "menu": None,
                "drafts": []
            }
            
        is_hinglish = any(w in user_input.lower() for w in ["hai", "koi", "btao", "dikhao", "karo", "kya", "mera", "mere", "aap", "haan"]) or user_language == "hi"

        if len(drafts) == 1:
            draft = drafts[0]
            plugin_key = draft.get("plugin_name", "unknown")
            service_info = _SERVICES_MAP.get(plugin_key, {})
            module_title = service_info.get("name", plugin_key.replace("_", " ").title())
            logger.info(f"[MultiDraftSwitcher.inspect_and_render_switcher] 1 active draft found: {plugin_key} ({module_title}) for {phone_number}")
            
            if is_hinglish:
                msg = f"Aapka ek saved draft mila hai: **{module_title}**.\nKya aap isey continue karna chahte hain ya naya form start karein?"
            else:
                msg = f"Found 1 saved draft: **{module_title}**.\nWould you like to continue this draft or start a new service request?"

            return {
                "has_drafts": True,
                "count": 1,
                "menu": msg,
                "single_draft": draft,
                "drafts": drafts
            }
            
        # Multiple active drafts exist (> 1) -> Build Dynamic Switcher Menu
        logger.info(f"[MultiDraftSwitcher.inspect_and_render_switcher] Rendering multi-draft switcher for {len(drafts)} drafts for {phone_number}")
        if is_hinglish:
            menu_lines = [f"Namaste! Aapke **{len(drafts)} active drafts** saved hain. Aap kisey continue karna chahte hain?\n"]
        else:
            menu_lines = [f"Here are your **{len(drafts)} active saved drafts**. Which application would you like to continue?\n"]

        options = []
        for idx, draft in enumerate(drafts, start=1):
            plugin_key = draft.get("plugin_name", "unknown")
            service_info = _SERVICES_MAP.get(plugin_key, {})
            module_title = service_info.get("name", plugin_key.replace("_", " ").title())
            
            menu_lines.append(f"{idx}.  **{module_title}**")
            options.append(f"{idx}. {module_title}")
            
        if is_hinglish:
            menu_lines.append(f"{len(drafts) + 1}. **Start a New Service Request**")
            menu_lines.append(f"{len(drafts) + 2}. **Delete All Drafts**")
            menu_lines.append("\nKripya option button choose karein ya number type karein.")
        else:
            menu_lines.append(f"{len(drafts) + 1}. **Start a New Service Request**")
            menu_lines.append(f"{len(drafts) + 2}. **Delete All Drafts**")
            menu_lines.append("\nPlease select a button below or enter the option number.")

        options.append("Start New Service Request")
        options.append("Delete All Drafts")
        
        return {
            "has_drafts": True,
            "count": len(drafts),
            "menu": "\n".join(menu_lines),
            "options": options,
            "drafts": drafts
        }

    @staticmethod
    def resolve_citizen_selection(user_input: str, drafts: List[Dict[str, Any]]) -> Optional[Dict[str, Any]]:
        """
        Parses citizen selection input (e.g. '1', '2', 'option 1', 'option 2', 'grievance', 'booking')
        and returns the selected target draft or None for fresh start or deletion.
        """
        import re
        clean_input = user_input.strip().lower()
        logger.info(f"[MultiDraftSwitcher.resolve_citizen_selection] Resolving input '{clean_input}' among {len(drafts)} candidate drafts")
        
        # If user explicitly wants to delete, cancel, or clear, DO NOT resolve as a resume selection!
        delete_words = ["delete", "cancel", "clear", "discard", "remove", "erase", "drop", "hatao", "hata", "mitado", "khatam"]
        if any(w in clean_input for w in delete_words):
            logger.info(f"[MultiDraftSwitcher.resolve_citizen_selection] Input '{clean_input}' contains deletion/cancellation keyword — bypassing resume selection")
            return None

        # Check numeric or 'option X' selection (e.g., '1', '2', 'option 1', 'option 2')
        digits = re.findall(r'\d+', clean_input)
        if digits:
            idx = int(digits[0]) - 1
            if 0 <= idx < len(drafts):
                chosen = drafts[idx]
                logger.info(f"[MultiDraftSwitcher.resolve_citizen_selection] Resolved by numeric index {idx} -> {chosen.get('plugin_name')}")
                return chosen
            elif idx == len(drafts):
                logger.info(f"[MultiDraftSwitcher.resolve_citizen_selection] User chose to start new application")
                return None  # Selected "Start New Application"
            elif idx == len(drafts) + 1:
                logger.info(f"[MultiDraftSwitcher.resolve_citizen_selection] User chose to delete all drafts")
                return None  # Selected "Delete All Drafts"
                
        # Check text/keyword selection matching module keys
        for draft in drafts:
            plugin_key = draft.get("plugin_name", "").lower()
            if (plugin_key in clean_input or clean_input in plugin_key or
                ("grievance" in clean_input and plugin_key == "grievance") or
                ("booking" in clean_input and plugin_key == "adv_booking")):
                logger.info(f"[MultiDraftSwitcher.resolve_citizen_selection] Resolved by keyword match -> {draft.get('plugin_name')}")
                return draft
                
        logger.warning(f"[MultiDraftSwitcher.resolve_citizen_selection] Could not resolve '{user_input}' to any draft")
        return None
