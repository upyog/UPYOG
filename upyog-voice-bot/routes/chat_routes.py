import os
import re
import json
import time
import logging
import threading
from typing import Dict, Any, Optional
from flask import Blueprint, request, jsonify, Response
from dotenv import load_dotenv

try:
    from groq import Groq
except ImportError:
    Groq = None

load_dotenv()

logger = logging.getLogger(__name__)

chat_bp = Blueprint("chat_routes", __name__)

GROQ_API_KEY = os.environ.get("GROQ_API_KEY")
GROQ_MODEL = os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b")
groq_client = None


# === GENERIC WORKFLOW INTERRUPTION & DRAFT MANAGER ===

def _set_pending_interruption(phone: str, data: dict):
    try:
        from database import r_client
        r_client.set(f"pending_interruption:{phone}", json.dumps(data), ex=600)
    except Exception as e:
        logger.error(f"[Interruption] Redis set error: {e}")


def _get_pending_interruption(phone: str) -> dict:
    try:
        from database import r_client
        raw = r_client.get(f"pending_interruption:{phone}")
        if raw:
            return json.loads(raw)
    except Exception as e:
        logger.error(f"[Interruption] Redis get error: {e}")
    return {}


def _clear_pending_interruption(phone: str):
    try:
        from database import r_client
        r_client.delete(f"pending_interruption:{phone}")
    except Exception as e:
        logger.error(f"[Interruption] Redis delete error: {e}")


# Redis-backed multi-draft pending state (survives across requests / gunicorn workers)
def _set_multi_draft_pending(phone: str, drafts: list):
    """Persist awaiting_multi_draft_choice state in Redis with 10-min TTL."""
    try:
        from database import r_client
        r_client.set(f"pending_multi_draft:{phone}", json.dumps({"status": "awaiting_multi_draft_choice", "drafts": drafts}), ex=600)
    except Exception as e:
        logger.warning(f"[MultiDraft] Redis set failed: {e}")


def _get_multi_draft_pending(phone: str):
    """Retrieve awaiting_multi_draft_choice state from Redis."""
    try:
        from database import r_client
        raw = r_client.get(f"pending_multi_draft:{phone}")
        if raw:
            return json.loads(raw)
    except Exception as e:
        logger.warning(f"[MultiDraft] Redis get failed: {e}")
    return None


def _clear_multi_draft_pending(phone: str):
    """Clear awaiting_multi_draft_choice state from Redis."""
    try:
        from database import r_client
        r_client.delete(f"pending_multi_draft:{phone}")
    except Exception:
        pass


# Formats any generic workflow draft dictionary into a clean Markdown summary for the UI
def format_draft_summary(draft_data: dict, wf_name: str) -> str:
    title = wf_name.replace('_', ' ').title()
    summary = f"**{title} Draft**\n\n"
    for k, v in draft_data.items():
        if not k.startswith("_") and v is not None:
            clean_k = k.replace('_', ' ').title()
            summary += f"• **{clean_k}**: {v}\n"
    return summary


# Summarizes a block of older chat messages and archives them in Qdrant long-term memory
def summarize_and_store_memory(phone_anchor, messages_to_summarize):
    global groq_client
    try:
        from memory_manager import MemoryManager
        from services.rag_service import model
        
        # Format messages for LLM
        convo_text = ""
        for msg in messages_to_summarize:
            role = "User" if msg.get("role") == "user" else "Assistant"
            convo_text += f"{role}: {msg.get('content')}\n"
            
        if not groq_client and Groq:
            groq_client = Groq(api_key=GROQ_API_KEY)
            
        prompt = f"""You are an AI memory summarization assistant.
Please summarize the following conversation chunk into 2 concise sentences. Focus on the core intent, entities, and any factual details discussed.

Conversation:
{convo_text}

Summary:"""

        if groq_client:
            response = groq_client.chat.completions.create(
                messages=[{"role": "user", "content": prompt}],
                model=GROQ_MODEL,
                max_tokens=400,
                temperature=0.3
            )
            summary_text = response.choices[0].message.content.strip()
            
            # Generate embedding
            embedding = model.encode([summary_text])[0].tolist()
            
            # Save to Qdrant
            success = MemoryManager.save_long_term_interaction(
                phone_number=phone_anchor,
                role="system_summary",
                content=summary_text,
                embedding=embedding
            )
            if success:
                logger.info(f"Successfully summarized and stored memory for {phone_anchor}")
    except Exception as e:
        logger.error(f"Error in summarize_and_store_memory: {e}")


"""
Main chat endpoint — three route aliases registered:
  /chat                  → direct local access (localhost:8090)
  /upyog-voice-bot/chat  → production via niautt EKS ingress
  /upyog-voice/chat      → backward compatibility with old deployment path
GET requests return a health check response so Kubernetes liveness
probes do not mark the pod as unhealthy.
"""
@chat_bp.route("/chat", methods=["GET", "POST"])
@chat_bp.route("/upyog-voice-bot/chat", methods=["GET", "POST"])
@chat_bp.route("/upyog-voice/chat", methods=["GET", "POST"])
def chat():
    if request.method == "GET":
        logger.info("[ENDPOINT /chat GET] Health check ping")
        return jsonify({"status": "ok", "message": "UPYOG Voice Bot Chat Endpoint"}), 200


    import services.rag_service as rag_service
    from services.rag_service import (
        is_hard_blocked, is_in_domain, get_rejection_message,
        retrieve_document
    )
    from services.voice_service import text_to_speech, translate_text
    from services.intent_service import (
        detect_language, classify_intent, _SERVICES_REGISTRY,
        build_greeting_response, workflows, process_user_message
    )
    from services.user_service import (
        extract_phone_from_session, save_user_profile_info,
        get_user_profile_info
    )
    from clients.upyog_client import verify_user_auth
    from database import save_user_profile_name

    try:
        if not rag_service.resources_ready_event.is_set() or any(x is None for x in [rag_service.model, rag_service.data, rag_service.index]):
            logger.warning("[ENDPOINT /chat POST] Resources still warming up — waiting up to 10s...")
            rag_service.resources_ready_event.wait(timeout=10)
            if any(x is None for x in [rag_service.model, rag_service.data, rag_service.index]):
                rag_service.load_resources()
            if any(x is None for x in [rag_service.model, rag_service.data, rag_service.index]):
                logger.error("[ENDPOINT /chat POST] Resources unavailable (503 Service Unavailable)")
                return jsonify({"error": "Loading resources..."}), 503

        user_data = request.json or {}
        user_input = user_data.get("query") or user_data.get("user_input") or ""
        session_id = user_data.get("session_id", "default")
        request_info = user_data.get("request_info") or user_data.get("RequestInfo", {})
        phone_anchor = extract_phone_from_session(session_id)
        from database import get_chat_history
        history = get_chat_history(phone_anchor) if phone_anchor != "default" else []
        file_name = user_data.get("file_name")
        file_data = user_data.get("file_data") 

        token = None
        cached_info = None
        is_authenticated = False

        # --- Auto-detect & Validate User Profile Info dynamically ---
        # 1. First check if parent portal (e.g. NIUATT) passed RequestInfo with token & userInfo
        req_user_info = request_info.get("userInfo", {}) if isinstance(request_info, dict) else {}
        req_auth_token = request_info.get("authToken") if isinstance(request_info, dict) else None
        req_auth_token = req_auth_token or user_data.get("auth_token")

        if req_user_info and req_auth_token and len(str(req_auth_token)) > 15:
            req_mobile = req_user_info.get("mobileNumber") or req_user_info.get("userName")
            clean_mobile = re.sub(r'\D', '', str(req_mobile or ''))[-10:]
            if clean_mobile and len(clean_mobile) == 10:
                phone_anchor = clean_mobile
                token = req_auth_token
                req_user_info["_auth_token"] = req_auth_token
                req_user_info["_verified_at"] = time.time()
                save_user_profile_info(phone_anchor, req_user_info)
                cached_info = req_user_info
                is_authenticated = True
                logger.info(f"[Auth] Auto-authenticated from RequestInfo for mobile={phone_anchor}")

        # 2. Check user-service Redis token store directly (access_token:<token>)
        if not is_authenticated:
            token = token or req_auth_token or user_data.get("auth_token") or (request_info.get("authToken") if isinstance(request_info, dict) else None)
            if token and len(str(token)) > 15:
                from database import get_user_from_redis_token
                redis_user = get_user_from_redis_token(str(token))
                if redis_user:
                    mobile = redis_user.get("mobileNumber") or redis_user.get("userName")
                    clean_mobile = re.sub(r'\D', '', str(mobile or ''))[-10:]
                    if clean_mobile and len(clean_mobile) == 10:
                        phone_anchor = clean_mobile
                        save_user_profile_info(phone_anchor, redis_user)
                        cached_info = redis_user
                        is_authenticated = True
                        logger.info(f"[Auth] Auto-authenticated from backbone Redis token store for mobile={phone_anchor}")

        # 3. Check session phone_anchor cache if not already authenticated
        if not is_authenticated and phone_anchor != "default":
            token = user_data.get("auth_token") or (request_info.get("authToken") if isinstance(request_info, dict) else None)
            if token and len(token) > 15:
                cached_info = get_user_profile_info(phone_anchor)
                cached_token = cached_info.get("_auth_token") if cached_info else None
                verified_at = cached_info.get("_verified_at", 0) if cached_info else 0
                
                # Trust cache if same token and verified in the last 10 minutes (600s)
                if cached_token == token and (time.time() - verified_at) < 600:
                    is_authenticated = True
                    logger.info(f"[Auth] Token for {phone_anchor} verified from cache (last check: {int(time.time() - verified_at)}s ago)")
                else:
                    logger.info(f"[Auth] Token cache miss/expired for {phone_anchor}. Validating with UPYOG...")
                    is_valid, fresh_user_info = verify_user_auth(token, phone_anchor)
                    if is_valid:
                        fresh_user_info["_auth_token"] = token
                        fresh_user_info["_verified_at"] = time.time()
                        save_user_profile_info(phone_anchor, fresh_user_info)
                        cached_info = fresh_user_info
                        is_authenticated = True
                    else:
                        from database import r_client
                        r_client.delete(f"user_profile_info:{phone_anchor}")
                        cached_info = None
                        is_authenticated = False
                        logger.warning(f"[Auth] Token verification failed for {phone_anchor}, operating in guest mode")

        # 4. Fallback: query UPYOG /user/_search using the token
        if not is_authenticated:
            token = user_data.get("auth_token") or (request_info.get("authToken") if isinstance(request_info, dict) else None)
            if token and len(str(token)) > 15:
                is_valid, fresh_user_info = verify_user_auth(token, "default")
                if is_valid and fresh_user_info:
                    mobile = fresh_user_info.get("mobileNumber") or fresh_user_info.get("userName")
                    clean_mobile = re.sub(r'\D', '', str(mobile or ''))[-10:]
                    if clean_mobile and len(clean_mobile) == 10:
                        phone_anchor = clean_mobile
                        fresh_user_info["_auth_token"] = token
                        fresh_user_info["_verified_at"] = time.time()
                        save_user_profile_info(phone_anchor, fresh_user_info)
                        cached_info = fresh_user_info
                        is_authenticated = True
                        logger.info(f"[Auth] Verified token with UPYOG and authenticated mobile={phone_anchor}")

        if file_name and file_data and token:
            from mcp_tools import upload_to_filestore
            file_store_id = upload_to_filestore(file_name, file_data, token)
            if file_store_id:
                user_input = json.dumps({"document": file_store_id})
                file_name = None

        if file_name and not user_input:
            user_input = json.dumps({"document": file_name})

        if not user_input and not file_name:
            return jsonify({"response": "", "lang": "en", "audio": ""})

        # PROFILE MEMORY INTERCEPTOR 
        # Capture name introductions and anchor permanently to phone number inside Redis
        name_match = re.search(r'\bi\s+am\s+([A-Za-z]+)\b|\bmy\s+name\s+is\s+([A-Za-z]+)\b', user_input, re.IGNORECASE)
        if name_match:
            detected_name = name_match.group(1) or name_match.group(2)
            save_user_profile_name(phone_anchor, detected_name.strip().capitalize())

        # script-aware language detection returning dict
        lang_info = detect_language(user_input)
        user_language = lang_info['lang']
        detected_script = lang_info['script']
        search_lang = lang_info['search_lang']

        logger.info(f"━━━ REQUEST [Session: {session_id}] ━━━")
        logger.info(f"Query: '{user_input}'")
        logger.info(f"Lang: {user_language} | Script: {detected_script} | SearchLang: {search_lang}")
        if request_info:
            logger.info(f"Auth RequestInfo Present: authToken={'[REDACTED]' if request_info.get('authToken') else 'None'}, msgId={request_info.get('msgId')}")
        logger.info(f"━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")

        # Hard block check - only block truly unrelated topics
        if is_hard_blocked(user_input):
            logger.info(f"[CHAT FLOW] Query hard-blocked for out-of-domain topic.")
            msg = ("मैं केवल UPYOG और शहरी सरकारी सेवाओं के बारे में "
                   "सहायता कर सकता हूँ।" if user_language == 'hi' else
                   "I can only help with UPYOG and urban government services.")
            audio_output = text_to_speech(msg, user_language)
            return jsonify({"response": msg, "lang": user_language,
                           "audio": audio_output, "mode": "blocked"})

        # ── Direct greeting pre-check (before LLM classifier) ───────────────────
        _greet_cfg = next(
            (s for s in _SERVICES_REGISTRY if s.get("key") == "greeting"), {}
        )
        _greet_kws = [w.lower() for w in _greet_cfg.get("keywords", [
            "hello", "hi", "hey", "namaste", "good morning", "good afternoon",
            "good evening", "hola", "howdy", "greetings", "नमस्ते", "हेलो"
        ])]
        
        # Word boundary match to ensure words like "hindi", "hinglish", "this" don't match "hi"
        _is_pure_greeting = (
            user_input.lower().strip() in _greet_kws or
            (len(user_input.split()) <= 2 and any(re.search(rf'\b{re.escape(w)}\b', user_input.lower()) for w in _greet_kws) and not any(l in user_input.lower() for l in ["hindi", "hinglish", "english", "translate", "karo", "batao", "status", "bill"]))
        )
        if _is_pure_greeting:
            greet_msg = build_greeting_response(user_language, workflows)
            audio_output = text_to_speech(greet_msg, user_language)
            logger.info("[Greeting] Responding with fresh greeting")
            return jsonify({
                "response": greet_msg, "lang": user_language,
                "mode": "greeting", "audio": audio_output
            })

        # ── Direct login guidance check (CHATBOT-02: Portal journey alignment) ───
        login_kws = [
            "how to login", "how to log in", "how do i login", "how can i login",
            "login kaise kare", "login kaise karte hain", "login process",
            "login process kya hai", "can you login", "log me in", "where is login",
            "login option", "login button", "login kaise hoga", "login kahan hai",
            "login kaise karein", "login kaise karey", "login steps", "login karna",
            "login kaise kiya jata hai", "login karna hai", "can i login without clicking",
            "login without clicking", "how to sign in", "sign in kaise kare"
        ]
        ui_clean = user_input.lower().strip()
        is_login_query = (
            any(kw in ui_clean for kw in login_kws) or
            (("login" in ui_clean or "log in" in ui_clean or "sign in" in ui_clean) and any(w in ui_clean for w in ["how", "kaise", "where", "kahan", "procedure", "karna", "process", "steps", "help", "batao", "bataiye", "can you", "without", "bina"]))
        )
        if is_login_query:
            if user_language == "hi":
                login_msg = (
                    "बाईं ओर के साइडबार (Left Sidebar) में नीचे जाएं और **Login** विकल्प पर क्लिक करें। "
                    "अपना पंजीकृत मोबाइल नंबर भरें और फिर प्राप्त OTP दर्ज करें। "
                    "लॉगिन करने के बाद, आप बुकिंग बना सकते हैं, शिकायत दर्ज कर सकते हैं और अपने आवेदन की स्थिति देख सकते हैं।"
                )
            else:
                login_msg = (
                    "In the left sidebar, scroll down and click on **Login**. "
                    "Enter your registered mobile number and then enter the OTP received on your phone. "
                    "Once logged in, you will be able to create bookings, register complaints, and check your application status."
                )
            audio_output = text_to_speech(login_msg, user_language)
            logger.info("[Login Guidance] Returned portal left-sidebar login flow instructions")
            return jsonify({
                "response": login_msg,
                "lang": user_language,
                "mode": "faq",
                "audio": audio_output
            })

        # ── Check if user is claiming they have logged in ("logging done", "login ho gaya") ──
        login_claim_kws = [
            "logging done", "login done", "logged in", "i have logged in",
            "i logged in", "done", "login ho gaya", "maine login kar liya",
            "login complete", "login kar liya", "login hogaya", "signed in",
            "i have signed in", "now logged in", "login completed", "done login",
            "login kar chuka hu", "login ho chuka hai"
        ]
        is_login_claim = any(ui_clean == kw or ui_clean.startswith(kw) for kw in login_claim_kws)
        if is_login_claim:
            if is_authenticated:
                user_name = (cached_info.get("name") if cached_info else None) or "Citizen"
                if user_language == "hi":
                    resp_msg = f"बहुत बढ़िया! आपकी पहचान सत्यापित हो गई है ({user_name})। अब आप विज्ञापन बुकिंग कर सकते हैं या शिकायत दर्ज कर सकते हैं। आप क्या करना चाहते हैं?"
                else:
                    resp_msg = f"Great! Your login session is verified ({user_name}). You can now proceed to book an advertisement or register a complaint. How would you like to proceed?"
            else:
                if user_language == "hi":
                    resp_msg = "मुझे अभी आपका सक्रिय लॉगिन सत्र नहीं मिला है। कृपया बाईं ओर के साइडबार में नीचे **Login** विकल्प पर क्लिक करके अपने मोबाइल नंबर और OTP से लॉगिन पूरा करें।"
                else:
                    resp_msg = "I do not detect an active logged-in session yet. Please complete the login by clicking **Login** in the left sidebar and entering your mobile number and OTP."
            audio_output = text_to_speech(resp_msg, user_language)
            logger.info(f"[Login Claim] is_authenticated={is_authenticated}, returning verified status response")
            return jsonify({
                "response": resp_msg,
                "lang": user_language,
                "mode": "faq",
                "audio": audio_output
            })

        # ===== EARLY INTERCEPTION: Multi-Draft Selection Menu =====
        phone = phone_anchor if phone_anchor != "default" else extract_phone_from_session(session_id)
        thread_key = phone if (phone and phone != "default") else session_id
        config = {"configurable": {"thread_id": thread_key}}

        pending_multi = _get_multi_draft_pending(phone)
        if pending_multi and pending_multi.get("status") == "awaiting_multi_draft_choice":
            from draft_switcher import MultiDraftSwitcher
            drafts = pending_multi.get("drafts", [])
            logger.info(f"[DraftSwitcher] Intercepted multi-draft response for {phone}. Input='{user_input}', Drafts={len(drafts)}")
            
            ui_check = user_input.strip().lower()
            digits = re.findall(r'\d+', ui_check)
            selected_idx = int(digits[0]) - 1 if digits else -1
            delete_keywords = ["delete", "cancel", "clear", "discard", "remove", "erase", "hatao", "hata", "mitado"]
            is_delete_choice = selected_idx == len(drafts) + 1 or any(w in ui_check for w in delete_keywords)

            if is_delete_choice:
                logger.info(f"[DraftSwitcher] User chose to delete/cancel drafts while in multi-draft switcher")
                _clear_multi_draft_pending(phone)
            else:
                selected_draft = MultiDraftSwitcher.resolve_citizen_selection(user_input, drafts)
                _clear_multi_draft_pending(phone)
                
                if selected_draft:
                    target_wf = selected_draft.get("plugin_name")
                    draft_data = selected_draft.get("draft_data", {})
                    logger.info(f"[DraftSwitcher] Selected: {target_wf}, draft_data keys: {list(draft_data.keys())}")
                    
                    # Restore selected draft into LangGraph workflow
                    if target_wf in workflows and draft_data:
                        draft_key = "draft_booking" if target_wf == "adv_booking" else "draft_grievance"
                        config_update = {"configurable": {"thread_id": phone if (phone and phone != "default") else session_id}}
                        workflows[target_wf].update_state(config_update, {draft_key: draft_data})
                        logger.info(f"[DraftSwitcher] Draft restored into LangGraph for {phone}/{target_wf}")
                        
                    # Send 'continue' — triggers resume path in intent_and_ui_node directly
                    agent_res = process_user_message("continue", phone, session_id, target_workflow=target_wf)
                    audio = text_to_speech(agent_res.get("response", ""), user_language)
                    return jsonify({
                        "response": agent_res.get("response", ""), "messages": agent_res.get("messages_list", []),
                        "lang": user_language, "mode": "agent_active", "audio": audio,
                        "input_type": agent_res.get("input_type", "text"), "options": agent_res.get("options", []),
                        "show_button": agent_res.get("show_button")
                    })
                else:
                    logger.warning(f"[DraftSwitcher] Could not resolve selection '{user_input}' from {len(drafts)} drafts")

        # ===== INTENT CLASSIFICATION FLOW =====
        ui_lower = user_input.strip().lower()
        is_ui_payload = False
        if (ui_lower.startswith("[") and ui_lower.endswith("]")) or (ui_lower.startswith("{") and ui_lower.endswith("}")):
            is_ui_payload = True

        profile_triggers = [
            "profile", "my profile", "profile details", "who am i", "what is my name",
            "show my details", "my details", "account details", "my account",
            "user details", "mera profile", "meri profile", "mera naam", "meri details",
            "profile info", "my info", "account info"
        ]
        is_profile_query = any(re.search(rf'\b{re.escape(w)}\b', ui_lower) for w in profile_triggers)

        # Regex patterns for Draft operations
        draft_delete_patterns = [
            r"\b(delete|remove|clear|discard|cancel|erase|drop)\s+(all\s+)?(my\s+)?(saved\s+)?(drafts?|applications?)\b",
            r"\b(my\s+)?(saved\s+)?(drafts?|applications?)\s+(ko\s+)?(delete|cancel|clear|discard|remove|hatao|hata\s*do|hataiye|mitado)\b",
            r"\b(delete|remove|clear|discard|cancel)\s+(the\s+)?drafts?\b",
            r"\b(delete|remove|clear|discard|cancel)\s+(draft\s*\d+|option\s*\d+|\d+)\b",
            r"\b(delete|remove|clear|discard|cancel)\s+(grievance|complaint|booking|advertisement|adv)\s+draft\b",
            r"\bdraft(s)?\s+(delete|cancel|clear|discard|remove|hatao|hata\s*do|hataiye|khatam)\b",
            r"\b(mere|mera|sab|saare)\s+draft(s)?\s+(delete|cancel|clear|hatao|hata\s*do|hataiye)\b",
            r"\bdelete\s+(all\s+)?drafts?\b",
            r"\bcancel\s+(all\s+)?drafts?\b",
            r"\bclear\s+(all\s+)?drafts?\b",
            r"\bdiscard\s+(all\s+)?drafts?\b",
            r"\bcancel\s+application\b",
            r"\bcancel\s+my\s+application\b",
            r"\bdelete\s+my\s+application\b",
            r"\bdelete\s+application\b"
        ]
        is_draft_delete = any(re.search(p, ui_lower) for p in draft_delete_patterns)

        draft_continue_patterns = [
            r"\b(continue|resume)\s+(my\s+)?(application|booking|complaint|draft|grievance)\b",
            r"\b(aage\s+badhao|jaari\s+rakhein|continue\s+karo)\b",
            r"^continue$",
            r"^resume$"
        ]
        is_draft_continue = any(re.search(p, ui_lower) for p in draft_continue_patterns)

        draft_save_patterns = [
            r"\bsave\s+(the\s+|my\s+)?(draft|application)\b",
            r"\bdraft\s+save\s*(karo|kar\s*do|karein)?\b"
        ]
        is_draft_save = any(re.search(p, ui_lower) for p in draft_save_patterns)

        draft_view_patterns = [
            r"\b(show|view|list|check|see|get|fetch|display|open|dikhao|batao|bataiye)\s+(all\s+)?(my\s+)?(saved\s+)?(drafts?)\b",
            r"\b(my\s+|saved\s+|all\s+|mere\s+|mera\s+)?drafts?\s+(list|dikhao|batao|bataiye|dekho|dekhein)\b",
            r"^(show\s+)?(my\s+)?drafts?$",
            r"^(saved\s+)?drafts?$",
            r"^(mere\s+|mera\s+)?drafts?(\s+dikhao)?$",
            r"\b(show|view|list|open)\s+drafts?\b"
        ]
        is_draft_view = any(re.search(p, ui_lower) for p in draft_view_patterns) or (
            "draft" in ui_lower and any(w in ui_lower for w in ["show", "resume", "continue", "open", "list", "view", "check", "dikhao", "batao"])
        )

        if is_ui_payload:
            intent_data = {"intent": "none", "service": "None", "emotion": "neutral"}
            logger.info("Bypassed intent classification for UI payload.")
        elif is_profile_query:
            intent_data = {"intent": "profile", "service": "None", "emotion": "neutral"}
            logger.info(f"[Intent] Pre-classified profile query: '{user_input}'")
        elif is_draft_delete:
            intent_data = {"intent": "draft_delete", "service": "None", "emotion": "neutral"}
            logger.info(f"[Intent] Pre-classified draft delete query: '{user_input}'")
        elif is_draft_continue:
            intent_data = {"intent": "draft_continue_application", "service": "None", "emotion": "neutral"}
            logger.info(f"[Intent] Pre-classified draft continue query: '{user_input}'")
        elif is_draft_save:
            intent_data = {"intent": "draft_save", "service": "None", "emotion": "neutral"}
            logger.info(f"[Intent] Pre-classified draft save query: '{user_input}'")
        elif is_draft_view:
            intent_data = {"intent": "draft_resume", "service": "None", "emotion": "neutral"}
            logger.info(f"[Intent] Pre-classified draft view/resume query: '{user_input}'")
        elif "end conversation" in ui_lower:
            intent_data = {"intent": "end_conversation", "service": "None", "emotion": "neutral"}
        else:
            intent_data = classify_intent(user_input, history, user_language)
            
        intent = intent_data['intent']
        logger.info(f"INTENT: {intent}, SERVICE: {intent_data.get('service')}, EMOTION: {intent_data.get('emotion')}")

        active_plugin = None
        
        # Check if there's an active session in any plugin
        active_plugins = []
        for wf_name, graph in workflows.items():
            state = graph.get_state(config)
            if state and state.values:
                draft_key = "draft_booking" if wf_name == "adv_booking" else "draft_grievance"
                draft = state.values.get(draft_key)
                if isinstance(draft, dict) and not draft.get("_cancelled"):
                    has_fields = any(v for k, v in draft.items() if not k.startswith("_") and v is not None and str(v).strip() != "")
                    has_options = bool(draft.get("_category_options") or draft.get("_sub_options") or draft.get("_locality_options") or draft.get("_slot_options"))
                    messages = state.values.get("messages", [])
                    
                    if (has_fields or has_options) and messages:
                        timestamp = getattr(state, "created_at", "") or ""
                        active_plugins.append((wf_name, timestamp, len(messages)))
        
        if active_plugins:
            active_plugins.sort(key=lambda x: (x[1], x[2]), reverse=True)
            active_plugin = active_plugins[0][0]

        # Map ML intents & dynamic config keywords to plugin names (Zero Hardcoding)
        plugin_intent = None
        ui_lower = user_input.lower()

        # Dynamic keyword & ID prefix matching from config.yml services registry
        for srv in _SERVICES_REGISTRY:
            s_key = srv.get("key")
            if s_key not in workflows:
                continue
            keywords = [k.lower() for k in srv.get("keywords", [])]
            id_prefixes = [p.lower() for p in srv.get("id_prefixes", [])]
            
            has_id = any(p in ui_lower for p in id_prefixes)
            has_kw = any(re.search(rf'\b{re.escape(w)}\b', ui_lower) for w in keywords)
            
            if has_id or has_kw:
                plugin_intent = s_key
                break

        if not plugin_intent and intent != "profile":
            if intent in ["grievance_candidate", "grievance_status_candidate"]:
                plugin_intent = "grievance"
            elif intent in ["adv_candidate", "adv_status_candidate", "booking_candidate"]:
                plugin_intent = "adv_booking"
            elif intent in ["adv_confirm", "booking_confirm", "adv_cancel", "booking_cancel", "grievance_confirm", "grievance_cancel"]:
                plugin_intent = active_plugin or ("adv_booking" if "adv" in intent or "booking" in intent else "grievance")

        # Explicit service switch occurs ONLY if the user explicitly requested the other service
        is_generic_response = ui_lower in ["yes", "no", "yeah", "yup", "ya", "ok", "okay", "sure", "confirm", "cancel", "haan", "nahi", "nahin", "1", "2", "option 1", "option 2"]
        if plugin_intent and active_plugin and plugin_intent != active_plugin and not is_generic_response:
            logger.info(f"[Router] User switched service: {active_plugin} -> {plugin_intent}")
            if active_plugin in workflows:
                prev_state = workflows[active_plugin].get_state(config)
                if prev_state and prev_state.values:
                    prev_draft_key = "draft_booking" if active_plugin == "adv_booking" else "draft_grievance"
                    prev_draft = prev_state.values.get(prev_draft_key) or {}
                    if prev_draft and any(v for k, v in prev_draft.items() if not k.startswith("_") and v is not None and str(v).strip() != ""):
                        from memory_manager import MemoryManager
                        MemoryManager.save_draft_state(phone, active_plugin, prev_draft)
                        logger.info(f"[AutoSave] Saved previous workflow draft for {phone}/{active_plugin}")
                    
                    empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if active_plugin == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                    workflows[active_plugin].update_state(config, {prev_draft_key: empty_draft, "missing_fields": [], "messages": []})
                    logger.info(f"[AutoSave] Reset in-memory state for {active_plugin}")
            active_plugin = plugin_intent
        elif active_plugin and is_generic_response:
            plugin_intent = active_plugin

        # === GENERIC WORKFLOW INTERRUPTION & DRAFT AUTO-SAVER ===
        question_triggers = [
            "?", "what is", "what does", "explain", "meaning", "kya hai", "kaise", "kyun",
            "tell me about", "rules for", "how to", "who is", "help with", "information about",
            "where can", "details of", "procedure for", "charges for", "fees for", "kya hota",
            "kaun", "kahan", "batao", "bataiye", "jaankari", "information"
        ]
        is_explicit_question = any(q in user_input.lower() for q in question_triggers)

        # Check if user_input matches any options or fields of the active workflow
        is_option_selection = False
        if active_plugin and active_plugin in workflows and not is_explicit_question:
            state = workflows[active_plugin].get_state(config)
            if state and state.values:
                draft_key = "draft_booking" if active_plugin == "adv_booking" else "draft_grievance"
                draft = state.values.get(draft_key) or {}
                
                if active_plugin == "grievance":
                    from workflow.grievance import _pgr_categories, _pgr_localities
                    pgr_cats = draft.get("_category_options") or _pgr_categories() or {}
                    cats = list(pgr_cats.keys())
                    pgr_locs = draft.get("_locality_options") or _pgr_localities() or []
                    locs = [l.get("name", "") if isinstance(l, dict) else str(l) for l in pgr_locs]
                else:
                    cats = list((draft.get("_category_options") or {}).keys())
                    locs = [l.get("name", "") if isinstance(l, dict) else str(l) for l in (draft.get("_locality_options") or [])]
                
                subs = [s.get("name", "") if isinstance(s, dict) else str(s) for s in (draft.get("_sub_options") or [])]
                slots = [s.get("name", "") if isinstance(s, dict) else str(s) for s in (draft.get("_slot_options") or [])]
                
                all_opts = [re.sub(r'[\s_]+', '', str(o).lower()) for o in (cats + subs + locs + slots) if o]
                clean_in = re.sub(r'[\s_]+', '', user_input.strip().lower())
                
                is_digit_choice = bool(re.match(r'^(?:option\s*)?\d+$', user_input.strip().lower()))
                
                if is_digit_choice or clean_in in all_opts or any(clean_in == o or (o in clean_in and len(user_input.split()) <= 3) for o in all_opts):
                    is_option_selection = True
                    logger.info(f"[Router] '{user_input}' matched active option in {active_plugin}")

        is_faq_or_interruption = is_explicit_question and not is_option_selection and not is_ui_payload

        if is_faq_or_interruption and active_plugin:
            logger.info(f"[Interruption] User asked a question during '{active_plugin}': '{user_input}'. Answering FAQ.")
            
            draft_saved = False
            saved_plugin_name = None
            
            if active_plugin in workflows:
                state = workflows[active_plugin].get_state(config)
                if state and state.values:
                    draft_key = "draft_booking" if active_plugin == "adv_booking" else "draft_grievance"
                    draft = state.values.get(draft_key) or {}
                    real_fields = {k: v for k, v in draft.items() if not k.startswith("_") and v is not None and str(v).strip() != ""}
                    if real_fields:
                        from memory_manager import MemoryManager
                        MemoryManager.save_draft_state(phone, active_plugin, draft)
                        draft_saved = True
                        saved_plugin_name = active_plugin
                        logger.info(f"[AutoSave] Saved draft for {phone}/{active_plugin}: {real_fields}")
                    
                    empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if active_plugin == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                    workflows[active_plugin].update_state(config, {draft_key: empty_draft, "missing_fields": []})

            faq_ans = retrieve_document(user_input, user_language, history, session_id=session_id)
            
            draft_note = ""
            if draft_saved and saved_plugin_name:
                plugin_display = "Advertisement Booking" if saved_plugin_name == "adv_booking" else "Grievance"
                if user_language == 'hi':
                    draft_note = f"\n\n*(नोट: आपका {plugin_display} ड्राफ्ट सुरक्षित सेव कर लिया गया है। इसे जारी रखने के लिए कभी भी 'Continue my application' कहें या ड्राफ्ट चुनें।)*"
                else:
                    draft_note = f"\n\n*(Note: Your {plugin_display} application draft has been saved. You can continue it anytime by saying 'Continue my application' or selecting it from your drafts.)*"
            
            full_ans = f"{faq_ans}{draft_note}"
            audio = text_to_speech(full_ans, user_language)
            return jsonify({
                "response": full_ans,
                "lang": user_language,
                "mode": "faq",
                "audio": audio,
                "input_type": "text",
                "options": []
            })

        # 2. Handle explicit "Continue Application" intent
        if intent == "draft_continue_application":
            from memory_manager import MemoryManager
            all_drafts = MemoryManager.get_all_draft_states(phone)
            logger.info(f"[ContinueApplication] Found {len(all_drafts)} drafts for phone {phone}")
            
            if len(all_drafts) == 1:
                target_wf = all_drafts[0].get("plugin_name", "adv_booking")
                draft_data = all_drafts[0].get("draft_data", {})
                
                if target_wf in workflows and draft_data:
                    draft_key = "draft_booking" if target_wf == "adv_booking" else "draft_grievance"
                    config_update = {"configurable": {"thread_id": phone if (phone and phone != "default") else session_id}}
                    workflows[target_wf].update_state(config_update, {draft_key: draft_data})
                
                agent_res = process_user_message("continue", phone, session_id, target_workflow=target_wf)
                audio = text_to_speech(agent_res.get("response", ""), user_language)
                return jsonify({
                    "response": agent_res.get("response", ""),
                    "messages": agent_res.get("messages_list", []),
                    "lang": user_language,
                    "mode": "agent_active",
                    "audio": audio,
                    "input_type": agent_res.get("input_type", "text"),
                    "options": agent_res.get("options", []),
                    "min_date": agent_res.get("min_date"),
                    "field": agent_res.get("field"),
                    "show_button": agent_res.get("show_button")
                })
            elif len(all_drafts) > 1:
                from draft_switcher import MultiDraftSwitcher
                switcher_res = MultiDraftSwitcher.inspect_and_render_switcher(phone, user_input=user_input, user_language=user_language)
                _set_multi_draft_pending(phone, switcher_res["drafts"])
                msg = switcher_res["menu"]
                audio = text_to_speech(msg, user_language)
                return jsonify({
                    "response": msg,
                    "lang": user_language,
                    "mode": "agent_active",
                    "audio": audio,
                    "input_type": "choice",
                    "options": switcher_res.get("options") or [f"Option {i+1}" for i in range(switcher_res["count"])] + ["Start New Service Request"],
                    "show_button": True
                })
            else:
                msg = (
                    "Aapka koi saved draft nahi mila. Kya aap naya application start karna chahte hain?"
                    if user_language == 'hi' else
                    "I couldn't find any saved drafts. Would you like to start a new application?"
                )
                audio = text_to_speech(msg, user_language)
                return jsonify({"response": msg, "lang": user_language, "mode": "faq", "audio": audio})

        # 3. Handle explicit "Save Draft" intent
        if intent == "draft_save":
            plugin = active_plugin or "adv_booking"
            if plugin and plugin in workflows:
                config_check = {"configurable": {"thread_id": phone if (phone and phone != "default") else session_id}}
                state = workflows[plugin].get_state(config_check)
                if state and state.values:
                    draft_key = "draft_booking" if plugin == "adv_booking" else "draft_grievance"
                    draft = state.values.get(draft_key) or {}
                    if draft:
                        from memory_manager import MemoryManager
                        MemoryManager.save_draft_state(phone, plugin, draft)
                        logger.info(f"Explicitly saved draft for {phone}/{plugin}: {draft}")
                
            msg = "Your draft application has been saved successfully. You can resume it anytime by saying 'Continue my application'!"
            audio = text_to_speech(msg, user_language)
            return jsonify({"response": msg, "lang": user_language, "mode": "faq", "audio": audio})

        if intent == "end_conversation":
            _clear_pending_interruption(phone)
            msg = "Goodbye! Have a great day!"
            audio = text_to_speech(msg, user_language)
            return jsonify({"response": msg, "lang": user_language, "mode": "faq", "audio": audio})

        # 4. Global Dynamic Multi-Draft Resume & Switcher
        if intent == "draft_resume":
            from draft_switcher import MultiDraftSwitcher
            
            switcher_res = MultiDraftSwitcher.inspect_and_render_switcher(phone, user_input=user_input, user_language=user_language)
            
            if switcher_res["has_drafts"]:
                msg = switcher_res["menu"]
                
                # Single Draft Case: Show summary & Continue/Cancel options
                if switcher_res["count"] == 1:
                    single_draft = switcher_res.get("single_draft") or switcher_res["drafts"][0]
                    plugin = single_draft.get("plugin_name", "adv_booking")
                    draft_data = single_draft.get("draft_data", {})
                    summary = format_draft_summary(draft_data, plugin)
                    msg = f"{summary}\n\nWhat would you like to do?"
                    _set_pending_interruption(phone, {"plugin": plugin, "status": "awaiting_resume"})
                    audio = text_to_speech(msg, user_language)
                    return jsonify({
                        "response": msg, "lang": user_language, "mode": "agent_active", "audio": audio,
                        "input_type": "choice", "options": ["Continue Application", "Cancel Draft"], "show_button": True
                    })
                else:
                    # Multiple Drafts Case (> 1): Display Dynamic Switcher Menu
                    _set_multi_draft_pending(phone, switcher_res["drafts"])
                    logger.info(f"[DraftSwitcher] Stored {len(switcher_res['drafts'])} drafts in Redis for {phone}")
                    audio = text_to_speech(msg, user_language)
                    return jsonify({
                        "response": msg, "lang": user_language, "mode": "agent_active", "audio": audio,
                        "input_type": "choice",
                        "options": switcher_res.get("options") or [f"Option {i+1}" for i in range(switcher_res["count"])] + ["Start New Service Request"],
                        "show_button": True
                    })
            else:
                msg = "I couldn't find any saved drafts for your account."
                audio = text_to_speech(msg, user_language)
                return jsonify({"response": msg, "lang": user_language, "mode": "faq", "audio": audio})
                
        if intent in ["draft_cancel", "draft_cancel_application", "draft_delete"]:
            pending = _get_pending_interruption(phone)
            _clear_pending_interruption(phone)
            _clear_multi_draft_pending(phone)
            
            from database import clear_short_term_memory
            from memory_manager import MemoryManager
            from draft_switcher import _SERVICES_MAP
            
            clear_short_term_memory(phone)
            
            target_plugins = []
            if "grievance" in ui_lower or "complaint" in ui_lower or "shikayat" in ui_lower:
                target_plugins = ["grievance"]
            elif "booking" in ui_lower or "advertisement" in ui_lower or "adv" in ui_lower or "ad" in ui_lower:
                target_plugins = ["adv_booking"]
            else:
                digits = re.findall(r'\d+', ui_lower)
                all_current = MemoryManager.get_all_draft_states(phone)
                if digits and all_current:
                    num = int(digits[0]) - 1
                    if 0 <= num < len(all_current):
                        target_plugins = [all_current[num].get("plugin_name")]
                elif pending and pending.get("plugin"):
                    target_plugins = [pending["plugin"]]
                elif active_plugin and ("my drafts" not in ui_lower and "all" not in ui_lower and "drafts" not in ui_lower):
                    target_plugins = [active_plugin]
                else:
                    target_plugins = list(workflows.keys())

            is_delete_all = set(target_plugins) == set(workflows.keys()) or any(w in ui_lower for w in ["all", "drafts", "saare", "sab"])
            if is_delete_all:
                MemoryManager.delete_draft_state(phone)
                for wf in workflows.keys():
                    if wf in workflows:
                        config_update = {"configurable": {"thread_id": phone if (phone and phone != "default") else session_id}}
                        draft_key = "draft_booking" if wf == "adv_booking" else "draft_grievance"
                        empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if wf == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                        workflows[wf].update_state(config_update, {draft_key: empty_draft, "missing_fields": []})
                        try:
                            process_user_message("[CANCEL_DRAFT]", phone, session_id, target_workflow=wf)
                        except Exception as e:
                            logger.warning(f"[DraftDelete] Failed running [CANCEL_DRAFT] on {wf}: {e}")
                logger.info(f"[DraftDelete] Cleared all drafts for phone={phone}")
                msg = (
                    "Aapke sabhi saved drafts safaltapoorvak delete kar diye gaye hain."
                    if user_language == 'hi' else
                    "Your saved drafts have been deleted successfully."
                )
            else:
                for wf in target_plugins:
                    MemoryManager.delete_draft_state(phone, wf)
                    if wf in workflows:
                        config_update = {"configurable": {"thread_id": phone if (phone and phone != "default") else session_id}}
                        draft_key = "draft_booking" if wf == "adv_booking" else "draft_grievance"
                        empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if wf == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                        workflows[wf].update_state(config_update, {draft_key: empty_draft, "missing_fields": []})
                        try:
                            process_user_message("[CANCEL_DRAFT]", phone, session_id, target_workflow=wf)
                        except Exception as e:
                            logger.warning(f"[DraftDelete] Failed running [CANCEL_DRAFT] on {wf}: {e}")
                
                remaining = MemoryManager.get_all_draft_states(phone)
                plugin_display = _SERVICES_MAP.get(target_plugins[0], {}).get("name", target_plugins[0].replace('_', ' ').title()) if target_plugins else "Draft"
                logger.info(f"[DraftDelete] Cleared draft '{target_plugins}' for phone={phone}, remaining={len(remaining)}")
                
                if user_language == 'hi':
                    if remaining:
                        msg = f"Aapka **{plugin_display}** draft delete kar diya gaya hai. Aapke paas abhi {len(remaining)} saved draft(s) baaki hain."
                    else:
                        msg = f"Aapka **{plugin_display}** draft delete kar diya gaya hai."
                else:
                    if remaining:
                        msg = f"Your **{plugin_display}** draft has been deleted successfully. You have {len(remaining)} saved draft(s) remaining."
                    else:
                        msg = f"Your **{plugin_display}** draft has been deleted successfully."

            audio = text_to_speech(msg, user_language)
            return jsonify({"response": msg, "lang": user_language, "mode": "faq", "audio": audio})

        # 5. Handle Citizen Profile / Identity Request
        if intent == "profile" or is_profile_query:
            logger.info(f"[Profile] Handling citizen profile request for phone={phone}")
            if active_plugin and active_plugin in workflows:
                state = workflows[active_plugin].get_state(config)
                if state and state.values:
                    draft_key = "draft_booking" if active_plugin == "adv_booking" else "draft_grievance"
                    draft = state.values.get(draft_key) or {}
                    if draft and any(v for k, v in draft.items() if not k.startswith("_") and v is not None):
                        from memory_manager import MemoryManager
                        MemoryManager.save_draft_state(phone, active_plugin, draft)
                        logger.info(f"[AutoSave] Saved draft for {phone}/{active_plugin} before showing profile")
                    empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if active_plugin == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                    workflows[active_plugin].update_state(config, {draft_key: empty_draft, "missing_fields": []})

            profile_ans = retrieve_document(user_input, user_language, history, session_id=session_id)
            audio = text_to_speech(profile_ans, user_language)
            return jsonify({
                "response": profile_ans,
                "lang": user_language,
                "mode": "faq",
                "audio": audio,
                "input_type": "text",
                "options": []
            })

        target_wf = plugin_intent or active_plugin
        if target_wf and target_wf in workflows:
            if not is_authenticated:
                logger.info(f"[Auth] Action requires authentication but user is not logged in (phone={phone_anchor}). Prompting login in UPYOG.")
                msg = (
                    "शिकायत दर्ज करने या विज्ञापन बुकिंग के लिए, कृपया पहले लॉगिन करें। बाईं ओर के साइडबार में नीचे जाएं और **Login** विकल्प पर क्लिक करें, मोबाइल नंबर भरें और OTP दर्ज करें।"
                    if user_language == 'hi' else
                    "To file a complaint or create an advertisement booking, please log in first.")
                audio = text_to_speech(msg, user_language)
                return jsonify({
                    "response": msg,
                    "lang": user_language,
                    "mode": "auth_required",
                    "auth_required": True,
                    "audio": audio
                })

            logger.info(f"Routing to dynamic plugin: {target_wf}")
            agent_res = process_user_message(user_input, phone, session_id, target_workflow=target_wf)
            resp_content = agent_res.get("response", "")

            if user_language == 'en' and any('ऀ' <= c <= 'ॿ' for c in resp_content):
                logger.info(f"[Workflow Lang] Output contained Devanagari for English session — translating to English")
                trans = translate_text(resp_content, "hi", "en")
                if trans and len(trans.strip()) > 0:
                    resp_content = trans
            elif user_language == 'hi' and not any('ऀ' <= c <= 'ॿ' for c in resp_content):
                logger.info(f"[Workflow Lang] Output contained English for Hindi session — translating to Hindi")
                trans = translate_text(resp_content, "en", "hi")
                if trans and len(trans.strip()) > 0:
                    resp_content = trans

            audio = text_to_speech(resp_content, user_language)
            return jsonify({
                "response": resp_content,
                "messages": agent_res.get("messages_list", []),
                "lang": user_language,
                "mode": "agent_active",
                "audio": audio,
                "input_type": agent_res.get("input_type", "text"),
                "options": agent_res.get("options", []),
                "min_date": agent_res.get("min_date"),
                "field": agent_res.get("field"),
                "show_button": agent_res.get("show_button"),
                "redirect_url": agent_res.get("redirect_url")
            })

        # PATH D: Normal RAG flow (intent is "faq")
        in_domain, reason = is_in_domain(user_input)
        if not in_domain and reason == "out_of_domain":
            message = get_rejection_message("out_of_domain", user_language)
            audio_output = text_to_speech(message, user_language)
            logger.info(f"[CHAT FLOW] Domain rejected (faq path): {reason}")
            return jsonify({
                "response": message,
                "lang": user_language,
                "mode": "rejected",
                "reason": reason,
                "audio": audio_output
            })

        if not in_domain and reason == "too_short":
            message = get_rejection_message("too_short", user_language)
            audio_output = text_to_speech(message, user_language)
            logger.info(f"[CHAT FLOW] Query rejected because too short: {reason}")
            return jsonify({
                "response": message,
                "lang": user_language,
                "mode": "clarify",
                "audio": audio_output
            })

        response_text = retrieve_document(user_input, user_language, history, session_id=session_id)

        if response_text and ("शिकायत" in response_text or "grievance" in response_text.lower() or "एक शिकायत" in response_text):
            audio_output = text_to_speech(response_text, user_language)
            logger.info("[CHAT FLOW] Fallback response contains grievance wording — setting mode to grievance_offered")
            return jsonify({
                "response": response_text,
                "lang": user_language,
                "mode": "grievance_offered",
                "audio": audio_output
            })

        response_text = re.sub(r'\bUpyog\b', 'UPYOG', response_text, flags=re.IGNORECASE)
        audio_output = text_to_speech(response_text, user_language)
        logger.info(f"[CHAT FLOW] Successfully generated final FAQ response (length: {len(response_text)} chars)")

        return jsonify({
            "response": response_text,
            "lang": user_language,
            "detected_script": detected_script,
            "mode": "faq",
            "audio": audio_output
        }), 200

    except Exception as e:
        err_str = str(e).lower()
        logger.error(f"[ENDPOINT /chat ERROR] Exception: {e}", exc_info=True)
        is_hi = ('user_language' in locals() and user_language == "hi")

        if any(w in err_str for w in ["rate_limit", "429", "token", "tpm", "quota", "too many requests"]):
            fallback_msg = (
                "एआई सहायक की टोकन सीमा कुछ समय के लिए पूरी हो गई है। कृपया थोड़ी देर प्रतीक्षा करें और संक्षिप्त प्रश्न पूछें।"
                if is_hi else
                "The AI assistant has temporarily reached its message token limit. Please wait a moment and try again with a shorter question."
            )
            mode = "rate_limit"
        elif any(w in err_str for w in ["401", "unauthorized", "session", "permissionerror", "invalid access token", "token expired"]):
            fallback_msg = (
                "आपका लॉगिन सत्र समाप्त हो गया है। कृपया जारी रखने के लिए ऊपर दाईं ओर लॉगिन बटन से पुनः लॉगिन करें।"
                if is_hi else
                "Your login session has expired. Please log in again using your registered mobile number via the Login button to continue."
            )
            mode = "auth_required"
        elif any(w in err_str for w in ["context_length", "maximum context"]):
            fallback_msg = (
                "बातचीत की लंबाई सीमा से अधिक हो गई है। कृपया एक नया प्रश्न पूछें।"
                if is_hi else
                "This conversation has exceeded the maximum length. Please ask a concise question or start a fresh query."
            )
            mode = "context_limit"
        else:
            fallback_msg = (
                "क्षमा करें, सर्वर से संपर्क नहीं हो पा रहा है। कृपया थोड़ी देर बाद पुनः प्रयास करें या सहायता केंद्र से संपर्क करें।"
                if is_hi else
                "I am currently experiencing a temporary server issue. Please try again in a moment or contact the municipal helpdesk."
            )
            mode = "error"

        audio_output = text_to_speech(fallback_msg, user_language if 'user_language' in locals() else "en")
        return jsonify({
            "response": fallback_msg,
            "lang": user_language if 'user_language' in locals() else "en",
            "mode": mode,
            "audio": audio_output
        }), 200


@chat_bp.after_app_request
def log_chat_to_redis(response):
    if not request.path.endswith("/chat"):
        return response
        
    try:
        if response.status_code == 200:
            req_data = request.get_json(silent=True) or {}
            session_id = req_data.get("session_id")
            user_input = req_data.get("query", "").strip()
            
            file_name = req_data.get("file_name")
            if not user_input and file_name:
                user_input = json.dumps({"document": file_name})
                
            res_data = response.get_json(silent=True) or {}
            response_text = res_data.get("response", "").strip()
            
            if session_id and user_input and response_text:
                from services.user_service import extract_phone_from_session
                phone_anchor = extract_phone_from_session(session_id)
                if phone_anchor != "default":
                    from database import get_chat_history, save_chat_history
                    redis_chat = get_chat_history(phone_anchor)
                    if not redis_chat or redis_chat[-1].get("content") != response_text or redis_chat[-2].get("content") != user_input:
                        redis_chat.append({"role": "user", "content": user_input})
                        redis_chat.append({"role": "assistant", "content": response_text})
                        
                        if len(redis_chat) >= 20:
                            messages_to_summarize = redis_chat[:10]
                            redis_chat = redis_chat[10:]
                            
                            threading.Thread(target=summarize_and_store_memory, args=(phone_anchor, messages_to_summarize)).start()
                            logger.info(f"Summarization Triggered for {phone_anchor}. Truncating Redis chat.")
                            
                        save_chat_history(phone_anchor, redis_chat)
    except Exception as e:
        logger.error(f"Error logging chat to Redis after_request: {e}")
        
    return response


"""
Streaming SSE endpoint — two route aliases:
  /stream                  → direct local access
  /upyog-voice-bot/stream  → production via niautt EKS ingress
GET requests return a health check response for Kubernetes liveness probes.
"""
@chat_bp.route("/stream", methods=["GET", "POST"])
@chat_bp.route("/upyog-voice-bot/stream", methods=["GET", "POST"])
@chat_bp.route("/upyog-voice/stream", methods=["GET", "POST"])
def stream():
    if request.method == "GET":
        logger.info("[ENDPOINT /stream GET] Health check ping")
        return jsonify({"status": "ok", "message": "UPYOG Voice Bot Stream Endpoint"}), 200

    import services.rag_service as rag_service
    from services.rag_service import retrieve_document_stream
    from services.voice_service import stop_generation
    from services.intent_service import detect_language_per_turn
    from services.user_service import extract_phone_from_session

    try:
        if not rag_service.resources_ready_event.is_set() or any(x is None for x in [rag_service.model, rag_service.data, rag_service.index]):
            rag_service.resources_ready_event.wait(timeout=10)
            if any(x is None for x in [rag_service.model, rag_service.data, rag_service.index]):
                rag_service.load_resources()
            if any(x is None for x in [rag_service.model, rag_service.data, rag_service.index]):
                return Response("data: {\"error\": \"Loading resources...\"}\n\n", mimetype='text/event-stream'), 503

        user_data = request.json or {}
        user_input = user_data.get("query", "")
        session_id = user_data.get("session_id", "default")
        phone_anchor = extract_phone_from_session(session_id)
        from database import get_chat_history
        history = get_chat_history(phone_anchor) if phone_anchor != "default" else []

        user_language, detected_script = detect_language_per_turn(user_input)
        logger.info(f"[ENDPOINT /stream POST] Stream request: '{user_input}' -> lang={user_language} (script: {detected_script})")

        return Response(retrieve_document_stream(user_input, user_language, history, phone_anchor=phone_anchor), mimetype='text/event-stream')

    except Exception as e:
        logger.error(f"[ENDPOINT /stream ERROR] Exception: {e}")
        return Response(f"data: {json.dumps({'type': 'error', 'error': str(e)})}\n\n", mimetype='text/event-stream')


"""
Stop endpoint — called when the user interrupts (barges in) while the bot is speaking.
Two route aliases:
  /stop                  → direct local access
  /upyog-voice-bot/stop  → production via niautt EKS ingress
"""
@chat_bp.route("/stop", methods=["POST"])
@chat_bp.route("/upyog-voice-bot/stop", methods=["POST"])
@chat_bp.route("/upyog-voice/stop", methods=["POST"])
def stop():
    """Stop endpoint - called when user barges in."""
    from services.voice_service import stop_generation
    stop_generation.set()
    logger.info("[ENDPOINT /stop POST] Stop signal set — interrupting generation thread")
    return jsonify({"status": "stopped"}), 200


def register_chat_routes(app):
    """Registers chat Blueprint with the Flask app."""
    app.register_blueprint(chat_bp)
