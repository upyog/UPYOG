import json
import logging
import re
from typing import Dict, Any, List
import os
import yaml

from langchain_core.messages import SystemMessage, AIMessage, HumanMessage
from langgraph.graph import StateGraph, END, START
from langchain_groq import ChatGroq

from memory_manager import MemoryManager, shared_memory
from workflow.base_state import BaseAgentState

logger = logging.getLogger(__name__)

llm = ChatGroq(model=os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b"), temperature=0) if os.environ.get("GROQ_API_KEY") else None


# ==========================================
# STATE
# ==========================================

class GrievanceState(BaseAgentState):
    """
    Extends BaseAgentState with grievance-specific fields.

    draft_grievance keys:
      category          -- main complaint category name (display)
      sub_category      -- specific complaint type name (display)
      description       -- free-text problem description
      locality          -- area/locality name (display)
      category_code     -- service code sent to PGR API
      locality_code     -- boundary code sent to PGR API
      _category_options -- cached {group: [{name, code}]} dict from MDMS
      _sub_options      -- cached [{name, code}] for selected category
      _locality_options -- cached [{name, code}] from egov-location
      _awaiting_confirm -- True after confirm_node shows the summary
      _confirmed        -- True when user says yes at confirmation step
      _cancelled        -- True when user says no at confirmation step
    """
    draft_grievance: Dict[str, Any]
    missing_fields: List[str]


# ==========
# CONFIG
# ==========

_this_dir = os.path.dirname(os.path.abspath(__file__))
_config_path = os.path.join(os.path.dirname(_this_dir), "config.yml")
if not os.path.exists(_config_path):
    _config_path = os.path.join(_this_dir, "config.yml")

try:
    with open(_config_path, "r") as _f:
        _raw_cfg = yaml.safe_load(_f)
except Exception as e:
    logger.error(f"Failed to load config.yml in grievance.py: {e}")
    _raw_cfg = {}

_grv_form_cfg = _raw_cfg.get("forms", {}).get("grievance", {})
FORM_FIELDS   = _grv_form_cfg.get("fields", [])
ALL_FIELDS    = [f["id"] for f in FORM_FIELDS]
FIELD_HINTS   = {f["id"]: f.get("label", f["id"]) for f in FORM_FIELDS}


# Checks the draft memory and figures out which complaint detail is missing next
def get_next_step(draft: dict) -> dict:
    if not isinstance(draft, dict):
        draft = {}
    for field in FORM_FIELDS:
        if not draft.get(field["id"]):
            return field
    return {}


from mcp_tools import pgr_get_categories, pgr_get_localities, pgr_create_complaint, pgr_search_complaints, pgr_search_complaints_raw

# ==========================================
# LIVE API HELPERS (thin wrappers)
# ==========================================

# Fetches the list of all complaint categories from the UPYOG MDMS database
def _pgr_categories() -> dict:
    try:
        return pgr_get_categories() or {}
    except Exception as e:
        logger.error(f"[grievance] _pgr_categories failed: {e}")
        return {}


# Fetches the list of all valid localities for the city from the UPYOG location database
def _pgr_localities() -> list:
    try:
        return pgr_get_localities() or []
    except Exception as e:
        logger.error(f"[grievance] _pgr_localities failed: {e}")
        return []


# ==========================================
# NODES
# ==========================================

# Checks if the user wants to cancel the current complaint or view past complaints
def intent_and_ui_node(state: GrievanceState):
    messages     = state.get("messages", [])
    phone_number = state.get("phone_number", "default")
    draft        = dict(state.get("draft_grievance", {}))
    user_msg     = messages[-1].content if messages else ""
    ui_lower     = user_msg.lower().strip()

    # Cancel command from draft management system
    if user_msg == "[CANCEL_DRAFT]":
        draft = {f: None for f in ALL_FIELDS}
        MemoryManager.delete_draft_state(phone_number, "grievance")
        return {"draft_grievance": draft, "missing_fields": ALL_FIELDS}

    # Reset — only match deliberate cancel/reset phrases, not substrings
    reset_phrases = [
        r"\bstart over\b", r"\bcancel\b", r"\brestart\b",
        r"\bnew complaint\b", r"\bstart new\b", r"\bclear draft\b",
        r"\breset\b", r"\bshuruaat karo\b"
    ]
    if any(re.search(p, ui_lower) for p in reset_phrases) and not draft.get("_awaiting_confirm"):
        draft = {f: None for f in ALL_FIELDS}
        MemoryManager.delete_draft_state(phone_number, "grievance")
        resp  = (
            "**Draft Reset**\n\n"
            "Your complaint draft has been cleared. Please describe the issue "
            "you are facing and I will guide you through the filing process."
        )
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user",      content=user_msg)
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
        return {"messages": [AIMessage(content=resp)], "draft_grievance": draft, "missing_fields": ALL_FIELDS}

    # Explicit continue signal
    if ui_lower == "continue" or "continue application" in ui_lower or "continue my complaint" in ui_lower:
        return {"draft_grievance": draft}

    # Check for direct complaint ID match (e.g. PG-PGR-2026-07-23-000339 or PGR-000339)
    pgr_id_match = re.search(r'(PG-PGR-[\w-]+|PGR-[\w-]+)', user_msg, re.IGNORECASE)
    complaint_id_filter = pgr_id_match.group(1) if pgr_id_match else None

    # Past complaint status check, Complaint ID search, or affirmative response
    past_kw = [
        "my complaint", "my complaints", "my grievance", "my grievances",
        "track", "ticket", "tickets", "status of complaint", "complaint status",
        "grievance status", "complaint history", "meri shikayat", "view complaint",
        "view complaints", "show complaint", "show complaints", "latest complaints",
        "recent complaints", "previous complaints", "past complaints", "list complaints"
    ]
    
    is_past_request = bool(complaint_id_filter) or any(w in ui_lower for w in past_kw) or (
        ("complaint" in ui_lower or "grievance" in ui_lower or "shikayat" in ui_lower) and
        any(w in ui_lower for w in ["show", "view", "track", "status", "list", "latest", "recent", "history", "previous", "past", "check", "my", "get"])
    )
    if not is_past_request and len(messages) >= 2 and isinstance(messages[-2], AIMessage):
        last_ai_lower = messages[-2].content.lower()
        if "viewing your complaints" in last_ai_lower or "view your complaints" in last_ai_lower or "complaints" in last_ai_lower:
            if ui_lower in ["yes", "yeah", "yup", "sure", "ok", "okay", "show", "view", "proceed"]:
                is_past_request = True

    if is_past_request:
        raw_list = pgr_search_complaints_raw(phone_number, complaint_id=complaint_id_filter)
        if raw_list and len(raw_list) > 0:
            raw_json = json.dumps(raw_list)
            header_text = f"Here are the details for Complaint ID `{complaint_id_filter}`:" if complaint_id_filter else "Here are your recent registered complaints:"
            resp = f"{header_text}\n<ui-complaint-history data='{raw_json}' />"
            MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
            MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
            return {
                "messages": [AIMessage(content=resp)],
                "draft_grievance": draft,
                "missing_fields": [],
                "input_type": "complaint_history",
                "options": raw_list
            }
        else:
            resp = f"No complaint found matching ID **{complaint_id_filter}**." if complaint_id_filter else "No previous complaints found for your account."
            MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
            MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
            return {"messages": [AIMessage(content=resp)], "draft_grievance": draft, "missing_fields": [], "input_type": "text", "options": []}

    # Pass-through
    return {"draft_grievance": draft}


# Decides whether to stop here or send the chat to the AI for data extraction
def intent_router(state: GrievanceState) -> str:
    messages = state.get("messages", [])
    if messages and isinstance(messages[-1], AIMessage):
        return END
    return "extraction"


# Uses AI and deterministic matching to extract missing complaint details
def extraction_node(state: GrievanceState):
    draft = state.get("draft_grievance") or {}
    if not isinstance(draft, dict):
        draft = {}
    messages = state.get("messages", [])
    if not messages:
        return {"draft_grievance": draft}

    user_msg = messages[-1].content.strip()
    ui_lower = user_msg.lower()
    last_ai  = ""
    if len(messages) >= 2 and isinstance(messages[-2], AIMessage):
        last_ai = messages[-2].content

    # Detect yes/no at confirmation step
    if draft.get("_awaiting_confirm"):
        _confirm = ["yes", "yeah", "yup", "ya", "ok", "okay", "sure", "haan", "confirm",
                    "submit", "file", "bilkul", "zaroor", "1", "option 1"]
        _cancel  = ["no", "nope", "cancel", "nahi", "nahin", "mat", "band", "2", "option 2"]
        if any(w in ui_lower for w in _cancel):
            draft["_cancelled"] = True
            draft["_awaiting_confirm"] = False
            return {"draft_grievance": draft}
        if any(w in ui_lower for w in _confirm):
            draft["_confirmed"] = True
            draft["_awaiting_confirm"] = False
            return {"draft_grievance": draft}
        return {"draft_grievance": draft}

    # Meta / navigation / draft resume commands should NEVER be extracted as form field values
    meta_commands = [
        "continue", "resume", "continue application", "continue my application",
        "continue complaint", "continue my complaint", "resume draft",
        "resume my application", "open draft", "show drafts", "show my drafts",
        "draft", "drafts", "my drafts", "view drafts", "list drafts", "my draft",
        "start over", "cancel draft", "delete draft", "clear draft",
        "aage badho", "chalu karo", "continue please", "proceed", "next", "skip"
    ]
    if any(ui_lower == w or ui_lower.startswith(w + " ") for w in meta_commands):
        logger.info(f"[grievance extract_node] User message '{user_msg}' is a meta/resume command; skipping field extraction.")
        return {"draft_grievance": draft}

    # Detect if user's message is a question/FAQ query rather than a form answer
    question_triggers = ["?", "what is", "what does", "explain", "meaning", "kya hai", "kaise", "kyun", "tell me about"]
    is_question = any(q in ui_lower for q in question_triggers)
    if is_question:
        logger.info(f"[grievance extract_node] User asked a question ('{user_msg}'), skipping field extraction.")
        return {"draft_grievance": draft}

    missing = [f for f in ALL_FIELDS if not draft.get(f)]
    if not missing:
        return {"draft_grievance": draft}

    next_step = get_next_step(draft)
    curr_field = next_step.get("id")

    # Deterministic extraction based on the active step being answered
    cleaned_input = re.sub(r'^\d+[\.\)]\s*', '', user_msg).strip() # Strip leading numbering e.g. "1. PropertyTax" -> "PropertyTax"

    if curr_field == "category":
        cats = draft.get("_category_options") or _pgr_categories()
        draft["_category_options"] = cats
        cat_keys = list(cats.keys())
        
        # Check option index matching (e.g. "1" or "Option 1")
        idx_match = re.match(r'^(?:option\s*)?(\d+)$', ui_lower)
        if idx_match:
            idx = int(idx_match.group(1)) - 1
            if 0 <= idx < len(cat_keys):
                chosen_cat = cat_keys[idx]
                draft["category"] = chosen_cat
                draft["_sub_options"] = cats[chosen_cat]
                logger.info(f"[grievance extract] category resolved by index {idx+1}: {chosen_cat}")
        
        if not draft.get("category"):
            norm_input = re.sub(r'[\s_]+', '', cleaned_input.lower())
            matched_cat = next((k for k in cat_keys if re.sub(r'[\s_]+', '', k.lower()) == norm_input), None)
            if not matched_cat:
                matched_cat = next((k for k in cat_keys if norm_input in re.sub(r'[\s_]+', '', k.lower()) or re.sub(r'[\s_]+', '', k.lower()) in norm_input), None)
            
            if matched_cat:
                draft["category"] = matched_cat
                draft["_sub_options"] = cats[matched_cat]
                logger.info(f"[grievance extract] category matched: {matched_cat}")

    elif curr_field == "sub_category":
        cat_selected = draft.get("category", "")
        cats = draft.get("_category_options") or _pgr_categories()
        sub_ops = draft.get("_sub_options") or cats.get(cat_selected, [])
        draft["_sub_options"] = sub_ops

        # Check option index matching
        idx_match = re.match(r'^(?:option\s*)?(\d+)$', ui_lower)
        if idx_match and sub_ops:
            idx = int(idx_match.group(1)) - 1
            if 0 <= idx < len(sub_ops):
                chosen_sub = sub_ops[idx]
                draft["sub_category"] = chosen_sub["name"]
                draft["category_code"] = chosen_sub["code"]
                logger.info(f"[grievance extract] sub_category resolved by index {idx+1}: {chosen_sub['name']}")

        if not draft.get("sub_category") and sub_ops:
            norm_input = re.sub(r'[\s_]+', '', cleaned_input.lower())
            matched = next((s for s in sub_ops if re.sub(r'[\s_]+', '', s["name"].lower()) == norm_input), None)
            if not matched:
                matched = next((s for s in sub_ops if norm_input in re.sub(r'[\s_]+', '', s["name"].lower()) or re.sub(r'[\s_]+', '', s["name"].lower()) in norm_input), None)
            if matched:
                draft["sub_category"] = matched["name"]
                draft["category_code"] = matched["code"]
                logger.info(f"[grievance extract] sub_category matched: {matched['name']}")

    elif curr_field == "description":
        # Any genuine answer to the problem description question is accepted
        generic_intents = ["i want to file a complaint", "file a complaint", "raise a grievance", "file complaint", "want to file a complaint", "i have a complaint"] + meta_commands
        if not any(g == ui_lower for g in generic_intents) and len(user_msg) >= 3 and ui_lower not in meta_commands:
            draft["description"] = user_msg
            logger.info(f"[grievance extract] description accepted: {user_msg}")

    elif curr_field == "locality":
        loc_ops = draft.get("_locality_options") or _pgr_localities()
        draft["_locality_options"] = loc_ops

        idx_match = re.match(r'^(?:option\s*)?(\d+)$', ui_lower)
        if idx_match and loc_ops:
            idx = int(idx_match.group(1)) - 1
            if 0 <= idx < len(loc_ops):
                chosen_loc = loc_ops[idx]
                draft["locality"] = chosen_loc["name"]
                draft["locality_code"] = chosen_loc["code"]
                logger.info(f"[grievance extract] locality resolved by index {idx+1}: {chosen_loc['name']}")

        if not draft.get("locality") and loc_ops:
            norm_input = re.sub(r'[\s_]+', '', cleaned_input.lower())
            matched = next((l for l in loc_ops if re.sub(r'[\s_]+', '', l["name"].lower()) == norm_input), None)
            if not matched:
                matched = next((l for l in loc_ops if norm_input in re.sub(r'[\s_]+', '', l["name"].lower()) or re.sub(r'[\s_]+', '', l["name"].lower()) in norm_input), None)
            if matched:
                draft["locality"] = matched["name"]
                draft["locality_code"] = matched["code"]
                logger.info(f"[grievance extract] locality matched: {matched['name']}")
            elif (
                len(cleaned_input) >= 3
                and not is_question
                and ui_lower not in meta_commands
                and cleaned_input.lower() not in ["yes", "no", "ok", "okay", "none", "na", "null", "undefined", "1", "2", "3", "4"]
            ):
                draft["locality"] = cleaned_input
                draft["locality_code"] = loc_ops[0]["code"] if loc_ops else "UNKNOWN"
                logger.info(f"[grievance extract] locality accepted verbatim: {cleaned_input}")

    # Fallback to LLM multi-field extraction if any missing fields remain and LLM is available
    remaining_missing = [f for f in ALL_FIELDS if not draft.get(f)]
    if remaining_missing and llm:
        schema_hints = {}
        for f in remaining_missing:
            if f == "category":
                cats = draft.get("_category_options") or _pgr_categories()
                opts = list(cats.keys())
                schema_hints[f] = f"MUST MATCH one of: {opts}" if opts else "Grievance Category"
            elif f == "sub_category":
                sub_ops = [s["name"] for s in (draft.get("_sub_options") or [])]
                schema_hints[f] = f"MUST MATCH one of: {sub_ops}" if sub_ops else "Specific complaint type"
            elif f == "locality":
                loc_ops = draft.get("_locality_options") or _pgr_localities()
                loc_names = [l["name"] for l in loc_ops[:25]]
                schema_hints[f] = f"MUST MATCH one of: {loc_names}" if loc_names else "Area or locality name"
            elif f == "description":
                schema_hints[f] = "Detailed description of the issue provided by the citizen (e.g. 'i want a refund', 'dirty water from tap', 'pothole on road')"

        collected_str = json.dumps({k: v for k, v in draft.items() if v and not k.startswith("_")})
        extract_prompt = f"""You are a data extraction AI for UPYOG Grievance filing.
Extract missing form field values from the user's message.

Missing fields: {json.dumps(remaining_missing)}
Field hints & valid options: {json.dumps(schema_hints)}
Already collected fields: {collected_str}
Bot's last question: "{last_ai}"
User's message: "{user_msg}"

Rules:
1. If the user answered the missing field in their message, extract the exact value.
2. For 'description', extract the user's description of their problem/request (e.g. 'i want a refund', 'water leakage', 'streetlight damaged').
3. Return null for any field not answered.
4. NEVER extract meta or navigation words like 'continue', 'resume', 'draft', 'show drafts', 'my drafts' as any field value.

Reply ONLY with valid JSON:
{{{', '.join(f'"{f}": "extracted value or null"' for f in remaining_missing)}}}"""

        try:
            ext = llm.invoke([SystemMessage(content=extract_prompt)])
            m = re.search(r'\{.*\}', ext.content.strip(), re.DOTALL)
            if m:
                extracted = json.loads(m.group(0))
                for field in remaining_missing:
                    val = extracted.get(field)
                    if val is not None and str(val).lower() not in ("null", "none", ""):
                        val_str = str(val).strip()
                        if val_str.lower() in meta_commands:
                            continue
                        if field == "description" and len(val_str) >= 3:
                            draft["description"] = val_str
                            logger.info(f"[grievance LLM extract] description = {val_str}")
                        elif field == "category" and not draft.get("category"):
                            cats = draft.get("_category_options") or _pgr_categories()
                            matched_cat = next((k for k in cats.keys() if k.lower() == val_str.lower()), None)
                            if matched_cat:
                                draft["category"] = matched_cat
                                draft["_sub_options"] = cats[matched_cat]
                        elif field == "sub_category" and not draft.get("sub_category"):
                            sub_ops = draft.get("_sub_options") or []
                            matched = next((s for s in sub_ops if s["name"].lower() == val_str.lower()), None)
                            if matched:
                                draft["sub_category"] = matched["name"]
                        elif field == "locality" and not draft.get("locality"):
                            loc_ops = draft.get("_locality_options") or _pgr_localities()
                            matched = next((l for l in loc_ops if l["name"].lower() == val_str.lower()), None)
                            if matched:
                                draft["locality"] = matched["name"]
                                draft["locality_code"] = matched["code"]
                            elif len(val_str) >= 3 and val_str.lower() not in meta_commands:
                                draft["locality"] = val_str
                                draft["locality_code"] = loc_ops[0]["code"] if loc_ops else "UNKNOWN"
        except Exception as e:
            logger.error(f"[grievance] LLM extraction error: {e}")

    # Auto-Save to Qdrant Disk Store on every turn if fields are present
    phone_number = state.get("phone_number", "default")
    if draft and any(v for k, v in draft.items() if not k.startswith("_") and v is not None) and phone_number and phone_number != "default":
        MemoryManager.save_draft_state(phone_number, "grievance", draft)

    return {"draft_grievance": draft}


# Asks the user the next missing question and shows dropdown options if applicable
def ask_next_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    draft = state.get("draft_grievance") or {}
    if not isinstance(draft, dict):
        draft = {}
    messages = state.get("messages", [])
    user_msg = messages[-1].content if messages else ""

    next_step = get_next_step(draft)
    if not next_step:
        return {}

    field_id    = next_step["id"]
    field_label = next_step.get("label", field_id)
    field_type  = next_step.get("type", "text")
    options: list = []

    if field_id == "category":
        cats = draft.get("_category_options") or _pgr_categories()
        draft["_category_options"] = cats
        options = list(cats.keys())

    elif field_id == "sub_category":
        cat_selected = draft.get("category", "")
        cats         = draft.get("_category_options") or _pgr_categories()
        sub_ops      = cats.get(cat_selected, [])
        draft["_sub_options"] = sub_ops
        options = [s["name"] for s in sub_ops]

    elif field_id == "locality":
        locs = draft.get("_locality_options") or _pgr_localities()
        draft["_locality_options"] = locs
        options = [l["name"] for l in locs[:25]]

    text = f"Please provide the {field_label}."
    if llm:
        is_hindi = any('ऀ' <= c <= 'ॿ' for c in user_msg)
        lang_rule = "Respond in Hindi." if is_hindi else "Respond in English. Do NOT output any Devanagari or Hindi text."
        ctx = f"""You are a UPYOG grievance filing assistant collecting a specific form field.
Collected so far: {json.dumps({k: v for k, v in draft.items() if v and not k.startswith("_")})}
Next field to collect: "{field_label}" (field id: "{field_id}")
Available options: {json.dumps(options) if options else "Free text -- user can type anything"}
User's last message: "{user_msg}"

CRITICAL INSTRUCTIONS:
1. Language: {lang_rule}
2. Ask ONLY for the "{field_label}" field. Do NOT ask anything else.
3. Do NOT ask follow-up questions about duration, impact, or history.
4. Do NOT list the options in text (the UI dropdown will show them).
5. If field is 'description': ask the user to briefly describe the problem they are facing.
6. Keep it to 1-2 sentences. Output ONLY the question, nothing else.
7. Use **bold** for the field name when mentioning it.
8. Maintain a formal, polite, professional tone. Never use informal or familial terms of address."""
        try:
            conv = llm.invoke([SystemMessage(content=ctx)])
            text = conv.content.strip()
        except Exception:
            pass

    if field_type == "dropdown" and options:
        opts_str = ", ".join(f"'{o}'" for o in options)
        resp = f"{text}\n<ui-dropdown options=[{opts_str}] />"
    else:
        resp = text

    if user_msg and user_msg not in ["continue", "hello", "[CANCEL_DRAFT]"]:
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    logger.info(f"[grievance.ask_next_node] Asked citizen for '{field_id}'. Missing fields left: {[f for f in ALL_FIELDS if not draft.get(f)]}")
    return {
        "messages":        [AIMessage(content=resp)],
        "draft_grievance": draft,
        "missing_fields":  [f for f in ALL_FIELDS if not draft.get(f)],
        "input_type":      "text",
        "options":         []
    }


# Shows a final summary receipt of the complaint details and asks the user to confirm submission
def confirm_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    draft        = dict(state.get("draft_grievance", {}))
    messages     = state.get("messages", [])
    user_msg     = messages[-1].content if messages else ""
    logger.info(f"[grievance.confirm_node] Prompting confirmation for phone={phone_number}")

    # Ensure description is meaningful
    desc = str(draft.get("description", "")).strip()
    if not desc or len(desc) < 3:
        sub = draft.get('sub_category') or draft.get('category') or 'Complaint'
        loc = draft.get('locality') or 'the specified locality'
        draft['description'] = f"{sub} issue reported at {loc}"

    summary = (
        "**Review Your Complaint Details:**\n\n"
        f"- **Category:** {draft.get('category', 'N/A')}\n"
        f"- **Complaint Type:** {draft.get('sub_category', 'N/A')}\n"
        f"- **Description:** {draft.get('description')}\n"
        f"- **Locality:** {draft.get('locality', 'N/A')}\n\n"
        "Please reply **Yes** to submit or **No** to cancel.\n"
        "<ui-button options=['Yes', 'No'] />"
    )
    draft["_awaiting_confirm"] = True

    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=summary)

    return {"messages": [AIMessage(content=summary)], "draft_grievance": draft}


# Submits the finalized complaint to the UPYOG server to register the grievance and get an ID
def create_complaint_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    draft        = dict(state.get("draft_grievance", {}))
    logger.info(f"[grievance.create_complaint_node] Submitting complaint for phone={phone_number}")

    payload = {
        "phone_number":  phone_number,
        "category_code": draft.get("category_code", ""),
        "sub_category":  draft.get("sub_category",  ""),
        "description":   draft.get("description",   ""),
        "locality":      draft.get("locality",       ""),
        "locality_code": draft.get("locality_code",  ""),
    }

    raw_resp = pgr_create_complaint(json.dumps(payload))
    resp = f"{raw_resp}\n\nIs there anything else I can help you with?"
    logger.info(f"[grievance.create_complaint_node] Result: {raw_resp}")
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    reset_draft = {f: None for f in ALL_FIELDS}
    MemoryManager.delete_draft_state(phone_number, "grievance")
    return {"messages": [AIMessage(content=resp)], "draft_grievance": reset_draft, "missing_fields": ALL_FIELDS, "input_type": "text", "options": []}


# Cancels the current complaint draft and clears the memory
def cancel_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    messages     = state.get("messages", [])
    user_msg     = messages[-1].content if messages else ""
    logger.info(f"[grievance.cancel_node] Cancelling complaint draft for phone={phone_number}")

    resp = (
        "**Complaint Withdrawn**\n\n"
        "Your complaint has been cancelled. No submission was made to the portal.\n\n"
        "If you wish to file a new complaint, please describe the issue and I will assist you."
    )
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    reset_draft = {f: None for f in ALL_FIELDS}
    MemoryManager.delete_draft_state(phone_number, "grievance")
    return {"messages": [AIMessage(content=resp)], "draft_grievance": reset_draft, "missing_fields": ALL_FIELDS}


# The main traffic controller that routes the flow to the correct next step based on what's missing
def router(state: GrievanceState) -> str:
    draft      = state.get("draft_grievance") or {}
    all_filled = all(draft.get(f) for f in ALL_FIELDS)

    if draft.get("_cancelled"):
        logger.info("[grievance.router] Route -> cancel")
        return "cancel"
    if draft.get("_confirmed"):
        logger.info("[grievance.router] Route -> create_complaint")
        return "create_complaint"
    if not all_filled:
        logger.info(f"[grievance.router] Route -> ask_next (missing fields: {[f for f in ALL_FIELDS if not draft.get(f)]})")
        return "ask_next"
    logger.info("[grievance.router] Route -> confirm")
    return "confirm"


# ==========================================
# GRAPH ASSEMBLY
# ==========================================

_wf = StateGraph(GrievanceState)

_wf.add_node("intent_and_ui",    intent_and_ui_node)
_wf.add_node("extraction",       extraction_node)
_wf.add_node("ask_next",         ask_next_node)
_wf.add_node("confirm",          confirm_node)
_wf.add_node("create_complaint", create_complaint_node)
_wf.add_node("cancel",           cancel_node)

_wf.add_edge(START, "intent_and_ui")
_wf.add_conditional_edges(
    "intent_and_ui",
    intent_router,
    {"extraction": "extraction", END: END}
)
_wf.add_conditional_edges(
    "extraction",
    router,
    {
        "ask_next":         "ask_next",
        "confirm":          "confirm",
        "create_complaint": "create_complaint",
        "cancel":           "cancel",
    }
)
_wf.add_edge("ask_next",         END)
_wf.add_edge("confirm",          END)
_wf.add_edge("create_complaint", END)
_wf.add_edge("cancel",           END)

# Export -- auto-discovered by load_plugins() via the <module>_graph naming convention
grievance_graph = _wf.compile(checkpointer=shared_memory)
