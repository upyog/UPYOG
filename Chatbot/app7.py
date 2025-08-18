'''
Steps to Run the code
Navigate to the Project Directory
Open your terminal and navigate to the directory where app7.py is located:cd/filepath
Create and Activate Virtual Environment Create a virtual environment in your project directory
Activate it
bash:python3 -m venv env
bash:source env/bin/activate
After activation, your prompt should look like this: (env) nisheetagrawal@Nisheets-MacBook-Pro-2 chatbot_dep %
Install Dependencies
Install the required libraries within the virtual environment:
bash : pip install flask flask-cors sentence-transformers faiss-cpu numpy pandas requests langdetect scikit-learn googletrans==4.0.0-rc1
'''

from flask import Flask, request, jsonify
from flask_cors import CORS
from sentence_transformers import SentenceTransformer
import faiss
import numpy as np
import pandas as pd
import os
import requests
from langdetect import detect, DetectorFactory
from langdetect.lang_detect_exception import LangDetectException
from sklearn.metrics.pairwise import cosine_similarity
from googletrans import Translator
import logging
from functools import lru_cache
import threading

# Set up logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Ensure consistent language detection
DetectorFactory.seed = 0

# Disable parallelism for tokenizers
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Global variables for lazy loading
model = None
data = None
index = None
google_translator = None
prompt_embeddings = None
is_loading = False
load_lock = threading.Lock()

def load_resources():
    """Load all required resources."""
    global model, data, index, google_translator, prompt_embeddings, is_loading
    
    with load_lock:
        if is_loading:
            return
        
        is_loading = True
        try:
            # Load FAQ data
            data = pd.read_csv('UpyogFAQ.csv')
            logger.info("FAQ data loaded successfully.")
            
            # Initialize SentenceTransformer model and generate embeddings
            model = SentenceTransformer('all-mpnet-base-v2')
            prompt_embeddings = model.encode(data['prompt'].tolist())
            logger.info("Embeddings generated successfully.")
            
            # Initialize FAISS index and add embeddings
            dimension = prompt_embeddings.shape[1]
            index = faiss.IndexFlatL2(dimension)
            index.add(prompt_embeddings.astype(np.float32))
            logger.info("FAISS index initialized and embeddings added.")
            
            # Initialize Google Translator
            google_translator = Translator()
            logger.info("All resources loaded successfully.")
            
        except Exception as e:
            logger.error(f"Error loading resources: {e}")
            raise
        finally:
            is_loading = False

# Start loading resources in background
threading.Thread(target=load_resources, daemon=True).start()

# GoogleTrans Supported Languages
GOOGLE_TRANS_SUPPORTED_LANGUAGES = {
    "as": "Assamese",
    "bn": "Bengali",
    "gu": "Gujarati",
    "hi": "Hindi",
    "kn": "Kannada",
    "ml": "Malayalam",
    "mr": "Marathi",
    "ne": "Nepali",
    "or": "Oriya",
    "pa": "Punjabi",
    "ta": "Tamil",
    "te": "Telugu",
    "ur": "Urdu"
}

# Bhashini API details to be replaced with organisation email later
BHASHINI_URL = "https://dhruva-api.bhashini.gov.in/services/inference/pipeline"
BHASHINI_HEADERS = {
    "Content-Type": "application/json",
    "ulcaApiKey": "2d6f1cf41fbb4befa9e7209a0b64e5da", 
    "userID": "5047f9b849-2ad2-499a-9a5f-e2556b2aab2d",  
    "Authorization": "-Cco1oZ9q2bWA5QbT3t-ngbWMhmiI4joE33fnyz4Aba__XtuEa0EiSU7wJmVbzN9"  
}
TRANSLATION_SERVICE_ID = "ai4bharat/indictrans-v2-all-gpu--t4"

@lru_cache(maxsize=1000)
def detect_language(query):
    """Detect language using langdetect with caching."""
    try:
        detected_lang = detect(query)
        logger.info(f"Detected language: {detected_lang}")
        return detected_lang
    except LangDetectException as e:
        logger.error(f"Language detection error: {e}. Falling back to English.")
        return "en"

@lru_cache(maxsize=1000)
def translate_text_google(text, source_lang, target_lang):
    """Translate text using GoogleTrans with caching."""
    try:
        logger.info(f"Using GoogleTrans to translate from {source_lang} to {target_lang}: {text}")
        translation = google_translator.translate(text, src=source_lang, dest=target_lang)
        logger.info(f"GoogleTrans Translation: {translation.text}")
        return translation.text
    except Exception as e:
        logger.error(f"GoogleTrans Error: {e}")
        return None

@lru_cache(maxsize=1000)
def translate_text_bhashini(text, source_lang, target_lang):
    """Translate text using Bhashini with caching."""
    logger.info(f"Using Bhashini to translate from {source_lang} to {target_lang}: {text}")
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
        logger.info(f"Bhashini API Response: {response.status_code} - {response.text}")

        if response.status_code == 200:
            translation_output = response.json()["pipelineResponse"][0]["output"][0]["target"]
            logger.info(f"Bhashini Translation: {translation_output}")
            return translation_output
        else:
            logger.error(f"Bhashini failed with status code {response.status_code}.")
            return None
    except Exception as e:
        logger.error(f"Bhashini Error: {e}")
        return None

def translate_text(text, source_lang, target_lang):
    """Translate using GoogleTrans if supported; otherwise, fallback to Bhashini."""
    # Check if source and target languages are supported by GoogleTrans
    if source_lang in GOOGLE_TRANS_SUPPORTED_LANGUAGES and target_lang in GOOGLE_TRANS_SUPPORTED_LANGUAGES:
        translated_text = translate_text_google(text, source_lang, target_lang)
        if translated_text:
            return translated_text
        logger.info("GoogleTrans failed. Falling back to Bhashini...")
    else:
        logger.info(f"Language not supported by GoogleTrans. Using Bhashini for {source_lang} to {target_lang} translation.")
    
    # Fall back to Bhashini
    return translate_text_bhashini(text, source_lang, target_lang)

def retrieve_document(query, user_lang):
    """Retrieve the best matching FAQ document."""
    try:
        logger.info(f"Original query: {query}, Detected language: {user_lang}")

        # Explicit handling for "hi" and "hello"
        if query.strip().lower() in ["hi", "hello"]:
            logger.info("Handling 'hi' or 'hello' query directly.")
            # Search for the predefined response in the FAQ
            response = data.loc[data['prompt'].str.lower().str.strip() == "hi", 'response'].values
            if len(response) > 0:
                return response[0]
            return "I'm sorry, I couldn't find a response for your greeting."

        # Skip translation if the query is already in English
        if user_lang == "en":
            logger.info("Query is already in English. Skipping translation.")
        else:
            logger.info(f"Translating query to English...")
            query_translated = translate_text(query, user_lang, "en")
            if not query_translated:
                logger.error("Failed to translate query to English.")
                return "I'm sorry, I couldn't process your query. Please try again."
            logger.info(f"Translated query: {query_translated}")
            query = query_translated

        # Search for the top-3 matching FAQs
        logger.info("Searching for the best matches...")
        query_embedding = model.encode([query])
        distances, indices = index.search(query_embedding.astype(np.float32), 3)  # Top-3 matches
        logger.info(f"Distances: {distances}, Indices: {indices}")

        # Use pre-computed embeddings for re-ranking
        top_k_embeddings = prompt_embeddings[indices[0]]
        cosine_scores = cosine_similarity(query_embedding, top_k_embeddings)[0]
        logger.info(f"Cosine scores: {cosine_scores}")

        # Find the best match based on cosine similarity
        best_match_idx = np.argmax(cosine_scores)
        if cosine_scores[best_match_idx] < 0.5:  # Confidence threshold
            logger.info("No confident match found for the input query.")
            return "I'm not sure how to answer that. Could you clarify your query?"

        best_match_question = data['prompt'].iloc[indices[0][best_match_idx]]
        response = data['response'].iloc[indices[0][best_match_idx]]
        logger.info(f"Best match question: {best_match_question}")
        logger.info(f"Retrieved response: {response}")

        # Translate back to the user's language if needed
        if user_lang != "en":
            logger.info(f"Translating response to {user_lang}...")
            response_translated = translate_text(response, "en", user_lang)
            if not response_translated:
                logger.error("Failed to translate response to the user's language.")
                return "I'm sorry, I couldn't translate the response to your language."
            logger.info(f"Translated response: {response_translated}")
            return response_translated

        return response
    except Exception as e:
        logger.error(f"Error in retrieve_document: {e}")
        return "An error occurred while processing your request."

@app.route("/chatbot", methods=["POST"])
def chatbot():
    """Chatbot endpoint for processing user queries."""
    global model, data, index, google_translator
    
    try:
        # Load resources if not already loaded
        if any(x is None for x in [model, data, index, google_translator]):
            logger.info("Loading resources...")
            load_resources()
        
        # Parse user input
        user_data = request.get_json()
        user_input = user_data.get("user_input", "").strip()
        if not user_input:
            return jsonify({"error": "No input provided."}), 400

        logger.info(f"User input received: {user_input}")

        # Detect language and retrieve response
        user_language = detect_language(user_input)
        logger.info(f"Detected language: {user_language}")

        # Get response from FAQ logic
        response = retrieve_document(user_input, user_language)
        if not response:
            response = "I'm sorry, I couldn't find a suitable answer. Please try rephrasing your query."

        logger.info(f"Final response: {response}")
        
        # Return the response in JSON format
        return jsonify({"response": response}), 200
    except Exception as e:
        logger.error(f"Error in chatbot endpoint: {e}")
        return jsonify({"error": "An internal server error occurred."}), 500

if __name__ == "__main__":
    try:
        logger.info("Starting Flask application...")
        # Run WITHOUT SSL, let Nginx handle HTTPS
        app.run(host='0.0.0.0', port=8000)
    except Exception as e:
        logger.error(f"Error starting Flask application: {e}")
        raise


