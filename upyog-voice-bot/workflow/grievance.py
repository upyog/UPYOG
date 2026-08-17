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

llm = ChatGroq(model="llama-3.1-8b-instant", temperature=0) if os.environ.get("GROQ_API_KEY") else None


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


from mcp_tools import pgr_get_categories, pgr_get_localities, pgr_create_complaint, pgr_search_complaints

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

# Checks if the user wants to cancel the current compl@aint or view past complaints
def intent_and_ui_node(state: GrievanceState):
    messages     = state.get("messages", [])
    phone_number = state.get("phone_number", "default")
    draft        = dict(state.get("draft_grievance", {}))
    user_msg     = messages[-1].content if messages else ""
    ui_lower     = user_msg.lower()

    # Reset — only match deliberate cancel/reset phrases, not substrings
    reset_phrases = [
        r"\bstart over\b", r"\bcancel\b", r"\brestart\b",
        r"\bnew complaint\b", r"\bstart new\b", r"\bclear draft\b",
        r"\breset\b", r"\bshuruaat karo\b"
    ]
    if any(re.search(p, ui_lower) for p in reset_phrases):
        draft = {f: None for f in ALL_FIELDS}
        resp  = (
            "**Draft Reset**\n\n"
            "Your complaint draft has been cleared. Please describe the issue "
            "you are facing and I will guide you through the filing process."
        )
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user",      content=user_msg)
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
        return {"messages": [AIMessage(content=resp)], "draft_grievance": draft, "missing_fields": ALL_FIELDS}

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
        from mcp_tools import pgr_search_complaints_raw
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


# Uses AI to read the user's message and extract missing complaint details
def extraction_node(state: GrievanceState):
    draft = state.get("draft_grievance") or {}
    if not isinstance(draft, dict):
        draft = {}
    messages = state.get("messages", [])
    if not messages:
        return {"draft_grievance": draft}

    user_msg = messages[-1].content
    last_ai  = ""
    if len(messages) >= 2 and isinstance(messages[-2], AIMessage):
        last_ai = messages[-2].content

    # Detect if user's message is a question/FAQ query rather than a form answer
    question_triggers = ["?", "what is", "what does", "explain", "meaning", "kya hai", "kaise", "kyun", "tell me about"]
    is_question = any(q in user_msg.lower() for q in question_triggers)
    if is_question:
        logger.info(f"[grievance extract_node] User asked a question ('{user_msg}'), skipping field extraction.")
        return {"draft_grievance": draft}

    # Detect yes/no at confirmation step
    if draft.get("_awaiting_confirm"):
        _confirm = ["yes", "yeah", "yup", "ya", "ok", "okay", "sure", "haan", "confirm",
                    "submit", "file", "bilkul", "zaroor"]
        _cancel  = ["no", "nope", "cancel", "nahi", "nahin", "mat", "band"]
        if any(w in user_msg.lower() for w in _cancel):
            draft["_cancelled"] = True
            return {"draft_grievance": draft}
        if any(w in user_msg.lower() for w in _confirm):
            draft["_confirmed"] = True
            return {"draft_grievance": draft}
        return {"draft_grievance": draft}

    # Normal field extraction
    missing = [f for f in ALL_FIELDS if not draft.get(f)]
    if not missing or not llm:
        return {"draft_grievance": draft}

    schema_hints: dict = {}
    for f in missing:
        if f == "category":
            cats = draft.get("_category_options") or _pgr_categories()
            draft["_category_options"] = cats
            opts = list(cats.keys())
            schema_hints[f] = (f"MUST MATCH one of: {opts}" if opts
                               else "Main grievance category (e.g. PublicToilets, StreetLights, Water, Garbage)")
        elif f == "sub_category":
            sub_ops = [s["name"] for s in (draft.get("_sub_options") or [])]
            schema_hints[f] = (f"MUST MATCH one of: {sub_ops}" if sub_ops
                               else "Specific complaint type")
        elif f == "locality":
            loc_ops = draft.get("_locality_options") or _pgr_localities()
            draft["_locality_options"] = loc_ops
            loc_names = [l["name"] for l in loc_ops[:30]]
            schema_hints[f] = (f"MUST MATCH one of: {loc_names}. "
                               "If not in list, use verbatim." if loc_names
                               else "Area or locality name")
        elif f == "description":
            schema_hints[f] = "Detailed description of the problem (minimum 5 chars)"

    collected_str = json.dumps({k: v for k, v in draft.items() if v and not k.startswith("_")})
    extract_prompt = f"""You are a smart, production-grade data extraction AI for UPYOG Grievance filing.
Your job is to extract missing form field values from the user's message based on conversational context.

Missing fields: {json.dumps(missing)}
Field hints & valid options: {json.dumps(schema_hints)}
Already collected fields: {collected_str}
Bot's last question: "{last_ai}"
User's current message: "{user_msg}"

EXTRACTION RULES:
1. CONVERSATIONAL CONTEXT: Pay close attention to "Bot's last question". If the bot explicitly asked the user for a specific missing field (e.g. 'category', 'sub_category', 'description' or 'locality') and the user provided an answer, EXTRACT IT into that field.
2. CATEGORY / SUB-CATEGORY / LOCALITY MATCHING: Match the user's input to the valid options list in the hints.
3. DESCRIPTION FIELD: Extract actual problem details or description of the issue (e.g. "road accident at night", "water pipe leaking", "street light broken"). Do NOT extract generic intent phrases like "i want to file a complaint".
4. Return null for any missing field that was NOT mentioned or answered in the user's message.

Output MUST be a single valid JSON object containing keys for all missing fields:
{{{', '.join(f'"{f}": "extracted value or null"' for f in missing)}}}"""

    try:
        ext = llm.invoke([SystemMessage(content=extract_prompt)])
        m   = re.search(r'\{.*\}', ext.content.strip(), re.DOTALL)
        if m:
            extracted = json.loads(m.group(0))
            for field in missing:
                val = extracted.get(field)
                if val is not None and str(val).lower() not in ("null", "none", ""):
                    val_str = str(val).strip()
                    logger.info(f"[grievance extract] {field} = {val_str}")

                    if field == "category":
                        cats = draft.get("_category_options") or _pgr_categories()
                        draft["_category_options"] = cats
                        norm_val = re.sub(r'[\s_]+', '', val_str.lower())
                        matched_cat = next((k for k in cats.keys() if re.sub(r'[\s_]+', '', k.lower()) == norm_val), None)
                        if matched_cat:
                            draft["category"] = matched_cat
                            draft["_sub_options"] = cats[matched_cat]
                        elif cats:
                            draft["category"] = val_str
                            first_key = list(cats.keys())[0]
                            draft["_sub_options"] = cats[first_key]

                    elif field == "sub_category":
                        sub_ops = draft.get("_sub_options") or []
                        norm_val = re.sub(r'[\s_]+', '', val_str.lower())
                        matched = next((s for s in sub_ops if re.sub(r'[\s_]+', '', s["name"].lower()) == norm_val), None)
                        if not matched and sub_ops:
                            matched = sub_ops[0]
                        if matched:
                            draft["sub_category"] = matched["name"]
                            draft["category_code"] = matched["code"]
                        else:
                            draft["sub_category"] = val_str

                    elif field == "locality":
                        loc_ops = draft.get("_locality_options") or _pgr_localities()
                        draft["_locality_options"] = loc_ops
                        norm_val = re.sub(r'[\s_]+', '', val_str.lower())
                        matched = next((l for l in loc_ops if re.sub(r'[\s_]+', '', l["name"].lower()) == norm_val), None)
                        if matched:
                            draft["locality"] = matched["name"]
                            draft["locality_code"] = matched["code"]
                        elif loc_ops:
                            draft["locality"] = val_str
                            draft["locality_code"] = loc_ops[0]["code"]

                    else:
                        draft[field] = val_str
            
            # Filter out generic intent strings from description
            desc_val = str(draft.get("description", "")).strip().lower()
            generic_intents = ["i want to file a complaint", "file a complaint", "raise a grievance", "file complaint", "want to file a complaint", "i have a complaint"]
            if any(g in desc_val for g in generic_intents):
                draft["description"] = None

            # Auto-Save to Qdrant Disk Store on every extraction turn!
            phone_number = state.get("phone_number", "default")
            if draft and phone_number and phone_number != "default":
                from memory_manager import MemoryManager
                MemoryManager.save_draft_state(phone_number, "grievance", draft)

    except Exception as e:
        logger.error(f"[grievance] extraction error: {e}")

    return {"draft_grievance": draft}


# Asks the user the next missing question and shows dropdown options if applicable
def ask_next_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    draft = state.get("draft_grievance") or {}
    if not isinstance(draft, dict):
        draft = {}
    messages = state.get("messages", [])
    user_msg     = messages[-1].content if messages else ""

    next_step  = get_next_step(draft)
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
        cats         = draft.get("_category_options") or {}
        sub_ops      = cats.get(cat_selected, [])
        draft["_sub_options"] = sub_ops
        options = [s["name"] for s in sub_ops]

    elif field_id == "locality":
        locs = draft.get("_locality_options") or _pgr_localities()
        draft["_locality_options"] = locs
        options = [l["name"] for l in locs[:25]]

    text = f"Please provide the {field_label}."
    if llm:
        ctx = f"""You are a UPYOG grievance filing assistant collecting a specific form field.
Collected so far: {json.dumps({k: v for k, v in draft.items() if v and not k.startswith("_")})}
Next field to collect: "{field_label}" (field id: "{field_id}")
Available options: {json.dumps(options) if options else "Free text -- user can type anything"}
User's last message: "{user_msg}"

CRITICAL INSTRUCTIONS:
1. Ask ONLY for the "{field_label}" field. Do NOT ask anything else.
2. Do NOT ask follow-up questions about duration, impact, or history.
3. Do NOT list the options in text (the UI dropdown will show them).
4. If field is 'description': ask the user to briefly describe the exact problem they are facing.
5. Keep it to 1-2 sentences. Output ONLY the question, nothing else.
6. Use **bold** for the field name when mentioning it.
7. Maintain a formal, professional tone. NEVER use informal or familial terms of address like 'दीदी' (Didi), 'काकी' (Kaki), 'बेटा' (Beta), 'भैया' (Bhaiya), etc."""
        try:
            conv = llm.invoke([SystemMessage(content=ctx)])
            text = conv.content.strip()
        except Exception:
            pass

    if field_type == "dropdown" and options:
        opts_str = ", ".join(f"'{o}'" for o in options)
        resp     = f"{text}\n<ui-dropdown options=[{opts_str}] />"
    else:
        resp = text


    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user",      content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    return {
        "messages":        [AIMessage(content=resp)],
        "draft_grievance": draft,
        "missing_fields":  [f for f in ALL_FIELDS if not draft.get(f)],
        "input_type":      "text",
        "options":         []
    }


def intent_and_ui_node(state: GrievanceState):
    messages = state.get("messages", [])
    if not messages:
        return {}

    user_msg = messages[-1].content if messages else ""
    draft = state.get("draft_grievance", {})
    
    if user_msg == "[CANCEL_DRAFT]":
        return {"draft_grievance": {}, "_active": False}

    ui_mode = False
    return {"draft_grievance": draft}


# Shows a final summary receipt of the complaint details and asks the user to confirm submission
def confirm_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    draft        = dict(state.get("draft_grievance", {}))
    messages     = state.get("messages", [])
    user_msg     = messages[-1].content if messages else ""

    # Ensure description is meaningful and not a generic intent
    desc = str(draft.get("description", "")).strip()
    generic_intents = ["i want to file a complaint", "file a complaint", "raise a grievance", "file complaint", "want to file a complaint", "i have a complaint"]
    if not desc or any(g in desc.lower() for g in generic_intents) or len(desc) < 5:
        sub = draft.get('sub_category') or draft.get('category') or 'Complaint'
        loc = draft.get('locality') or 'the specified locality'
        draft['description'] = f"{sub} issue reported at {loc}"

    summary = (
        "**Review Your Complaint Details:**\n\n"
        f"- **Category:** {draft.get('category', 'N/A')}\n"
        f"- **Complaint Type:** {draft.get('sub_category', 'N/A')}\n"
        f"- **Description:** {draft.get('description')}\n"
        f"- **Locality:** {draft.get('locality', 'N/A')}\n\n"
        "Please reply **Yes** to submit or **No** to cancel."
    )
    draft["_awaiting_confirm"] = True

    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user",      content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=summary)

    return {"messages": [AIMessage(content=summary)], "draft_grievance": draft}


# Submits the finalized complaint to the UPYOG server to register the grievance and get an ID
def create_complaint_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    draft        = dict(state.get("draft_grievance", {}))

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
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    reset_draft = {f: None for f in ALL_FIELDS}
    MemoryManager.delete_draft_state(phone_number, "grievance")
    return {"messages": [AIMessage(content=resp)], "draft_grievance": reset_draft, "missing_fields": ALL_FIELDS, "input_type": "text", "options": []}


# Cancels the current complaint draft and clears the memory
def cancel_node(state: GrievanceState):
    phone_number = state.get("phone_number", "default")
    messages     = state.get("messages", [])
    user_msg     = messages[-1].content if messages else ""

    resp = (
        "**Complaint Withdrawn**\n\n"
        "Your complaint has been cancelled. No submission was made to the portal.\n\n"
        "If you wish to file a new complaint, please describe the issue and I will assist you."
    )
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user",      content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    reset_draft = {f: None for f in ALL_FIELDS}
    MemoryManager.delete_draft_state(phone_number, "grievance")
    return {"messages": [AIMessage(content=resp)], "draft_grievance": reset_draft, "missing_fields": ALL_FIELDS}


# The main traffic controller that routes the flow to the correct next step based on what's missing
def router(state: GrievanceState) -> str:
    draft      = state.get("draft_grievance") or {}
    all_filled = all(draft.get(f) for f in ALL_FIELDS)

    if draft.get("_cancelled"):
        return "cancel"
    if draft.get("_confirmed"):
        return "create_complaint"
    if not all_filled:
        return "ask_next"
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
