# UPYOG Voice Bot v2 — Complete Knowledge Transfer Document

**Project:** UPYOG Conversational Voice Assistant  
**Version:** v2  
**Prepared for:** Incoming developers / intern onboarding  
**Difficulty level:** Starts from zero, goes to full depth  

---

## Table of Contents

1. What is this project?
2. What problem does it solve?
3. How does it work — the big picture
4. Technology stack explained
5. Architecture in detail
6. File structure
7. Backend — app.py explained
8. Frontend — index.html explained
9. Key features explained in depth
10. APIs used
11. How a single conversation turn works (end to end)
12. Grievance flow explained
13. Language detection explained
14. Deployment explained
15. Common bugs and fixes
16. What was built in v1 vs v2
17. Glossary

---

## 1. What is this project?

UPYOG Voice Bot v2 is a **speech-to-speech AI assistant** for Indian citizens to interact with government urban services.

Think of it like this: instead of a citizen navigating a complicated government website to pay property tax or file a complaint, they just **talk** to the bot — in Hindi or English — and the bot understands, answers, and helps them complete tasks.

It is similar to asking a question to a very knowledgeable government employee who:
- Never sleeps
- Speaks both Hindi and English
- Knows everything about UPYOG services
- Can file complaints on your behalf
- Lets you interrupt them mid-sentence

---

## 2. What problem does it solve?

### The original problem
UPYOG is a government platform with 27 urban services (property tax, trade license, water connection, birth certificates, etc.). Most citizens do not know:
- Which service to use
- What documents are needed
- How to navigate the portal
- Who to contact if something is wrong

### What the bot does
- Answers questions about any UPYOG service in plain conversational language
- Detects when a citizen is frustrated or stuck (e.g., "mera pani 3 din se nahi aa raha") and offers to file a grievance
- Collects grievance details through voice conversation
- Submits the complaint to the UPYOG grievance API automatically
- Responds in whichever language the user speaks — Hindi or English — and switches automatically

---

## 3. How does it work — the big picture

```
CITIZEN SPEAKS
      ↓
Browser microphone captures audio
      ↓
Web Speech API converts speech to text (STT)
      ↓
Text sent to Flask backend (Python server)
      ↓
Backend detects language (Hindi / English)
      ↓
Intent classifier runs (Groq LLM call)
      → Is this a grievance? → Grievance flow
      → Is this a question?  → RAG flow
      ↓
FAISS searches knowledge base for relevant context
      ↓
Groq LLM generates a response using that context
      ↓
Bhashini converts text response to audio (TTS)
      ↓
Audio sent back to browser
      ↓
CITIZEN HEARS THE ANSWER
```

This full cycle — from the citizen finishing a sentence to hearing the answer — takes approximately 2–4 seconds.

---

## 4. Technology stack explained

Every technology used is explained here from scratch.

---

### 4.1 Python and Flask (Backend)

**What is Python?**  
Python is a programming language. Think of it as the language we use to write instructions for the computer.

**What is Flask?**  
Flask is a lightweight Python framework for building web servers. A web server is a program that listens for requests (like when a browser asks "give me information about property tax") and sends back responses.

In this project, Flask runs on **port 8090**. Port is like a door number on a building — when the browser sends a request to `http://localhost:8090/chat`, Flask opens that door and handles the request.

**Key Flask concepts used:**
- `@app.route('/chat', methods=['POST'])` — this tells Flask: "when someone calls the /chat URL with a POST request, run this function"
- `request.json` — reads the data sent by the browser
- `jsonify(...)` — converts Python dictionary to JSON to send back

---

### 4.2 Groq + Llama 3.1 (The AI Brain)

**What is an LLM?**  
LLM stands for Large Language Model. It is an AI model trained on massive amounts of text that can understand questions and generate human-like answers. ChatGPT is an LLM. Llama is also an LLM.

**What is Groq?**  
Groq is a company that runs LLM inference (meaning: takes your question, runs it through the AI model, gives you an answer) extremely fast. They use special hardware chips called LPUs.

**Why Groq instead of OpenAI?**  
Speed. Groq's `llama-3.1-8b-instant` model responds in under 500ms. For a voice bot, speed is everything — a 3-second delay feels broken.

**How it is called in code:**
```python
from groq import Groq
client = Groq(api_key=os.environ.get("GROQ_API_KEY"))

response = client.chat.completions.create(
    model="llama-3.1-8b-instant",
    messages=[
        {"role": "system", "content": "You are UPYOG Assistant..."},
        {"role": "user", "content": "What is property tax?"}
    ],
    max_tokens=300,
    temperature=0.3
)
answer = response.choices[0].message.content
```

**What is temperature?**  
Temperature controls how creative or random the model is. 0.0 = very predictable and factual. 1.0 = very creative and varied. We use 0.3 — mostly factual but slightly natural.

**What is max_tokens?**  
Tokens are pieces of words. "property" is 1 token. "property tax" is 2 tokens. We limit to 300 tokens so responses are concise — important for voice, where a 500-word answer takes too long to listen to.

---

### 4.3 FAISS (Knowledge Base Search)

**What is RAG?**  
RAG stands for Retrieval-Augmented Generation. Instead of asking the LLM to answer from its training data (which may be wrong or outdated), we:
1. Store our own knowledge base (UPYOG FAQs, service details)
2. When a question comes in, search the knowledge base for relevant information
3. Give that information to the LLM along with the question
4. LLM uses the retrieved information to answer accurately

This prevents hallucination (making up wrong answers).

**What is FAISS?**  
FAISS (Facebook AI Similarity Search) is a library that searches through large collections of text very fast. It works using vectors.

**What are vectors?**  
A vector is a list of numbers that represents the meaning of a piece of text. For example:
- "property tax payment" → [0.23, -0.45, 0.78, ...]  
- "property tax fees" → [0.21, -0.43, 0.76, ...] (similar numbers = similar meaning)
- "biryani recipe" → [-0.89, 0.12, -0.34, ...] (very different numbers = unrelated)

When a user asks a question, we convert it to a vector and find knowledge base entries with similar vectors.

**What is the similarity threshold 1.08?**  
FAISS uses L2 distance (lower = more similar). A score of 0 means exact match. We set 1.08 as the cutoff — anything with a score above 1.08 is considered too different from our knowledge base and gets rejected (or handled differently).

**What is the embedding model?**  
We use `all-mpnet-base-v2` from SentenceTransformers. This model converts text sentences into vectors. It was chosen because it is one of the best models for semantic similarity.

```python
from sentence_transformers import SentenceTransformer
model = SentenceTransformer('all-mpnet-base-v2')
vector = model.encode(["How to pay property tax?"])
```

**The knowledge base files:**
- `UpyogFAQ.csv` — frequently asked questions about UPYOG services
- `FRS_KnowledgeBase` — functional requirement specifications with detailed service information

---

### 4.4 Bhashini (Language and Voice)

**What is Bhashini?**  
Bhashini is India's national AI language platform built by the government. It provides:
- **STT (Speech to Text)** — converts spoken audio to written text, for Indian languages
- **TTS (Text to Speech)** — converts written text to natural-sounding speech
- **Translation** — translates between Indian languages

**Why Bhashini and not Google/AWS?**  
Bhashini is specifically trained on Indian languages and accents. It handles Hindi much better than generic Western TTS engines. It is also free for government projects.

**How TTS works in the bot:**
1. LLM generates a text response
2. Backend sends that text to Bhashini's TTS API
3. Bhashini returns audio (WAV or MP3)
4. Backend encodes it as base64 and sends to browser
5. Browser plays it

**What is base64?**  
Binary data (like audio files) cannot be sent directly in JSON. Base64 is a way to encode binary data as text characters so it can travel inside JSON. The browser decodes it back to audio.

---

### 4.5 Web Speech API (Browser-side STT)

**What is it?**  
The Web Speech API is built into modern browsers (especially Chrome). It gives JavaScript access to the device microphone and can convert speech to text in real time.

**Why use it instead of Bhashini for STT?**  
Speed. The Web Speech API processes audio locally in the browser or via Google's servers very fast. There is no round-trip to our backend just for transcription. Only the final text goes to our backend.

**Key code:**
```javascript
const recognition = new webkitSpeechRecognition();
recognition.continuous = true;       // keeps listening
recognition.interimResults = true;   // shows partial results while speaking
recognition.lang = 'hi-IN';         // Hindi recognition

recognition.onresult = (event) => {
    // called every time new speech is detected
    const transcript = event.results[event.resultIndex][0].transcript;
};
```

**Why `continuous = true`?**  
In walkie-talkie mode, you press a button, speak, press again. With `continuous = true`, the mic stays open permanently. This is the telephone-call model — always listening, no button needed.

---

### 4.6 Environment Variables and .env file

**What are environment variables?**  
API keys (secret passwords to use services like Groq and Bhashini) should never be written directly in code. If the code goes on GitHub, everyone can see the keys and misuse them.

Instead, we store them in a `.env` file:
```
GROQ_API_KEY=gsk_xxxxxxxxxxxxxxxxxxxx
BHASHINI_API_KEY=xxxxxxxxxxxxxxxx
BHASHINI_USER_ID=xxxxxxxx
```

**How does Python read them?**
```python
from dotenv import load_dotenv
import os

load_dotenv()  # reads .env file
api_key = os.environ.get("GROQ_API_KEY")
```

The `.env` file is never committed to Git. Only `.env.example` (with blank values) is shared.

---

## 5. Architecture in detail

### Two-zone structure

The project has two completely separate zones:

**Zone A — Original project (READ ONLY)**  
The v1 bot, running on port 8080. This is the backup demo. Never modified.

**Zone B — v2 project (our working code)**  
Located in `upyog-voice-v2/`. Runs on port 8090. This is what we built.

### Backend architecture

```
upyog-voice-v2/
├── app.py              ← Flask server (the brain)
├── UpyogFAQ.csv        ← Knowledge base data
├── FRS_KnowledgeBase/  ← Additional knowledge base
├── requirements.txt    ← Python dependencies
├── .env                ← API keys (never in Git)
└── index.html          ← Frontend (served by Flask)
```

### Request flow in detail

```
Browser                          Flask (app.py)
  │                                    │
  │── POST /chat ──────────────────────►│
  │   {query, history, session_id}      │
  │                                    │
  │                           detect_language(query)
  │                                    │
  │                           is_hard_blocked(query)?
  │                           → if yes: return rejection
  │                                    │
  │                           check grievance session state
  │                           → if collecting: handle_grievance_turn()
  │                           → if offer pending: check yes/no
  │                                    │
  │                           classify_intent(query, history, lang)
  │                           → Groq call #1 (fast, 150 tokens)
  │                           → returns: faq / grievance_candidate / etc.
  │                                    │
  │                           if grievance_candidate:
  │                             build_grievance_offer()
  │                             return offer message
  │                                    │
  │                           if faq:
  │                             get_rag_response(query, history, lang)
  │                             → FAISS search (find relevant chunks)
  │                             → Groq call #2 (main answer, 300 tokens)
  │                                    │
  │                           get_tts_audio(response, lang)
  │                           → Bhashini API call
  │                           → returns base64 audio
  │                                    │
  │◄── JSON response ─────────────────│
  │    {response, lang, audio, mode}   │
```

---

## 6. File structure

### app.py sections (in order)

```python
# SECTION 1: Imports
# All libraries loaded here

# SECTION 2: Constants
# FAISS threshold, blocked topics, Groq model name

# SECTION 3: Initialization
# Load FAISS index, load embedding model, create Groq client

# SECTION 4: Language detection
# detect_language(text) → returns dict with lang, script, search_lang

# SECTION 5: Domain filtering  
# is_hard_blocked(query) → True/False

# SECTION 6: Intent classification
# classify_intent(query, history, lang) → dict with intent, emotion, service

# SECTION 7: RAG response
# get_rag_response(query, history, lang, search_lang) → text

# SECTION 8: TTS
# get_tts_audio(text, lang) → base64 audio string

# SECTION 9: Grievance session management
# get_grievance_session(session_id) → dict
# handle_grievance_turn(session_id, user_input, lang) → dict
# submit_grievance(data) → dict with success and ticket_number
# build_grievance_offer(service, emotion, lang) → string

# SECTION 10: Flask routes
# @app.route('/chat', methods=['POST'])
# @app.route('/stop', methods=['POST'])
# @app.route('/')  ← serves index.html

# SECTION 11: Entry point
# if __name__ == '__main__': app.run(port=8090)
```

### index.html sections (in order)

```
HEAD
├── Google Fonts (Noto Sans Devanagari — for Hindi text rendering)
└── CSS styles

BODY
├── Header bar (UPYOG Assistant title + EN/HI/Auto buttons)
├── Chat container (scrollable message history)
├── Interim display (shows what's being transcribed in real time)
├── Status bar (LISTENING / THINKING / SPEAKING indicator)
└── Start/End Session button

SCRIPT
├── State variables (currentState, sessionActive, history[], etc.)
├── createRecognition() — builds fresh Web Speech API object
├── destroyRecognition() — cleanly kills current recognition instance
├── handleResult() — processes speech as it comes in
├── handleSpeechEnd() — triggers silence timer
├── attemptSend() — validates and sends transcript to backend
├── sendQuery() — main fetch call to /chat endpoint
├── playAudio() — plays TTS audio, triggers onTurnComplete when done
├── triggerBargeIn() — stops audio, destroys recognition, restarts fresh
├── startBargeInMonitor() — continuously checks mic volume during speaking
├── detectLanguage() — client-side Hinglish detector
├── addBotMessage() — renders bot message with optional buttons
├── addUserMessage() — renders user message bubble
├── setState() — updates UI state indicator
└── onTurnComplete() — called after bot finishes speaking, resumes mic
```

---

## 7. Backend — app.py explained

### Language detection in depth

```python
def detect_language(text: str) -> dict:
```

This function returns a dictionary, not just a string. Why? Because we need three pieces of information:

- `lang` — what language to **respond in** (hi or en)
- `script` — what script was detected (devanagari, roman_hindi, english, transliterated_english)
- `search_lang` — what language to **search the knowledge base in**

**The detection logic has 4 cases:**

**Case 1: Pure Devanagari**  
Count how many characters are in the Unicode range \u0900–\u097F (that range is all Devanagari characters). If more than 50% are Devanagari → it is Hindi. But wait — check first if it is actually English words written in Devanagari script (transliterated English like "व्हाट आर द नंबर"). If more than 35% of words match known English-in-Devanagari patterns → treat as English.

**Case 2: Roman script with Hindi phonetics (Hinglish)**  
Text like "mera pani nahi aa raha" uses English letters but Hindi words. We check against a list of common Hindi phonetic words (kya, kaise, nahi, mera, haan, etc.). If 2 or more match → classify as Hindi.

**Case 3: Pure English**  
No Devanagari, no Hindi phonetics → English.

**Case 4: Mixed (code-switching)**  
Count Hindi-origin words vs English-origin words. Majority wins.

---

### Intent classification in depth

```python
def classify_intent(query: str, history: list, lang: str) -> dict:
```

This is a separate Groq API call with a carefully written prompt. It does NOT answer the question — it only classifies what the user wants to do.

**Why a separate call?**  
Because combining classification and answering in one call produces worse results. The classifier prompt is optimized purely for intent detection, not for answering.

**Returns one of four intents:**

| Intent | Meaning | Example |
|--------|---------|---------|
| `faq` | User wants information | "What is trade license?" |
| `grievance_candidate` | User has a personal problem | "Mera pani 3 din se nahi aa raha" |
| `grievance_confirm` | User said yes to grievance offer | "Haan, file karo" |
| `grievance_cancel` | User said no to grievance offer | "Rehne do" |

**Also returns:**
- `emotion` — neutral / frustrated / stuck / urgent
- `service` — which UPYOG service is involved (if any)
- `reasoning` — one sentence explanation (for debugging)

---

### RAG response in depth

```python
def get_rag_response(query, history, lang, search_lang=None):
```

**Step 1: FAISS search**  
The query is converted to a vector using the embedding model. FAISS searches the pre-built index and returns the top 5 closest knowledge base chunks with their distance scores. Only chunks with score below 1.5 (relaxed threshold — just for context, not as a hard gate) are included.

**Step 2: Build system prompt**  
The system prompt has three parts:
1. Language rule (mandatory response language)
2. UPYOG assistant persona and domain knowledge
3. Retrieved context from FAISS (or instruction to answer from general UPYOG knowledge if nothing found)

**Critical design decision:** FAISS context is optional. If FAISS finds nothing relevant, the LLM still answers using its general knowledge of UPYOG services. This prevents the "मेरे पास जानकारी नहीं है" problem where the bot rejected valid questions just because they were phrased conversationally.

**Step 3: Build message history**  
The last 6 turns of conversation are included so the LLM has context. This allows follow-up questions to work: "And how much does it cost?" after asking about trade license.

**Step 4: Groq call**  
Temperature 0.3, max_tokens 300.

---

### Grievance flow in depth

**The problem with keyword-based triggers (old approach):**  
Detecting "complaint" or "shikayat" as keywords caused "What is property mutation?" to trigger the grievance flow because "mutation" has no keyword guard.

**The new approach — emotion detection:**  
`classify_intent()` detects personal problems based on:
- Personal pronouns (mera, meri, I, my)
- Time expressions (3 din se, kaafi time se, months ago)
- Inability expressions (nahi ho raha, pending hai, nahi aaya)
- Frustration signals (kuch karo, action lena hai, sun nahi rahe)

**Three-state grievance machine:**

```
State 0: INACTIVE
  → classify_intent finds grievance_candidate
  → build empathetic offer message
  → set offer_pending = True
  → State 1

State 1: OFFER PENDING
  → classify_intent on next message
  → if grievance_confirm → State 2
  → if grievance_cancel → State 0
  → if something else → State 0 (user ignored offer), answer question

State 2: COLLECTING
  → handle_grievance_turn() runs instead of RAG
  → collects fields one by one
  → when all fields done → submit_grievance()
  → ticket number returned → State 0
```

**Why offer first instead of auto-starting?**  
A user asking "pani kab aayega" might just want information, not to file a complaint. Forcing the grievance flow on them is annoying. The offer gives them control.

---

## 8. Frontend — index.html explained

### The state machine

The frontend is always in exactly one of five states:

```
IDLE
  When: session not started
  UI: "Start Session" button visible
  Mic: OFF

SESSION_ACTIVE → immediately transitions to LISTENING

LISTENING
  When: mic is open, waiting for user to speak
  UI: green pulsing dot, "Listening..."
  Mic: ON, recognition running

PROCESSING
  When: query sent to backend, waiting for response
  UI: spinner, "Thinking..."
  Mic: OFF (recognition stopped)

SPEAKING
  When: TTS audio playing
  UI: blue wave, "Speaking..."
  Mic: OFF but volume monitor is ON (for barge-in detection)
  → if voice energy detected → LISTENING (barge-in)
  → if audio ends normally → LISTENING (onTurnComplete)
```

### Continuous listening implementation

**The problem with browser STT:**  
The Web Speech API automatically stops listening after a period of silence. This is fine for a single query but breaks a continuous conversation.

**The solution:**
```javascript
recognition.onend = () => {
    // This fires whenever recognition stops for any reason
    // Restart immediately if session is still active
    if (sessionActive && currentState === 'LISTENING') {
        setTimeout(() => recognition.start(), 100);
    }
};
```

**Why recreate the object every 5 turns?**  
The browser's speech engine accumulates state over time and starts behaving erratically (cutting off sentences, increasing latency). Destroying the object completely and creating a fresh one resets this. Done every 5 conversation turns.

```javascript
function destroyRecognition() {
    if (recognition) {
        recognition.onstart = null;
        recognition.onresult = null;
        // ... null all handlers
        try { recognition.abort(); } catch(e) {}
        recognition = null;
    }
}
```

### The silence timer system

**Problem:** The browser fires `onspeechend` after ~1 second of silence, even if the user just paused mid-sentence. This causes half-sentences to be sent.

**Solution:** Ignore `onspeechend`. Instead, use a 2.5-second silence timer:

```javascript
const SILENCE_WAIT_MS = 2500;
const MIN_WORDS_TO_SEND = 3;

function handleResult(event) {
    clearTimeout(silenceTimer);
    // ... collect transcript ...
    
    // Reset 2.5s timer every time new speech comes in
    silenceTimer = setTimeout(() => {
        attemptSend(transcript);
    }, SILENCE_WAIT_MS);
}

function attemptSend(text) {
    // Only send if at least 3 words — prevents fragment sends
    if (text.split(' ').length < MIN_WORDS_TO_SEND) return;
    sendQuery(text);
}
```

### Barge-in detection

**What is barge-in?**  
Interrupting the bot while it is speaking. ChatGPT cannot do this — you have to wait for it to finish. Our bot can be interrupted mid-sentence.

**How it works:**
```javascript
async function startBargeInMonitor() {
    const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
    const audioCtx = new AudioContext();
    const analyser = audioCtx.createAnalyser();
    // ... connect stream to analyser ...
    
    function checkVolume() {
        analyser.getFloatTimeDomainData(buffer);
        // Calculate RMS (root mean square = volume level)
        const rms = Math.sqrt(buffer.reduce((s,v) => s+v*v, 0) / buffer.length);
        
        // Only check during SPEAKING state
        if (currentState === 'SPEAKING' && rms > bargeInThreshold) {
            voiceFrames++;
            if (voiceFrames > 8) {  // sustained for ~300ms
                triggerBargeIn();
            }
        }
        requestAnimationFrame(checkVolume);  // runs every frame (~16ms)
    }
    checkVolume();
}
```

**Why 8 frames?**  
To avoid false triggers from ambient noise. 8 frames at ~16ms each = ~128ms. But we check every animation frame, not every 16ms exactly, so it works out to roughly 300ms of sustained voice before barge-in triggers.

**What triggerBargeIn() does:**
1. Pause and destroy current audio element
2. Abort the in-flight fetch request to backend
3. Call `/stop` on backend (signals it to abort TTS generation)
4. Clear all transcript buffers and timers
5. Call `destroyRecognition()` — kills the zombie STT engine
6. Wait 300ms (browser audio context needs to settle)
7. Call `createRecognition()` and `recognition.start()` — fresh start

---

## 9. Key features explained in depth

### Language switching per turn

Every single turn, language is detected fresh. There is no session-level language lock.

The flow:
1. User speaks → Web Speech API transcribes (language hint from last turn)
2. Text sent to backend
3. `detect_language()` runs on backend → returns `lang` and `script`
4. `lang` is used for: LLM system prompt, TTS language, response language
5. Backend returns `data.lang` in response
6. Frontend updates `recognition.lang` for the NEXT turn

This means:
- Turn 1 in Hindi → bot responds in Hindi → STT switches to hi-IN
- Turn 2 in English → bot responds in English → STT switches to en-IN
- Turn 3 back to Hindi → bot responds in Hindi → STT switches back

The STT language update is a hint, not a lock. Detection still happens on backend from the text itself.

### Clickable option buttons for grievance

When the grievance flow reaches a step with structured choices (categories, localities, ward names), the backend returns:

```json
{
    "has_options": true,
    "options": ["Water & Sewerage", "Solid Waste", "Roads", "Street Lighting"],
    "field": "category"
}
```

The frontend renders these as clickable buttons. When clicked:
1. All buttons in the set are disabled (prevents double-click)
2. Clicked button is highlighted
3. The option text is sent to `sendQuery()` as if the user spoke it
4. Backend receives it like any other voice input

Voice still works — user can say "first one" or "water" or "pehla" and `match_spoken_choice()` on the backend maps it to the correct option.

### Number normalization for voice

When a field requires digits (mobile number, OTP), the user speaks numbers in words:
- "nine nine nine nine nine nine nine nine nine nine" → "9999999999"
- "नौ नौ नौ..." → "9999999999"
- "one two three four five six" → "123456"

The backend `normalize_spoken_number()` function handles this mapping for both English and Hindi number words.

---

## 10. APIs used

### Groq API
- **URL:** `https://api.groq.com/openai/v1/chat/completions`
- **Auth:** Bearer token in header (GROQ_API_KEY)
- **Used for:** Intent classification, RAG response generation
- **Model:** `llama-3.1-8b-instant`
- **Rate limits:** High — rarely hit in normal usage

### Bhashini API
- **Used for:** TTS (text to speech in Hindi and English)
- **Auth:** User ID + API key in headers
- **Returns:** Audio file (WAV or base64)
- **Documentation:** bhashini.gov.in

### UPYOG Grievance API
- **Source:** Found in `app7.py` in the original project
- **Used for:** Submitting citizen complaints, checking ticket status
- **Auth:** Specific to the ULB/municipality setup

### Web Speech API
- **Built into Chrome browser** — no API key needed
- **URL:** Not applicable — browser-native
- **Used for:** Microphone access, real-time speech to text

---

## 11. How a single conversation turn works (end to end)

Let us trace one complete turn: user says "मेरे घर में पानी नहीं आ रहा है तीन दिन से"

**Step 1: Browser (0ms)**  
Web Speech API is running continuously. User speaks. `onresult` fires repeatedly as words are recognized. Interim display shows the text in real time.

**Step 2: Silence detected (2500ms after last word)**  
Silence timer fires. `attemptSend()` runs. Text has 8 words — passes the 3-word minimum. `sendQuery()` called.

**Step 3: Frontend sends request (2500ms)**
```javascript
fetch('/chat', {
    method: 'POST',
    body: JSON.stringify({
        query: "मेरे घर में पानी नहीं आ रहा है तीन दिन से",
        history: [...last 8 turns...],
        session_id: "session_1715961234567",
        lang: "hi"  // hint from previous turn
    })
})
```

**Step 4: Backend receives (2500ms + network)**  
Flask `/chat` route runs.

**Step 5: Language detection (2505ms)**  
`detect_language()` sees Devanagari characters → returns `{lang: 'hi', script: 'devanagari', search_lang: 'hi'}`

**Step 6: Hard block check (2506ms)**  
`is_hard_blocked()` checks against blocked topics list. "पानी" is not in blocked list. Passes.

**Step 7: Grievance session check (2507ms)**  
`get_grievance_session("session_1715961234567")` — no active grievance session. Continue.

**Step 8: Intent classification (2507ms–2900ms)**  
Groq call #1 fires with the classifier prompt. Response comes back in ~300ms:
```json
{
    "intent": "grievance_candidate",
    "emotion": "frustrated",
    "service": "Water & Sewerage",
    "reasoning": "User reports personal water supply failure for 3 days"
}
```

**Step 9: Grievance offer built (2901ms)**  
`build_grievance_offer(service="Water & Sewerage", emotion="frustrated", lang="hi")` returns:

"मैं समझ सकता हूँ कि यह स्थिति परेशान करने वाली है। Water & Sewerage के बारे में आपकी समस्या को आधिकारिक रूप से दर्ज कराने के लिए क्या मैं आपके लिए एक शिकायत दर्ज करूँ?"

**Step 10: TTS (2902ms–3300ms)**  
`get_tts_audio(offer_text, 'hi')` calls Bhashini. Returns base64 audio.

**Step 11: Response sent (3301ms)**
```json
{
    "response": "मैं समझ सकता हूँ...",
    "lang": "hi",
    "audio": "base64encodedaudio...",
    "mode": "grievance_offered",
    "emotion": "frustrated"
}
```

**Step 12: Frontend processes response (3302ms)**  
- Adds bot message to chat with amber/orange styling (grievance offer style)
- Shows "Say Yes to proceed or No to continue" hint
- Sets `offer_pending = true` in session
- Calls `playAudio(data.audio)`

**Step 13: Audio plays (3303ms)**  
User hears the offer in Hindi.

**Step 14: onTurnComplete (after audio ends)**  
`destroyRecognition()` called if it's a scheduled recreation turn. `recognition.start()` called. State set to LISTENING. Mic is open again.

**Total time: approximately 3–4 seconds**

---

## 12. Grievance flow explained step by step

Assuming user said "haan" (yes) after the offer:

**Turn 2: User says "haan"**
- Intent classified as `grievance_confirm`
- Session state updated: `active=True, collecting=True`
- `handle_grievance_turn()` runs with empty input (first question)

**The steps array (from API requirements in app7.py):**
```python
steps = [
    ("category", "What type of complaint?", "शिकायत किस विषय पर है?"),
    ("description", "Please describe the issue.", "कृपया समस्या बताएं।"),
    ("mobile", "Your mobile number?", "आपका मोबाइल नंबर?"),
    ("locality", "Your area or locality?", "आपका इलाका या क्षेत्र?"),
]
```

**Turn 2 response:**  
Bot asks first question with clickable category buttons.

**Turn 3: User clicks "Water & Sewerage" button**
- `session["data"]["category"] = "Water & Sewerage"`
- Bot asks next question: describe the issue

**Turn 4: User speaks "teen din se ghar mein pani nahi aa raha"**
- `session["data"]["description"] = "तीन दिन से घर में पानी नहीं आ रहा"`
- Bot asks for mobile number

**Turn 5: User says "nine eight seven six five four three two one zero"**
- `normalize_spoken_number()` converts → "9876543210"
- `session["data"]["mobile"] = "9876543210"`
- Bot asks for locality

**Turn 6: User clicks locality button or speaks locality name**
- All fields collected
- Bot shows confirmation summary

**Turn 7: User says "haan" (confirm)**
- `submit_grievance(session["data"])` called
- UPYOG Grievance API hit with POST request
- Returns ticket number: "GRV2024051701234"

**Final bot response:**  
"आपकी शिकायत दर्ज हो गई है। आपका टिकट नंबर है: GRV2024051701234। इस नंबर से आप अपनी शिकायत की स्थिति जांच सकते हैं।"

Session cleared. Back to normal FAQ mode.

---

## 13. Language detection explained

### Why not just use a library?

Libraries like `langdetect` or `fastText` work well for pure Hindi or pure English. They fail for:
- Hinglish (Roman script Hindi): "mera pani nahi aa raha"
- Transliterated English in Devanagari: "व्हाट आर द नंबर ऑफ मो'एस"
- Code-switching mid-sentence: "Property tax ka process kya hai?"

Our custom detector handles all these cases.

### The decision tree

```
Input text
    │
    ├── Count Devanagari characters
    │   Is devanagari_ratio > 0.5?
    │   YES →
    │       Check for transliterated English patterns
    │       (व्हाट, हाउ, द, नंबर, आर, etc.)
    │       transliterated_ratio > 0.35?
    │       YES → lang=en, script=transliterated_english
    │       NO  → lang=hi, script=devanagari
    │
    └── Roman script
        Check Hindi phonetic word list
        (kya, kaise, nahi, mera, haan, etc.)
        hindi_hits >= 2?
        YES → lang=hi, script=roman_hindi
        NO  → lang=en, script=english
```

---

## 14. Deployment explained

### Why not Docker?

Docker was used in v1 and caused problems — the microphone did not work because Docker containers isolate audio devices. For a voice bot, this is fatal.

v2 uses systemd + Nginx — simpler, more reliable, and the mic works.

### Systemd service

Systemd is the process manager for Linux. It keeps the bot running forever, restarts it if it crashes, and starts it automatically when the VM reboots.

The service file is at `/etc/systemd/system/upyog-voice-v2.service`:

```ini
[Unit]
Description=UPYOG Voice Bot v2
After=network.target

[Service]
User=ubuntu
WorkingDirectory=/home/ubuntu/upyog-voice-v2
EnvironmentFile=/home/ubuntu/upyog-voice-v2/.env
ExecStart=/home/ubuntu/upyog-voice-v2/venv/bin/python3 app.py
Restart=always
RestartSec=5

[Install]
WantedBy=multi-user.target
```

**Key commands:**
```bash
sudo systemctl start upyog-voice-v2    # start
sudo systemctl stop upyog-voice-v2     # stop
sudo systemctl restart upyog-voice-v2  # restart
sudo systemctl status upyog-voice-v2   # check if running
sudo journalctl -u upyog-voice-v2 -f   # watch live logs
```

### Nginx as reverse proxy

Nginx sits in front of Flask. Citizen accesses `https://domain.com/upyog-voice/` → Nginx forwards to `http://localhost:8090/`.

**Why Nginx?**  
- Flask's built-in server is not production-grade
- Nginx handles HTTPS (SSL certificates)
- HTTPS is MANDATORY for the microphone to work — browsers block mic access on HTTP

**Why is HTTPS required for mic?**  
Browser security policy: access to microphone, camera, and geolocation is only allowed on secure origins (HTTPS or localhost). On HTTP, `getUserMedia()` throws `NotAllowedError`.

### Virtual environment

```bash
python3 -m venv venv          # create isolated Python environment
source venv/bin/activate       # activate it
pip install -r requirements.txt  # install dependencies inside it
```

Venv ensures the bot's Python packages do not conflict with other Python projects on the same VM (the text-to-text bot uses different package versions).

---

## 15. Common bugs and fixes

### Bug: "मेरे पास जानकारी नहीं है" for valid questions

**Cause:** FAISS similarity score exceeded 1.08 threshold, so the pipeline rejected the query before sending it to LLM. Happened because conversational sentences ("बच्चों का बर्थ सर्टिफिकेट बनाना है") do not embed similarly to FAQ-style text ("Birth Certificate Application Process").

**Fix:** Made FAISS optional context (not a hard gate). LLM answers from general UPYOG knowledge even when FAISS returns nothing relevant.

---

### Bug: Grievance flow triggering on informational questions

**Cause:** Keyword matching — words like "complaint" or "problem" in any context triggered the flow.

**Fix:** Replaced keyword matching with LLM-based intent classification. Now only triggers when the user is describing a personal problem they are experiencing right now, not just asking about processes.

---

### Bug: Mic goes silent after barge-in

**Cause:** `triggerBargeIn()` was pausing audio and updating UI state but NOT restarting the speech recognition engine. Recognition was in a zombie state.

**Fix:** `triggerBargeIn()` now calls `destroyRecognition()` + waits 300ms + calls `createRecognition()` + `recognition.start()`.

---

### Bug: Half sentences sent to backend

**Cause:** `onspeechend` fired after 1 second of silence, even if user paused mid-sentence.

**Fix:** Ignore `onspeechend`. Use a 2.5-second silence timer that resets on every new interim result. Also added minimum 3-word gate before sending.

---

### Bug: Language not switching — bot keeps responding in Hindi even when user speaks English

**Cause:** Session-level language variable was never updated, OR TTS function was not receiving the per-turn `lang` value — it was using a cached/default value.

**Fix:**
1. Removed all session-level language locks
2. Made TTS function take explicit `lang` parameter on every call
3. Added language instruction as FIRST line of system prompt (before all other instructions) so LLM prioritizes it
4. Frontend updates `recognition.lang` after every response

---

### Bug: High latency after multiple turns

**Cause:** Audio blob URLs not being revoked (memory leak), history array growing too large (sending 30+ turns to backend), fetch requests from previous turns not being aborted.

**Fix:**
- Revoke blob URLs after each audio plays
- Cap in-memory history at 20 items
- Only send last 8 turns to backend
- Abort pending fetch on barge-in

---

## 16. What was built in v1 vs v2

| Feature | v1 (Gemini project) | v2 (Claude project) |
|---------|--------------------|--------------------|
| STT method | Button-press to speak | Continuous listening — no button |
| Language | Locked to session | Per-turn detection and switching |
| Barge-in | Not supported | Supported — interrupt mid-sentence |
| Grievance trigger | Keyword matching | LLM emotion detection |
| Grievance UI | Text input only | Voice + clickable buttons |
| FAISS threshold | Hard gate | Optional context — LLM always tries |
| Recognition recovery | No | Recreates every 5 turns |
| Number input | Typed only | Voice "nine nine nine" → "999" |
| Port | 8080 | 8090 (isolated, v1 preserved as backup) |
| Deployment | Docker (mic broken) | systemd + Nginx (mic works) |
| Latency | 3–5s, degrading over session | 2–4s, stable |

---

## 17. Glossary

| Term | Definition |
|------|------------|
| STT | Speech to Text — converting spoken audio to written words |
| TTS | Text to Speech — converting written words to spoken audio |
| LLM | Large Language Model — AI that understands and generates text |
| RAG | Retrieval-Augmented Generation — searching a knowledge base before asking LLM |
| FAISS | Facebook AI Similarity Search — fast vector similarity search library |
| Vector / Embedding | A list of numbers representing the meaning of text |
| Groq | Company providing fast LLM inference via special hardware |
| Bhashini | India's government language AI platform (STT, TTS, translation) |
| Flask | Python web framework for building APIs |
| Systemd | Linux process manager — keeps services running |
| Nginx | Web server used as reverse proxy in front of Flask |
| HTTPS | Secure HTTP — required for browser microphone access |
| WebRTC | Web Real-Time Communication — browser API for audio/video streaming |
| Barge-in | Interrupting the bot while it is speaking |
| Session | One continuous conversation from Start to End Session |
| Port | A numbered door on a server — our bot listens on port 8090 |
| Base64 | Encoding binary data (like audio) as text so it can travel in JSON |
| Virtual environment (venv) | Isolated Python installation for a specific project |
| Hinglish | Hindi words written using English/Roman letters |
| Devanagari | The script used to write Hindi (like अ, ब, क, etc.) |
| Intent | What the user is trying to do (ask a question vs file a complaint) |
| Grievance | An official complaint filed with a government body |
| Token | A piece of a word — LLMs process text in tokens, not characters |
| Temperature | Controls LLM creativity — 0.0 is factual, 1.0 is creative |
| ULB | Urban Local Body — municipal corporation / city government |
| UPYOG | Urban Platform for Urban Governance — the government services platform |
| NUDM | National Urban Digital Mission — the overarching government initiative |

---

*This document covers everything built in UPYOG Voice Bot v2. For questions about specific code sections, refer to the inline comments in app.py and index.html.*
