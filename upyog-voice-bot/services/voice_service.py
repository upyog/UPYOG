import os
import re
import json
import logging
import asyncio
import tempfile
import base64
import requests
import threading
import edge_tts
from dotenv import load_dotenv

load_dotenv()

logger = logging.getLogger(__name__)

# Global state for stop flag (barge-in interruption)
stop_generation = threading.Event()

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

# English words commonly transliterated into Devanagari
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

    distinct_hindi = {
        'kya', 'kaise', 'kahan', 'kyun', 'kaun', 'kab', 'kitna', 'kitni',
        'mujhe', 'aapko', 'mera', 'meri', 'mere', 'humara', 'hamare', 'apna', 'apni',
        'nahin', 'nahi', 'haan', 'theek', 'accha', 'achha',
        'batao', 'chahiye', 'milega', 'karo', 'karein', 'dijiye', 'bataye', 'bataiye',
        'dikhao', 'dikhaye', 'hatao', 'mitado', 'shikayat', 'namaste', 'dhanyawad',
        'samasya', 'paani', 'sadak', 'kachra', 'bijli'
    }

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


def detect_language_per_turn(text: str) -> tuple:
    info = detect_language(text)
    return info['lang'], info['script']


# ============== BHASHINI TRANSLATION ==============

def translate_text_bhashini(text: str, source_lang: str, target_lang: str):
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
        response = requests.post(BHASHINI_URL, headers=BHASHINI_HEADERS, json=payload, timeout=10)
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


def translate_text(text: str, source_lang: str, target_lang: str) -> str:
    if source_lang == target_lang or not text:
        logger.debug(f"[TRANSLATE] Skipping translation since source_lang ({source_lang}) == target_lang ({target_lang})")
        return text
    translated = translate_text_bhashini(text, source_lang, target_lang)
    if translated:
        return translated
    logger.warning("[TRANSLATE] Bhashini translation returned None, falling back to original text.")
    return text


# ============== TEXT-TO-SPEECH (TTS) ==============

async def generate_edge_tts(text: str, voice: str, output_path: str):
    communicate = edge_tts.Communicate(text, voice)
    await communicate.save(output_path)


# Pre-compiled Pipeline for Fast TTS Text Sanitization
_TTS_REGEX_PIPELINE = [
    (re.compile(r'<ui-[^>]*>'), ''),                                                       # UI tags
    (re.compile(r'\[(?:CANCEL_DRAFT|RELOAD|CLOSE|SUBMIT|OPTION|seed:\d+)\]', re.I), ''),      # Internal commands
    (re.compile(r'```[\s\S]*?```'), ''),                                                   # Code blocks
    (re.compile(r'`([^`]+)`'), r'\1'),                                                     # Inline code
    (re.compile(r'!\[[^\]]*\]\([^\)]+\)'), ''),                                            # Images
    (re.compile(r'\[([^\]]+)\]\([^\)]+\)'), r'\1'),                                        # Links
    (re.compile(r'(?m)^\s*(?:#{1,6}|[-*_]{3,}|>\s*|[-*+]\s+)\s*'), ''),                   # Headers, HRs, quotes, bullets
    (re.compile(r'[*~_]{1,3}([^*~_]+)[*~_]{1,3}'), r'\1'),                                 # Bold / Italic / Strike
    (re.compile(r'([A-Za-z0-9]+)[-_]([A-Za-z0-9]+)'), r'\1 \2'),                           # Hyphenated IDs/Dates -> Space (no "dash dash")
    (re.compile(r'[\U00010000-\U0010ffff\U00002600-\U000027BF\U0000FE00-\U0000FE0F|•–—\-\#\*~]'), ' '), # Emojis, pipes & symbols
    (re.compile(r'\s+'), ' ')                                                              # Normalize whitespace
]

_TTS_BRANDING_COMPILED = {
    "hi": [(re.compile(r'\bUPYOG\b|Upyog', re.I), 'उपयोग'), (re.compile(r'\bNUDM\b'), 'एन.यू.डी.एम.'), (re.compile(r'\bMoHUA\b'), 'मोहुआ')],
    "en": [(re.compile(r'\bUPYOG\b|Upyog', re.I), 'Oop-yog'), (re.compile(r'\bNUDM\b'), 'N-U-D-M'), (re.compile(r'\bMoHUA\b'), 'Mo-hua')]
}


def clean_text_for_tts(text: str, language_code: str = "en") -> str:
    """Fast, pre-compiled markdown and symbol sanitizer for natural voice synthesis."""
    if not text:
        return ""

    for pattern, replacement in _TTS_REGEX_PIPELINE:
        text = pattern.sub(replacement, text)

    for pattern, replacement in _TTS_BRANDING_COMPILED.get(language_code, _TTS_BRANDING_COMPILED["en"]):
        text = pattern.sub(replacement, text)

    return text.strip()


def text_to_speech(text: str, language_code: str, gender: str = "female"):
    """Convert text to speech using Edge-TTS with Bhashini fallback."""
    logger.info(f"[TTS GENERATION] Input text length: {len(text)} | Language: '{language_code}' | Gender: '{gender}'")

    # Clean markdown, headers and apply pronunciation rules
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
        response = requests.post(BHASHINI_URL, headers=BHASHINI_HEADERS, json=payload, timeout=15)
        if response.status_code == 200:
            audio_data = response.json()["pipelineResponse"][0]["audio"][0]["audioContent"]
            logger.info(f"[TTS BHASHINI] Successfully generated audio. Payload length: {len(audio_data)} chars")
            return audio_data
        logger.error(f"[TTS BHASHINI] Bhashini status code {response.status_code}: {response.text}")
        return None
    except Exception as e:
        logger.error(f"[TTS BHASHINI] Bhashini TTS Error: {e}")
        return None
