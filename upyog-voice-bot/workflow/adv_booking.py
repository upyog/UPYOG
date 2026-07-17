import json
import logging
import re
from typing import Dict, Any, List, Optional
import os

from langchain_core.messages import SystemMessage, AIMessage, HumanMessage
from langgraph.graph import StateGraph, END, START
from langchain_groq import ChatGroq


from memory_manager import MemoryManager, shared_memory

from workflow.base_state import BaseAgentState
# BaseAgentState = The universal "ID Card" (TypedDict) for all modules. Contains messages, phone, session_id etc.

logger = logging.getLogger(__name__)

llm = ChatGroq(model="llama-3.1-8b-instant", temperature=0) if os.environ.get("GROQ_API_KEY") else None

# ==========================================
# STATE
# ==========================================
class AdvBookingState(BaseAgentState):
    # Extends BaseAgentState (adds 2 new fields specific to Advertisement booking)

    draft_booking: Dict[str, Any]
    # draft_booking = Temporary storage for all collected form data during the conversation.
    # Example: {"addType": "Hoarding", "location": "Jor Bagh", "start_date": "2026-08-01"}
    # It starts empty {} and gets filled field by field as user answers questions.

    missing_fields: List[str]
    # missing_fields = List of field names not yet collected from the user.
    # Example: ["faceArea", "start_date", "end_date"] means these 3 are still needed.

# ==========================================
# DIRECT MCP CALLERS (no LangGraph tool-use)
# ==========================================

def _mdms_get(module_name: str, master_name: str) -> List[str]:
    """Call UPYOG MDMS live and return list of option names."""
    from mcp_tools import mdms_get
    try:
        result = mdms_get(module_name, master_name)
        return result if result else []
    except Exception as e:
        logger.error(f"mdms_get({module_name},{master_name}) failed: {e}")
        return []

def _slot_search(draft: dict) -> List[dict]:
    """Call UPYOG slot search. Returns list of slot dicts with frontend-compatible keys."""
    from mcp_tools import slot_search
    start = draft.get("start_date", "")
    end = draft.get("end_date") or start
    try:
        raw = slot_search(
            addType=draft.get("addType", ""),
            faceArea=draft.get("faceArea", ""),
            location=draft.get("location", ""),
            start_date=start,
            end_date=end,
            nightLight=str(draft.get("nightLight", "No")).lower() == "yes"
        )
        data = json.loads(raw) if isinstance(raw, str) else raw
        if not data:
            raise ValueError("Empty slot result from API")
        # Normalize to frontend field names: type, area, light, date, status
        normalized = []
        seen = set()
        for slot in data:
            s_date = slot.get("fromDate") or slot.get("bookingStartDate") or slot.get("date") or start
            if s_date in seen:
                continue
            seen.add(s_date)
            normalized.append({
                "type": slot.get("addType") or slot.get("type") or draft.get("addType", ""),
                "area": slot.get("faceArea") or slot.get("area") or draft.get("faceArea", ""),
                "light": slot.get("nightLight") or slot.get("light") or draft.get("nightLight", "No"),
                "date": s_date,
                "status": slot.get("status", "Available")
            })
        return normalized
    except Exception as e:
        logger.error(f"slot_search failed: {e}")
        return []

# ==========================================
# FIELD DEFINITIONS — matches UPYOG Adv Search form
# Order: Advertisement Type → Location → Face Area → From Date → To Date → Night Light
# Then: Selected Slots → Applicant Details (via form) → Address → Document
# ==========================================

ALL_FIELDS = ["addType", "location", "faceArea", "start_date", "end_date", "nightLight", "selected_slots", "applicant_details", "address", "doc_sample", "doc_address", "doc_identity"]
SEARCH_FIELDS = ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"]

STEP_MAP = [
    {
        "field": "addType",
        "label": "Advertisement Type",
        "question": "Please select the Advertisement Type:",
        "mdms": ("Advertisement", "AdType"),
        "ui": "dropdown"
    },
    {
        "field": "location",
        "label": "Advertisement Location",
        "question": "Please select the Advertisement Location:",
        "mdms": ("Advertisement", "Location"),
        "ui": "dropdown"
    },
    {
        "field": "faceArea",
        "label": "Face Area",
        "question": "Please select the Face Area:",
        "mdms": ("Advertisement", "FaceArea"),
        "ui": "dropdown"
    },
    {
        "field": "start_date",
        "label": "From Date",
        "question": "Please select the From Date for your booking:",
        "mdms": None,
        "ui": "date"
    },
    {
        "field": "end_date",
        "label": "To Date",
        "question": "Please select the To Date for your booking:",
        "mdms": None,
        "ui": "date"
    },
    {
        "field": "nightLight",
        "label": "Advertisement With Night Light",
        "question": "Do you need Night Light for the advertisement? Select Yes or No:",
        "mdms": ("Advertisement", "NightLight"),
        "ui": "dropdown"
    },
    {
        "field": "address",
        "label": "Address",
        "question": "Please provide your full address (including pincode, street, house number, etc.):",
        "mdms": None,
        "ui": "text"
    },
    {
        "field": "doc_sample",
        "label": "Sample Document",
        "question": "Please upload a Sample Document of the Advertisement as a PDF or image:",
        "mdms": None,
        "ui": "file"
    },
    {
        "field": "doc_address",
        "label": "Address Proof",
        "question": "Please upload a copy of your Address Proof (Electricity Bill, etc.) as a PDF or image:",
        "mdms": None,
        "ui": "file"
    },
    {
        "field": "doc_identity",
        "label": "Identity Proof",
        "question": "Please upload a copy of your Identity Proof (Aadhaar, PAN, etc.) as a PDF or image:",
        "mdms": None,
        "ui": "file"
    }
]

FIELD_HINTS = {
    "location":   "location name such as 'Jor Bagh', 'Green Park', or 'Hauz Khas'",
    "faceArea":   "face area / size such as 'Unipole 20 X 10' or 'Kiosk Poles 30 X 40 (Both Side)'",
    "start_date": "a date in YYYY-MM-DD format — only extract if the message clearly has a date",
    "end_date":   "a date in YYYY-MM-DD format — only extract if the message clearly has a date",
    "nightLight": "exactly 'Yes' or 'No'",
    "selected_slots": "internal field, do not extract",
    "applicant_details": "internal field, do not extract",
    "address": "a full address provided by the user",
    "doc_sample": "internal field, do not extract",
    "doc_address": "internal field, do not extract",
    "doc_identity": "internal field, do not extract",
}


def get_next_step(draft: dict) -> Optional[dict]:
    for step in STEP_MAP:
        if not draft.get(step["field"]):
            return step
    return None


# ==========================================
# MULTI-NODE LANGGRAPH REFACTOR
# ==========================================

def intent_and_ui_node(state: AdvBookingState):
    messages = state.get("messages", [])
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking", {}))
    user_msg = messages[-1].content if messages else ""
    
    # 1. UI Payloads
    if user_msg.strip().startswith("[") and user_msg.strip().endswith("]"):
        try:
            parsed = json.loads(user_msg.strip())
            if isinstance(parsed, list) and len(parsed) > 0:
                # Normalize keys just in case frontend capitalized them
                for slot in parsed:
                    if "Ad Type" in slot and "type" not in slot: slot["type"] = slot["Ad Type"]
                    if "Face Area" in slot and "area" not in slot: slot["area"] = slot["Face Area"]
                    if "Night Light" in slot and "light" not in slot: slot["light"] = slot["Night Light"]
                    if "Booking Date" in slot and "date" not in slot: slot["date"] = slot["Booking Date"]
                
                draft_booking["selected_slots"] = parsed
                user_msg = "I have selected a slot."
        except Exception as e:
            logger.error(f"Failed to parse slot payload: {e}")
            
    # -----------------------------------------------------------------
    # CASE 2: Applicant Form or Document Upload (JSON object from UI)
    # The React frontend sends a JSON dictionary for forms/files.
    # Example 1 (Form): {"name":"Rahul", "mobile":"9876543210", "email":"r@g.com"}
    # Example 2 (File): {"document": "fileStoreId-uuid-123"}
    # We detect it by checking if the message starts/ends with curly braces { }.
    # -----------------------------------------------------------------
    if user_msg.strip().startswith("{") and user_msg.strip().endswith("}"):
        try:
            parsed = json.loads(user_msg.strip())
            
            # Sub-case A: Applicant Details Form submitted
            if "name" in parsed and "mobile" in parsed:
                draft_booking["applicant_details"] = parsed  # Save the whole dict
                user_msg = "I have submitted my details."    # Translate JSON to English for the Bot
            
            # Sub-case B: Document Uploaded
            elif "document" in parsed:
                next_missing = next((f for f in ALL_FIELDS if not draft_booking.get(f)), None)
                if next_missing and next_missing.startswith("doc_"):
                    draft_booking[next_missing] = parsed["document"]
                    name_clean = next_missing.replace("doc_", "").title()
                    user_msg = f"I have uploaded my {name_clean} document."
        except Exception: pass

    # 2. Reset logic
    reset_keywords = ["start over", "new booking", "cancel booking", "start new", "restart", "cancel"]
    if any(w in user_msg.lower() for w in reset_keywords):  # Check if ANY reset keyword is in the message
        draft_booking = {}  # Wipe entire draft. e.g. {"addType":"Hoarding",...} becomes {}

    # 3. Resume logic
    resume_keywords = ["resume", "proceed", "continue", "previous left", "start with previous"]
    if any(w in user_msg.lower() for w in resume_keywords) and "booking" in user_msg.lower():
        if draft_booking:  # Only resume if there IS an existing incomplete draft
            user_msg = "continue"  # Signal to ask_next_node to pick up from where we left off
        else:
            resp = "I don't see any incomplete bookings in progress. Would you like to start a new advertisement booking instead?"
            MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
            MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
            return {"messages": [AIMessage(content=resp)], "draft_booking": {}, "missing_fields": ALL_FIELDS}

    # 4. Past Bookings Shortcut
    ui_msg = user_msg.lower()
    is_past_booking = False
    
    # Check for direct ADV- ID
    if "adv-" in ui_msg:  # Direct booking ID. e.g. user typed "status of ADV-1013-0001?"
        is_past_booking = True
    elif any(w in ui_msg for w in ["past", "history", "previous", "status", "track", "my ad", "my application", "specific"]):
        # e.g. "show my previous bookings" or "track my ad status"
        is_past_booking = True
    elif (("my" in ui_msg or "show" in ui_msg or "fetch" in ui_msg or "find" in ui_msg) and "booking" in ui_msg):
        # e.g. "show my booking" / "find my latest booking" -- combined keyword trigger
        is_past_booking = True

    if user_msg != "continue" and is_past_booking:
        from mcp_tools import search_ads
        try:
            logger.info(f"=====> [PLUGIN: ADV_BOOKING] CALLING LIVE UPYOG API (search_ads) for phone {phone_number} <=====")
            raw = search_ads(phone_number)
            
            # Step C: Extract filters from user text via LLM
            prompt = f"""Extract any dates or booking IDs from this query to filter past bookings.
Query: '{user_msg}'
Return ONLY a JSON object (no other text) with:
- "date_str": A string representing the exact date in YYYY-MM-DD format if mentioned, else null.
- "booking_id": The exact ADV-... ID, or just the partial digits (like the last 4 numbers) if mentioned, else null."""
            
            try:
                if llm:
                    res = llm.invoke([HumanMessage(content=prompt)])
                    extracted = json.loads(res.content.strip().replace("```json", "").replace("```", "").strip())
                else:
                    extracted = {}
            except Exception as e:
                logger.error(f"LLM extraction error: {e}")
                extracted = {}
            
            date_str = extracted.get("date_str")
            booking_id = extracted.get("booking_id")
            
            # Step D: Filter by date/ID in Python
            # e.g. If user asked for bookings on "2026-07-15", we keep only bookings where that date appears
            if raw and raw != "[]":
                bookings = json.loads(raw)
                if date_str or booking_id:
                    filtered = []
                    for b in bookings:
                        b_str = json.dumps(b)
                        if booking_id and booking_id in b_str:
                            filtered.append(b)
                            continue
                        if date_str and date_str in b_str:
                            filtered.append(b)
                            continue
                    
                    if not filtered:
                        raw = "[]"
                    else:
                        # Slice to max 5 to prevent token limits
                        raw = json.dumps(filtered[:5])
                else:
                    # User asked for all past bookings without a specific ID/date
                    # MUST slice to max 5 to prevent crashing the React UI with 50+ bookings!
                    raw = json.dumps(bookings[:5])
            
        except Exception as e:
            logger.error(f"Past booking filter error: {e}")
            raw = "[]"
            
        if raw == "[]" or not raw:
            resp = "I couldn't find any recent advertisement bookings matching your request."
        else:
            if "detail" in ui_msg.lower():
                try:
                    b = json.loads(raw)[0]
                    resp = f"**Booking No**: {b.get('bookingNo') or b.get('applicationNo') or 'N/A'}\n"
                    
                    app_detail = b.get('applicantDetail', {})
                    resp += f"**Applicant Name**: {app_detail.get('applicantName', 'N/A')}\n"
                    resp += f"**Mobile No**: {app_detail.get('applicantMobileNo', 'N/A')}\n"
                    
                    resp += f"**Booking Date**: {b.get('bookingDate', 'N/A')}\n"
                    resp += f"**Status**: {b.get('status', 'N/A')}\n"
                    
                    addr = b.get('address', {})
                    addr_str = f"{addr.get('houseNo') or addr.get('doorNo', '')}, {addr.get('streetName','')}, {addr.get('city','')}".strip(', ')
                    resp += f"**Address**: {addr_str}\n"
                    
                    docs = b.get('documents', [])
                    doc_types = [d.get('documentType', '').split('.')[-1] for d in docs if d.get('documentType')]
                    resp += f"**Documents Uploaded**: {', '.join(doc_types) if doc_types else 'None'}"
                except Exception:
                    resp = f"Here are your recent advertisement bookings:\n<ui-booking-history data='{raw}' />"
            else:
                resp = f"Here are your recent advertisement bookings:\n<ui-booking-history data='{raw}' />"
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
        return {"messages": [AIMessage(content=resp)], "draft_booking": draft_booking, "missing_fields": []}

    # 5. Greeting Prompt
    greet_keywords = ["hello", "hi", "hey", "good morning", "good evening", "greetings"]
    is_greeting = any(user_msg.lower().strip() == w for w in greet_keywords) or (len(user_msg.split()) <= 2 and any(w in user_msg.lower() for w in greet_keywords))
    if is_greeting:
        if draft_booking and llm:
            sys_msg = "You are a helpful UPYOG assistant. The user just greeted you. They have an incomplete draft booking in progress.\nGreet them warmly and ask if they would like to resume their previous incomplete booking or start a new one.\nDo not list the collected fields. Keep it to 1-2 friendly sentences."
            resp = llm.invoke([SystemMessage(content=sys_msg), AIMessage(content=user_msg)]).content.strip()
            resp += "\n<ui-dropdown options=['Resume booking', 'Start new booking'] />"
        elif not draft_booking and llm:
            sys_msg = "You are a helpful UPYOG advertisement booking assistant. The user just greeted you.\nGreet them warmly and ask how you can help them book an advertisement today. Keep it to 1-2 friendly sentences."
            resp = llm.invoke([SystemMessage(content=sys_msg), AIMessage(content=user_msg)]).content.strip()
        else:
            resp = "Hello! Do you want to resume your previous booking or start a new one?" if draft_booking else "Hello! How can I help you book an advertisement today?"
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
        return {"messages": [AIMessage(content=resp)], "draft_booking": draft_booking, "missing_fields": []}

    # Pass the state forward without adding any new AIMessages
    return {"draft_booking": draft_booking}


def intent_router(state: AdvBookingState) -> str:
    messages = state.get("messages", [])  # All messages in conversation so far
    if messages and isinstance(messages[-1], AIMessage):
        # The last message is from the AI (bot already responded in intent_and_ui_node).
        # e.g. Bot sent back a booking history table. No more processing needed this turn.
        return END      
    return "extraction"  


def extraction_node(state: AdvBookingState):
    draft_booking = dict(state.get("draft_booking", {}))  # e.g. {"addType":"Hoarding","location":""}
    messages = state.get("messages", [])
    if not messages:
        return {"draft_booking": draft_booking}  # Nothing to process if no messages exist

    user_msg = messages[-1].content  # e.g. "I want a hoarding at Jor Bagh"

    # Skip extraction if message is a raw UI payload (already handled in intent_and_ui_node)
    # e.g. [{"Ad Type":"Hoarding",...}] is slots data, not text to extract from
    if user_msg.strip().startswith("[") and user_msg.strip().endswith("]"):
        return {"draft_booking": draft_booking}  # Nothing to extract -- pass through
    if user_msg.strip().startswith("{") and user_msg.strip().endswith("}"):
        return {"draft_booking": draft_booking}  # JSON form data -- already handled upstream
    
    # If draft is empty and user just said "yes/ok/sure", treat as empty -- don't try to extract a value
    # e.g. First-time user says "yes" to start -- there's nothing to extract from "yes"
    generic_confirmations = ["yes", "yeah", "sure", "ok", "okay", "yep", "start", "begin", "y"]
    if not draft_booking and user_msg.strip().lower() in generic_confirmations:
        user_msg = ""  # Blank out -- nothing to extract from "yes"
        
    # Find the next field that still needs to be collected
    # Example: If draft has addType+location already, next_missing = "faceArea"
    next_missing = next((f for f in ALL_FIELDS if not draft_booking.get(f)), None)
    
    if user_msg and next_missing and llm:
        hint = FIELD_HINTS.get(next_missing, "the relevant value")
        extract_prompt = f"""You are a strict data extraction AI for UPYOG Advertisement bookings.
Your ONLY job is to extract the field '{next_missing}' from the user's message.
- Expected value type: {hint}
- If the user's message clearly answers this, extract it.
- If the user's message is irrelevant, ambiguous, or generic (e.g. "I don't know", "skip", "continue", "hello"), return null.

Already collected fields: {json.dumps(draft_booking)}
User message: "{user_msg}"

Reply ONLY with valid JSON. No markdown tags, no explanations.
{{"{next_missing}": "extracted value or null"}}"""
        try:
            ext = llm.invoke([SystemMessage(content=extract_prompt)])
            m = re.search(r'\{.*\}', ext.content.strip(), re.DOTALL)
            if m:
                val = json.loads(m.group(0)).get(next_missing)
                if val and str(val).lower() not in ("null", "none", ""):
                    draft_booking[next_missing] = val
                    logger.info(f"[extract_node] {next_missing} = {val}")
        except Exception as e:
            logger.error(f"Extraction error: {e}")
            
    return {"draft_booking": draft_booking}


def ask_next_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking", {}))
    messages = state.get("messages", [])
    user_msg = messages[-1].content if messages else ""
    
    next_step = get_next_step(draft_booking)
    if not next_step:
        return {} 

    options = []
    ui_type = next_step["ui"]
    field_label = next_step["label"]

    if next_step["mdms"]:
        options = _mdms_get(*next_step["mdms"])

    if llm:
        ctx = f"""You are a concise UPYOG advertisement booking assistant.
Collected so far: {json.dumps({k: v for k, v in draft_booking.items() if v})}
Next field to collect: "{field_label}"
Question to ask: "{next_step['question']}"
User said: "{user_msg}"

Instructions:
- DO NOT say "Hello" unless they explicitly asked a general question.
- NEVER repeat or confirm what the user just selected.
- JUST ask the EXACT next question in a professional manner.
- DO NOT list options.
- Output ONLY the conversational text to the user."""
        try:
            conv = llm.invoke([SystemMessage(content=ctx)])
            text = conv.content.strip()
        except Exception:
            text = next_step["question"]
    else:
        text = next_step["question"]

    if ui_type == "dropdown" and options:
        opts_str = ", ".join(f"'{o}'" for o in options)
        resp = f"{text}\n<ui-dropdown options=[{opts_str}] />"
    elif ui_type == "date":
        resp = f"{text}\n<ui-calendar mode=\"single\" minDate=\"tomorrow\" />"
    else:
        resp = text

    if user_msg and user_msg not in ["continue", "hello", "I have selected a slot.", "I have submitted my details."]:
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)

    return {"messages": [AIMessage(content=resp)], "missing_fields": [f for f in ALL_FIELDS if not draft_booking.get(f)]}


def slot_search_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking", {}))
    slots = _slot_search(draft_booking)
    slots_json = json.dumps(slots)
    resp = f"Great! Here are the available slots. Please select one or more:\n<ui-slot-table data={slots_json} />"
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
    return {"messages": [AIMessage(content=resp)], "missing_fields": ["selected_slots"]}


def applicant_form_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    resp = "Great! Please fill in your details to finalize the booking:\n<ui-applicant-form cartAmount=\"0\" />"
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
    return {"messages": [AIMessage(content=resp)], "missing_fields": ["applicant_details"]}


def create_booking_node(state: AdvBookingState):
    from mcp_tools import create_booking
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking", {}))
    
    # Extract the addType from selected_slots (more accurate than the initial draft value)
    # e.g. If user selected [{"type":"Hoarding","date":"2026-08-01"}], use "Hoarding" as addType
    slots = draft_booking.get("selected_slots", [{}])  # e.g. [{"type":"Hoarding","date":"2026-08-01"}]
    if slots:
        draft_booking["addType"] = slots[0].get("type", draft_booking.get("faceArea", ""))
        
    app_details = draft_booking.get("applicant_details", {})
    draft_booking["applicantName"] = app_details.get("name", "")
    draft_booking["mobileNumber"] = app_details.get("mobile", "")
    draft_booking["emailId"] = app_details.get("email", "")
    draft_booking["address"] = draft_booking.get("address", "")
    
    # Hand off to mcp_tools.py -- it will build the UPYOG JSON payload and POST to the API
    resp = create_booking(json.dumps(draft_booking))  # e.g. Returns "Success! Application No: ADV-1013-0001"
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)  # Save to Qdrant
    return {"messages": [AIMessage(content=resp)], "draft_booking": {}, "missing_fields": []}
    # draft_booking is reset to {} so next conversation starts fresh with no leftover data


def router(state: AdvBookingState) -> str:
    draft_booking = state.get("draft_booking", {})  # Current collected data at this point in graph
    
    # Check 1: Are the 6 core search fields all filled AND slots not yet selected?
    # e.g. draft has addType+location+faceArea+start_date+end_date+nightLight but NO selected_slots
    if all(draft_booking.get(f) for f in SEARCH_FIELDS) and not draft_booking.get("selected_slots"):
        return "slot_search"    # Go show the slot availability table to the user
        
    # Check 2: Are slots selected but applicant form not filled yet?
    # e.g. draft has selected_slots but NO applicant_details (Name/Mobile/Email)
    if draft_booking.get("selected_slots") and not draft_booking.get("applicant_details"):
        return "applicant_form" # Go show the Name/Mobile/Email form
        
    # Check 3: Are ALL fields (including all 3 documents) filled?
    # e.g. draft has everything: addType, location, ..., doc_sample, doc_address, doc_identity
    if all(draft_booking.get(f) for f in ALL_FIELDS):
        return "create_booking" # FINAL STEP: Submit everything to UPYOG live API!
        
    return "ask_next"           # Default: at least one field is still missing -- ask next question

# ==========================================
# GRAPH
# ==========================================
workflow_graph = StateGraph(AdvBookingState)

workflow_graph.add_node("intent_and_ui", intent_and_ui_node)
workflow_graph.add_node("extraction", extraction_node)
workflow_graph.add_node("slot_search", slot_search_node)
workflow_graph.add_node("applicant_form", applicant_form_node)
workflow_graph.add_node("create_booking", create_booking_node)
workflow_graph.add_node("ask_next", ask_next_node)

workflow_graph.add_edge(START, "intent_and_ui")
workflow_graph.add_conditional_edges(
    "intent_and_ui",
    intent_router,
    {
        "extraction": "extraction",
        END: END
    }
)
workflow_graph.add_conditional_edges(
    "extraction",
    router,
    {
        "slot_search": "slot_search",
        "applicant_form": "applicant_form",
        "create_booking": "create_booking",
        "ask_next": "ask_next"
    }
)
workflow_graph.add_edge("slot_search", END)
workflow_graph.add_edge("applicant_form", END)
workflow_graph.add_edge("create_booking", END)
workflow_graph.add_edge("ask_next", END)

adv_booking_graph = workflow_graph.compile(checkpointer=shared_memory)
