// assets/audio.js — Audio Recording, Speech Recognition, Barge-in, and Voice Playback

let recognition = null;
let currentAudio = null;
let currentTranscript = '';
let finalTranscript = '';
let silenceTimer = null;
let forceTimer = null;

const audioPool = [];
let analyser = null;
let micStream = null;
let bargeInThreshold = (typeof CONFIG !== 'undefined' && CONFIG.bargeInThreshold) ? CONFIG.bargeInThreshold : 0.03;
let voiceFrames = 0;

// ============== SESSION INACTIVITY TIMER (5 SECONDS) ==============
let sessionInactivityTimer = null;

function startSessionInactivityTimer() {
  clearSessionInactivityTimer();
  if (sessionActive && currentState === States.LISTENING) {
    sessionInactivityTimer = setTimeout(() => {
      if (sessionActive && currentState === States.LISTENING) {
        endSessionAuto();
      }
    }, 5000);
  }
}

function clearSessionInactivityTimer() {
  if (sessionInactivityTimer) {
    clearTimeout(sessionInactivityTimer);
    sessionInactivityTimer = null;
  }
}

function endSessionAuto() {
  if (!sessionActive) return;
  sessionActive = false;
  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  clearSessionInactivityTimer();

  destroyRecognition();

  if (currentAudio) {
    currentAudio.pause();
    currentAudio.src = '';
    currentAudio = null;
  }

  if (micStream) {
    micStream.getTracks().forEach(t => t.stop());
    micStream = null;
  }

  updateSessionBtnIcon(false);
  setState(States.IDLE);
}

// ============== SPEECH RECOGNITION ==============
function destroyRecognition() {
  if (recognition) {
    recognition.onstart = null;
    recognition.onresult = null;
    recognition.onspeechend = null;
    recognition.onend = null;
    recognition.onerror = null;
    try { recognition.abort(); } catch (e) { }
    recognition = null;
  }
}

function createRecognition(lang) {
  destroyRecognition();
  const SR = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SR) {
    console.warn('Speech recognition not supported in this browser');
    if (typeof addBotMessage === 'function') {
      addBotMessage('Speech recognition is not supported in this browser. Please use Google Chrome, Edge, or a WebSpeech-compatible browser.', 'error', null, null, null);
    }
    return null;
  }

  recognition = new SR();
  recognition.continuous = true;
  recognition.interimResults = true;
  recognition.maxAlternatives = 1;
  recognition.lang = 'en-IN';

  recognition.onstart = function onRecognitionStart() {
    setState(States.LISTENING);
  };
  recognition.onresult = handleResult;
  recognition.onspeechend = handleSpeechEnd;
  recognition.onend = function onRecognitionEnd() {
    if (sessionActive && currentState === States.LISTENING) {
      setTimeout(() => {
        if (sessionActive && recognition) {
          try {
            recognition.start();
          } catch (e) {
            console.warn('[UPYOG SPEECH] Recognition restart failed, resetting to idle:', e);
            sessionActive = false;
            updateSessionBtnIcon(false);
            setState(States.IDLE);
          }
        }
      }, 100);
    } else if (!sessionActive && currentState === States.LISTENING) {
      setState(States.IDLE);
    }
  };
  recognition.onerror = function onRecognitionError(e) {
    console.warn('[UPYOG SPEECH] Recognition error:', e.error);
    if (e.error === 'no-speech' || e.error === 'aborted') return;
    if (e.error === 'not-allowed' || e.error === 'service-not-allowed' || e.error === 'audio-capture') {
      sessionActive = false;
      updateSessionBtnIcon(false);
      setState(States.IDLE);
      if (typeof addBotMessage === 'function') {
        addBotMessage('Microphone access was denied or is unavailable. Please check your browser microphone permissions or ensure the connection is HTTPS.', 'error', null, null, null);
      }
      return;
    }
    setTimeout(() => {
      if (sessionActive) {
        createRecognition(lastDetectedLang);
        try { recognition.start(); } catch (err) { }
      }
    }, 500);
  };
  return recognition;
}

function createAndStartRecognition() {
  const lang = lastDetectedLang || 'en';
  createRecognition(lang);
  if (recognition) {
    try {
      recognition.start();
    } catch (e) {
      console.error('[UPYOG MIC] Error starting recognition:', e);
      if (typeof addBotMessage === 'function') {
        addBotMessage('Sorry, microphone could not start. Please ensure microphone permissions are granted.', null, null, null, null);
      }
      sessionActive = false;
      updateSessionBtnIcon(false);
      setState(States.IDLE);
    }
  } else {
    sessionActive = false;
    updateSessionBtnIcon(false);
    setState(States.IDLE);
  }
}

function handleResult(event) {
  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  let interim = '';
  let newFinal = '';
  for (let i = event.resultIndex; i < event.results.length; i++) {
    const transcript = event.results[i][0].transcript;
    if (event.results[i].isFinal) newFinal += transcript + ' ';
    else interim += transcript;
  }
  if (newFinal) finalTranscript += newFinal;
  const displayText = finalTranscript + interim;
  if (displayText) {
    clearSessionInactivityTimer();
    const interimText = document.getElementById('speech-preview-text');
    const interimDisplay = document.getElementById('speech-preview');
    if (interimText) interimText.innerText = displayText;
    if (interimDisplay) interimDisplay.classList.add('show');
  }
  const textToSend = finalTranscript.trim() || interim.trim();
  if (textToSend) {
    silenceTimer = setTimeout(() => { attemptSend(textToSend); }, CONFIG.silenceWaitMs);
    if (!forceTimer) {
      forceTimer = setTimeout(() => { if (textToSend) attemptSend(textToSend); }, CONFIG.forceSendMs);
    }
  }
}

function handleSpeechEnd() { }

function attemptSend(text) {
  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  const words = text.trim().split(/\s+/);
  if (words.length < CONFIG.minWordsToSend) return;
  try { recognition.stop(); } catch (e) { }
  finalTranscript = '';
  currentTranscript = '';
  sendQuery(text.trim());
}

function onTurnComplete() {
  turnCount++;
  finalTranscript = '';
  currentTranscript = '';
  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  silenceTimer = null;
  forceTimer = null;
  if (!sessionActive) {
    setState(States.IDLE);
    return;
  }

  destroyRecognition();
  createRecognition(lastDetectedLang);

  try {
    recognition.start();
    setState(States.LISTENING);
  } catch (e) {
    if (e.name === 'InvalidStateError') {
      setState(States.LISTENING);
    } else {
      setTimeout(() => {
        if (sessionActive) {
          destroyRecognition();
          createRecognition(lastDetectedLang);
          recognition.start();
          setState(States.LISTENING);
        }
      }, 300);
    }
  }
}

// ============== BARGE-IN MONITOR ==============
async function startBargeInMonitor() {
  try {
    micStream = await navigator.mediaDevices.getUserMedia({ audio: true });
    const audioCtx = new AudioContext();
    const source = audioCtx.createMediaStreamSource(micStream);
    analyser = audioCtx.createAnalyser();
    analyser.fftSize = 512;
    source.connect(analyser);
    const buffer = new Float32Array(analyser.fftSize);

    function checkVolume() {
      if (!sessionActive) return;
      analyser.getFloatTimeDomainData(buffer);
      const rms = Math.sqrt(buffer.reduce((s, v) => s + v * v, 0) / buffer.length);
      if (currentState === States.SPEAKING) {
        if (rms > bargeInThreshold) {
          voiceFrames++;
          if (voiceFrames > 4) {
            triggerBargeIn();
            voiceFrames = 0;
            requestAnimationFrame(checkVolume);
            return;
          }
        } else {
          voiceFrames = 0;
        }
      } else {
        voiceFrames = 0;
      }
      requestAnimationFrame(checkVolume);
    }
    checkVolume();
  } catch (e) { }
}

function triggerBargeIn() {
  if (currentAudio) {
    currentAudio.onerror = null;
    currentAudio.onended = null;
    currentAudio.pause();
    currentAudio.currentTime = 0;
    currentAudio.src = '';
    currentAudio = null;
  }
  if (currentFetch) {
    currentFetch.abort();
    currentFetch = null;
  }
  fetch(`${API_URL}/stop`, { method: 'POST' }).catch(() => { });

  finalTranscript = '';
  currentTranscript = '';
  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  silenceTimer = null;
  forceTimer = null;
  destroyRecognition();
  setState('LISTENING');
  clearInterimDisplay();
  setTimeout(() => {
    if (sessionActive) {
      createRecognition(lastDetectedLang);
      try { recognition.start(); } catch (e) { }
    }
  }, 300);
}

function clearInterimDisplay() {
  const interimDisplay = document.getElementById('speech-preview');
  const interimText = document.getElementById('speech-preview-text');
  if (interimDisplay) {
    interimDisplay.classList.remove('show');
  }
  if (interimText) {
    interimText.innerText = '';
  }
}

// ============== INTERRUPT & STOP VOICE BUTTONS ==============
function showInterruptBtn() {
  const interruptWrapper = document.getElementById('interrupt-wrapper');
  if (interruptWrapper) interruptWrapper.classList.add('visible');
}

function hideInterruptBtn() {
  const interruptWrapper = document.getElementById('interrupt-wrapper');
  if (interruptWrapper) interruptWrapper.classList.remove('visible');
}

function handleStopVoiceClick() {
  console.log('[UPYOG STOP] handleStopVoiceClick invoked — stopping audio and ending session');
  removeThinkingIndicator();
  if (currentAudio) {
    currentAudio.onended = null;
    currentAudio.onerror = null;
    currentAudio.pause();
    currentAudio.src = '';
    currentAudio = null;
  }

  if (currentFetch) {
    currentFetch.abort();
    currentFetch = null;
  }

  fetch(`${API_URL}/stop`, { method: 'POST' }).catch(() => { });

  audioPool.forEach(u => { try { URL.revokeObjectURL(u); } catch (e) { } });
  audioPool.length = 0;

  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  silenceTimer = null;
  forceTimer = null;
  destroyRecognition();

  if (micStream) {
    micStream.getTracks().forEach(t => t.stop());
    micStream = null;
  }

  sessionActive = false;
  updateSessionBtnIcon(false);

  hideInterruptBtn();
  setState(States.IDLE);
}

function handleInterruptClick() {
  console.log('[UPYOG INTERRUPT] handleInterruptClick invoked — pausing voice and starting listening');
  removeThinkingIndicator();
  if (currentAudio) {
    currentAudio.onended = null;
    currentAudio.onerror = null;
    currentAudio.pause();
    currentAudio.src = '';
    currentAudio = null;
  }

  if (currentFetch) {
    currentFetch.abort();
    currentFetch = null;
  }

  fetch(`${API_URL}/stop`, { method: 'POST' }).catch(() => { });

  audioPool.forEach(u => { try { URL.revokeObjectURL(u); } catch (e) { } });
  audioPool.length = 0;

  finalTranscript = '';
  currentTranscript = '';
  clearTimeout(silenceTimer);
  clearTimeout(forceTimer);
  silenceTimer = null;
  forceTimer = null;

  hideInterruptBtn();

  if (sessionActive) {
    destroyRecognition();
    createRecognition(lastDetectedLang);
    setState(States.LISTENING);
    clearInterimDisplay();
    setTimeout(() => {
      if (sessionActive && recognition) {
        try { recognition.start(); } catch (e) { }
      }
    }, 150);
  }
}

// ============== AUDIO PLAYBACK ==============
async function playAudio(base64) {
  setState(States.SPEAKING);

  destroyRecognition();
  showInterruptBtn();

  try {
    if (currentAudio) {
      currentAudio.onerror = null;
      currentAudio.onended = null;
      currentAudio.pause();
      currentAudio.src = '';
      currentAudio.load();
      currentAudio = null;
    }
    audioPool.forEach(url => { try { URL.revokeObjectURL(url); } catch (e) { } });
    audioPool.length = 0;

    const mimeType = base64.startsWith('UklGR') ? 'audio/wav' : 'audio/mp3';
    const byteCharacters = atob(base64);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) byteNumbers[i] = byteCharacters.charCodeAt(i);
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], { type: mimeType });
    const url = URL.createObjectURL(blob);
    audioPool.push(url);

    const audio = currentAudio = new Audio(url);
    let audioHandled = false;

    function afterAudioDone() {
      if (audioHandled) return;
      audioHandled = true;
      URL.revokeObjectURL(url);
      currentAudio = null;
      hideInterruptBtn();
      if (sessionActive) {
        setState(States.LISTENING);
        onTurnComplete();
      } else {
        setState(States.IDLE);
      }
    }

    audio.onended = afterAudioDone;
    audio.onerror = afterAudioDone;

    await currentAudio.play();
  } catch (e) {
    hideInterruptBtn();
    if (sessionActive) {
      setState(States.LISTENING);
      onTurnComplete();
    } else {
      setState(States.IDLE);
    }
  }
}

function updateSessionBtnIcon(active) {
  const sessionBtn = document.getElementById('session-toggle-btn');
  if (!sessionBtn) return;
  if (!active) {
    sessionBtn.className = 'icon-button voice-button start';
    sessionBtn.innerHTML = ICONS.mic;
    sessionBtn.title = 'Start Session';
    return;
  }

  if (currentState === States.SPEAKING) {
    sessionBtn.className = 'icon-button voice-button end speaking';
    sessionBtn.innerHTML = ICONS.pause;
    sessionBtn.title = 'Interrupt & Ask';
  } else if (currentState === States.PROCESSING) {
    sessionBtn.className = 'icon-button voice-button end processing';
    sessionBtn.innerHTML = ICONS.mic;
    sessionBtn.title = 'End Session';
  } else if (currentState === States.LISTENING) {
    sessionBtn.className = 'icon-button voice-button end listening';
    sessionBtn.innerHTML = ICONS.mic;
    sessionBtn.title = 'End Session';
  } else {
    sessionBtn.className = 'icon-button voice-button end';
    sessionBtn.innerHTML = ICONS.mic;
    sessionBtn.title = 'End Session';
  }
}

async function handleSessionToggle() {
  if (currentState === States.SPEAKING) {
    handleInterruptClick();
    return;
  }

  if (sessionActive) {
    sessionActive = false;
    updateSessionBtnIcon(false);
    clearTimeout(silenceTimer);
    clearTimeout(forceTimer);
    clearSessionInactivityTimer();
    destroyRecognition();
    if (currentAudio) {
      currentAudio.onerror = null;
      currentAudio.onended = null;
      currentAudio.pause();
      currentAudio.src = '';
      currentAudio = null;
    }
    if (micStream) {
      micStream.getTracks().forEach(t => t.stop());
      micStream = null;
    }
    setState(States.IDLE);
  } else {
    sessionActive = true;
    updateSessionBtnIcon(true);
    turnCount = 0;
    finalTranscript = '';
    currentTranscript = '';
    try {
      createAndStartRecognition();
      setState(States.LISTENING);
    } catch (e) {
      sessionActive = false;
      updateSessionBtnIcon(false);
      setState(States.IDLE);
    }
  }
}

// Bind audio buttons
function bindAudioControls() {
  const stopVoiceBtn = document.getElementById('stop-voice-btn');
  const interruptMicBtn = document.getElementById('interrupt-mic-btn');
  const sessionBtn = document.getElementById('session-toggle-btn');

  if (stopVoiceBtn && !stopVoiceBtn.dataset.bound) {
    stopVoiceBtn.dataset.bound = "true";
    stopVoiceBtn.addEventListener('click', handleStopVoiceClick);
  }
  if (interruptMicBtn && !interruptMicBtn.dataset.bound) {
    interruptMicBtn.dataset.bound = "true";
    interruptMicBtn.addEventListener('click', handleInterruptClick);
  }
  if (sessionBtn && !sessionBtn.dataset.bound) {
    sessionBtn.dataset.bound = "true";
    sessionBtn.addEventListener('click', handleSessionToggle);
  }
}

if (typeof onDOMReady === 'function') {
  onDOMReady(bindAudioControls);
} else if (document.readyState === 'loading') {
  document.addEventListener('DOMContentLoaded', bindAudioControls);
} else {
  bindAudioControls();
}
