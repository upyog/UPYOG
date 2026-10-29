import os
import re
import json
import logging
import random
import glob
import ast
import importlib
import yaml
from datetime import datetime, timedelta
from typing import Dict, Any, List, Tuple, Optional
from dotenv import load_dotenv
from langchain_core.messages import HumanMessage

try:
    from groq import Groq
except ImportError:
    Groq = None

load_dotenv()

logger = logging.getLogger(__name__)

GROQ_API_KEY = os.environ.get("GROQ_API_KEY")
GROQ_MODEL = os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b")
groq_client = None


# ============== DYNAMIC PER-TURN LANGUAGE DETECTION ==============
from services.voice_service import (
    HINDI_PHONETIC_WORDS,
    ENGLISH_IN_DEVANAGARI,
    detect_language,
    detect_language_per_turn
)


# ============== LLM INTENT CLASSIFIER ==============

def classify_intent(query: str, history: list, lang: str) -> dict:
    """Classifies user intent using a fast LLM call."""
    global groq_client
    logger.info(f"[INTENT CLASSIFIER] Classifying intent for query='{query}', lang='{lang}'")

    recent_history = history[-3:] if len(history) >= 3 else history
    history_text = "\n".join([
        f"{turn.get('role', 'user').upper()}: {turn.get('content', '')}"
        for turn in recent_history
    ])

    classifier_prompt = f"""You are an intent classifier for UPYOG —
a government urban services chatbot for Indian cities.

Classify the user message into exactly one category.

━━━ CATEGORY DEFINITIONS ━━━

"greeting" — User says hello, hi, namaste, good morning, good evening, or any social opener with NO service request.
Examples: "hello", "hi", "namaste", "good morning", "नमस्ते"

"faq" — User wants INFORMATION or EXPLANATION about any UPYOG service.

"grievance_candidate" — User is describing a PERSONAL PROBLEM happening RIGHT NOW to them specifically.
"grievance_confirm" — User is saying YES to bot's offer to file a grievance.
"grievance_cancel" — User says NO to the grievance offer.
"grievance_status_candidate" — User wants to check the status of their existing complaints, view complaint history, or look up a complaint ID.
Examples:
→ "show my complaints"
→ "show my latest complaints"
→ "track my complaint"
→ "my complaint status"

"profile" — User is asking about their personal identity, user profile, account details, name, registered phone number, or who they are.
Examples:
→ "show my profile details"
→ "show my profile"
→ "my profile"
→ "who am I"
→ "what is my name"
→ "my account details"
→ "mera profile dikhao"
→ "meri profile details"
→ "my details"

"booking_candidate" — User wants to BOOK or RESERVE a resource (e.g. community hall).
"booking_confirm" — User is saying YES to the bot's offer to book a resource.
"booking_cancel" — User says NO to the booking offer.

"adv_candidate" — User wants to book advertisement space, hoardings, or unipoles.
"adv_confirm" — User says YES to the bot's offer to book an ad.
"adv_cancel" — User says NO to the ad booking offer.

"adv_status_candidate" — User wants to check the status of their existing advertisement bookings or know their booking ID.
Examples:
→ "show my bookings"
→ "what is my booking number"
→ "track my ad booking"
→ "my applications"

"draft_resume" — User wants to VIEW, SHOW, LIST, or RESUME their saved form drafts or applications.
Examples: "show my drafts", "my drafts", "view saved drafts", "draft dikhao"

"draft_delete" — User wants to DELETE, CANCEL, CLEAR, or DISCARD their draft(s) or application(s).
Examples: "delete my drafts", "delete draft", "clear drafts", "cancel draft", "draft delete karo", "draft hata do"

"draft_save" — User wants to SAVE their currently filled draft or form to finish later.
Examples: "save draft", "save my application", "draft save karo"

"draft_continue_application" — User wants to CONTINUE or RESUME filling their pending form/draft.
Examples: "continue my application", "resume complaint", "continue"

━━━ IMPORTANT RULES ━━━
1. Look at the last message and context. If the user says "yes" or "haan":
   - If the previous turn offered an advertisement -> "adv_confirm"
2. Pure social openers (hello/hi/namaste) with NO service content = "greeting".

━━━ CONVERSATION CONTEXT (last 3 turns) ━━━
{history_text}

━━━ CURRENT MESSAGE ━━━
"{query}"
Language: {lang}

Respond ONLY with this JSON, no other text:
{{
  "intent": "greeting" | "faq" | "profile" | "grievance_candidate" | "grievance_confirm" | "grievance_cancel" | "grievance_status_candidate" | "booking_candidate" | "booking_confirm" | "booking_cancel" | "adv_candidate" | "adv_confirm" | "adv_cancel" | "adv_status_candidate" | "draft_resume" | "draft_delete" | "draft_save" | "draft_continue_application" | "end_conversation",
  "reasoning": "one sentence why",
  "service": "specific UPYOG service name or null",
  "emotion": "neutral" | "frustrated" | "stuck" | "urgent"
}}"""

    try:
        if not groq_client and Groq:
            groq_client = Groq(api_key=GROQ_API_KEY)

        if not groq_client:
            return {"intent": "faq", "reasoning": "Groq client unconfigured", "service": None, "emotion": "neutral"}

        response = groq_client.chat.completions.create(
            model=GROQ_MODEL,
            messages=[{"role": "user", "content": classifier_prompt}],
            max_tokens=400,
            temperature=0.1
        )

        raw = response.choices[0].message.content.strip()
        json_match = re.search(r'\{.*\}', raw, re.DOTALL)
        if json_match:
            result = json.loads(json_match.group(0))
        else:
            raw_clean = raw.replace("```json", "").replace("```", "").strip()
            result = json.loads(raw_clean)
        logger.info(f"[INTENT CLASSIFIER] Groq parsed response: {result}")

        valid_intents = [
            "greeting", "faq", "profile", "grievance_candidate", "grievance_confirm", "grievance_cancel", "grievance_status_candidate",
            "booking_candidate", "booking_confirm", "booking_cancel",
            "adv_candidate", "adv_confirm", "adv_cancel", "adv_status_candidate",
            "draft_resume", "draft_delete", "draft_save", "draft_continue_application", "end_conversation"
        ]
       
        if result.get("intent") not in valid_intents:
            logger.warning(f"[INTENT CLASSIFIER] Invalid intent '{result.get('intent')}' returned — resetting to 'faq'")
            result["intent"] = "faq"

        return result

    except Exception as e:
        logger.error(f"[INTENT CLASSIFIER] Classifier exception: {e}")
        return {"intent": "faq", "reasoning": "classifier failed", "service": None, "emotion": "neutral"}


# ─── Config-driven greeting builder ────────────────────────────────────────────

def _load_services_registry() -> list:
    """Load the services list from config.yml once at startup."""
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    cfg_path = os.path.join(base_dir, "config.yml")
    if not os.path.exists(cfg_path):
        cfg_path = os.path.join(base_dir, "workflow", "config.yml")
    try:
        with open(cfg_path, "r") as f:
            return yaml.safe_load(f).get("services", [])
    except Exception as e:
        logger.error(f"Failed to load config.yml in _load_services_registry: {e}")
        return []

_SERVICES_REGISTRY: list = _load_services_registry()


def build_greeting_response(lang: str, active_workflows: dict) -> str:
    """
    Returns a short, polite, professional greeting using LLM.
    No service list — just a professional hello.
    """
    rand_seed = random.randint(1, 9999)

    if lang == "hi":
        prompt = (
            f"[seed:{rand_seed}] You are UPYOG AI, an official government services AI assistant for UPYOG. "
            f"The user just said hello. Reply with a polite, professional, short greeting in Hindi (Devanagari script). "
            f"STRICT RULE: Keep tone professional and formal. DO NOT use informal, familiar, or colloquial terms of address such as 'दीदी' (Didi), 'काकी' (Kaki), 'बेटा' (Beta), 'भैया' (Bhaiya), 'चाचा', 'अंकल', etc. "
            f"Use formal Hindi (e.g., 'नमस्ते! मैं UPYOG AI हूँ। मैं आपकी क्या सहायता कर सकता हूँ?'). "
            f"Just greet them politely and ask how you can help. 1-2 sentences only. No bullet points, no service lists."
        )
    else:
        prompt = (
            f"[seed:{rand_seed}] You are UPYOG AI, an official government services AI assistant for UPYOG. "
            f"The user just said hello. Reply with a polite, professional, short greeting in English. "
            f"STRICT RULE: Keep tone professional and formal. DO NOT use informal or colloquial terms of address. "
            f"Just greet them politely and ask how you can help. 1-2 sentences only. No bullet points, no service lists."
        )

    try:
        global groq_client
        if not groq_client and Groq:
            groq_client = Groq(api_key=GROQ_API_KEY)
        if groq_client:
            resp = groq_client.chat.completions.create(
                model=GROQ_MODEL,
                messages=[{"role": "user", "content": prompt}],
                max_tokens=400,
                temperature=0.2,
            )
            content = resp.choices[0].message.content.strip()
            if content:
                return content
        if lang == "hi":
            return "नमस्ते! मैं UPYOG AI हूँ। मैं आपकी क्या सहायता कर सकता हूँ?"
        return "Hello! I'm UPYOG AI. How can I help you today?"
    except Exception:
        if lang == "hi":
            return "नमस्ते! मैं UPYOG AI हूँ। मैं आपकी क्या सहायता कर सकता हूँ?"
        return "Hello! I'm UPYOG AI. How can I help you today?"


# ==========================================
# DYNAMIC PLUGIN LOADER & ROUTER
# ==========================================

workflows: Dict[str, Any] = {}


def load_plugins():
    """Dynamically loads and registers all LangGraph workflow plugins from workflow/ folder."""
    base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    workflow_dir = os.path.join(base_dir, "workflow")
    logger.info(f"Scanning for plugins in {workflow_dir}")
    
    for file_path in glob.glob(os.path.join(workflow_dir, "*.py")):
        module_name = os.path.basename(file_path)[:-3]
        if module_name in ["__init__", "base_state"] or module_name.startswith("."):
            continue
            
        try:
            module = importlib.import_module(f"workflow.{module_name}")
            graph_var_name = f"{module_name}_graph"
            if hasattr(module, graph_var_name):
                workflows[module_name] = getattr(module, graph_var_name)
                logger.info(f"Successfully registered workflow plugin: {module_name}")
        except Exception as e:
            logger.error(f"Failed to load plugin '{module_name}': {e}")


# Load plugins immediately
load_plugins()


def process_user_message(user_input: str, phone_number: str, session_id: str,
                         target_workflow: str = "adv_booking") -> Dict[str, Any]:
    """Dispatches user input to the correct LangGraph plugin workflow (adv_booking, grievance, etc)."""
    intent = target_workflow
    if intent not in workflows:
        return {"response": "This service is currently unavailable. Please try again later.", "status": "ok"}

    target_graph = workflows[intent]
    thread_key = phone_number if (phone_number and phone_number != "default") else session_id
    config = {"configurable": {"thread_id": thread_key}}

    try:
        events = target_graph.stream(
            {"messages": [HumanMessage(content=user_input)], "phone_number": phone_number,
             "session_id": session_id, "active_service": intent},
            config,
            stream_mode="values"
        )
    except Exception as stream_err:
        logger.error(f"[process_user_message] Workflow error in '{intent}': {stream_err}", exc_info=True)
        return {
            "response": "I apologize, but I encountered a temporary technical issue while processing this request. Please try again or rephrase your input.",
            "status": "ok",
            "input_type": "text",
            "options": []
        }
    
    final_message = None
    graph_input_type = "text"
    graph_options = []

    event_list = list(events) if events else []
    if event_list:
        for ev in event_list:
            if isinstance(ev, dict) and ev.get("messages"):
                final_message = ev["messages"][-1]
            if isinstance(ev, dict):
                ev_type = ev.get("input_type")
                ev_opts = ev.get("options")
                if ev_type is not None:
                    graph_input_type = ev_type
                if ev_opts is not None:
                    graph_options = ev_opts

    response_text = final_message.content if hasattr(final_message, "content") else str(final_message)
    
    input_type = graph_input_type or "text"
    options = graph_options or []
    
    # Parse <ui-dropdown>, <ui-button>, <ui-checkbox-group> options=[...] />
    choice_match = re.search(r'<ui-(dropdown|button|checkbox-group) options=\[([^\]]*)\]\s*/>', response_text)
    if choice_match:
        tag_type = choice_match.group(1)
        input_type = "choice" if tag_type in ["dropdown", "button"] else "checkbox"
        raw_options = choice_match.group(2)
        try:
            options = ast.literal_eval(f"[{raw_options}]")
        except Exception:
            options = [opt.strip().strip('\'"') for opt in raw_options.split(',')]
        response_text = re.sub(r'<ui-(dropdown|button|checkbox-group)[^>]+>', '', response_text).strip()
        
    min_date = None
    # Parse <ui-calendar ... />
    cal_match = re.search(r'<ui-calendar([^>]*)>', response_text)
    if cal_match:
        input_type = "date"
        cal_attrs = cal_match.group(1)
        min_date_match = re.search(r'minDate="([^"]*)"', cal_attrs)
        tomorrow_str = (datetime.now() + timedelta(days=1)).strftime("%Y-%m-%d")
        if min_date_match:
            val = min_date_match.group(1)
            if val == "tomorrow" or str(val).strip() <= datetime.now().strftime("%Y-%m-%d"):
                min_date = tomorrow_str
            else:
                min_date = str(val).strip()
        else:
            min_date = tomorrow_str
        response_text = re.sub(r'<ui-calendar[^>]*>', '', response_text).strip()
        
    # Parse <ui-file ... />
    if re.search(r'<ui-file[^>]*>', response_text):
        input_type = "file"
        response_text = re.sub(r'<ui-file[^>]*>', '', response_text).strip()
        
    # Parse <ui-slot-table data=[...] />
    slot_tag_match = re.search(r'<ui-slot-table data=(\[.*?\])\s*/>', response_text, re.DOTALL)
    if slot_tag_match:
        input_type = "slot_table"
        try:
            options = json.loads(slot_tag_match.group(1))
        except Exception:
            pass
        response_text = re.sub(r'<ui-slot-table[^>]*>', '', response_text).strip()

    # Parse <ui-applicant-form cartAmount="..." />
    form_match = re.search(r'<ui-applicant-form cartAmount="([^"]*)"\s*/>', response_text)
    if form_match:
        input_type = "applicant_form"
        options = {"cartAmount": form_match.group(1)}
        response_text = re.sub(r'<ui-applicant-form[^>]*>', '', response_text).strip()
        
    # Parse <ui-booking-history data='[...]' /> or <ui-complaint-history data='[...]' />
    history_match = re.search(r"<ui-(booking|complaint)-history data='(\[.*?\])'\s*/>", response_text, re.DOTALL)
    if not history_match:
        history_match = re.search(r'<ui-(booking|complaint)-history data=(\[.*?\])\s*/>', response_text, re.DOTALL)
    if history_match:
        tag_kind = history_match.group(1)
        input_type = "complaint_history" if tag_kind == "complaint" else "booking_history"
        try:
            options = json.loads(history_match.group(2))
        except Exception:
            pass
        response_text = re.sub(r'<ui-(booking|complaint)-history[^>]*/>', '', response_text).strip()
    
    messages_list = []
    if "\n\n**Continuing Your " in response_text:
        parts = response_text.split("\n\n**Continuing Your ", 1)
        msg1 = parts[0].strip()
        msg2 = ("**Continuing Your " + parts[1]).strip()
        messages_list = [msg1, msg2]
    
    return {
        "response": response_text,
        "messages_list": messages_list,
        "input_type": input_type,
        "options": options,
        "min_date": min_date,
        "status": "done"
    }


def handle_adv_turn(session_id: str, user_input: str, auth_token: Optional[str] = None,
                    workflow: str = "booking", reset: bool = False,
                    file_name: Optional[str] = None, file_data: Optional[str] = None) -> Dict[str, Any]:
    """Proxy conversation directly to Local LangGraph Agent."""
    try:
        from services.user_service import extract_phone_from_session
        phone_number = extract_phone_from_session(session_id)
    except Exception:
        phone_number = "default"
    
    logger.info(f"[AdAgent] Calling local dynamic plugin engine with phone: {phone_number}")
    
    try:
        result = process_user_message(user_input, phone_number, session_id, target_workflow=workflow)
        logger.info(f"[PluginAgent] Response status: {result.get('status')}")
        return result
    except Exception as e:
        import traceback
        error_details = traceback.format_exc()
        logger.error(f"[AdAgent] Error running LangGraph:\n{error_details}")
        return {
            "response": "Sorry, something went wrong while processing your request. Please try again.",
            "status": "error"
        }


adv_sessions: Dict[str, Any] = {}
