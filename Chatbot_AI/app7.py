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

# Ensure consistent language detection
DetectorFactory.seed = 0

# Disable parallelism for tokenizers
os.environ["TOKENIZERS_PARALLELISM"] = "false"

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Load FAQ data
data = pd.read_csv('UpyogFAQ.csv')
print("FAQ data loaded successfully.")
print(data.head()) 

# Initialize SentenceTransformer model and generate embeddings
model = SentenceTransformer('all-mpnet-base-v2')
prompt_embeddings = model.encode(data['prompt'].tolist())
print("Embeddings generated successfully.")

# Initialize FAISS index and add embeddings
dimension = prompt_embeddings.shape[1]
index = faiss.IndexFlatL2(dimension)
index.add(prompt_embeddings.astype(np.float32))
print("FAISS index initialized and embeddings added.")

# Initialize Google Translator
google_translator = Translator()

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


def detect_language(query):
    """Detect language using langdetect."""
    try:
        detected_lang = detect(query)
        print(f"Detected language: {detected_lang}")
        return detected_lang
    except LangDetectException as e:
        print(f"Language detection error: {e}. Falling back to English.")
        return "en"


def translate_text_google(text, source_lang, target_lang):
    """Translate text using GoogleTrans."""
    try:
        print(f"Using GoogleTrans to translate from {source_lang} to {target_lang}: {text}")
        translation = google_translator.translate(text, src=source_lang, dest=target_lang)
        print(f"GoogleTrans Translation: {translation.text}")
        return translation.text
    except Exception as e:
        print(f"GoogleTrans Error: {e}")
        return None


def translate_text_bhashini(text, source_lang, target_lang):
    """Translate text using Bhashini."""
    print(f"Using Bhashini to translate from {source_lang} to {target_lang}: {text}")
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
        print(f"Bhashini API Response: {response.status_code} - {response.text}")

        if response.status_code == 200:
            translation_output = response.json()["pipelineResponse"][0]["output"][0]["target"]
            print(f"Bhashini Translation: {translation_output}")
            return translation_output
        else:
            print(f"Bhashini failed with status code {response.status_code}.")
            return None
    except Exception as e:
        print(f"Bhashini Error: {e}")
        return None


def translate_text(text, source_lang, target_lang):
    """Translate using GoogleTrans if supported; otherwise, fallback to Bhashini."""
    # Check if source and target languages are supported by GoogleTrans
    if source_lang in GOOGLE_TRANS_SUPPORTED_LANGUAGES and target_lang in GOOGLE_TRANS_SUPPORTED_LANGUAGES:
        translated_text = translate_text_google(text, source_lang, target_lang)
        if translated_text:
            return translated_text
        print("GoogleTrans failed. Falling back to Bhashini...")
    else:
        print(f"Language not supported by GoogleTrans. Using Bhashini for {source_lang} to {target_lang} translation.")
    
    # Fall back to Bhashini
    return translate_text_bhashini(text, source_lang, target_lang)


def retrieve_document(query, user_lang):
    """Retrieve the best matching FAQ document."""
    try:
        print(f"Original query: {query}, Detected language: {user_lang}")

        # Explicit handling for "hi" and "hello"
        if query.strip().lower() in ["hi", "hello"]:
            print("Handling 'hi' or 'hello' query directly.")
            # Search for the predefined response in the FAQ
            response = data.loc[data['prompt'].str.lower().str.strip() == "hi", 'response'].values
            if len(response) > 0:
                return response[0]
            return "I'm sorry, I couldn't find a response for your greeting."

        # Skip translation if the query is already in English
        if user_lang == "en":
            print("Query is already in English. Skipping translation.")
        else:
            print(f"Translating query to English...")
            query_translated = translate_text(query, user_lang, "en")
            if not query_translated:
                print("Failed to translate query to English.")
                return "I'm sorry, I couldn't process your query. Please try again."
            print(f"Translated query: {query_translated}")
            query = query_translated

        # Search for the top-3 matching FAQs
        print("Searching for the best matches...")
        query_embedding = model.encode([query])
        distances, indices = index.search(query_embedding.astype(np.float32), 3)  # Top-3 matches
        print(f"Distances: {distances}, Indices: {indices}")

        # Re-rank using cosine similarity
        top_k_questions = [data['prompt'].iloc[i] for i in indices[0]]
        top_k_embeddings = model.encode(top_k_questions)
        cosine_scores = cosine_similarity(query_embedding, top_k_embeddings)[0]
        print(f"Cosine scores: {cosine_scores}")

        # Find the best match based on cosine similarity
        best_match_idx = np.argmax(cosine_scores)
        if cosine_scores[best_match_idx] < 0.5:  # Confidence threshold
            print("No confident match found for the input query.")
            return "I'm not sure how to answer that. Could you clarify your query?"

        best_match_question = top_k_questions[best_match_idx]
        response = data['response'].iloc[indices[0][best_match_idx]]
        print(f"Best match question: {best_match_question}")
        print(f"Retrieved response: {response}")

        # Translate back to the user's language if needed
        if user_lang != "en":
            print(f"Translating response to {user_lang}...")
            response_translated = translate_text(response, "en", user_lang)
            if not response_translated:
                print("Failed to translate response to the user's language.")
                return "I'm sorry, I couldn't translate the response to your language."
            print(f"Translated response: {response_translated}")
            return response_translated

        return response
    except Exception as e:
        print("Error in retrieve_document:", e)
        return "An error occurred while processing your request."



@app.route("/chatbot", methods=["POST"])
def chatbot():
    """Chatbot endpoint for processing user queries."""
    try:
        # Parse user input
        user_data = request.get_json()
        user_input = user_data.get("user_input", "").strip()
        if not user_input:
            return jsonify({"error": "No input provided."}), 400

        print(f"User input received: {user_input}")

        # Detect language and retrieve response
        user_language = detect_language(user_input)
        print(f"Detected language: {user_language}")

        # Get response from FAQ logic
        response = retrieve_document(user_input, user_language)
        if not response:
            response = "I'm sorry, I couldn't find a suitable answer. Please try rephrasing your query."

        print(f"Final response: {response}")
        
        # Return the response in JSON format
        return jsonify({"response": response}), 200
    except Exception as e:
        print(f"Error in chatbot endpoint: {e}")
        return jsonify({"error": "An internal server error occurred."}), 500


if __name__ == "__main__":
    app.run(debug=True, port=7002)
