import json
import logging
import re
from typing import Dict, Any, List, Optional
import os
import yaml

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

from mcp_tools import create_booking, mdms_get, slot_search, search_ads

# Fetches dropdown options (like AdType or Location) directly from the UPYOG master database
def _mdms_get(module_name: str, master_name: str) -> List[str]:
    try:
        result = mdms_get(module_name, master_name)
        return result if result else []
    except Exception as e:
        logger.error(f"mdms_get({module_name},{master_name}) failed: {e}")
        return []

# Checks with the UPYOG API to see which dates and spaces are actually available for booking
def _slot_search(draft: dict, phone_number: str = "default") -> list:
    if not isinstance(draft, dict):
        draft = {}
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
            nightLight=str(draft.get("nightLight", "No")).lower() == "yes",
            phone_anchor=phone_number if (phone_number and phone_number != "default") else None
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
_this_dir = os.path.dirname(os.path.abspath(__file__))
config_path = os.path.join(os.path.dirname(_this_dir), "config.yml")
if not os.path.exists(config_path):
    config_path = os.path.join(_this_dir, "config.yml")

try:
    with open(config_path, "r") as f:
        _raw_cfg = yaml.safe_load(f)
except Exception as e:
    logger.error(f"Failed to load config.yml: {e}")
    _raw_cfg = {}

adv_form_cfg = _raw_cfg.get("forms", {}).get("adv_booking", {})
FORM_FIELDS = adv_form_cfg.get("fields", [])

ALL_FIELDS = [f["id"] for f in FORM_FIELDS]
SEARCH_FIELDS = ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"]
FIELD_HINTS = {f["id"]: f.get("label", f["id"]) for f in FORM_FIELDS}

# Looks at what we have so far, and figures out the very next question we need to ask the user
def get_next_step(draft: dict) -> dict:
    if not isinstance(draft, dict):
        draft = {}
    for field in FORM_FIELDS:
        if not draft.get(field["id"]):
            return field
    return {}


# ==========================================
# MULTI-NODE LANGGRAPH REFACTOR
# ==========================================

# Checks if the user is clicking UI buttons (canceling, resuming, or uploading documents) before talking to the AI
def intent_and_ui_node(state: AdvBookingState):
    # 1. Fetch current chat history and user's phone number from the state memory
    messages = state.get("messages", [])
    phone_number = state.get("phone_number", "default")
    
    # 2. Get the current draft booking data (if any), ensuring it's a valid dictionary
    draft_booking = state.get("draft_booking") or {}
    if not isinstance(draft_booking, dict):
        draft_booking = {}
        
    # 3. Extract the very last message sent by the user
    user_msg = messages[-1].content if messages else ""
    
    # 4. Check if the user is asking for services we don't support yet (like Trade License)
    unsupported_keywords = ["trade license", "property tax","fire noc"]
    if any(w in user_msg.lower() for w in unsupported_keywords):
        # 5. Tell the user we only support Advertisement booking right now
        resp = "Currently, I can only assist you with Advertisement Bookings. Support for Trade License and Property Tax services is under development and will be launched soon. Please let me know if you would like to proceed with an advertisement booking!"
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content=user_msg)
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
        # 6. Stop further processing and return the response immediately
        return {"messages": [AIMessage(content=resp)], "draft_booking": draft_booking, "missing_fields": []}
    
    # 7. Check if the frontend UI sent us a hidden JSON payload containing Slot selections (starts with [ )
    if user_msg.strip().startswith("[") and user_msg.strip().endswith("]"):
        try:
            parsed = json.loads(user_msg.strip())
            if isinstance(parsed, list) and len(parsed) > 0:
                # 8. Normalize the field names in case the frontend sent capitalized keys
                for slot in parsed:
                    if "Ad Type" in slot and "type" not in slot: slot["type"] = slot["Ad Type"]
                    if "Face Area" in slot and "area" not in slot: slot["area"] = slot["Face Area"]
                    if "Night Light" in slot and "light" not in slot: slot["light"] = slot["Night Light"]
                    if "Booking Date" in slot and "date" not in slot: slot["date"] = slot["Booking Date"]
                
                # 9. Save the selected slots into our temporary draft booking memory
                draft_booking["selected_slots"] = parsed
                # 10. Replace the ugly JSON message with a clean text message so the bot understands what happened
                user_msg = "I have selected a slot."
        except Exception as e:
            logger.error(f"Failed to parse slot payload: {e}")
            
    # 11. Check if the frontend UI sent us a hidden JSON payload for Forms or Document Uploads (starts with { )
    # Sub-case A: Check if the user submitted the Applicant Details Form
    if user_msg.strip().startswith("{") and user_msg.strip().endswith("}"):
        try:
            parsed = json.loads(user_msg.strip())
            if "name" in parsed and "mobile" in parsed:
                # 12. Save the applicant's name and mobile into the draft memory
                draft_booking["applicant_details"] = parsed
                user_msg = "I have submitted my details."
        except Exception: pass

    # Sub-case B: Check if the user uploaded a Document (can be JSON or a text tag)
    uploaded_doc = None
    if user_msg.strip().startswith("{") and user_msg.strip().endswith("}"):
        try:
            parsed = json.loads(user_msg.strip())
            if "document" in parsed:
                # 13. Extract the unique fileStoreId of the uploaded document
                uploaded_doc = parsed["document"]
        except Exception: pass
    elif user_msg.strip().startswith("[Uploaded Document:"):
        uploaded_doc = user_msg.strip()

    # 14. If a document was successfully uploaded
    if uploaded_doc:
        # 15. Find the first empty document field in our draft memory (like doc_sample or doc_address)
        next_doc_field = next((f for f in ALL_FIELDS if f.startswith("doc_") and not draft_booking.get(f)), None)
        if next_doc_field:
            # 16. Save the uploaded file's ID into that empty field
            draft_booking[next_doc_field] = uploaded_doc
            logger.info(f"[adv_booking] Assigned document to {next_doc_field}: {uploaded_doc}")
            # 17. Replace the user's message with "continue" to auto-trigger the next question
            user_msg = "continue"

    # 18. Check if the user wants to clear their current booking progress and start over
    reset_keywords = ["start over", "new booking", "cancel booking", "start new", "restart", "cancel"]
    if any(w in user_msg.lower() for w in reset_keywords):
        # 19. Wipe all fields in the draft memory completely clean
        draft_booking = {f: None for f in ALL_FIELDS}
        draft_booking["selected_slots"] = None
        draft_booking["applicant_details"] = None
        MemoryManager.delete_draft_state(phone_number, "adv_booking")
        # 20. Tell the user the booking was cancelled and start fresh
        return {"messages": [AIMessage(content="I have cleared your draft. Let's start a new booking.")], "draft_booking": draft_booking, "missing_fields": ALL_FIELDS}

    # 3. Resume logic — 'continue' alone (sent by draft switcher) always resumes
    resume_keywords = ["resume", "proceed", "continue", "previous left", "start with previous"]
    is_pure_continue = user_msg.strip().lower() == "continue"
    is_resume_with_booking = any(w in user_msg.lower() for w in resume_keywords) and "booking" in user_msg.lower()
    if is_pure_continue or is_resume_with_booking:
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
    elif any(w in ui_msg for w in ["past", "history", "previous", "status", "track", "my advertisement", "my ads", "my application", "specific"]):
        # e.g. "show my previous bookings" or "track my ad status"
        is_past_booking = True
    elif (("my" in ui_msg or "show" in ui_msg or "fetch" in ui_msg or "find" in ui_msg) and "booking" in ui_msg):
        # e.g. "show my booking" / "find my latest booking" -- combined keyword trigger
        is_past_booking = True

    if user_msg != "continue" and is_past_booking:
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
            
            if not isinstance(extracted, dict):
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

    # Pass the state forward without adding any new AIMessages
    return {"draft_booking": draft_booking}


# Decides if the bot should stop here (because a UI action was handled) or send the message to the AI for extraction
def intent_router(state: AdvBookingState) -> str:
    messages = state.get("messages", [])  # All messages in conversation so far
    if messages and isinstance(messages[-1], AIMessage):
        # The last message is from the AI (bot already responded in intent_and_ui_node).
        # e.g. Bot sent back a booking history table. No more processing needed this turn.
        return END      
    return "extraction"  


# Uses LLM (AI) to read the user's English sentence and extract the actual data (like Date or Location) into our draft memory
def extraction_node(state: AdvBookingState):
    draft_booking = state.get("draft_booking") or {}
    if not isinstance(draft_booking, dict):
        draft_booking = {}
    messages = state.get("messages", [])
    if not messages:
        return {"draft_booking": draft_booking}

    user_msg = messages[-1].content
    last_assistant_msg = ""
    if len(messages) >= 2 and isinstance(messages[-2], AIMessage):
        last_assistant_msg = messages[-2].content

    if user_msg.strip().startswith("[") and user_msg.strip().endswith("]"):
        return {"draft_booking": draft_booking}
    if user_msg.strip().startswith("{") and user_msg.strip().endswith("}"):
        return {"draft_booking": draft_booking}
    
    generic_confirmations = ["yes", "yeah", "sure", "ok", "okay", "yep", "start", "begin", "y"]
    if not draft_booking and user_msg.strip().lower() in generic_confirmations:
        user_msg = ""

    # Detect if user's message is a question/FAQ query rather than a form answer
    question_triggers = ["?", "what is", "what does", "explain", "meaning", "kya hai", "kaise", "kyun", "tell me about"]
    is_question = any(q in user_msg.lower() for q in question_triggers)
    if is_question:
        logger.info(f"[extract_node] User asked a question ('{user_msg}'), skipping field extraction.")
        return {"draft_booking": draft_booking}
        
    # Check if the user is attempting to edit or update an existing field
    is_edit_query = any(w in user_msg.lower() for w in ["change", "update", "edit", "modify", "set", "correct", "instead of"])
    
    missing_fields = [f for f in ALL_FIELDS if not draft_booking.get(f)]
    
    if is_edit_query or draft_booking.get("_awaiting_confirm"):
        # When editing/updating, allow LLM to extract ANY non-doc field
        llm_extractable_fields = [f for f in ALL_FIELDS if not f.startswith("doc_")]
    else:
        llm_extractable_fields = [f for f in missing_fields if not f.startswith("doc_")]
        if not draft_booking.get("applicant_details"):
            llm_extractable_fields = [f for f in llm_extractable_fields if f != "address"]
    
    if user_msg and llm_extractable_fields and llm:
        schema_hints = {}
        for f in llm_extractable_fields:
            # Fully automated dynamic hint generation using config.yml definitions
            field_def = next((item for item in FORM_FIELDS if item["id"] == f), None)
            
            if field_def and field_def.get("mdms"):
                options = _mdms_get(*field_def["mdms"])
                if options:
                    schema_hints[f] = f"MUST BE EXACTLY ONE OF: {options}. Map the user's input to the closest matching option."
                else:
                    schema_hints[f] = FIELD_HINTS.get(f, "relevant value")
            elif f == "nightLight":
                schema_hints[f] = "Must be exactly 'Yes' or 'No'"
            else:
                schema_hints[f] = FIELD_HINTS.get(f, "relevant value")
        
        extract_prompt = f"""You are a strict data extraction AI for UPYOG Advertisement bookings.
Your ONLY job is to extract ANY updated or newly specified fields from the user's message.
Target fields to extract: {json.dumps(llm_extractable_fields)}
Hints: {json.dumps(schema_hints)}

Already collected fields: {json.dumps(draft_booking)}
Question the user is answering: "{last_assistant_msg}"
User message: "{user_msg}"

CRITICAL RULES:
- If the user's message clearly answers or updates one or more target fields, extract them.
- If a field is not mentioned or changed, return null for that field.
- If the user just says "Yes" or "No", use the "Question the user is answering" to figure out which field they are answering (e.g., nightLight).
- STRICT RULE FOR 'addType': Only extract if they mention a specific type (e.g. Hoarding, Unipole, Kiosk, Banner). Do NOT extract generic words like 'adv' or 'advertisement' as addType!

Reply ONLY with valid JSON containing the extracted fields. No explanations.
Example:
{{
  "location": "extracted value",
  "address": null
}}"""
        try:
            ext = llm.invoke([SystemMessage(content=extract_prompt)])
            m = re.search(r'\{.*\}', ext.content.strip(), re.DOTALL)
            if m:
                extracted_data = json.loads(m.group(0))
                for field in missing_fields:
                    val = extracted_data.get(field)
                    # Fixed bug: val could be bool False or string "No". 
                    # We check if val is not None instead of `if val`.
                    if val is not None and str(val).lower() not in ("null", "none", ""):
                        draft_booking[field] = val
                        logger.info(f"[extract_node] {field} = {val}")
        except Exception as e:
            logger.error(f"Extraction error: {e}")
            
    return {"draft_booking": draft_booking}


# Asks the user the next missing question (and shows UI dropdown buttons if it's a multiple-choice question)
def ask_next_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    draft_booking = state.get("draft_booking") or {}
    if not isinstance(draft_booking, dict):
        draft_booking = {}
    messages = state.get("messages", [])
    user_msg = messages[-1].content if messages else ""
    
    next_step = get_next_step(draft_booking)
    if not next_step:
        return {} 

    options = []
    ui_type = next_step.get("type", "text")
    field_label = next_step.get("label", next_step["id"])

    if next_step.get("mdms"):
        options = _mdms_get(*next_step["mdms"])

    text = f"Please provide the {field_label}."
    if llm:
        ctx = f"""You are a conversational UPYOG advertisement booking concierge.
Collected so far: {json.dumps({k: v for k, v in draft_booking.items() if v})}
Your task: Ask the user to provide the next missing field: "{field_label}".
Available options for this field: {json.dumps(options) if options else "None (free text/date/upload)"}

User just said: "{user_msg}"

Instructions:
1. Generate a natural, polite, conversational question asking for the "{field_label}".
2. Note: "Advertisement Type" refers to outdoor municipal advertising structure types (such as Hoarding, Unipole, Kiosk, Billboard, Banner, Poster, Digital Screen). NEVER ask about or refer to media file formats like videos, images, or audio formats.
3. Do NOT say "Hello" unless the user explicitly greeted you.
4. NEVER repeat or confirm what the user just selected. 
5. DO NOT list the available options in the text (the UI will handle that).
6. Maintain a formal, professional tone. NEVER use informal or familial terms of address like 'दीदी' (Didi), 'काकी' (Kaki), 'बेटा' (Beta), 'भैया' (Bhaiya), etc.
7. Output ONLY the conversational question text."""
        try:
            conv = llm.invoke([SystemMessage(content=ctx)])
            text = conv.content.strip()
        except Exception:
            pass

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

    return {"messages": [AIMessage(content=resp)], "missing_fields": [f for f in ALL_FIELDS if not draft_booking.get(f)], "input_type": "text", "options": []}


# Shows a beautiful UI table to the user displaying the available slots they can pick from
def slot_search_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    messages = state.get("messages", [])
    user_msg = messages[-1].content if messages else ""
    
    draft_booking = dict(state.get("draft_booking", {}))
    slots = _slot_search(draft_booking, phone_number=phone_number)
    if not slots:
        resp = "Sorry, no available advertisement slots were found for your selected location and date range. Please try selecting a different date range or location."
        MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
        return {"messages": [AIMessage(content=resp)], "missing_fields": ["start_date", "end_date"], "input_type": "text", "options": []}

    slots_json = json.dumps(slots)
    resp = f"Great! Here are the available slots. Please select one or more:\n<ui-slot-table data={slots_json} />"
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
    return {"messages": [AIMessage(content=resp)], "missing_fields": ["selected_slots"], "input_type": "text", "options": []}


# Renders a Form on the screen so the user can type their personal details (Name, Mobile, etc.)
def applicant_form_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    messages = state.get("messages", [])
    user_msg = messages[-1].content if messages else ""
    
    resp = f"Great! Please fill in your details to finalize the booking:\n<ui-applicant-form cartAmount=\"0\" />"
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
    return {"messages": [AIMessage(content=resp)], "missing_fields": ["applicant_details"], "input_type": "text", "options": []}


# Shows a final summary "Receipt" of all the details entered and asks for final Confirmation
def confirm_booking_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking") or {})
    draft_booking["_awaiting_confirm"] = True

    app_details = draft_booking.get("applicant_details", {})
    name = app_details.get("name") or draft_booking.get("applicantName", "N/A")
    mobile = app_details.get("mobile") or draft_booking.get("mobileNumber", "N/A")
    email = app_details.get("email") or draft_booking.get("emailId", "N/A")

    summary_text = (
        "### Advertisement Booking Summary\n\n"
        f"- **Advertisement Type:** {draft_booking.get('addType', 'N/A')}\n"
        f"- **Location:** {draft_booking.get('location', 'N/A')}\n"
        f"- **Face Area:** {draft_booking.get('faceArea', 'N/A')}\n"
        f"- **Start Date:** {draft_booking.get('start_date', 'N/A')}\n"
        f"- **End Date:** {draft_booking.get('end_date', 'N/A')}\n"
        f"- **Night Light:** {draft_booking.get('nightLight', 'No')}\n"
        f"- **Applicant Name:** {name}\n"
        f"- **Mobile:** {mobile}\n"
        f"- **Email:** {email}\n"
        f"- **Address:** {draft_booking.get('address', 'N/A')}\n"
        f"- **Sample Document:** Uploaded\n"
        f"- **Address Proof:** Uploaded\n"
        f"- **Identity Proof:** Uploaded\n\n"
        "Would you like to submit this advertisement booking?\n"
        "<ui-dropdown options=['Confirm & Submit', 'Edit Details'] />"
    )

    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="user", content="Show summary")
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=summary_text)

    return {"messages": [AIMessage(content=summary_text)], "draft_booking": draft_booking, "missing_fields": [], "input_type": "text", "options": []}


# Sends the complete verified form to the UPYOG API to officially register the booking
def create_booking_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking", {}))
    
    app_details = draft_booking.get("applicant_details", {})
    draft_booking["applicantName"] = app_details.get("name", "")
    draft_booking["mobileNumber"] = app_details.get("mobile", "")
    draft_booking["emailId"] = app_details.get("email", "")
    
    raw_resp = create_booking(json.dumps(draft_booking))
    resp = f"{raw_resp}\n\nIs there anything else I can help you with?"
    
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
    
    # Reset draft cleanly
    reset_draft = {f: None for f in ALL_FIELDS}
    reset_draft["selected_slots"] = None
    reset_draft["applicant_details"] = None
    reset_draft["_awaiting_confirm"] = None
    
    # Delete from Qdrant since it's now a completed booking, no longer a draft
    MemoryManager.delete_draft_state(phone_number, "adv_booking")
    
    return {"messages": [AIMessage(content=resp)], "draft_booking": reset_draft, "missing_fields": ALL_FIELDS, "input_type": "text", "options": []}


# If the user clicks "Edit Details", this asks them what exactly they want to change
def edit_prompt_node(state: AdvBookingState):
    phone_number = state.get("phone_number", "default")
    draft_booking = dict(state.get("draft_booking") or {})
    
    resp = "Please tell me what details you would like to edit or change."
    MemoryManager.save_long_term_interaction(phone_number=phone_number, role="assistant", content=resp)
    return {"messages": [AIMessage(content=resp)], "draft_booking": draft_booking, "input_type": "text", "options": []}


# The main traffic controller that checks the draft and routes the flow to the correct next step
def router(state: AdvBookingState) -> str:
    draft_booking = state.get("draft_booking", {})
    messages = state.get("messages", [])
    user_msg = messages[-1].content if messages else ""
    
    if all(draft_booking.get(f) for f in SEARCH_FIELDS) and not draft_booking.get("selected_slots"):
        return "slot_search"
        
    if draft_booking.get("selected_slots") and not draft_booking.get("applicant_details"):
        return "applicant_form"
        
    if all(draft_booking.get(f) for f in ALL_FIELDS):
        if draft_booking.get("_awaiting_confirm"):
            confirm_words = ["confirm", "submit", "yes", "yeah", "yup", "ok", "okay", "confirm & submit"]
            if any(w in user_msg.lower() for w in confirm_words):
                return "create_booking"
            edit_words = ["edit", "edit details", "modify", "correct", "update"]
            if any(w in user_msg.lower() for w in edit_words) and not any(f in user_msg.lower() for f in ["location", "address", "date", "type", "area"]):
                return "edit_prompt"
        return "confirm_booking"
        
    return "ask_next"

# ==========================================
# GRAPH
# ==========================================
workflow_graph = StateGraph(AdvBookingState)

workflow_graph.add_node("intent_and_ui", intent_and_ui_node)
workflow_graph.add_node("extraction", extraction_node)
workflow_graph.add_node("slot_search", slot_search_node)
workflow_graph.add_node("applicant_form", applicant_form_node)
workflow_graph.add_node("confirm_booking", confirm_booking_node)
workflow_graph.add_node("edit_prompt", edit_prompt_node)
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
        "confirm_booking": "confirm_booking",
        "edit_prompt": "edit_prompt",
        "create_booking": "create_booking",
        "ask_next": "ask_next"
    }
)
workflow_graph.add_edge("slot_search", END)
workflow_graph.add_edge("applicant_form", END)
workflow_graph.add_edge("confirm_booking", END)
workflow_graph.add_edge("edit_prompt", END)
workflow_graph.add_edge("create_booking", END)
workflow_graph.add_edge("ask_next", END)

adv_booking_graph = workflow_graph.compile(checkpointer=shared_memory)
