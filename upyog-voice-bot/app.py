""""
UPYOG Voice Assistant v2 - Telephone Call Model
=================================================
A new project that adapts the existing chatbot with continuous listening,
barge-in support, and streaming responses.

Port: 8090 (Original project runs on 8080)
"""

from flask import Flask, request, jsonify, send_from_directory, Response
from flask_cors import CORS
from sentence_transformers import SentenceTransformer 
import faiss 
import numpy as np
import pandas as pd 
import os 
import time 
import requests
from langdetect import detect, DetectorFactory
import logging
from functools import lru_cache
import threading
import json
import re
from database import get_any_user_profile_name, save_user_profile_name



try:
    from groq import Groq
except ImportError:
    Groq = None

from dotenv import load_dotenv
load_dotenv()


GROQ_API_KEY = os.environ.get("GROQ_API_KEY")
GROQ_MODEL = os.environ.get("GROQ_MODEL", "openai/gpt-oss-20b")

# Set up logging
logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] [%(name)s:%(lineno)d] %(message)s"
)
logger = logging.getLogger(__name__)
# ============== HELPER: Extract Phone Number from Session ==============

def extract_phone_from_session(session_id: str) -> str:
    """
    Extract phone number from session_id format.
    Handles: "user_9876543210" or "user-9876543210"
    Returns: "9876543210" or "default" if not found
    """
    if not session_id:
        return "default"
   
    # Normalize: convert hyphens to underscores
    session_clean = session_id.replace("-", "_")
    parts = session_clean.split("_")
   
    # Check if second part is a 10-digit number
    if len(parts) > 1:
        potential_phone = parts[1]
        if potential_phone.isdigit() and len(potential_phone) == 10:
            logger.info(f"Extracted phone {potential_phone} from session {session_id}")
            return potential_phone
   
    logger.warning(f"Could not extract phone from session_id: {session_id}")
    return "default"

# ============== END HELPER ==============

_USER_PROFILE_CACHE = {}

def save_user_profile_info(phone_anchor: str, user_info: dict) -> bool:
    try:
        _USER_PROFILE_CACHE[str(phone_anchor)] = dict(user_info)
        from database import r_client
        r_client.set(f"user_profile_info:{phone_anchor}", json.dumps(user_info))
        return True
    except Exception as e:
        logger.warning(f"Error saving user profile info: {e}")
        return True

def get_user_profile_info(phone_anchor: str) -> dict:
    # Check in-memory cache first
    if str(phone_anchor) in _USER_PROFILE_CACHE:
        return _USER_PROFILE_CACHE[str(phone_anchor)]
    try:
        from database import r_client
        data = r_client.get(f"user_profile_info:{phone_anchor}")
        if data:
            parsed = json.loads(data.decode('utf-8') if isinstance(data, bytes) else data)
            _USER_PROFILE_CACHE[str(phone_anchor)] = parsed
            return parsed
    except Exception as e:
        logger.warning(f"Error getting user profile info: {e}")
    return _USER_PROFILE_CACHE.get(str(phone_anchor), {})

# Ensure consistent language detection
DetectorFactory.seed = 0

# Disable parallelism for tokenizers
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Global state for stop flag (barge-in)
stop_generation = threading.Event()

# Groq client - initialized lazily
groq_client = None

# Bhashini API details
BHASHINI_URL = "https://dhruva-api.bhashini.gov.in/services/inference/pipeline"
BHASHINI_HEADERS = {
    "Content-Type": "application/json",
    "ulcaApiKey": os.environ.get("BHASHINI_API_KEY"),
    "userID": os.environ.get("BHASHINI_USER_ID"),
    "Authorization": os.environ.get("BHASHINI_AUTH")
}
TRANSLATION_SERVICE_ID = "ai4bharat/indictrans-v2-all-gpu--t4"
TTS_SERVICE_ID = "ai4bharat/indic-tts-coqui-indo_aryan-gpu--t4"
TTS_SERVICE_ID_DRAVIDIAN = "ai4bharat/indic-tts-coqui-dravidian-gpu--t4"
TTS_SERVICE_ID_MISC = "ai4bharat/indic-tts-coqui-misc-gpu--t4"

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Global variables for lazy loading
model = None
data = None
index = None
frs_data = None
frs_index = None
prompt_embeddings = None
is_loading = False
load_lock = threading.Lock()

# FAISS similarity threshold (same as existing project)
FAISS_THRESHOLD = 1.08
EMBEDDING_MODEL = 'all-mpnet-base-v2'

# Loads all required AI models and system resources into memory on startup
def load_resources():
    global model, data, index, prompt_embeddings, frs_data, frs_index, is_loading

    with load_lock:
        if is_loading:
            return

        is_loading = True
        try:
            current_dir = os.path.dirname(os.path.abspath(__file__))

            # Load FAQ data
            data_path = os.path.join(current_dir, 'data', 'UpyogFAQ.csv')
            data = pd.read_csv(data_path)
            logger.info(f"FAQ data loaded from {data_path}")

            # Initialize SentenceTransformer model and generate embeddings
            model = SentenceTransformer(EMBEDDING_MODEL)
            prompt_embeddings = model.encode(data['prompt'].tolist())
            logger.info("Embeddings generated successfully.")

            # Initialize FAISS index and add embeddings
            dimension = prompt_embeddings.shape[1]
            index = faiss.IndexFlatL2(dimension)
            index.add(prompt_embeddings.astype(np.float32))
            logger.info("FAQ FAISS index initialized.")

            # Load FRS Knowledge Base
            frs_path = os.path.join(current_dir, 'data', 'frs_smart_faq.csv')
            frs_idx_path = os.path.join(current_dir, 'data', 'frs_smart_index.faiss')
            if os.path.exists(frs_path) and os.path.exists(frs_idx_path):
                frs_data = pd.read_csv(frs_path)
                frs_index = faiss.read_index(frs_idx_path)
                logger.info(f"FRS Knowledge Base loaded with {len(frs_data)} specifications.")
            else:
                logger.warning("FRS Knowledge Base NOT found.")

            logger.info("All resources loaded successfully.")

        except Exception as e:
            logger.error(f"Error loading resources: {e}")
            raise
        finally:
            is_loading = False

# Start loading resources in background
threading.Thread(target=load_resources, daemon=True).start()

# ============== DOMAIN FILTERING TO PREVENT HALLUCINATION ==============

# System prompt for Groq - defines what the bot can answer
SYSTEM_PROMPT = """You are UPYOG Assistant — an AI helper exclusively for the
UPYOG platform and NUDM (National Urban Digital Mission) government services.

YOUR KNOWLEDGE DOMAIN (you may ONLY answer about these):
- UPYOG platform features, modules, and services
- NUDM mission, goals, and implementation
- Urban Local Body (ULB) services: Property Tax, Trade License, Fire NOC,
  Water & Sewerage, Birth & Death certificates, Building Plan Approval,
  Waste Management, GIS Services, Grievance Redressal, Asset Management,
  Community Hall Booking, Street Vendors, Livelihood Services, Works Management,
  Solid Waste Management, Door to Door Services, and all other UPYOG modules
- How to apply for, track, or understand any of these services
- Document requirements for any of these services
- Fees, timelines, and processes for any of these services

STRICT RULES — follow these without exception:

RULE 1 — OUT OF DOMAIN REJECTION:
If the user asks about ANYTHING not in your knowledge domain above
(fitness, cooking, general knowledge, politics, entertainment, other software,
health advice, legal advice unrelated to ULB services, etc.)
you MUST respond with ONLY this (in the user's language):
  English: "I can only help with UPYOG and NUDM related queries.
            Please ask me about government urban services."
  Hindi:   "मैं केवल UPYOG और NUDM से संबंधित प्रश्नों में सहायता कर सकता हूँ।
            कृपया शहरी सेवाओं के बारे में पूछें।"
Do NOT attempt to answer. Do NOT say "I think" or "perhaps". Just redirect.

RULE 2 — FRAGMENTED INPUT HANDLING:
If the user's input is incomplete, fragmented, or makes no clear sense
(e.g. "ka Labh uthana hai", "kaise", "what about the", "aur phir"),
do NOT guess what they mean and do NOT answer a random topic.
Instead ask for clarification:
  English: "I didn't catch that completely. Could you please repeat your question?"
  Hindi:   "मैं आपका प्रश्न पूरी तरह समझ नहीं पाया। क्या आप दोबारा पूछ सकते हैं?"

RULE 3 — KNOWLEDGE BASE FIRST:
Always check the retrieved context from the knowledge base first.
If the retrieved context has a similarity score above threshold, reject.
Do NOT add information from your general training data.
Do NOT make up fees, timelines, document names, or process steps.
If the knowledge base does not have the answer, say so honestly.

RULE 4 — TRANSACTIONAL LIMITATION:
- You can ONLY execute/book/create transactions for "Advertisement Booking".
- If the user asks you to apply, register, pay, or book for "Trade License" or "Property Tax", you MUST state directly and professionally:
  "Currently, UPYOG AI can only execute bookings for Advertisements. I cannot process or apply for Property Tax payments or Trade Licenses directly. However, I can guide you on the steps, fees, or documents required for them. Please let me know if you would like me to explain the guidelines or document requirements!"
  (In Hindi: "वर्तमान में, UPYOG AI केवल विज्ञापन बुकिंग ही कर सकता है। मैं सीधे संपत्ति कर भुगतान या व्यापार लाइसेंस के लिए आवेदन नहीं कर सकता। हालांकि, मैं आपको उनके लिए आवश्यक चरणों, शुल्क या दस्तावेजों के बारे में मार्गदर्शन कर सकता हूँ। कृपया मुझे बताएं कि क्या आप चाहते हैं कि मैं दिशा-निर्देश या दस्तावेज़ आवश्यकताओं की व्याख्या करूँ!")

RULE 4 — NO HALLUCINATION:
Never invent information. If you are not sure, say:
  English: "I don't have specific information about that in my knowledge base.
            Please contact your nearest ULB office for accurate details."
  Hindi:   "मेरे पास इस विषय में सटीक जानकारी नहीं है।
            सटीक जानकारी के लिए कृपया अपने नजदीकी ULB कार्यालय से संपर्क करें।"

RULE 5 — LANGUAGE MIRROR:
Always reply in the same language the user used.
If Hindi → reply in pure Devanagari Hindi.
If English → reply in English.
Never mix scripts.

RULE 6 — PROFESSIONAL TONE AND FORMAL ADDRESS:
Maintain a formal, polite, and professional tone at all times as an official government services AI assistant.
STRICT RULE: NEVER use informal, overly familiar, or colloquial Hindi terms of address such as "दीदी" (Didi), "काकी" (Kaki), "बेटा" (Beta), "भैया" (Bhaiya), "चाचा" (Chacha), "अंकल" (Uncle), "आंटी" (Aunty), etc.
Always address the citizen respectfully using formal language (e.g. "आप") and clean professional greetings (e.g. "नमस्ते", "नमस्कार", "Hello") without adding informal terms of address.
"""

# Keywords that are clearly out of domain — reject immediately
OUT_OF_DOMAIN_KEYWORDS = [
    # fitness / health
    "exercise", "workout", "gym", "yoga", "diet", "weight loss", "calories",
    "muscle", "leg raise", "pushup", "push-up", "running", "jogging", "meditation",
    "fitness", "health", "doctor", "medicine", "pain", "body", "weight",
    # food
    "recipe", "cook", "cooking", "khana", "restaurant", "food delivery", "biryani",
    "pizza", "burger", "sabzi", "dal", "roti",
    # entertainment
    "movie", "film", "song", "music", "cricket", "ipl", "match", "game", "gaming",
    "netflix", "youtube", "serial", "actor", "actress", "bollywood", "hollywood",
    # finance (non-ULB)
    "stock", "share market", "crypto", "bitcoin", "mutual fund", "gst rate", "income tax return",
    "loan", "credit", "emi", "interest rate", "bank", "sbi", "hdfc",
    # general knowledge
    "history of india", "capital of", "president of", "prime minister", "election",
    "weather", "news", "politics", "party", "vote",
    # other platforms/software
    "google", "amazon", "flipkart", "zomato", "swiggy", "uber", "ola", "whatsapp",
    "facebook", "instagram", "twitter", "chatgpt", "ai chatbot",
    # personal questions
    "who are you", "tell me about yourself", "your name", "who made you",
    # other unrelated
    "astrology", "horoscope", "love", "marriage", "career", "job", "salary"
]

# UPYOG-related keywords
UPYOG_KEYWORDS = [
    "upyog", "nudm", "ulb", "urban local body", "municipal", "municipality",
    "property tax", "trade license", "fire noc", "noc",
    "birth", "death", "certificate", "registration",
    "grievance", "complaint", "shikayat", "pgr", "redressal",
    "water", "sewerage", "drain", "sewage",
    "building plan", "construction", "edcr", "approval",
    "waste", "garbage", "safai", "swachh", "sanitation",
    "vendor", "hawker", "street vendor", "hawker",
    "community hall", "venue", "booking",
    "asset", "inventory", "works", "maintenance",
    "solid waste", "door to door", "collection",
    "gis", "map", "geospatial", "property",
    "livelihood", "employment", "skill",
    "challenge", "innovation", "solution",
    "mohua", "niua", "national urban digital mission",
    # Hindi terms
    "संपत्ति कर", "व्यापार लाइसेंस", "जन्म", "मृत्यु", "प्रमाण पत्र",
    "शिकायत", "जल", "सीवरेज", "कचरा", "सफाई", "भवन", "नक्शा",
    "नगरपालिका", "उपयोग", "नगर सेवाएं"
]

# Only hard-block things that are DEFINITELY not UPYOG related
HARD_BLOCK_TOPICS = [
    # entertainment
    'cricket', 'ipl', 'bollywood', 'movie', 'film', 'song', 'actor',
    'netflix', 'hotstar', 'youtube', 'web series', 'serial',
    # food
    'recipe', 'biryani', 'restaurant', 'zomato', 'swiggy', 'pizza',
    'dosa', 'samosa', 'chai', 'coffee',
    # finance (non-ULB)
    'stock market', 'share bazaar', 'crypto', 'bitcoin', 'mutual fund',
    'income tax', 'gst return', 'itr filing', 'nps', 'pf',
    # fitness
    'exercise', 'gym', 'yoga', 'diet', 'weight loss', 'leg raise',
    'workout', 'fitness',
    # other
    'weather forecast', 'horoscope', 'astrology', 'love', 'relationship',
    'jod', 'pyaar', 'shaadi',
]

# Only blocks things that are definitely not UPYOG related (strict blocklist)
def is_hard_blocked(query: str) -> bool:
    q = query.lower()
    blocked = any(topic in q for topic in HARD_BLOCK_TOPICS)
    if blocked:
        matched = [t for t in HARD_BLOCK_TOPICS if t in q]
        logger.info(f"[DOMAIN CHECK] Query '{query}' matched hard-block topics: {matched}")
    else:
        logger.debug(f"[DOMAIN CHECK] Query '{query}' passed hard-block check.")
    return blocked

# Legacy wrapper for backward compatibility with older domain checks
def is_in_domain(query: str) -> tuple:
    if is_hard_blocked(query):
        return False, "out_of_domain"
    return True, "ok"

def get_rejection_message(reason: str, lang: str) -> str:
    logger.info(f"[REJECTION] Generating rejection message for reason='{reason}', lang='{lang}'")
    if lang == 'hi':
        return "मैं केवल UPYOG और शहरी सरकारी सेवाओं के बारे में सहायता कर सकता हूँ। कृपया UPYOG सेवाओं के बारे में पूछें।"
    return "I can only help with UPYOG and urban government services. Please ask about UPYOG services."

# ============== DYNAMIC PER-TURN LANGUAGE DETECTION ==============

# Phonetic Hindi words in Roman script
HINDI_PHONETIC_WORDS = {
    'kya', 'kaise', 'kahan', 'kab', 'kyun', 'kaun', 'kitna', 'kitne', 'kitni',
    'hai', 'hain', 'tha', 'thi', 'the', 'hoga', 'hogi', 'hoge', 'honge', 'ho', 'hai',
    'mujhe', 'aapko', 'humko', 'tumhe', 'unhe', 'apna', 'mera', 'meri', 'mere', 'tera', 'teri', 'tere',
    'aur', 'ya', 'lekin', 'toh', 'ki', 'ke', 'ka', 'ko', 'se', 'par', 'mein', 'me', 'mai',
    'nahi', 'nahin', 'mat', 'bilkul', 'haan', 'theek', 'accha', 'sahi', 'galat', 'kuch', 'kuchh',
    'batao', 'bataye', 'bataiye', 'samjhao', 'dikhao', 'chahiye', 'milega', 'milegi',
    'karo', 'karein', 'karega', 'karegi', 'dijiye', 'lijiye', 'dekhiye', 'suniye',
    'paisa', 'paise', 'rupaye', 'mahina', 'saal', 'din', 'ghanta', 'minute',
    'ghar', 'daftar', 'office', 'kaam', 'kam',
    'naam', 'number', 'document', 'form', 'form mein', 'apply', 'karein', 'karna',
    'bhai', 'yaar', 'sir', 'madam', 'dada', 'babu',
    'aap', 'tum', 'hum', 'woh', 'yeh', 'ye', 'vo', 'unka', 'iska', 'uska',
    'abhi', 'phir', 'fir', 'kabhi', 'hamesha', 'kal', 'aaj', 'raat', 'din',
    'ek', 'do', 'teen', 'char', 'paanch', 'chalo', 'chal', 'jao', 'aao',
    'dekh', 'sun', 'bolo', 'likh', 'padh', 'samajh',
    'sirf', 'bas', 'hi', 'bhi', 'to', 'hi', 'to',
    'kaafi', 'zyada', 'kam', 'chotu', 'bada', 'chhota',
    'ke', 'ka', 'ki', 'ko', 'se', 'me', 'mein', 'pe', 'ka'
}

# Common Englis# English words commonly transliterated into Devanagari
ENGLISH_IN_DEVANAGARI = [
    'व्हाट', 'वॉट', 'हाउ', 'व्हेन', 'व्हेयर', 'वेयर', 'व्हाई', 'हू', 'विच',
    'इज', 'आर', 'वॉज', 'वेयर', 'हैव', 'हैज', 'डू', 'डज',
    'कैन', 'कुड', 'विल', 'वुड', 'शुड', 'मस्ट',
    'द', 'थे', 'ए', 'एन', 'इन', 'ऑन', 'एट', 'बाय', 'फॉर',
    'ऑफ', 'टू', 'फ्रॉम', 'विद', 'अबाउट', 'ई', 'पे', 'पेमेंट', 'फी', 'फीस',
    'नंबर', 'टोटल', 'लिस्ट', 'प्रोसेस', 'स्टेटस', 'ट्रेड', 'लाइसेंस', 'प्रॉपर्टी', 'टैक्स',
    'एमओयू', 'एनयूएलएम', 'यूएलबी', 'एनयूडीएम',
    'यूज़र', 'सर्च', 'सबमिट', 'अप्लाई', 'सर्विस', 'स्टेट', 'पोर्टल', 'अकाउंट'
]

def detect_language(text: str) -> dict:
    if not text or not text.strip():
        result = {'lang': 'en', 'script': 'english', 'search_lang': 'en'}
        logger.debug(f"[LANG DETECT] Empty text provided -> default result: {result}")
        return result

    text = text.strip()
    words = text.split()
    total_alpha = sum(1 for c in text if c.isalpha())

    if total_alpha == 0:
        result = {'lang': 'en', 'script': 'english', 'search_lang': 'en'}
        logger.debug(f"[LANG DETECT] No alpha characters in '{text}' -> result: {result}")
        return result

    # Count Devanagari characters
    devanagari_chars = sum(1 for c in text if 'ऀ' <= c <= 'ॿ')
    devanagari_ratio = devanagari_chars / total_alpha

    if devanagari_ratio > 0.5:
        # Check for transliterated English question words first
        if any(w in text for w in ['व्हाट', 'वॉट', 'हाउ', 'व्हेन', 'वेयर', 'व्हाई', 'हू', 'विच']):
            result = {'lang': 'en', 'script': 'transliterated_english', 'search_lang': 'en'}
            logger.info(f"[LANG DETECT] Devanagari English question word detected in '{text}' -> result: {result}")
            return result

        # Check for pure Hindi words in Devanagari script
        hindi_dev_words = ['है', 'हैं', 'था', 'थी', 'करोगे', 'करो', 'नहीं', 'दो', 'काम', 'खराब', 'चाहिए', 'बताओ', 'बताएं', 'करना', 'करते', 'सकते', 'सकता', 'सकती', 'कृपया', 'भरें', 'भरने', 'करें', 'किसे']
        if any(w in words for w in hindi_dev_words):
            result = {'lang': 'hi', 'script': 'devanagari', 'search_lang': 'hi'}
            logger.info(f"[LANG DETECT] Devanagari Hindi words detected in '{text}' -> result: {result}")
            return result

        english_word_count = sum(1 for w in words if any(eng == w for eng in ENGLISH_IN_DEVANAGARI))
        english_ratio = english_word_count / len(words) if words else 0

        if english_ratio >= 0.3:
            result = {'lang': 'en', 'script': 'transliterated_english', 'search_lang': 'en'}
            logger.info(f"[LANG DETECT] Devanagari English words ratio {english_ratio:.2f} in '{text}' -> result: {result}")
            return result

        result = {'lang': 'hi', 'script': 'devanagari', 'search_lang': 'hi'}
        logger.info(f"[LANG DETECT] Devanagari script detected in '{text}' -> result: {result}")
        return result

    # Roman script — check for Hindi phonetics
    text_lower = text.lower()
    words_lower = re.findall(r'\b\w+\b', text_lower)

    # Distinct Hindi words that strongly indicate Hindi/Hinglish
    distinct_hindi = {
        'kya', 'kaise', 'kahan', 'kyun', 'kaun', 'kab', 'kitna', 'kitni',
        'mujhe', 'aapko', 'mera', 'meri', 'mere', 'humara', 'hamare', 'apna', 'apni',
        'nahin', 'nahi', 'haan', 'theek', 'accha', 'achha',
        'batao', 'chahiye', 'milega', 'karo', 'karein', 'dijiye', 'bataye', 'bataiye',
        'dikhao', 'dikhaye', 'hatao', 'mitado', 'shikayat', 'namaste', 'dhanyawad',
        'samasya', 'paani', 'sadak', 'kachra', 'bijli'
    }

    # Common Hinglish phrases
    hinglish_phrases = [
        r'\bkya\s+hai\b', r'\bkaise\s+kare\b', r'\bkaise\s+karein\b',
        r'\bmujhe\s+', r'\bmera\s+', r'\bmeri\s+', r'\bmere\s+',
        r'\bshikayat\s+darj\b', r'\bpaani\s+ki\b', r'\bkaro\b', r'\bkarein\b',
        r'\bbatao\b', r'\bbataiye\b', r'\bdikhao\b', r'\bchahiye\b',
        r'\btheek\s+hai\b', r'\baapka\s+', r'\baapke\s+'
    ]

    has_phrase = any(re.search(p, text_lower) for p in hinglish_phrases)
    hindi_matches = [w for w in words_lower if w in distinct_hindi]

    if has_phrase or len(hindi_matches) >= 2 or (len(words_lower) <= 3 and len(hindi_matches) >= 1):
        result = {'lang': 'hi', 'script': 'roman_hindi', 'search_lang': 'hi'}
        logger.info(f"[LANG DETECT] Roman Hindi phonetic words matched ({len(hindi_matches)}) in '{text}' -> result: {result}")
        return result

    result = {'lang': 'en', 'script': 'english', 'search_lang': 'en'}
    logger.info(f"[LANG DETECT] Defaulting to English for '{text}' -> result: {result}")
    return result

# Legacy wrapper for backward compatibility to detect language on a per-message basis
def detect_language_per_turn(text: str) -> tuple:
    info = detect_language(text)
    return info['lang'], info['script']

# ============== TRANSLATION ==============

# Translates text using the official Bhashini API with caching for performance
def translate_text_bhashini(text, source_lang, target_lang):
    logger.info(f"[BHASHINI TRANSLATE] Translating {len(text)} chars from {source_lang} to {target_lang}")
    payload = {
        "pipelineTasks": [
            {
                "taskType": "translation",
                "config": {
                    "language": {
                        "sourceLanguage": source_lang,
                        "targetLanguage": target_lang
                    }
                },
                "serviceId": TRANSLATION_SERVICE_ID
            }
        ],
        "inputData": {
            "input": [{"source": text}]
        }
    }

    try:
        response = requests.post(BHASHINI_URL, headers=BHASHINI_HEADERS, json=payload)
        if response.status_code == 200:
            translation_output = response.json()["pipelineResponse"][0]["output"][0]["target"]
            logger.info(f"[BHASHINI TRANSLATE] Success: '{text[:30]}...' -> '{translation_output[:30]}...'")
            return translation_output
        else:
            logger.error(f"[BHASHINI TRANSLATE] Failed with status code {response.status_code}: {response.text}")
            return None
    except Exception as e:
        logger.error(f"[BHASHINI TRANSLATE] Exception: {e}")
        return None

def translate_text(text, source_lang, target_lang):
    if source_lang == target_lang or not text:
        logger.debug(f"[TRANSLATE] Skipping translation since source_lang ({source_lang}) == target_lang ({target_lang})")
        return text
    translated = translate_text_bhashini(text, source_lang, target_lang)
    if translated:
        return translated
    logger.warning(f"[TRANSLATE] Bhashini translation returned None, falling back to original text.")
    return text

# ============== TTS ==============

import asyncio
import tempfile
import base64
import edge_tts

async def generate_edge_tts(text, voice, output_path):
    communicate = edge_tts.Communicate(text, voice)
    await communicate.save(output_path)

def clean_text_for_tts(text: str, language_code: str = "en") -> str:
    """Strip all markdown formatting, headings, bullet marks, emojis and symbols for natural TTS speech."""
    if not text:
        return ""

    # 1. Remove code blocks ```...```
    text = re.sub(r'```[\s\S]*?```', '', text)
    # 2. Inline code `...`
    text = re.sub(r'`([^`]+)`', r'\1', text)
    # 3. Markdown links [text](url) -> text
    text = re.sub(r'\[([^\]]+)\]\([^\)]+\)', r'\1', text)
    # 4. Markdown images ![alt](url) -> ''
    text = re.sub(r'\!\[([^\]]*)\]\([^\)]+\)', '', text)

    # 5. Remove markdown headings (# Heading, ## Heading, ### Heading...)
    text = re.sub(r'(?m)^\s*#{1,6}\s*', '', text)
    # Strip any remaining stray hash characters so TTS never says 'hash'
    text = text.replace('#', '')

    # 6. Remove horizontal rules (---, ***, ___)
    text = re.sub(r'(?m)^\s*[-*_]{3,}\s*$', '', text)

    # 7. Remove blockquotes (> quote)
    text = re.sub(r'(?m)^\s*>\s*', '', text)

    # 8. Remove bold / italic / strikethrough (**, *, __, _, ~~)
    text = re.sub(r'\*{1,3}([^*]+)\*{1,3}', r'\1', text)
    text = re.sub(r'_{1,3}([^_]+)_{1,3}', r'\1', text)
    text = re.sub(r'~~([^~]+)~~', r'\1', text)
    text = text.replace('*', '').replace('~', '')

    # 9. Clean bullet markers at start of lines (- item, * item, + item)
    text = re.sub(r'(?m)^\s*[-*+]\s+', '', text)

    # 10. Replace table pipes | with a comma/space
    text = text.replace('|', ', ')

    # 11. Normalize dashes and bullets
    text = text.replace('•', '').replace('–', '-').replace('—', '-')

    # 12. Strip emojis and special symbols
    text = re.sub(
        r'[\U00002600-\U000027BF]|[\U0001F300-\U0001FAFF]|[\U00002702-\U000027B0]|[\U0000FE00-\U0000FE0F]|[\U0001F000-\U0001F9FF]|‍|️',
        '', text
    )

    # 13. Branding & Pronunciation fixes
    if language_code == "hi":
        text = re.sub(r'\bUPYOG\b', 'उपयोग', text, flags=re.IGNORECASE)
        text = text.replace('Upyog', 'उपयोग')
        text = text.replace('NUDM', 'एन.यू.डी.एम.')
        text = text.replace('MoHUA', 'मोहुआ')
    else:
        text = re.sub(r'\bUPYOG\b', 'Oop-yog', text, flags=re.IGNORECASE)
        text = text.replace('Upyog', 'Oop-yog')
        text = text.replace('NUDM', 'N-U-D-M')
        text = text.replace('MoHUA', 'Mo-hua')

    # 14. Normalize whitespace
    text = re.sub(r'\n{2,}', '\n', text)
    text = re.sub(r'[ \t]+', ' ', text)
    return text.strip()


# Converts AI text to speech audio using Edge-TTS with Bhashini as a fallback
def text_to_speech(text, language_code, gender="female"):
    """Convert text to speech using Edge-TTS with Bhashini fallback."""
    logger.info(f"[TTS GENERATION] Input text length: {len(text)} | Language: '{language_code}' | Gender: '{gender}'")

    # Clean markdown, headers (###, etc.) and apply pronunciation rules
    text = clean_text_for_tts(text, language_code)

    # Ensure script matches language
    if language_code == "en" and any('ऀ' <= c <= 'ॿ' for c in text):
        logger.info("[TTS SCRIPT FIX] Devanagari detected in English TTS text — translating to English")
        text = translate_text(text, "hi", "en")
    elif language_code == "hi" and not any('ऀ' <= c <= 'ॿ' for c in text):
        logger.info("[TTS SCRIPT FIX] Non-Devanagari detected in Hindi TTS text — translating to Hindi")
        text = translate_text(text, "en", "hi")

    logger.info(f"[TTS] Generating TTS for language: {language_code}")

    # Try Edge-TTS first for English/Hindi
    if language_code in ["en", "hi"]:
        voice_map = {
            "en": "en-IN-NeerjaNeural",
            "hi": "hi-IN-MadhurNeural"
        }
        voice = voice_map.get(language_code)
        logger.info(f"[TTS EDGE-TTS] Attempting Edge-TTS with voice '{voice}'")
        try:
            with tempfile.NamedTemporaryFile(delete=False, suffix='.mp3') as temp_audio:
                temp_path = temp_audio.name
            try:
                asyncio.run(generate_edge_tts(text, voice, temp_path))
            except Exception as e:
                logger.error(f"[TTS EDGE-TTS] Edge-TTS generation exception: {e}")
            
            with open(temp_path, "rb") as f:
                raw_audio = f.read()
            os.unlink(temp_path)
            
            if len(raw_audio) > 100:
                logger.info(f"[TTS EDGE-TTS] Successfully generated audio. Payload length: {len(raw_audio)} bytes")
                return base64.b64encode(raw_audio).decode('utf-8')
            else:
                logger.error("[TTS EDGE-TTS] Edge-TTS generated empty or invalid audio file, falling back to Bhashini.")
        except Exception as e:
            logger.error(f"[TTS EDGE-TTS] Edge-TTS failed: {e}. Falling back to Bhashini...")

    # Fallback to Bhashini
    if language_code == "en":
        tts_service_id = TTS_SERVICE_ID_MISC
    elif language_code in ["hi", "mr", "bn", "gu", "pa", "as", "or"]:
        tts_service_id = TTS_SERVICE_ID
    elif language_code in ["kn", "ml", "ta", "te"]:
        tts_service_id = TTS_SERVICE_ID_DRAVIDIAN
    else:
        tts_service_id = TTS_SERVICE_ID_MISC

    logger.info(f"[TTS BHASHINI] Triggering Bhashini TTS with serviceId '{tts_service_id}' for lang '{language_code}'")

    payload = {
        "pipelineTasks": [{"taskType": "tts", "config": {"language": {"sourceLanguage": language_code}, "serviceId": tts_service_id, "gender": gender, "samplingRate": 8000}}],
        "inputData": {"input": [{"source": text}]}
    }

    try:
        response = requests.post(BHASHINI_URL, headers=BHASHINI_HEADERS, json=payload)
        if response.status_code == 200:
            audio_data = response.json()["pipelineResponse"][0]["audio"][0]["audioContent"]
            logger.info(f"[TTS BHASHINI] Successfully generated audio. Payload length: {len(audio_data)} chars")
            return audio_data
        logger.error(f"[TTS BHASHINI] Bhashini status code {response.status_code}: {response.text}")
        return None
    except Exception as e:
        logger.error(f"[TTS BHASHINI] Bhashini TTS Error: {e}")
        return None

# ============== LLM RESPONSE GENERATION ==============

def contains_urdu_script(text):
    return bool(re.compile(r'[؀-ۿ]').search(text))

def get_rag_response(query: str, history: list, lang: str, search_lang: str = None, session_id: str = "default") -> str:
    """
    LLM-first architecture: LLM understands human language, FAISS provides optional context.
    FAISS is NOT a hard gate - if no context found, LLM answers from general knowledge.
    """
    global groq_client

    if search_lang is None:
        search_lang = lang

    # --- Fetch persistent profile values from Redis & in-memory cache ---
    phone_anchor = extract_phone_from_session(session_id)
    user_info = get_user_profile_info(phone_anchor) if phone_anchor != "default" else None
    if not user_info:
        for k, v in _USER_PROFILE_CACHE.items():
            if isinstance(v, dict) and (v.get("mobileNumber") or v.get("name")):
                user_info = v
                break
    
    # --- LOAD USER'S LONG-TERM MEMORY (BOOKINGS) ---
    long_term_bookings_str = ""
    if phone_anchor != "default":
        long_term_bookings_str = ""
    
    # --- LOAD USER'S PERSISTENT CHAT HISTORY (LONG-TERM REDIS MEMORY) ---
    long_term_chat_str = ""
    qdrant_summary_str = ""
    if phone_anchor != "default":
        try:
            from database import get_chat_history
            redis_chat = get_chat_history(phone_anchor)
            if redis_chat:
                long_term_chat_str = "\n\nUSER'S PAST CHAT HISTORY (LONG-TERM REDIS MEMORY):\n"
                for msg in redis_chat[-15:]:
                    role_label = "User" if msg.get("role") == "user" else "Assistant"
                    long_term_chat_str += f"{role_label}: {msg.get('content')}\n"
                    
            from memory_manager import MemoryManager
            query_emb = model.encode([query])[0].tolist()
            past_summaries = MemoryManager.search_long_term_memory(phone_anchor, query_emb, limit=3)
            if past_summaries:
                qdrant_summary_str = "\n\nUSER'S PAST CHAT HISTORY (SUMMARIES FROM QDRANT):\n"
                for s in past_summaries:
                    qdrant_summary_str += f"- [{s.get('date_str')}] {s.get('content')}\n"
        except Exception as e:
            logger.error(f"Error loading chat history or summaries for RAG context: {e}")
    
    profile_details_str = "NO ACTIVE CITIZEN PROFILE FOUND."
    profile_name = "User"
    if user_info:
        profile_name = user_info.get("name") or user_info.get("userName") or "User"
        profile_details_str = f"""ACTIVE CITIZEN PROFILE:
- Name: {profile_name}
- Mobile Number: {user_info.get("mobileNumber") or user_info.get("userName") or "N/A"}
- Email ID: {user_info.get("emailId") or "N/A"}
- Roles: {', '.join([r.get('name') for r in user_info.get('roles', [])]) if user_info.get('roles') else 'Citizen'}
- Tenant ID: {user_info.get("tenantId") or "pg"}"""
    else:
        profile_name = get_any_user_profile_name()
        if profile_name and profile_name != "User":
            profile_details_str = f"""ACTIVE CITIZEN PROFILE:
- Name: {profile_name}"""

    if long_term_bookings_str:
        profile_details_str += long_term_bookings_str
    if qdrant_summary_str:
        profile_details_str += qdrant_summary_str
    if long_term_chat_str:
        profile_details_str += long_term_chat_str

    # Step 1: Try FAISS for supporting context (relaxed, not a hard gate)
    context = ""
    try:
        # Use search_lang for FAISS (English searches English KB, Hindi searches Hindi)
        query_for_search = query
        if search_lang != 'en':
            translated = translate_text(query, search_lang, "en")
            if translated and len(translated.strip()) > 2:
                query_for_search = translated
                logger.info(f"[RAG FAISS] Translated query for vector search: '{query_for_search}'")

        query_embedding = model.encode([query_for_search])
        distances, indices = index.search(query_embedding.astype(np.float32), k=5)

        relevant_chunks = []
        for dist, idx in zip(distances[0], indices[0]):
            if idx >= 0 and dist < 1.4:  # relaxed threshold for context gathering
                if 'prompt' in data.columns and 'response' in data.columns:
                    relevant_chunks.append(f"Q: {data['prompt'].iloc[idx]}\nA: {data['response'].iloc[idx]}")

        if relevant_chunks:
            context = "\n\n".join(relevant_chunks[:3])
            logger.info(f"[RAG FAISS] FAISS context found: {len(relevant_chunks)} relevant chunks (top distance: {distances[0][0]:.3f})")
        else:
            logger.info("[RAG FAISS] No FAISS context found - LLM will answer from general knowledge")

    except Exception as e:
        logger.error(f"[RAG FAISS] FAISS search error (non-fatal): {e}")
        context = ""

    # Step 2: Build language instruction
    if lang == 'hi':
        lang_rule = "CRITICAL LANGUAGE INSTRUCTION: The user is asking in Hindi. You MUST respond in pure Hindi language using Devanagari script ONLY (हिंदी लिपि). Do NOT use Roman script, English sentences, or Romanized Hinglish under any circumstances. Exception: keep UPYOG, NUDM, NOC, GIS, ULB, MoU as-is."
    else:
        lang_rule = "CRITICAL LANGUAGE INSTRUCTION: The user is asking in English. You MUST respond in pure standard English script and language ONLY. Do NOT use Romanized Hinglish, Hindi words, or Devanagari script under any circumstances."

    # Step 3: Build context section
    if context:
        context_section = f"""KNOWLEDGE BASE CONTEXT (use as primary reference):
{context}

If the context answers the question, use it.
If partially relevant, combine with your knowledge of UPYOG/NUDM.
If not relevant, answer from your general knowledge."""
    else:
        context_section = """No specific context found in knowledge base.
Answer from your general knowledge about:
- UPYOG platform services and processes
- NUDM (National Urban Digital Mission)
- Indian Urban Local Body (ULB) services
- Standard government processes for urban services in India"""

    # Step 4: Build conversation history with language isolation
    history_messages = []
    for turn in history[-6:]:
        if "content" in turn and "role" in turn:
            content = turn["content"]
            if turn["role"] == "assistant":
                if lang == 'en' and any('ऀ' <= c <= 'ॿ' for c in content):
                    translated = translate_text(content, "hi", "en")
                    if translated and len(translated.strip()) > 0:
                        content = translated
                elif lang == 'hi' and not any('ऀ' <= c <= 'ॿ' for c in content):
                    translated = translate_text(content, "en", "hi")
                    if translated and len(translated.strip()) > 0:
                        content = translated
            history_messages.append({"role": turn["role"], "content": content})

    # Step 5: System prompt - LLM as the brain
    system = f"""{lang_rule}

You are UPYOG Assistant — expert on:
- UPYOG (Urban Platform for Urban Governance) — India's government urban services platform
- NUDM (National Urban Digital Mission)
- All ULB services: Property Tax, Trade License, Fire NOC, Water & Sewerage,
  Birth & Death Certificates, Building Plan Approval, Waste Management,
  GIS Services, Community Hall, Street Vendors, Livelihood, Works Management, etc.
- MoU details, NUDM state partnerships, citizen processes

{profile_details_str}

If the user asks "who am I?", "what is my name?", "show my details", "show my profile", or asks for their registered details (mobile number, email address, UUID, or roles), respond by listing their CITIZEN PROFILE details above. Address them warmly by their name ({profile_name}).

CRITICAL RULES:

RULE 1 — UNDERSTAND HUMAN LANGUAGE:
Users speak naturally, not in FAQ format.
"बच्चों का बर्थ सर्टिफिकेट बनाना है" = birth certificate process
"कितने MoU साइन हुए" = number of MoUs
Understand MEANING, not literal words.

RULE 2 — ALWAYS TRY TO HELP:
If you know about the topic, answer it.
NEVER say "जानकारी नहीं है" for UPYOG-related questions.
If the user asks "what is my name?", "do you remember my name?", or "who am I?", greet them warmly by their name ({profile_name}) stored in your profile system context matrix.

RULE 3 — CONVERSATIONAL SCENARIOS:
Users describe situations, not textbook questions.
"mera beta paida hua certificate chahiye" → birth certificate
"shop kholi hai license lena hai" → trade license
Map human scenarios to UPYOG services.

RULE 4 — STRICT DOMAIN:
Only UPYOG/NUDM/ULB services. Politely redirect for unrelated topics.

RULE 5 — BE HONEST:
If unsure about numbers/dates, say "approximately" rather than refusing.

RULE 6 — TRANSACTIONAL LIMITATION:
- You can ONLY execute/book/create transactions for "Advertisement Booking".
- You CANNOT apply, register, pay, or book for "Trade License" or "Property Tax". You must state directly and clearly that you can guide and provide information about them, but you cannot execute or book payments for them.

RULE 7 — FORMATTING (ALWAYS APPLY):
Format every response using markdown for a clear, professional look:
- Use **bold** for service names, key terms, and important values.
- Use numbered lists (1. 2. 3.) for step-by-step processes.
- Use bullet points (-) for features, requirements, or multiple items.
- Use headers (e.g. ### Section Title) with the title text immediately on the same line as the hashes (NEVER leave hashes alone on a line).
- Keep paragraphs short (2-3 lines max).
- End with a helpful follow-up question when appropriate.
- Do NOT use emojis. This is a government services portal.

RULE 8 — PROFESSIONAL TONE AND FORMAL ADDRESS:
Maintain a formal, polite, and professional tone at all times as an official government services AI assistant.
STRICT RULE: NEVER use informal, overly familiar, or colloquial Hindi terms of address such as "दीदी" (Didi), "काकी" (Kaki), "बेटा" (Beta), "भैया" (Bhaiya), "चाचा" (Chacha), "अंकल" (Uncle), "आंटी" (Aunty), etc.
Always address the citizen respectfully using formal language (e.g. "आप") and clean professional greetings (e.g. "नमस्ते", "नमस्कार", "Hello") without adding informal terms of address.

RULE 9 — NEVER FABRICATE PERSONAL DATA:
NEVER invent, guess, or hallucinate complaint IDs, booking numbers, application numbers, dates, or statuses.
If the user asks to see their complaints or bookings (e.g. "show my complaints", "my grievances", "my bookings"),
respond: "To view your registered complaints, please type 'show my complaints' or provide your complaint ID (e.g. PG-PGR-XXXX) and I will look it up for you."
Do NOT list fake IDs or made-up complaint descriptions.

{context_section}"""

    # Step 6: Call Groq
    messages = [{"role": "system", "content": system}]
    messages.extend(history_messages)
    messages.append({"role": "user", "content": query})

    logger.info(f"[GROQ RAG] Calling Groq API with {len(messages)} messages (history turns: {len(history_messages)})")
    start_time = time.time()

    try:
        if not groq_client:
            groq_client = Groq(api_key=GROQ_API_KEY)

        response = groq_client.chat.completions.create(
            model=GROQ_MODEL,
            messages=messages,
            max_tokens=600,
            temperature=0.3
        )
        ans = response.choices[0].message.content.strip()
        elapsed = time.time() - start_time
        logger.info(f"[GROQ RAG] Received answer in {elapsed:.2f}s (len: {len(ans)} chars)")

        if lang == 'en' and any('ऀ' <= c <= 'ॿ' for c in ans):
            logger.info("[GROQ RAG] Output contained Devanagari for English query — translating to English")
            translated = translate_text(ans, "hi", "en")
            if translated and len(translated.strip()) > 0:
                ans = translated
        elif lang == 'hi' and not any('ऀ' <= c <= 'ॿ' for c in ans):
            logger.info("[GROQ RAG] Output contained Non-Devanagari for Hindi query — translating to Hindi")
            translated = translate_text(ans, "en", "hi")
            if translated and len(translated.strip()) > 0:
                ans = translated
        return ans

    except Exception as e:
        logger.error(f"[GROQ RAG] Groq error: {e}")
        return "क्षमा करें, तकनीकी समस्या है।" if lang == 'hi' else "Sorry, technical issue."

# ============== RETRIEVAL (legacy wrapper) ==============

# Retrieves relevant documents using an LLM-first approach with FAISS as optional context
def retrieve_document(query, user_lang, history, session_id="default"):
    global stop_generation
    stop_generation.clear()

    # Use new get_rag_response which has LLM-first architecture
    # The old FAISS hard gate is removed - LLM will answer from general knowledge if no context
    return get_rag_response(query, history, user_lang, search_lang=user_lang, session_id=session_id)

# Streaming version of document retrieval that yields text chunks for SSE rendering
def retrieve_document_stream(query, user_lang, history, phone_anchor="default"):
    global stop_generation
    stop_generation.clear()
    logger.info(f"[STREAMING] Starting SSE stream for query='{query}', lang='{user_lang}'")

    try:
        query_for_search = translate_text(query, user_lang, "en") if user_lang in ["hi", "mr", "bn", "gu", "ta", "te", "kn", "ml"] else query
        if not query_for_search or len(query_for_search.strip()) < 3:
            query_for_search = query

        faq_context = []
        if index is not None:
            faq_dist, faq_indices = index.search(model.encode([query_for_search]).astype(np.float32), 3)
            for d, idx in zip(faq_dist[0], faq_indices[0]):
                if idx != -1 and d < FAISS_THRESHOLD:
                    faq_context.append({"q": data['prompt'].iloc[idx], "a": data['response'].iloc[idx]})

        frs_context = []
        if frs_index is not None:
            frs_dist, frs_indices = frs_index.search(model.encode([query_for_search]).astype(np.float32), 5)
            for d, idx in zip(frs_dist[0], frs_indices[0]):
                if idx != -1 and d < FAISS_THRESHOLD:
                    frs_context.append({"module": frs_data.iloc[idx]['module'], "text": f"Q: {frs_data.iloc[idx]['question']} A: {frs_data.iloc[idx]['answer']}"})

        logger.info(f"[STREAMING] Context chunks matched: FAQ={len(faq_context)}, FRS={len(frs_context)}")

        if not Groq or not GROQ_API_KEY:
            logger.warning("[STREAMING] Groq SDK/Key not present — sending fallback response")
            response_text = faq_context[0]['a'] if faq_context else "I'm sorry, I'm having trouble thinking right now."
            yield f"data: {json.dumps({'type': 'text', 'text': response_text})}\n\n"
            return

        client = Groq(api_key=GROQ_API_KEY)

        if user_lang == "hi":
            lang_instruction = (
                "CRITICAL: YOUR OUTPUT MUST BE IN HINDI DEVANAGARI SCRIPT ONLY.\n"
                "DO NOT USE ENGLISH ALPHABETS TO WRITE HINDI WORDS (No Hinglish).\n"
                "Example: Use 'नमस्ते' NOT 'Namaste'. Use 'उपयोग' NOT 'Upyog'.\n"
            )
        else:
            lang_instruction = "You MUST respond in clear, simple English only."

        context_str = ""
        if faq_context:
            context_str += "FAQ Knowledge:\n" + "\n".join([f"Q: {c['q']} A: {c['a']}" for c in faq_context])
        if frs_context:
            context_str += "\nTechnical Specs:\n" + "\n".join([c['text'] for c in frs_context])

        qdrant_summary_str = ""
        if phone_anchor != "default":
            try:
                from memory_manager import MemoryManager
                query_emb = model.encode([query_for_search])[0].tolist()
                past_summaries = MemoryManager.search_long_term_memory(phone_anchor, query_emb, limit=3)
                if past_summaries:
                    qdrant_summary_str = "\nPAST CHAT SUMMARIES FROM QDRANT:\n" + "\n".join([f"- [{s.get('date_str')}] {s.get('content')}" for s in past_summaries])
            except Exception as e:
                logger.error(f"Error fetching Qdrant summaries in stream: {e}")

        system_instr = (
            f"You are the UPYOG AI Concierge. CURRENT OUTPUT LANGUAGE: {'HINDI (DEVANAGARI)' if user_lang == 'hi' else 'ENGLISH'}.\n"
            f"{lang_instruction}\n\n"
            "STRICT GROUNDING RULES:\n"
            "1. USE ONLY THE PROVIDED CONTEXT. Do not use outside knowledge.\n"
            "2. Max 3-4 sentences or a short structured list.\n"
            "3. You can only execute/book/create transactions for 'Advertisement Booking'. You CANNOT book or execute payments for 'Trade License' or 'Property Tax'. State directly that you can only guide/provide information about them, not perform transactions.\n"
            "4. FORMATTING: Use **bold** for key terms and service names. Use numbered lists for steps. Use bullet points for features or requirements. Do NOT use emojis. Keep the tone professional and formal.\n"
            "5. PROFESSIONAL TONE: NEVER use informal or familial terms of address such as 'दीदी' (Didi), 'काकी' (Kaki), 'बेटा' (Beta), 'भैया' (Bhaiya), 'चाचा', 'अंकल', etc. Use clean formal greetings (e.g. 'नमस्ते', 'नमस्कार', 'Hello').\n\n"
            f"CONTEXT PROVIDED:\n{context_str if context_str else 'NO CONTEXT. ASK FOR CLARIFICATION.'}\n{qdrant_summary_str}"
        )

        messages = [
            {"role": "system", "content": system_instr},
            *history[-10:],
            {"role": "user", "content": query}
        ]

        try:
            response = client.chat.completions.create(
                model=GROQ_MODEL,
                messages=messages,
                temperature=0.1,
                max_tokens=500,
                stream=True
            )

            full_response = ""
            for chunk in response:
                if stop_generation.is_set():
                    logger.info("Stream interrupted by stop signal")
                    break

                if chunk.choices and chunk.choices[0].delta.content:
                    content = chunk.choices[0].delta.content
                    full_response += content
                    yield f"data: {json.dumps({'type': 'text', 'text': content})}\n\n"

            # Generate TTS after full response
            if not stop_generation.is_set() and full_response:
                audio_output = text_to_speech(full_response, user_lang)
                if audio_output:
                    yield f"data: {json.dumps({'type': 'audio', 'audio': audio_output})}\n\n"

        except Exception as e:
            logger.error(f"Streaming error: {e}")
            yield f"data: {json.dumps({'type': 'error', 'error': str(e)})}\n\n"

    except Exception as e:
        logger.error(f"Error in retrieve_document_stream: {e}")
        yield f"data: {json.dumps({'type': 'error', 'error': str(e)})}\n\n"


# ==========================================
# DYNAMIC PLUGIN LOADER & ROUTER
# ==========================================
import glob
import importlib
from langchain_core.messages import HumanMessage
from typing import Dict, Any

workflows = {}

def load_plugins():
    workflow_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "workflow")
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

# Load plugins immediately at module import time so `workflows` dictionary is populated for Flask
load_plugins()

# Dispatches the user's input to the correct LangGraph plugin workflow (adv_booking, grievance, etc)
def process_user_message(user_input: str, phone_number: str, session_id: str,
                         target_workflow: str = "adv_booking") -> Dict[str, Any]:
    intent = target_workflow
    if intent not in workflows:
        return {"response": f"Service '{intent}' unavailable.", "status": "error"}

    target_graph = workflows[intent]
    thread_key = phone_number if (phone_number and phone_number != "default") else session_id
    config = {"configurable": {"thread_id": thread_key}}

    events = target_graph.stream(
        {"messages": [HumanMessage(content=user_input)], "phone_number": phone_number,
         "session_id": session_id, "active_service": intent},
        config,
        stream_mode="values"
    )
    
    final_message = None
    graph_input_type = "text"
    graph_options = []

    event_list = list(events) if events else []
    if event_list:
        for ev in event_list:
            if isinstance(ev, dict) and ev.get("messages"):
                final_message = ev["messages"][-1]
            # Capture input_type/options from ANY event that explicitly sets them
            if isinstance(ev, dict):
                ev_type = ev.get("input_type")
                ev_opts = ev.get("options")
                if ev_type is not None:
                    graph_input_type = ev_type
                if ev_opts is not None:
                    graph_options = ev_opts

    response_text = final_message.content if hasattr(final_message, "content") else str(final_message)
    
    import ast
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
        except:
            options = [opt.strip().strip('\'"') for opt in raw_options.split(',')]
        response_text = re.sub(r'<ui-(dropdown|button|checkbox-group)[^>]+>', '', response_text).strip()
        
    min_date = None
    # Parse <ui-calendar ... />
    cal_match = re.search(r'<ui-calendar([^>]*)>', response_text)
    if cal_match:
        input_type = "date"
        cal_attrs = cal_match.group(1)
        min_date_match = re.search(r'minDate="([^"]*)"', cal_attrs)
        from datetime import datetime, timedelta
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
        
    # Parse <ui-slot-table data=[...] />  — handles nested JSON objects
    slot_tag_match = re.search(r'<ui-slot-table data=(\[.*?\])\s*/>', response_text, re.DOTALL)
    if slot_tag_match:
        input_type = "slot_table"
        try:
            import json
            options = json.loads(slot_tag_match.group(1))
        except:
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
            import json
            options = json.loads(history_match.group(2))
        except:
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

### --- Added Proxy Function for Ad Agent --- ###
def handle_adv_turn(session_id, user_input, auth_token=None, workflow="booking", reset=False, file_name=None, file_data=None):
    """
    Proxy conversation directly to Local LangGraph Agent.
    CRITICAL: Extract phone_number from session_id before sending to agent.
    This ensures each phone number has separate MemorySaver memory.
    """
    # Extract phone number from session_id 
    phone_number = extract_phone_from_session(session_id)
    
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

adv_sessions = {}
### --- [END] --- ###


# ============== ROUTES ==============

"""
Serves the chatbot UI (index.html).
Route '/' handles direct local access at localhost:8090.
Route '/upyog-voice-bot' handles requests routed through niautt's EKS ingress
at niautt.niua.in/upyog-voice-bot.
strict_slashes=False accepts both trailing-slash and non-trailing-slash URLs.
"""
@app.route("/")
@app.route("/upyog-voice-bot", strict_slashes=False)
@app.route("/upyog-voice-bot/")
# Serves the main React frontend application
def index_page():
    return send_from_directory(os.path.dirname(os.path.abspath(__file__)), 'index.html')

"""
Serves static files from the assets/ folder (styles.css, constants.js, icons.js).
Three route aliases match all deployment paths — local dev, EKS ingress, and
the production VM nginx proxy.
"""
@app.route("/assets/<path:filename>")
@app.route("/upyog-voice-bot/assets/<path:filename>")
@app.route("/upyog-voice/assets/<path:filename>")
def serve_assets(filename):
    return send_from_directory(os.path.join(os.path.dirname(os.path.abspath(__file__)), 'assets'), filename)

"""
Main chat endpoint — three route aliases registered:
  /chat                  → direct local access (localhost:8090)
  /upyog-voice-bot/chat  → production via niautt EKS ingress
  /upyog-voice/chat      → backward compatibility with old deployment path
GET requests return a health check response so Kubernetes liveness
probes do not mark the pod as unhealthy.
"""
@app.route("/chat", methods=["GET", "POST"])
@app.route("/upyog-voice-bot/chat", methods=["GET", "POST"])
@app.route("/upyog-voice/chat", methods=["GET", "POST"])
# Standard non-streaming chat endpoint using the LLM-first architecture
def chat():
    if request.method == "GET":
        logger.info("[ENDPOINT /chat GET] Health check ping")
        return jsonify({"status": "ok", "message": "UPYOG Voice Bot Chat Endpoint"}), 200

    global model, data, index, is_loading

    try:
        if is_loading or any(x is None for x in [model, data, index]):
            logger.warning("[ENDPOINT /chat POST] Resources still loading — waiting 1s...")
            time.sleep(1)
            if any(x is None for x in [model, data, index]):
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

        # Intercept unsupported transactional requests professionally (Trade License, Property Tax)
        unsupported_keywords = ["trade license", "property tax", "property text", "व्यापार लाइसेंस", "संपत्ति कर"]
        action_keywords = ["pay", "book", "apply", "register", "fill", "payment", "details", "भरें", "भुगतान", "आवेदन"]
        ui_lower = user_input.lower()
        if any(u in ui_lower for u in unsupported_keywords) and any(a in ui_lower for a in action_keywords):
            msg = (
                "वर्तमान में, मैं केवल विज्ञापन बुकिंग में आपकी सहायता कर सकता हूँ। व्यापार लाइसेंस और संपत्ति कर सेवाओं पर काम चल रहा है और वे जल्द ही शुरू की जाएंगी। कृपया मुझे बताएं कि क्या आप विज्ञापन बुकिंग के साथ आगे बढ़ना चाहते हैं!"
                if user_language == 'hi' else
                "Currently, I can only assist you with Advertisement Bookings. Support for Trade License and Property Tax services is under development and will be launched soon. Please let me know if you would like to proceed with an advertisement booking!"
            )
            audio_output = text_to_speech(msg, user_language)
            return jsonify({
                "response": msg,
                "lang": user_language,
                "audio": audio_output,
                "mode": "blocked"
            })

        # ── Direct greeting pre-check (before LLM classifier) ───────────────────
        # Greet keywords are loaded from config.yml `greeting_keywords`; fallback to
        # a minimal built-in list so zero Python code needs updating when config changes.
        _greet_cfg = next(
            (s for s in _SERVICES_REGISTRY if s.get("key") == "greeting"), {}
        )
        _greet_kws = [w.lower() for w in _greet_cfg.get("keywords", [
            "hello", "hi", "hey", "namaste", "good morning", "good afternoon",
            "good evening", "hola", "howdy", "greetings", "नमस्ते", "हेलो"
        ])]
        _is_pure_greeting = (
            user_input.lower().strip() in _greet_kws or
            (len(user_input.split()) <= 3 and any(w in user_input.lower() for w in _greet_kws))
        )
        if _is_pure_greeting:
            # Show fresh dynamic greeting
            greet_msg = build_greeting_response(user_language, workflows)
            audio_output = text_to_speech(greet_msg, user_language)
            logger.info("[Greeting] Responding with fresh greeting")
            return jsonify({
                "response": greet_msg, "lang": user_language,
                "mode": "greeting", "audio": audio_output
            })

        # ===== EARLY INTERCEPTION: Multi-Draft Selection Menu =====
        # Must run BEFORE intent classification to avoid keyword conflicts (e.g. "booking" triggering status search)
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
                # Will fall through to draft_delete intent directly below
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
            # Run intent classifier FIRST - before any FAISS filtering
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
                # Generic confirmation/cancellation intents stay with active_plugin if one is ongoing
                plugin_intent = active_plugin or ("adv_booking" if "adv" in intent or "booking" in intent else "grievance")

        # Explicit service switch occurs ONLY if the user explicitly requested the other service (NOT on generic yes/no/confirm/cancel responses)
        is_generic_response = ui_lower in ["yes", "no", "yeah", "yup", "ya", "ok", "okay", "sure", "confirm", "cancel", "haan", "nahi", "nahin", "1", "2", "option 1", "option 2"]
        if plugin_intent and active_plugin and plugin_intent != active_plugin and not is_generic_response:
            logger.info(f"[Router] User switched service: {active_plugin} -> {plugin_intent}")
            # Auto-save previous workflow draft before switching if it has user-filled data
            if active_plugin in workflows:
                prev_state = workflows[active_plugin].get_state(config)
                if prev_state and prev_state.values:
                    prev_draft_key = "draft_booking" if active_plugin == "adv_booking" else "draft_grievance"
                    prev_draft = prev_state.values.get(prev_draft_key) or {}
                    if prev_draft and any(v for k, v in prev_draft.items() if not k.startswith("_") and v is not None and str(v).strip() != ""):
                        from memory_manager import MemoryManager
                        MemoryManager.save_draft_state(phone, active_plugin, prev_draft)
                        logger.info(f"[AutoSave] Saved previous workflow draft for {phone}/{active_plugin}")
                    
                    # Reset previous workflow in-memory state so it doesn't hijack subsequent turns
                    empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if active_plugin == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                    workflows[active_plugin].update_state(config, {prev_draft_key: empty_draft, "missing_fields": [], "messages": []})
                    logger.info(f"[AutoSave] Reset in-memory state for {active_plugin}")
            active_plugin = plugin_intent
        elif active_plugin and is_generic_response:
            # Strictly preserve active_plugin for generic yes/no/confirm/cancel answers
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
                
                # Fetch categories dynamically from grievance / adv_booking if not yet stored
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
                
                # Normalize all options (alphanumeric only, lowercase)
                all_opts = [re.sub(r'[\s_]+', '', str(o).lower()) for o in (cats + subs + locs + slots) if o]
                clean_in = re.sub(r'[\s_]+', '', user_input.strip().lower())
                
                # Also check digit selection e.g. "1", "option 1"
                is_digit_choice = bool(re.match(r'^(?:option\s*)?\d+$', user_input.strip().lower()))
                
                if is_digit_choice or clean_in in all_opts or any(clean_in == o or (o in clean_in and len(user_input.split()) <= 3) for o in all_opts):
                    is_option_selection = True
                    logger.info(f"[Router] '{user_input}' matched active option in {active_plugin}")

        # An interruption occurs ONLY when the citizen asks an EXPLICIT question during an active form
        is_faq_or_interruption = is_explicit_question and not is_option_selection and not is_ui_payload

        if is_faq_or_interruption and active_plugin:
            logger.info(f"[Interruption] User asked a question during '{active_plugin}': '{user_input}'. Answering FAQ.")
            
            draft_saved = False
            saved_plugin_name = None
            
            # 1. Automatically save active workflow state into Qdrant ONLY IF real user data exists
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
                    
                    # Reset active LangGraph graph state so subsequent queries don't auto-lock into form
                    empty_draft = {f: None for f in (["category", "sub_category", "description", "locality"] if active_plugin == "grievance" else ["addType", "location", "faceArea", "start_date", "end_date", "nightLight"])}
                    workflows[active_plugin].update_state(config, {draft_key: empty_draft, "missing_fields": []})

            # 2. Answer the citizen's question via FAQ retrieval / RAG
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
                
                # Restore into LangGraph checkpointer
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
            from memory_manager import MemoryManager
            
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
            
            # Check if citizen requested to delete a specific draft by module or number
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
                    # Clear drafts across all registered workflows
                    target_plugins = list(workflows.keys())

            # Delete target plugin drafts or all drafts
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
            # If an active workflow exists, auto-save its draft first
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
            # Check if user is authenticated for transactional workflows (adv_booking, grievance)
            if not is_authenticated:
                logger.info(f"[Auth] Action requires authentication but user is not logged in (phone={phone_anchor}). Prompting login in UPYOG.")
                msg = (
                    "शिकायत दर्ज करने या विज्ञापन आवेदन करने के लिए, कृपया पहले UPYOG पोर्टल पर लॉगिन करें।"
                    if user_language == 'hi' else
                    "To file a complaint or create an application for advertisement, please log in to UPYOG first."
                )
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

            # Ensure workflow response strictly aligns with user_language
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
        logger.error(f"[ENDPOINT /chat ERROR] Exception: {e}", exc_info=True)
        return jsonify({"error": str(e)}), 500

import threading

# Summarizes a block of older chat messages and archives them in Qdrant long-term memory
def summarize_and_store_memory(phone_anchor, messages_to_summarize):
    global groq_client, model
    try:
        from memory_manager import MemoryManager
        
        # Format messages for LLM
        convo_text = ""
        for msg in messages_to_summarize:
            role = "User" if msg.get("role") == "user" else "Assistant"
            convo_text += f"{role}: {msg.get('content')}\n"
            
        if not groq_client:
            from groq import Groq
            groq_client = Groq(api_key=GROQ_API_KEY)
            
        prompt = f"""You are an AI memory summarization assistant.
Please summarize the following conversation chunk into 2 concise sentences. Focus on the core intent, entities, and any factual details discussed.

Conversation:
{convo_text}

Summary:"""

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

@app.after_request
def log_chat_to_redis(response):
    # Only intercept chat endpoints
    if request.path not in ["/chat", "/upyog-voice-bot/chat"]:
        return response
        
    try:
        if response.status_code == 200:
            req_data = request.get_json(silent=True) or {}
            session_id = req_data.get("session_id")
            user_input = req_data.get("query", "").strip()
            
            # Fallback label for file uploads
            file_name = req_data.get("file_name")
            if not user_input and file_name:
                user_input = json.dumps({"document": file_name})
                
            res_data = response.get_json(silent=True) or {}
            response_text = res_data.get("response", "").strip()
            
            if session_id and user_input and response_text:
                phone_anchor = extract_phone_from_session(session_id)
                if phone_anchor != "default":
                    from database import get_chat_history, save_chat_history
                    redis_chat = get_chat_history(phone_anchor)
                    # Check safety buffer to avoid duplicate saves on page refresh or retries
                    if not redis_chat or redis_chat[-1].get("content") != response_text or redis_chat[-2].get("content") != user_input:
                        redis_chat.append({"role": "user", "content": user_input})
                        redis_chat.append({"role": "assistant", "content": response_text})
                        
                        # --- SLIDING WINDOW SUMMARIZATION ---
                        if len(redis_chat) >= 20: # 10 turns
                            messages_to_summarize = redis_chat[:10] # Take oldest 10
                            redis_chat = redis_chat[10:] # Keep newest 10
                            
                            # Run summarization asynchronously to avoid blocking the HTTP response
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
@app.route("/stream", methods=["GET", "POST"])
@app.route("/upyog-voice-bot/stream", methods=["GET", "POST"])
# Streaming SSE (Server-Sent Events) endpoint for a faster typing effect on the UI
def stream():
    if request.method == "GET":
        logger.info("[ENDPOINT /stream GET] Health check ping")
        return jsonify({"status": "ok", "message": "UPYOG Voice Bot Stream Endpoint"}), 200

    global model, data, index, is_loading, stop_generation

    try:
        if is_loading or any(x is None for x in [model, data, index]):
            time.sleep(1)
            if any(x is None for x in [model, data, index]):
                return Response("data: {\"error\": \"Loading resources...\"}\n\n", mimetype='text/event-stream'), 503

        user_data = request.json or {}
        user_input = user_data.get("query", "")
        # NOTE: Don't use lang from frontend - detect fresh per turn
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

@app.route("/stop", methods=["POST"])
@app.route("/upyog-voice-bot/stop", methods=["POST"])
def stop():
    """Stop endpoint - called when user barges in."""
    global stop_generation
    stop_generation.set()
    logger.info("[ENDPOINT /stop POST] Stop signal set — interrupting generation thread")
    return jsonify({"status": "stopped"}), 200

# ============== UPYOG LOGIN & OTP API INTEGRATIONS ==============

def send_otp_upyog(mobile):
    from mcp_tools import UPYOG_BASE_URL, _cfg, _BASIC_AUTH
    auth_cfg   = _cfg.get("auth", {})
    state_tenant = _cfg.get("state_tenant", "pg")
    url = f"{UPYOG_BASE_URL}{_cfg.get('endpoints', {}).get('send_otp', '/user-otp/v1/_send')}?tenantId={state_tenant}&_={int(time.time() * 1000)}"
    payload = {
        "otp": {
            "mobileNumber": mobile,
            "tenantId":     state_tenant,
            "userType":     auth_cfg.get("user_type", "CITIZEN").lower(),
            "type":         "login"
        },
        "RequestInfo": {
            "apiId":              "Rainmaker",
            "msgId":              f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }
    headers = {"Content-Type": "application/json"}
    if _BASIC_AUTH:
        headers["Authorization"] = _BASIC_AUTH
    try:
        res = requests.post(url, json=payload, headers=headers)
        # UPYOG OTP API returns 201 Created on success (not 200) — both are valid
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

def verify_otp_upyog(mobile, otp):
    from mcp_tools import UPYOG_BASE_URL, _cfg, _BASIC_AUTH
    auth_cfg     = _cfg.get("auth", {})
    state_tenant = _cfg.get("state_tenant", "pg")
    url = f"{UPYOG_BASE_URL}{auth_cfg.get('token_path', '/user/oauth/token')}"
    data = {
        "username":   mobile,
        "password":   otp,
        "grant_type": auth_cfg.get("grant_type", "password"),
        "scope":      auth_cfg.get("scope", "read"),
        "tenantId":   state_tenant,
        "userType":   auth_cfg.get("user_type", "CITIZEN")
    }
    headers = {
        "Content-Type":  "application/x-www-form-urlencoded",
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

def fetch_user_details_upyog(mobile, auth_token):
    from mcp_tools import UPYOG_BASE_URL, _cfg
    state_tenant = _cfg.get("state_tenant", "pg")
    url = f"{UPYOG_BASE_URL}{_cfg.get('endpoints', {}).get('user_search', '/user/_search')}?_={int(time.time() * 1000)}"
    payload = {
        "tenantId":  state_tenant,
        "userName":  mobile,
        "pageSize":  "100",
        "RequestInfo": {
            "apiId":              "Rainmaker",
            "authToken":          auth_token,
            "msgId":              f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {}
        }
    }
    try:
        res  = requests.post(url, json=payload, headers={"Content-Type": "application/json"})
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

def verify_user_auth(auth_token, uuid_or_mobile=None, tenant_id=None):
    from mcp_tools import UPYOG_BASE_URL, _cfg
    state_tenant = tenant_id or _cfg.get("state_tenant", "pg")
    url = f"{UPYOG_BASE_URL}{_cfg.get('endpoints', {}).get('user_search', '/user/_search')}?_={int(time.time() * 1000)}"
    headers = {
        "Content-Type": "application/json",
        "auth-token": str(auth_token)
    }
    payload = {
        "tenantId": state_tenant,
        "pageSize": "100",
        "RequestInfo": {
            "apiId":              "Rainmaker",
            "authToken":          auth_token,
            "msgId":              f"{int(time.time() * 1000)}|en_IN",
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

@app.route("/api/send-otp", methods=["POST"])
@app.route("/upyog-voice-bot/api/send-otp", methods=["POST"])
def api_send_otp():
    req_data = request.json or {}
    mobile = req_data.get("mobile")
    if not mobile or len(mobile) != 10:
        return jsonify({"error": "Invalid mobile number"}), 400

    client_ip = request.remote_addr or "unknown"
    rate_key = f"otp_ratelimit:{mobile}:{client_ip}"
    try:
        from database import r_client
        attempts = r_client.incr(rate_key)
        if attempts == 1:
            r_client.expire(rate_key, 600)  # 10 minutes rate limit window
        if attempts > 5:
            logger.warning(f"[RateLimit] Excessive OTP requests for mobile={mobile} from IP={client_ip}")
            return jsonify({"error": "Too many OTP requests. Please wait 10 minutes before requesting again."}), 429
    except Exception as rate_err:
        logger.error(f"[RateLimit] Rate limit check error: {rate_err}")

    res = send_otp_upyog(mobile)
    return jsonify(res)

@app.route("/api/verify-otp", methods=["POST"])
@app.route("/upyog-voice-bot/api/verify-otp", methods=["POST"])
def api_verify_otp():
    req_data = request.json or {}
    mobile = req_data.get("mobile")
    otp = req_data.get("otp")
    if not mobile or not otp:
        return jsonify({"error": "Mobile and OTP are required"}), 400

    # 1. Verify OTP
    verify_res = verify_otp_upyog(mobile, otp)
    if "access_token" not in verify_res:
        return jsonify({"error": "Invalid OTP or verification failed", "details": verify_res}), 400
    
    # 2. Get user info (either from verify response or search)
    user_info = verify_res.get("UserRequest", verify_res.get("userInfo", {}))
    if not user_info or not user_info.get("uuid"):
        # fallback search
        search_res = fetch_user_details_upyog(mobile, verify_res["access_token"])
        if search_res:
            user_info = search_res

    # 3. Save verified user profile & auth token into Redis cache
    if user_info:
        user_info["_auth_token"] = verify_res["access_token"]
        user_info["_verified_at"] = time.time()
        save_user_profile_info(mobile, user_info)
        logger.info(f"[Auth] Logged in and cached user profile info in Redis for mobile={mobile}")

    return jsonify({
        "access_token": verify_res["access_token"],
        "user_info": user_info
    })

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
        if not groq_client:
            groq_client = Groq(api_key=GROQ_API_KEY)

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
    import yaml
    base_dir = os.path.dirname(os.path.abspath(__file__))
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
    import random
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
        import groq as groq_lib
        global groq_client
        if not groq_client:
            groq_client = groq_lib.Groq(api_key=GROQ_API_KEY)
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



if __name__ == "__main__":
    from memory_manager import init_collections
    try:
        init_collections()
        load_plugins()
        logger.info("Starting UPYOG Voice Assistant v2 on port 8090...")
        app.run(host='0.0.0.0', port=8090)
    except Exception as e:
        logger.error(f"Error starting Flask application: {e}")
        raise
