"""
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

try:
    from groq import Groq
except ImportError:
    Groq = None

from dotenv import load_dotenv
load_dotenv()

GROQ_API_KEY = os.environ.get("GROQ_API_KEY")

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

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

def load_resources():
    """Load all required resources."""
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

def is_hard_blocked(query: str) -> bool:
    """Only block things that are DEFINITELY not UPYOG related."""
    q = query.lower()
    return any(topic in q for topic in HARD_BLOCK_TOPICS)

def is_in_domain(query: str) -> tuple:
    """Legacy wrapper for backward compatibility."""
    if is_hard_blocked(query):
        return False, "out_of_domain"
    return True, "ok"

def get_rejection_message(reason: str, lang: str) -> str:
    """Get rejection message based on reason and language."""
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

# Common English words (for majority voting)
COMMON_ENGLISH_WORDS = {
    'the', 'is', 'are', 'was', 'were', 'be', 'been', 'being',
    'have', 'has', 'had', 'do', 'does', 'did', 'will', 'would', 'could', 'should',
    'may', 'might', 'must', 'shall', 'can',
    'i', 'you', 'he', 'she', 'it', 'we', 'they', 'what', 'which', 'who', 'whom',
    'this', 'that', 'these', 'those', 'am', 'is', 'are', 'was', 'were',
    'my', 'your', 'his', 'her', 'its', 'our', 'their',
    'and', 'but', 'or', 'not', 'no', 'yes', 'if', 'then', 'else',
    'how', 'when', 'where', 'why', 'what', 'which',
    'please', 'thanks', 'thank', 'sorry', 'ok', 'okay',
    'process', 'apply', 'register', 'form', 'document', 'certificate', 'license',
    'need', 'want', 'require', 'require', 'required',
    'online', 'offline', 'website', 'portal', 'app', 'mobile', 'phone',
    'payment', 'pay', 'fee', 'charge', 'cost', 'price',
    'time', 'date', 'day', 'week', 'month', 'year',
    'help', 'information', 'info', 'details', 'tell', 'know', 'understand',
    'download', 'upload', 'check', 'status', 'track',
    'submit', 'approval', 'approve', 'reject', 'accept',
    'property', 'tax', 'water', 'sewerage', 'trade', 'business', 'building',
    'application', 'request', 'complaint', ' grievance', 'issue', 'problem',
    'service', 'facility', 'benefit', 'scheme', 'program',
    'available', 'provide', 'give', 'get', 'receive'
}

# English words commonly transliterated into Devanagari
ENGLISH_IN_DEVANAGARI = [
    'व्हाट', 'हाउ', 'व्हेन', 'व्हेयर', 'व्हाई', 'हू', 'विच',
    'इज', 'आर', 'वॉज', 'वेयर', 'हैव', 'हैज', 'डू', 'डज',
    'कैन', 'कुड', 'विल', 'वुड', 'शुड', 'मस्ट',
    'द', 'ए', 'एन', 'इन', 'ऑन', 'एट', 'बाय', 'फॉर',
    'ऑफ', 'टू', 'फ्रॉम', 'विद', 'अबाउट',
    'नंबर', 'टोटल', 'लिस्ट', 'प्रोसेस', 'स्टेटस',
    'एमओयू', 'एनयूएलएम', 'यूएलबी', 'एनयूडीएम',
    'यूज़र', 'सर्च', 'सबमिट', 'अप्लाई', 'पेमेंट'
]

def detect_language(text: str) -> dict:
    """
    Script-aware language detection.
    Returns dict: {'lang': 'hi'|'en', 'script': ..., 'search_lang': 'hi'|'en'}
    """
    if not text or not text.strip():
        return {'lang': 'en', 'script': 'english', 'search_lang': 'en'}

    text = text.strip()
    words = text.split()
    total_alpha = sum(1 for c in text if c.isalpha())

    if total_alpha == 0:
        return {'lang': 'en', 'script': 'english', 'search_lang': 'en'}

    # Count Devanagari characters
    devanagari_chars = sum(1 for c in text if 'ऀ' <= c <= 'ॿ')
    devanagari_ratio = devanagari_chars / total_alpha

    if devanagari_ratio > 0.5:
        # Mostly Devanagari — check if transliterated English
        english_word_count = sum(1 for w in words if any(eng in w for eng in ENGLISH_IN_DEVANAGARI))
        english_ratio = english_word_count / len(words) if words else 0

        if english_ratio > 0.4:
            # Transliterated English
            return {'lang': 'en', 'script': 'transliterated_english', 'search_lang': 'en'}

        # True Hindi
        return {'lang': 'hi', 'script': 'devanagari', 'search_lang': 'hi'}

    # Roman script — check for Hindi phonetics
    text_lower = text.lower()
    words_lower = re.findall(r'\b\w+\b', text_lower)

    hindi_phonetic = [
        'kya', 'kaise', 'kahan', 'kab', 'kyun', 'kaun',
        'hai', 'hain', 'tha', 'thi', 'hoga', 'hogi', 'hoge',
        'mujhe', 'aapko', 'mera', 'meri', 'mere', 'humara', 'hamare',
        'nahi', 'nahin', 'haan', 'theek', 'accha', 'theek hai',
        'batao', 'chahiye', 'milega', 'karo', 'dijiye', 'bataye',
        'aur', 'lekin', 'toh', 'bhi', 'sirf',
        'din', 'mahina', 'saal', 'ghanta',
    ]

    hindi_word_count = sum(1 for w in words_lower if w in hindi_phonetic)

    if hindi_word_count >= 2:
        return {'lang': 'hi', 'script': 'roman_hindi', 'search_lang': 'hi'}

    return {'lang': 'en', 'script': 'english', 'search_lang': 'en'}

def detect_language_per_turn(text: str) -> tuple:
    """Legacy wrapper for backward compatibility."""
    info = detect_language(text)
    return info['lang'], info['script']

# ============== TRANSLATION ==============

def translate_text_bhashini(text, source_lang, target_lang):
    """Translate text using Bhashini with caching."""
    logger.info(f"Translating from {source_lang} to {target_lang}")
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
            return translation_output
        else:
            logger.error(f"Bhashini translation failed with status {response.status_code}")
            return None
    except Exception as e:
        logger.error(f"Bhashini Translation Error: {e}")
        return None

def translate_text(text, source_lang, target_lang):
    """Translate text with fallback."""
    if source_lang == target_lang or not text:
        return text
    translated = translate_text_bhashini(text, source_lang, target_lang)
    if translated:
        return translated
    return text

# ============== TTS ==============

import asyncio
import tempfile
import base64
import edge_tts

async def generate_edge_tts(text, voice, output_path):
    communicate = edge_tts.Communicate(text, voice)
    await communicate.save(output_path)

def text_to_speech(text, language_code, gender="female"):
    """Convert text to speech using Edge-TTS with Bhashini fallback."""
    # Branding
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

    # Ensure script matches language
    if language_code == "en" and any('ऀ' <= c <= 'ॿ' for c in text):
        text = translate_text(text, "hi", "en")
    elif language_code == "hi" and not any('ऀ' <= c <= 'ॿ' for c in text):
        text = translate_text(text, "en", "hi")

    # Strip emojis and markdown
    text = re.sub(
        u'[\U00002600-\U000027BF]|[\U0001F300-\U0001FAFF]|[\U00002702-\U000027B0]|[\U0000FE00-\U0000FE0F]|[\U0001F000-\U0001F9FF]|‍|️',
        '', text
    ).strip()
    text = text.replace('**', '').replace('*', '')

    logger.info(f"Generating TTS for language: {language_code}")

    # Try Edge-TTS first for English/Hindi
    if language_code in ["en", "hi"]:
        voice_map = {
            "en": "en-IN-NeerjaNeural",
            "hi": "hi-IN-MadhurNeural"
        }
        voice = voice_map.get(language_code)
        try:
            with tempfile.NamedTemporaryFile(delete=False, suffix='.mp3') as temp_audio:
                temp_path = temp_audio.name
            try:
                asyncio.run(generate_edge_tts(text, voice, temp_path))
            except:
                pass
            with open(temp_path, "rb") as f:
                audio_content = base64.b64encode(f.read()).decode('utf-8')
            os.unlink(temp_path)
            return audio_content
        except Exception as e:
            logger.error(f"Edge-TTS failed: {e}")

    # Fallback to Bhashini
    if language_code == "en":
        tts_service_id = TTS_SERVICE_ID_MISC
    elif language_code in ["hi", "mr", "bn", "gu", "pa", "as", "or"]:
        tts_service_id = TTS_SERVICE_ID
    elif language_code in ["kn", "ml", "ta", "te"]:
        tts_service_id = TTS_SERVICE_ID_DRAVIDIAN
    else:
        tts_service_id = TTS_SERVICE_ID_MISC

    payload = {
        "pipelineTasks": [{"taskType": "tts", "config": {"language": {"sourceLanguage": language_code}, "serviceId": tts_service_id, "gender": gender, "samplingRate": 8000}}],
        "inputData": {"input": [{"source": text}]}
    }

    try:
        response = requests.post(BHASHINI_URL, headers=BHASHINI_HEADERS, json=payload)
        if response.status_code == 200:
            return response.json()["pipelineResponse"][0]["audio"][0]["audioContent"]
        return None
    except Exception as e:
        logger.error(f"Bhashini TTS Error: {e}")
        return None

# ============== LLM RESPONSE GENERATION ==============

def contains_urdu_script(text):
    return bool(re.compile(r'[؀-ۿ]').search(text))

def get_rag_response(query: str, history: list, lang: str, search_lang: str = None) -> str:
    """
    LLM-first architecture: LLM understands human language, FAISS provides optional context.
    FAISS is NOT a hard gate - if no context found, LLM answers from general knowledge.
    """
    global groq_client

    if search_lang is None:
        search_lang = lang

    # Step 1: Try FAISS for supporting context (relaxed, not a hard gate)
    context = ""
    try:
        # Use search_lang for FAISS (English searches English KB, Hindi searches Hindi)
        query_for_search = query
        if search_lang != 'en':
            # Translate to English for better FAISS matches
            translated = translate_text(query, search_lang, "en")
            if translated and len(translated.strip()) > 2:
                query_for_search = translated

        query_embedding = model.encode([query_for_search])
        distances, indices = index.search(query_embedding.astype(np.float32), k=5)

        relevant_chunks = []
        for dist, idx in zip(distances[0], indices[0]):
            if idx >= 0 and dist < 1.4:  # relaxed threshold for context gathering
                if 'prompt' in data.columns and 'response' in data.columns:
                    relevant_chunks.append(f"Q: {data['prompt'].iloc[idx]}\nA: {data['response'].iloc[idx]}")

        if relevant_chunks:
            context = "\n\n".join(relevant_chunks[:3])
            logger.info(f"FAISS context found: {len(relevant_chunks)} chunks")
        else:
            logger.info("No FAISS context found - LLM will answer from general knowledge")

    except Exception as e:
        logger.error(f"FAISS error (non-fatal): {e}")
        context = ""

    # Step 2: Build language instruction
    if lang == 'hi':
        lang_rule = "LANGUAGE: Respond in Hindi using Devanagari script only. Exception: keep UPYOG, NUDM, NOC, GIS, ULB, MoU as-is."
    else:
        lang_rule = "LANGUAGE: Respond in English only."

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

    # Step 4: Build conversation history
    history_messages = []
    for turn in history[-6:]:
        if "content" in turn and "role" in turn:
            history_messages.append({"role": turn["role"], "content": turn["content"]})

    # Step 5: System prompt - LLM as the brain
    system = f"""{lang_rule}

You are UPYOG Assistant — expert on:
- UPYOG (Urban Platform for Urban Governance) — India's government urban services platform
- NUDM (National Urban Digital Mission)
- All ULB services: Property Tax, Trade License, Fire NOC, Water & Sewerage,
  Birth & Death Certificates, Building Plan Approval, Waste Management,
  GIS Services, Community Hall, Street Vendors, Livelihood, Works Management, etc.
- MoU details, NUDM state partnerships, citizen processes

CRITICAL RULES:

RULE 1 — UNDERSTAND HUMAN LANGUAGE:
Users speak naturally, not in FAQ format.
"बच्चों का बर्थ सर्टिफिकेट बनाना है" = birth certificate process
"कितने MoU साइन हुए" = number of MoUs
Understand MEANING, not literal words.

RULE 2 — ALWAYS TRY TO HELP:
If you know about the topic, answer it.
NEVER say "जानकारी नहीं है" for UPYOG-related questions.

RULE 3 — CONVERSATIONAL SCENARIOS:
Users describe situations, not textbook questions.
"mera beta paida hua certificate chahiye" → birth certificate
"shop kholi hai license lena hai" → trade license
Map human scenarios to UPYOG services.

RULE 4 — STRICT DOMAIN:
Only UPYOG/NUDM/ULB services. Politely redirect for unrelated topics.

RULE 5 — BE HONEST:
If unsure about numbers/dates, say "approximately" rather than refusing.

{context_section}"""

    # Step 6: Call Groq
    messages = [{"role": "system", "content": system}]
    messages.extend(history_messages)
    messages.append({"role": "user", "content": query})

    try:
        if not groq_client:
            groq_client = Groq(api_key=GROQ_API_KEY)

        response = groq_client.chat.completions.create(
            model="llama-3.1-8b-instant",
            messages=messages,
            max_tokens=400,
            temperature=0.3
        )
        return response.choices[0].message.content.strip()

    except Exception as e:
        logger.error(f"Groq error: {e}")
        return "क्षमा करें, तकनीकी समस्या है।" if lang == 'hi' else "Sorry, technical issue."

# ============== RETRIEVAL (legacy wrapper) ==============

def retrieve_document(query, user_lang, history):
    """Retrieve document using LLM-first approach with FAISS as optional context."""
    global stop_generation
    stop_generation.clear()

    # Use new get_rag_response which has LLM-first architecture
    # The old FAISS hard gate is removed - LLM will answer from general knowledge if no context
    return get_rag_response(query, history, user_lang, search_lang=user_lang)

def retrieve_document_stream(query, user_lang, history):
    """Streaming version - yields chunks for SSE."""
    global stop_generation
    stop_generation.clear()

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

        # Stream the LLM response
        if not Groq or not GROQ_API_KEY:
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

        system_instr = (
            f"You are the UPYOG AI Concierge. CURRENT OUTPUT LANGUAGE: {'HINDI (DEVANAGARI)' if user_lang == 'hi' else 'ENGLISH'}.\n"
            f"{lang_instruction}\n\n"
            "STRICT GROUNDING RULES:\n"
            "1. USE ONLY THE PROVIDED CONTEXT. Do not use outside knowledge.\n"
            "2. Max 2-3 sentences.\n\n"
            f"CONTEXT PROVIDED:\n{context_str if context_str else 'NO CONTEXT. ASK FOR CLARIFICATION.'}"
        )

        messages = [
            {"role": "system", "content": system_instr},
            *history[-10:],
            {"role": "user", "content": query}
        ]

        try:
            response = client.chat.completions.create(
                model="llama-3.1-8b-instant",
                messages=messages,
                temperature=0.1,
                max_tokens=150,
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
def index_page():
    """Serve the frontend."""
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
def chat():
    """Standard non-streaming chat endpoint with LLM-first architecture."""
    if request.method == "GET":
        return jsonify({"status": "ok", "message": "UPYOG Voice Bot Chat Endpoint"}), 200

    global model, data, index, is_loading

    try:
        if is_loading or any(x is None for x in [model, data, index]):
            time.sleep(1)
            if any(x is None for x in [model, data, index]):
                return jsonify({"error": "Loading resources..."}), 503

        user_data = request.json
        user_input = user_data.get("query", "")
        session_id = user_data.get("session_id", "default")
        history = user_data.get("history", [])

        if not user_input:
            return jsonify({"response": "", "lang": "en", "audio": ""})

        # NEW: Script-aware language detection returning dict
        lang_info = detect_language(user_input)
        user_language = lang_info['lang']
        detected_script = lang_info['script']
        search_lang = lang_info['search_lang']

        print(f"━━━ REQUEST ━━━")
        print(f"Query: {user_input}")
        print(f"Lang: {user_language} | Script: {detected_script} | SearchLang: {search_lang}")
        print(f"━━━━━━━━━━━━━━")

        # Hard block check - only block truly unrelated topics
        if is_hard_blocked(user_input):
            msg = ("मैं केवल UPYOG और शहरी सरकारी सेवाओं के बारे में "
                   "सहायता कर सकता हूँ।" if user_language == 'hi' else
                   "I can only help with UPYOG and urban government services.")
            audio_output = text_to_speech(msg, user_language)
            return jsonify({"response": msg, "lang": user_language,
                           "audio": audio_output, "mode": "blocked"})

        # GATE 1: Check if grievance flow is active for this session
        grievance_session = get_grievance_session(session_id)
        if grievance_session.get("active") and grievance_session.get("collecting"):
            result = handle_grievance_turn(session_id, user_input, user_language)
            audio_output = text_to_speech(result["message"], result["lang"])
            return jsonify({
                "response": result["message"],
                "lang": result["lang"],
                "mode": "grievance_collecting",
                "grievance_type": result.get("type"),
                "audio": audio_output,
                "input_type": result.get("input_type", "text"),
                "options": result.get("options", []),
                "field": result.get("field", "")
            })

        # ===== INTENT CLASSIFICATION FLOW =====
        # Run intent classifier FIRST - before any FAISS filtering

        # Check if we have an active grievance offer pending
        g_offer = grievance_sessions.get(session_id, {})

        if g_offer.get("offer_pending"):
            # PATH B: Bot already offered grievance, waiting for confirmation
            intent = classify_intent(user_input, history, user_language)

            if intent["intent"] == "grievance_confirm":
                # User said YES - start grievance collection
                grievance_sessions[session_id] = {
                    "active": True,
                    "collecting": True,
                    "step": "START",
                    "data": {},
                    "offer_pending": False,
                    "auth_token": None,
                    "user_info": None,
                    "categories": {},
                    "localities": []
                }
                result = handle_grievance_turn(session_id, "", user_language)
                audio_output = text_to_speech(result["message"], result["lang"])
                return jsonify({
                    "response": result["message"],
                    "lang": user_language,
                    "mode": "grievance_collecting",
                    "step": "START",
                    "audio": audio_output,
                    "input_type": result.get("input_type", "text"),
                    "options": result.get("options", []),
                    "field": result.get("field", "")
                })

            elif intent["intent"] == "grievance_cancel":
                # User said NO - clear offer, continue normally
                grievance_sessions[session_id] = {"active": False, "offer_pending": False}
                response_text = retrieve_document(user_input, user_language, history)
                response_text = re.sub(r'\bUpyog\b', 'UPYOG', response_text, flags=re.IGNORECASE)
                audio_output = text_to_speech(response_text, user_language)
                return jsonify({
                    "response": response_text,
                    "lang": user_language,
                    "detected_script": detected_script,
                    "mode": "faq",
                    "audio": audio_output
                })

            else:
                # User asked something else - drop offer, answer their question
                grievance_sessions[session_id] = {"active": False, "offer_pending": False}
                # Fall through to PATH C

        # PATH C: Normal flow - classify intent first
        intent = classify_intent(user_input, history, user_language)
        logger.info(f"INTENT: {intent['intent']}, SERVICE: {intent.get('service')}, EMOTION: {intent.get('emotion')}")

        if intent["intent"] == "grievance_candidate":
            # User has a problem - OFFER grievance (don't auto-start)
            grievance_sessions[session_id] = {
                "active": False,
                "offer_pending": True,
                "pending_service": intent.get("service"),
                "pending_emotion": intent.get("emotion", "neutral")
            }
            offer_message = build_grievance_offer(
                service=intent.get("service"),
                emotion=intent.get("emotion", "neutral"),
                lang=user_language
            )
            audio_output = text_to_speech(offer_message, user_language)
            return jsonify({
                "response": offer_message,
                "lang": user_language,
                "mode": "grievance_offered",
                "detected_emotion": intent.get("emotion"),
                "audio": audio_output
            })

        # PATH D: Normal RAG flow (intent is "faq")
        # Apply domain filter AFTER intent classification
        in_domain, reason = is_in_domain(user_input)
        if not in_domain and reason == "out_of_domain":
            message = get_rejection_message("out_of_domain", user_language)
            audio_output = text_to_speech(message, user_language)
            logger.info(f"Domain rejected (faq path): {reason}")
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
            return jsonify({
                "response": message,
                "lang": user_language,
                "mode": "clarify",
                "audio": audio_output
            })

        response_text = retrieve_document(user_input, user_language, history)

        # Check if response is a grievance offer (from FAISS fallback)
        if response_text and ("शिकायत" in response_text or "grievance" in response_text.lower() or "एक शिकायत" in response_text):
            # This is a fallback grievance offer - change mode
            audio_output = text_to_speech(response_text, user_language)
            logger.info("Intent: faq but fallback offered grievance")
            return jsonify({
                "response": response_text,
                "lang": user_language,
                "mode": "grievance_offered",
                "audio": audio_output
            })

        # Branding fix
        response_text = re.sub(r'\bUpyog\b', 'UPYOG', response_text, flags=re.IGNORECASE)

        # Generate TTS (language matches detected language)
        audio_output = text_to_speech(response_text, user_language)

        return jsonify({
            "response": response_text,
            "lang": user_language,
            "detected_script": detected_script,
            "mode": "faq",
            "audio": audio_output
        }), 200

    except Exception as e:
        logger.error(f"Chat Error: {e}")
        return jsonify({"error": str(e)}), 500

"""
Streaming SSE endpoint — two route aliases:
  /stream                  → direct local access
  /upyog-voice-bot/stream  → production via niautt EKS ingress
GET requests return a health check response for Kubernetes liveness probes.
"""
@app.route("/stream", methods=["GET", "POST"])
@app.route("/upyog-voice-bot/stream", methods=["GET", "POST"])
def stream():
    """Streaming SSE endpoint for lower perceived latency."""
    if request.method == "GET":
        return jsonify({"status": "ok", "message": "UPYOG Voice Bot Stream Endpoint"}), 200

    global model, data, index, is_loading, stop_generation

    try:
        if is_loading or any(x is None for x in [model, data, index]):
            time.sleep(1)
            if any(x is None for x in [model, data, index]):
                return Response("data: {\"error\": \"Loading resources...\"}\n\n", mimetype='text/event-stream'), 503

        user_data = request.json
        user_input = user_data.get("query", "")
        # NOTE: Don't use lang from frontend - detect fresh per turn
        history = user_data.get("history", [])

        # DYNAMIC PER-TURN LANGUAGE DETECTION
        user_language, detected_script = detect_language_per_turn(user_input)
        logger.info(f"Stream language detection: '{user_input}' -> {user_language} (script: {detected_script})")

        return Response(retrieve_document_stream(user_input, user_language, history), mimetype='text/event-stream')

    except Exception as e:
        logger.error(f"Stream Error: {e}")
        return Response(f"data: {json.dumps({'type': 'error', 'error': str(e)})}\n\n", mimetype='text/event-stream')

"""
Stop endpoint — called when the user interrupts (barges in) while the bot is speaking.
Two route aliases:
  /stop                  → direct local access
  /upyog-voice-bot/stop  → production via niautt EKS ingress
"""
@app.route("/stop", methods=["POST"])
@app.route("/upyog-voice-bot/stop", methods=["POST"])
def stop():
    """Stop endpoint - called when user barges in."""
    global stop_generation
    stop_generation.set()
    logger.info("Stop signal received - aborting generation")
    return jsonify({"status": "stopped"}), 200

# ============== LLM INTENT CLASSIFIER ==============

def classify_intent(query: str, history: list, lang: str) -> dict:
    """
    Classifies user intent using a fast LLM call.
    Returns: {
        "intent": "faq" | "grievance_candidate" | "grievance_confirm" | "grievance_cancel",
        "reasoning": "brief explanation",
        "service": "which UPYOG service is involved, if any",
        "emotion": "neutral" | "frustrated" | "stuck" | "urgent"
    }
    """
    global groq_client

    # Build last 3 turns of context
    recent_history = history[-3:] if len(history) >= 3 else history
    history_text = "\n".join([
        f"{turn.get('role', 'user').upper()}: {turn.get('content', '')}"
        for turn in recent_history
    ])

    classifier_prompt = f"""You are an intent classifier for UPYOG —
a government urban services chatbot for Indian cities.

Classify the user message into exactly one category.

━━━ CATEGORY DEFINITIONS ━━━

"faq" — User wants INFORMATION or EXPLANATION.
They are learning, not complaining.
They use words like: what is, how does, kya hai, kaise hota hai,
batao, explain, tell me about, documents kya chahiye.
The problem described is HYPOTHETICAL or GENERAL, not personal.

Examples of faq:
→ "what is property mutation?"
→ "property tax kaise calculate hota hai?"
→ "trade license ke liye kya documents chahiye?"
→ "UPYOG mein kaun si services hain?"
→ "desludging service kya hoti hai?"
→ "complaint process kya hai?"  ← asking ABOUT process, not filing one
→ "fire NOC ke liye kya karna padta hai?"

"grievance_candidate" — User is describing a PERSONAL PROBLEM happening
RIGHT NOW to them specifically.
Key signals:
- Personal pronouns: I, my, mera, meri, mere, humara, hamare
- Time of suffering: days, weeks, months, din se, hafte se, mahine se
- Something not working FOR THEM: nahi aa raha, nahi hua, pending hai,
  reject ho gaya, kaam nahi kar raha, nahi mila, abhi tak nahi
- Frustration or helplessness: kaise karoon, kya karoon, pareshan hoon,
  help nahi kar rahe, action lena hai, complaint karni hai
- A specific service failing: pani, bijli, garbage, property, license,
  certificate, noc, registration, sewerage, road, light
- Asking for action not information: kuch karna chahta hoon,
  inke against action, shikayat darz karni hai

Examples of grievance_candidate:
→ "पानी नहीं आ रहा है बहुत दिन हो गए हैं"
→ "मैंने बहुत दिन पहले प्रॉपर्टी रजिस्टर करी थी अभी तक हो ही नहीं है"
→ "वह हेल्प नहीं कर रहे हैं मुझे इनके अगेंस्ट कुछ एक्शन लेना है"
→ "I am not able to pay my property tax online"
→ "mera trade license 3 mahine se pending hai"
→ "garbage not collected in my area for 2 weeks"
→ "birth certificate ke liye apply kiya tha but still pending"
→ "fire NOC reject ho gaya bina reason ke"
→ "nagar palika wale sun nahi rahe"
→ "building plan approve nahi ho raha kaafi time se"
→ "water supply band hai"
→ "mere ghar ke saamne road toot gayi है"
→ "sewerage overflow ho raha है"
→ "street light kharab है kaafi din se"
→ "sanitation worker nahi aata hamare area mein"

"grievance_confirm" — User is saying YES to bot's offer to file a grievance.
Only classify this if the PREVIOUS bot message was asking
"would you like to file a grievance?"
Examples: yes, haan, please, theek hai karo, file karo,
          bilkul, zaroor, go ahead, haan karein

"grievance_cancel" — User says NO to the grievance offer.
Examples: no, nahi, rehne do, chhodo, cancel,
          baad mein, abhi nahi, nevermind, bas

━━━ IMPORTANT RULES ━━━
1. When in doubt between faq and grievance_candidate,
   check: is the user describing THEIR OWN current problem?
   If YES → grievance_candidate
   If they are asking HOW something works → faq

2. Sentences with "nahi ho raha", "pending hai", "nahi aaya",
   "kaam nahi kar raha" about a government service
   → ALWAYS grievance_candidate

3. Short frustrated sentences like "sun nahi rahe",
   "help nahi kar rahe", "kuch karo"
   → grievance_candidate (read with conversation context)

━━━ CONVERSATION CONTEXT (last 3 turns) ━━━
{history_text}

━━━ CURRENT MESSAGE ━━━
"{query}"
Language: {lang}

Respond ONLY with this JSON, no other text:
{{
  "intent": "faq" | "grievance_candidate" | "grievance_confirm" | "grievance_cancel",
  "reasoning": "one sentence why",
  "service": "specific UPYOG service name or null",
  "emotion": "neutral" | "frustrated" | "stuck" | "urgent"
}}"""

    try:
        if not groq_client:
            groq_client = Groq(api_key=GROQ_API_KEY)

        response = groq_client.chat.completions.create(
            model="llama-3.1-8b-instant",
            messages=[{"role": "user", "content": classifier_prompt}],
            max_tokens=150,
            temperature=0.1
        )

        raw = response.choices[0].message.content.strip()
        raw = raw.replace("```json", "").replace("```", "").strip()
        result = json.loads(raw)

        valid_intents = ["faq", "grievance_candidate", "grievance_confirm", "grievance_cancel"]
        if result.get("intent") not in valid_intents:
            result["intent"] = "faq"

        return result

    except Exception as e:
        logger.error(f"Intent classifier error: {e}")
        return {"intent": "faq", "reasoning": "classifier failed", "service": None, "emotion": "neutral"}


def build_grievance_offer(service: str, emotion: str, lang: str) -> str:
    """
    Builds an empathetic offer to file a grievance.
    """
    service_text = f" {service}" if service else ""
    service_text_hi = f" {service} के बारे में" if service else ""

    if emotion in ["frustrated", "urgent"]:
        if lang == 'hi':
            return (
                f"मैं समझ सकता हूँ कि यह स्थिति परेशान करने वाली है।"
                f"{service_text_hi} आपकी समस्या को आधिकारिक रूप से दर्ज कराने के लिए "
                f"क्या मैं आपके लिए एक शिकायत दर्ज करूँ? "
                f"इससे संबंधित विभाग को सूचित किया जाएगा और आपको एक ट्रैकिंग नंबर मिलेगा।"
            )
        else:
            return (
                f"I understand this situation must be frustrating for you. "
                f"Would you like me to file an official grievance{service_text}? "
                f"This will notify the concerned department and you'll receive a tracking number."
            )
    else:
        if lang == 'hi':
            return (
                f"लगता है आपको{service_text_hi} एक समस्या आ रही है। "
                f"क्या आप चाहेंगे कि मैं इसके लिए एक आधिकारिक शिकायत दर्ज करूँ? "
                f"हाँ कहें तो मैं प्रक्रिया शुरू करता हूँ।"
            )
        else:
            return (
                f"It seems you're facing an issue{service_text}. "
                f"Would you like me to file an official grievance for this? "
                f"Just say yes and I'll guide you through the process."
            )


# ============== GRIEVANCE FLOW ==============
# Sourced from app7.py - UPYOG Public Grievance Redressal

GRIEVANCE_API_BASE = "https://niuatt.niua.in"
GRIEVANCE_TENANT_ID = "pg.citya"
GRIEVANCE_MDMS_TENANT = "pg"
GRIEVANCE_HEADERS = {"Content-Type": "application/x-www-form-urlencoded", "Authorization": os.environ.get("UPYOG_BASIC_AUTH")}

# Grievance session storage
grievance_sessions = {}

def get_grievance_session(session_id):
    """Get or create grievance session for a session ID."""
    if session_id not in grievance_sessions:
        grievance_sessions[session_id] = {
            "active": False,
            "step": "START",
            "data": {},
            "auth_token": None,
            "user_info": None,
            "categories": {},
            "localities": []
        }
    return grievance_sessions[session_id]

def clear_grievance_session(session_id):
    """Clear grievance session after completion or cancellation."""
    grievance_sessions.pop(session_id, None)

def generate_otp(mobile):
    """Send OTP to mobile number."""
    url = f"{GRIEVANCE_API_BASE}/user/otp/generate"
    payload = {
        "otp": {
            "mobileNumber": mobile,
            "type": "login",
            "tenantId": GRIEVANCE_MDMS_TENANT,
            "userType": "CITIZEN"
        },
        "RequestInfo": {"apiId": "Rainmaker", "msgId": "1|en_IN"}
    }
    try:
        res = requests.post(url, json=payload)
        return res.json()
    except Exception as e:
        logger.error(f"OTP Generation Error: {e}")
        return {"error": str(e)}

def verify_otp(mobile, otp):
    """Verify OTP and get auth token."""
    url = f"{GRIEVANCE_API_BASE}/user/oauth/token"
    data = {
        "username": mobile,
        "password": otp,
        "grant_type": "password",
        "scope": "read",
        "tenantId": GRIEVANCE_MDMS_TENANT,
        "userType": "CITIZEN"
    }
    try:
        res = requests.post(url, data=data, headers=GRIEVANCE_HEADERS)
        return res.json()
    except Exception as e:
        logger.error(f"OTP Verification Error: {e}")
        return {"error": str(e)}

def fetch_categories(auth_token):
    """Fetch grievance categories from MDMS."""
    url = f"{GRIEVANCE_API_BASE}/mdms-v2/v1/_search?tenantId={GRIEVANCE_MDMS_TENANT}"
    payload = {
        "MdmsCriteria": {
            "tenantId": GRIEVANCE_MDMS_TENANT,
            "moduleDetails": [{"moduleName": "RAINMAKER-PGR", "masterDetails": [{"name": "ServiceDefs"}]}]
        },
        "RequestInfo": {"apiId": "Rainmaker", "authToken": auth_token, "msgId": "1|en_IN", "plainAccessRequest": {}}
    }
    try:
        res = requests.post(url, json=payload)
        data = res.json()
        defs = data.get("MdmsRes", {}).get("RAINMAKER-PGR", {}).get("ServiceDefs", [])
        structured = {}
        for d in defs:
            if not d.get("active", True): continue
            menu = d.get("menuPath") or "Others"
            if menu not in structured: structured[menu] = []
            structured[menu].append({"name": d["name"], "code": d["serviceCode"]})
        return structured
    except Exception as e:
        logger.error(f"MDMS Categories Fetch Error: {e}")
        return {}

def fetch_localities(auth_token):
    """Fetch localities for the tenant."""
    url = f"{GRIEVANCE_API_BASE}/egov-location/location/v11/boundarys/_search?hierarchyTypeCode=ADMIN&boundaryType=Locality&tenantId={GRIEVANCE_TENANT_ID}"
    payload = {
        "RequestInfo": {"apiId": "Rainmaker", "authToken": auth_token, "msgId": "1|en_IN", "plainAccessRequest": {}}
    }
    try:
        res = requests.post(url, json=payload)
        data = res.json()
        boundaries = data.get("TenantBoundary", [])
        if boundaries and boundaries[0].get("boundary"):
            return [{"name": b["name"], "code": b["code"]} for b in boundaries[0]["boundary"]]
        return []
    except Exception as e:
        logger.error(f"MDMS Localities Fetch Error: {e}")
        return []

def create_grievance(auth_token, user_info, grievance_data):
    """Create grievance via PGR API."""
    url = f"{GRIEVANCE_API_BASE}/pgr-services/v2/request/_create?tenantId={GRIEVANCE_TENANT_ID}"

    citizen_block = {
        "id": user_info.get("id"),
        "userName": user_info.get("userName") or user_info.get("mobileNumber"),
        "name": user_info.get("name"),
        "type": user_info.get("type", "CITIZEN"),
        "mobileNumber": user_info.get("mobileNumber"),
        "emailId": user_info.get("emailId", ""),
        "roles": user_info.get("roles", [{"id": None, "name": "Citizen", "code": "CITIZEN", "tenantId": "pg"}]),
        "tenantId": user_info.get("tenantId", "pg"),
        "uuid": user_info.get("uuid")
    }

    payload = {
        "service": {
            "tenantId": GRIEVANCE_TENANT_ID,
            "serviceCode": grievance_data.get("category_code"),
            "accountId": user_info.get("uuid"),
            "citizen": citizen_block,
            "priority": "HIGH",
            "description": grievance_data.get("description", ""),
            "additionalDetail": {},
            "source": "web",
            "address": {
                "tenantId": GRIEVANCE_TENANT_ID,
                "landmark": grievance_data.get("address_manual", ""),
                "city": "New Delhi",
                "district": "New Delhi",
                "region": "New Delhi",
                "state": "Demo",
                "pincode": grievance_data.get("pincode") or "143001",
                "locality": {
                    "code": grievance_data.get("locality_code", ""),
                    "name": grievance_data.get("locality_name", "")
                },
                "geoLocation": {"latitude": 0.0, "longitude": 0.0}
            }
        },
        "workflow": {"action": "APPLY", "comments": "", "assignes": []},
        "RequestInfo": {
            "apiId": "Rainmaker",
            "ver": ".01",
            "ts": "",
            "action": "_create",
            "did": "1",
            "key": "",
            "authToken": auth_token,
            "msgId": f"{int(time.time() * 1000)}|en_IN",
            "plainAccessRequest": {},
            "userInfo": user_info
        }
    }

    try:
        res = requests.post(url, json=payload)
        resp_data = res.json()
        logger.info(f"PGR Create Response: {resp_data}")

        if "Errors" in resp_data:
            logger.error(f"PGR Create Failed: {resp_data}")
            return {"success": False, "error": resp_data.get("Errors", "Unknown error")}

        sw_list = resp_data.get("ServiceWrappers", [])
        if not sw_list:
            return {"success": False, "error": "No ServiceWrappers in response"}

        ticket_id = sw_list[0].get("service", {}).get("serviceRequestId")
        if ticket_id:
            return {"success": True, "ticket_number": ticket_id, "raw": resp_data}
        return {"success": False, "error": "No ticket number returned"}

    except Exception as e:
        logger.error(f"PGR Create Exception: {e}")
        return {"success": False, "error": str(e)}

# is_grievance_intent removed - replaced by LLM-based classify_intent() function

def is_cancel_intent(text: str) -> bool:
    """Detect if user wants to cancel the grievance flow."""
    text_lower = text.lower()

    # Check for Devanagari range in Unicode
    for c in text:
        if 'ऀ' <= c <= 'ॿ':
            return True  # If Devanagari present, assume might be cancellation

    cancel_patterns = [
        r'\b(cancel|cancelled|canceling|nevermind|never\s*mind)\b',
        r'\b(chhod|chhodo|chhoda|chodna)\b',
        r'\b(skip|forget\s*it)\b',
        r"\b(stop|don.?t\s+want|don.?t\s+think\s+so)\b"
    ]

    for pattern in cancel_patterns:
        if re.search(pattern, text_lower):
            return True

    return False

def is_confirmation(text: str) -> bool:
    """Detect if user confirmed or said yes."""
    text_lower = text.lower()

    # Check for Devanagari range
    for c in text:
        if 'ऀ' <= c <= 'ॿ':
            return True

    confirm_patterns = [
        r'\b(yes|yeah|yup|ya|ok|okay|sure|confirm|proceed|go\s*ahead)\b',
        r'\b(haan|theek|karo|dijiyega|dijiye)\b',
        r'\b(submit|register|file)\b'
    ]

    for pattern in confirm_patterns:
        if re.search(pattern, text_lower):
            return True

    return False

def is_negative(text: str) -> bool:
    """Detect if user said no or rejected."""
    text_lower = text.lower()

    # Check for Devanagari range
    for c in text:
        if 'ऀ' <= c <= 'ॿ':
            return True

    negative_patterns = [
        r'\b(no|nope|nah|not|nothing|don?t\s+want|cancel)\b'
    ]

    for pattern in negative_patterns:
        if re.search(pattern, text_lower):
            return True

    return False

# ── Digit word mappings ──────────────────────────────────────
DIGIT_MAP_EN = {
    'zero':'0','one':'1','two':'2','three':'3','four':'4',
    'five':'5','six':'6','seven':'7','eight':'8','nine':'9',
    'oh':'0','o':'0','nought':'0',
}

DIGIT_MAP_HI_DEVANAGARI = {
    'शून्य':'0','एक':'1','दो':'2','तीन':'3','चार':'4',
    'पाँच':'5','पांच':'5','छह':'6','छः':'6','सात':'7',
    'आठ':'8','नौ':'9',
}

DIGIT_MAP_HI_ROMAN = {
    'shunya':'0','ek':'1','do':'2','teen':'3','char':'4',
    'paanch':'5','panch':'5','chhe':'6','chah':'6','chhah':'6',
    'saat':'7','aath':'8','nau':'9','nao':'9','nav':'9',
}

ALL_DIGIT_MAPS = {**DIGIT_MAP_EN, **DIGIT_MAP_HI_DEVANAGARI, **DIGIT_MAP_HI_ROMAN}

MULTIPLIER_MAP = {
    'once': 1, 'twice': 2, 'double': 2, 'triple': 3, 'thrice': 3,
    'two times': 2, 'three times': 3, 'four times': 4, 'five times': 5,
    'six times': 6, 'seven times': 7, 'eight times': 8, 'nine times': 9,
    'ten times': 10,
    'ek baar': 1, 'do baar': 2, 'teen baar': 3, 'char baar': 4,
    'paanch baar': 5, 'chhe baar': 6, 'saat baar': 7, 'aath baar': 8,
    'nau baar': 9, 'das baar': 10, 'dus baar': 10,
    'ek bari': 1, 'do bari': 2, 'teen bari': 3, 'char bari': 4,
    'paanch bari': 5, 'das bari': 10, 'dus bari': 10,
    'एक बार': 1, 'दो बार': 2, 'तीन बार': 3, 'चार बार': 4,
    'पाँच बार': 5, 'दस बार': 10,
}

def normalize_spoken_number(text: str) -> str:
    if not text: return text
    original = text.strip()
    cleaned = re.sub(r'[\s\-]', '', original)
    if cleaned.isdigit(): return cleaned

    text_lower = original.lower()
    result_digits = []
    sorted_multipliers = sorted(MULTIPLIER_MAP.keys(), key=len, reverse=True)
    working = text_lower
    
    for phrase in sorted_multipliers:
        if phrase in working:
            count = MULTIPLIER_MAP[phrase]
            pattern = re.escape(phrase) + r'\s+(\w+)'
            match = re.search(pattern, working)
            if match:
                following_word = match.group(1)
                digit = ALL_DIGIT_MAPS.get(following_word, None)
                if digit is None and following_word.isdigit(): digit = following_word
                if digit:
                    replacement = digit * count
                    working = working[:match.start()] + replacement + working[match.end():]
                    continue
            
            if phrase in ('double', 'twice'):
                pattern2 = r'double\s+(\w+)|twice\s+(\w+)'
                for m in re.finditer(pattern2, working):
                    word = m.group(1) or m.group(2)
                    digit = ALL_DIGIT_MAPS.get(word, word if word.isdigit() else None)
                    if digit:
                        working = working.replace(m.group(0), digit * 2, 1)
            if phrase == 'triple':
                pattern3 = r'triple\s+(\w+)'
                for m in re.finditer(pattern3, working):
                    word = m.group(1)
                    digit = ALL_DIGIT_MAPS.get(word, word if word.isdigit() else None)
                    if digit:
                        working = working.replace(m.group(0), digit * 3, 1)

    FILLERS = {'and','aur','then','phir','comma','point','dot','please','ok','okay','um','uh','er','hmm','mera','number','hai','my','is','the','it'}
    tokens = re.split(r'[\s,\-]+', working)
    for token in tokens:
        token = token.strip().lower()
        if not token or token in FILLERS: continue
        if token.isdigit():
            result_digits.append(token)
            continue
        digit = ALL_DIGIT_MAPS.get(token)
        if digit:
            result_digits.append(digit)
            continue
        devanagari_digit = ''
        all_deva = True
        for ch in token:
            if '\u0966' <= ch <= '\u096F':
                devanagari_digit += str(ord(ch) - ord('\u0966'))
            else:
                all_deva = False
                break
        if all_deva and devanagari_digit:
            result_digits.append(devanagari_digit)
            continue

    result = ''.join(result_digits)
    return result if result else original

def normalize_field(field_type: str, value: str) -> str:
    if field_type in ('mobile', 'phone', 'otp', 'pincode', 'number'):
        normalized = normalize_spoken_number(value)
        print(f"[NORMALIZE] {field_type}: '{value}' → '{normalized}'")
        return normalized
    return value.strip()

def validate_field(field_type: str, value: str) -> tuple:
    if field_type == 'mobile':
        digits_only = re.sub(r'\D', '', value)
        if len(digits_only) != 10:
            return False, f"मुझे 10 अंकों का मोबाइल नंबर चाहिए। आपने {len(digits_only)} अंक दिए। कृपया दोबारा बोलें।"
        return True, None
    if field_type == 'otp':
        digits_only = re.sub(r'\D', '', value)
        if len(digits_only) not in (4, 6):
            return False, f"OTP 4 या 6 अंकों का होना चाहिए। कृपया दोबारा बोलें।"
        return True, None
    return True, None

def handle_grievance_turn(session_id, user_input, lang):
    """Handle a single turn in the grievance flow."""
    session = get_grievance_session(session_id)
    step = session["step"]

    # Check for cancellation
    if is_cancel_intent(user_input):
        clear_grievance_session(session_id)
        if lang == 'hi':
            return {"type": "cancelled", "message": "ठीक है, शिकायत दर्ज नहीं की गई। आप किस और विषय पर पूछना चाहेंगे?", "lang": lang}
        return {"type": "cancelled", "message": "OK, complaint not registered. What else can I help you with?", "lang": lang}

    # Step handlers
    if step == "START":
        session["active"] = True
        session["step"] = "AWAITING_PHONE"
        if lang == 'hi':
            return {"type": "collect", "message": "शिकायत दर्ज करने के लिए पहले आपका मोबाइल नंबर चाहिए। कृपया अपना 10 अंकों का मोबाइल नंबर बताएं।", "lang": lang, "field": "mobile", "input_type": "number", "options": []}
        return {"type": "collect", "message": "To register a complaint, I need your mobile number first. Please tell me your 10-digit mobile number.", "lang": lang, "field": "mobile", "input_type": "number", "options": []}

    elif step == "AWAITING_PHONE":
        normalized_value = normalize_field("mobile", user_input)
        is_valid, error_msg = validate_field("mobile", normalized_value)
        if not is_valid:
            if lang != 'hi':
                digits_count = len(re.sub(r'\D', '', normalized_value))
                error_msg = f"I need a 10-digit mobile number. You provided {digits_count} digits. Please try again."
            return {"type": "collect", "message": error_msg, "lang": lang, "field": "mobile", "input_type": "number", "options": []}

        mobile = re.sub(r'\D', '', normalized_value)
        session["data"]["mobile"] = mobile
        session["step"] = "AWAITING_OTP"

        # Send OTP
        otp_result = generate_otp(mobile)
        logger.info(f"OTP sent: {otp_result}")

        if lang == 'hi':
            return {"type": "collect", "message": f"OTP आपके मोबाइल नंबर {mobile} पर भेजा गया है। कृपया 6 अंकों का OTP बताएं।", "lang": lang, "field": "otp", "input_type": "number", "options": []}
        return {"type": "collect", "message": f"An OTP has been sent to your mobile number {mobile}. Please tell me the 6-digit OTP.", "lang": lang, "field": "otp", "input_type": "number", "options": []}

    elif step == "AWAITING_OTP":
        normalized_value = normalize_field("otp", user_input)
        is_valid, error_msg = validate_field("otp", normalized_value)
        if not is_valid:
            if lang != 'hi':
                error_msg = "OTP should be 4 or 6 digits. Please tell me the correct OTP."
            return {"type": "collect", "message": error_msg, "lang": lang, "field": "otp", "input_type": "number", "options": []}

        otp_value = re.sub(r'\D', '', normalized_value)
        mobile = session["data"]["mobile"]

        # Verify OTP
        verify_result = verify_otp(mobile, otp_value)
        logger.info(f"OTP verify result keys: {verify_result.keys() if isinstance(verify_result, dict) else 'N/A'}")

        if "access_token" not in verify_result:
            if lang == 'hi':
                return {"type": "error", "message": "OTP गलत है। कृपया पुनः प्रयास करें।", "lang": lang}
            return {"type": "error", "message": "OTP is incorrect. Please try again.", "lang": lang}

        session["data"]["auth_token"] = verify_result["access_token"]
        session["data"]["user_info"] = verify_result.get("UserRequest", verify_result.get("userInfo", {}))
        
        # Fetch categories
        categories = fetch_categories(session["data"]["auth_token"])
        session["categories"] = categories

        if not categories:
            if lang == 'hi':
                return {"type": "error", "message": "श्रेणियां लोड नहीं हो सकीं। कृपया कुछ देर बाद पुनः प्रयास करें।", "lang": lang}
            return {"type": "error", "message": "Could not load categories. Please try again later.", "lang": lang}

        cat_list = list(categories.keys())
        session["step"] = "AWAITING_CATEGORY"
        session["data"]["category_group"] = None

        if lang == 'hi':
            return {"type": "collect", "message": f"श्रेणी चुनें: {', '.join(cat_list)}", "lang": lang, "field": "category", "input_type": "choice", "options": cat_list}
        return {"type": "collect", "message": f"Please choose a category: {', '.join(cat_list)}", "lang": lang, "field": "category", "input_type": "choice", "options": cat_list}

    elif step == "AWAITING_CATEGORY":
        categories = session["categories"]
        selected_cat = None
        cat_list = list(categories.keys())

        for cat_name in categories.keys():
            if cat_name.lower() in user_input.lower():
                selected_cat = cat_name
                break

        if not selected_cat:
            if lang == 'hi':
                return {"type": "collect", "message": f"मुझे श्रेणी समझ नहीं आया। कृपया इनमें से चुनें:", "lang": lang, "field": "category", "input_type": "choice", "options": cat_list}
            return {"type": "collect", "message": f"I didn't understand. Please choose from:", "lang": lang, "field": "category", "input_type": "choice", "options": cat_list}

        session["data"]["category_group"] = selected_cat
        session["data"]["available_subs"] = categories[selected_cat]
        session["step"] = "AWAITING_SUB_CATEGORY"

        sub_list = [s["name"] for s in categories[selected_cat]]
        if lang == 'hi':
            return {"type": "collect", "message": f"आप किस प्रकार की शिकायत करना चाहते हैं:", "lang": lang, "field": "sub_category", "input_type": "choice", "options": sub_list}
        return {"type": "collect", "message": f"What type of complaint:", "lang": lang, "field": "sub_category", "input_type": "choice", "options": sub_list}

    elif step == "AWAITING_SUB_CATEGORY":
        subs = session["data"].get("available_subs", [])
        selected_sub = None
        sub_list = [s["name"] for s in subs]

        for s in subs:
            if s["name"].lower() in user_input.lower():
                selected_sub = s
                break

        if not selected_sub:
            if lang == 'hi':
                return {"type": "collect", "message": f"मुझे समझ नहीं आया। कृपया चुनें:", "lang": lang, "field": "sub_category", "input_type": "choice", "options": sub_list}
            return {"type": "collect", "message": f"I didn't understand. Please choose:", "lang": lang, "field": "sub_category", "input_type": "choice", "options": sub_list}

        session["data"]["category_code"] = selected_sub["code"]
        session["data"]["category_name"] = selected_sub["name"]
        session["step"] = "AWAITING_DESCRIPTION"

        if lang == 'hi':
            return {"type": "collect", "message": "अब कृपया अपनी शिकायत का विवरण लिखें। समस्या क्या है?", "lang": lang, "field": "description", "input_type": "text", "options": []}
        return {"type": "collect", "message": "Now please describe your complaint. What is the issue?", "lang": lang, "field": "description", "input_type": "text", "options": []}

    elif step == "AWAITING_DESCRIPTION":
        if len(user_input.strip()) < 10:
            if lang == 'hi':
                return {"type": "collect", "message": "कृपया थोड़ा विस्तार से बताएं कि समस्या क्या है।", "lang": lang, "field": "description", "input_type": "text", "options": []}
            return {"type": "collect", "message": "Please describe the problem in more detail.", "lang": lang, "field": "description", "input_type": "text", "options": []}

        session["data"]["description"] = user_input
        session["step"] = "AWAITING_LOCALITY"

        # Fetch localities
        localities = fetch_localities(session["data"]["auth_token"])
        session["localities"] = localities

        if not localities:
            # Skip locality if not available
            session["step"] = "CONFIRM"
            session["data"]["locality_code"] = ""
            session["data"]["locality_name"] = ""
            return build_confirmation(session, lang)

        loc_list = [l["name"] for l in localities[:20]]  # Limit to first 20
        if lang == 'hi':
            return {"type": "collect", "message": f"अपना इलाका/क्षेत्र चुनें:", "lang": lang, "field": "locality", "input_type": "choice", "options": loc_list}
        return {"type": "collect", "message": f"Please choose your area/locality:", "lang": lang, "field": "locality", "input_type": "choice", "options": loc_list}

    elif step == "AWAITING_LOCALITY":
        localities = session["localities"]
        selected_loc = None
        loc_list = [l["name"] for l in localities[:20]]

        for loc in localities:
            if loc["name"].lower() in user_input.lower():
                selected_loc = loc
                break

        if not selected_loc and len(user_input.strip()) > 2:
            # Use custom locality if user typed something
            selected_loc = {"name": user_input.strip(), "code": ""}

        if not selected_loc:
            if lang == 'hi':
                return {"type": "collect", "message": f"मुझे इलाका समझ नहीं आया। कृपया चुनें:", "lang": lang, "field": "locality", "input_type": "choice", "options": loc_list}
            return {"type": "collect", "message": f"I didn't understand. Please choose:", "lang": lang, "field": "locality", "input_type": "choice", "options": loc_list}

        session["data"]["locality_code"] = selected_loc["code"]
        session["data"]["locality_name"] = selected_loc["name"]
        session["step"] = "CONFIRM"

        return build_confirmation(session, lang)

    elif step == "CONFIRM":
        if is_negative(user_input):
            clear_grievance_session(session_id)
            if lang == 'hi':
                return {"type": "cancelled", "message": "ठीक है, शिकायत दर्ज नहीं की गई। आप किस और विषय पर पूछना चाहेंगे?", "lang": lang}
            return {"type": "cancelled", "message": "OK, complaint not registered. What else can I help you with?", "lang": lang}

        if not is_confirmation(user_input):
            return build_confirmation(session, lang)

        # User confirmed - submit grievance
        result = create_grievance(
            session["data"]["auth_token"],
            session["data"]["user_info"],
            session["data"]
        )

        clear_grievance_session(session_id)

        if result["success"]:
            ticket = result["ticket_number"]
            if lang == 'hi':
                return {"type": "done", "message": f"आपकी शिकायत सफलतापूर्वक दर्ज हो गई। आपका टिकट नंबर है: {ticket}", "lang": lang}
            return {"type": "done", "message": f"Your complaint has been registered successfully. Your ticket number is: {ticket}", "lang": lang}
        else:
            error_msg = result.get("error", "Unknown error")
            if lang == 'hi':
                return {"type": "error", "message": f"शिकायत दर्ज करने में समस्या हुई: {error_msg}", "lang": lang}
            return {"type": "error", "message": f"Failed to submit complaint: {error_msg}", "lang": lang}

    # Fallback
    if lang == 'hi':
        return {"type": "error", "message": "कुछ गलत हो गया। कृपया फिर से शुरू करें।", "lang": lang}
    return {"type": "error", "message": "Something went wrong. Please start again.", "lang": lang}

def build_confirmation(session, lang):
    """Build confirmation summary for the user."""
    data = session["data"]
    summary_en = f"Please confirm your complaint details:\n\nCategory: {data.get('category_name', 'N/A')}\nDescription: {data.get('description', 'N/A')}\nLocality: {data.get('locality_name', 'N/A')}\n\nSay 'yes' to submit or 'no' to cancel."
    summary_hi = f"कृपया अपनी शिकायत की जानकारी की पुष्टि करें:\n\nश्रेणी: {data.get('category_name', 'N/A')}\nविवरण: {data.get('description', 'N/A')}\nइलाका: {data.get('locality_name', 'N/A')}\n\nदर्ज करने के लिए 'हाँ' बोलें या रद्द करने के लिए 'नहीं'।"

    session["step"] = "CONFIRM"
    return {"type": "confirm", "message": summary_hi if lang == 'hi' else summary_en, "lang": lang}

if __name__ == "__main__":
    try:
        logger.info("Starting UPYOG Voice Assistant v2 on port 8090...")
        app.run(host='0.0.0.0', port=8090)
    except Exception as e:
        logger.error(f"Error starting Flask application: {e}")
        raise