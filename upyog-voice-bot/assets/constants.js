// CSS class names for the animated background layers injected into #bg-layers
const BG_LAYERS = [
    "ambient-layer",
    "organic-shape-1",
    "organic-shape-2",
    "organic-shape-3",
    "organic-shape-4",
    "grain-overlay"
];

// Language buttons shown in the header toggle (EN / HI / Auto)
const LANG_OPTIONS = [
    { value: "en",   label: "EN" },
    { value: "hi",   label: "HI" },
    { value: "auto", label: "Auto", active: true }
];

// Options for the language dropdown inside the settings panel
const LANG_PREFERENCES = [
    { value: "auto", label: "Auto Detect" },
    { value: "hi",   label: "Force Hindi" },
    { value: "en",   label: "Force English" }
];

// Voice session states — drives the status pill and button appearance
const States = {
    IDLE:       "IDLE",
    LISTENING:  "LISTENING",
    PROCESSING: "PROCESSING",
    SPEAKING:   "SPEAKING"
};

// Human-readable labels shown in the status pill for each state
const STATE_LABELS = {
    IDLE:       "Ready",
    LISTENING:  "Listening...",
    PROCESSING: "Thinking...",
    SPEAKING:   "Speaking..."
};

// Placeholder text for the text input during grievance data collection
const FIELD_PLACEHOLDERS = {
    mobile:  "Enter mobile number...",
    otp:     "Enter OTP...",
    default: "Type your answer..."
};

// Short hint messages shown below bot choice/number prompts
const HINT_MESSAGES = {
    speakChoice: "You can also speak your choice",
    speakDigits: "Speak digits clearly, or type below"
};

// Tunable behaviour values — change here to affect the whole app
const CONFIG = {
    minWordsToSend:   3,       // ignore speech shorter than this many words
    silenceWaitMs:    2500,    // ms of silence before auto-sending speech
    forceSendMs:      8000,    // ms cap — send even if silence timer hasn't fired
    maxHistoryMemory: 20,      // max conversation turns kept in browser memory
    historyToBackend: 8,       // how many recent turns are sent with each request
    bargeInThreshold: 0.02,    // mic RMS level that triggers barge-in
    niuattOrigin:     "https://niuatt.niua.in" // trusted postMessage origin
};
