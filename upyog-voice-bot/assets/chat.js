// assets/chat.js — Chat UI, State Management, Authentication, and Message Dispatching

// Helper to execute callback when DOM is ready (or immediately if already parsed)
function onDOMReady(fn) {
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', fn);
  } else {
    fn();
  }
}

// ============== REDIS LOGIN LOGIC ==============
const isLocal = window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
let conversationSessionId = null;

function ensureSessionId() {
  if (!conversationSessionId) {
    const savedPhone = localStorage.getItem("upyog_redis_phone");
    const savedToken = localStorage.getItem("upyog_auth_token");
    if (savedPhone && savedToken && savedPhone.length === 10 && /^\d+$/.test(savedPhone)) {
      conversationSessionId = sessionStorage.getItem("upyog_active_session");
      if (!conversationSessionId || !conversationSessionId.includes(savedPhone)) {
        conversationSessionId = `user_${savedPhone}_${Math.random().toString(36).substring(2, 9)}`;
        sessionStorage.setItem("upyog_active_session", conversationSessionId);
      }
    } else {
      conversationSessionId = sessionStorage.getItem("upyog_active_session");
      if (!conversationSessionId || !conversationSessionId.startsWith("guest_")) {
        conversationSessionId = `guest_${Math.random().toString(36).substring(2, 9)}`;
        sessionStorage.setItem("upyog_active_session", conversationSessionId);
      }
    }
  }
  return conversationSessionId;
}

function openLoginModal() {
  if (!isLocal) return; // Never show login popup modal on external/testing/production environments
  const modal = document.getElementById("phone-login-modal");
  if (modal) {
    modal.style.display = "flex";
    showMobileScreen();
  }
}

function closeLoginModal() {
  const modal = document.getElementById("phone-login-modal");
  if (modal) modal.style.display = "none";
}

function handleLoginKeyPress(event) {
  if (event.key === "Enter") handlePhoneLogin();
}

function handleOtpKeyPress(event) {
  if (event.key === "Enter") handleOtpVerify();
}

function showMobileScreen() {
  const mobileScreen = document.getElementById("login-mobile-screen");
  const otpScreen = document.getElementById("login-otp-screen");
  const loginError = document.getElementById("login-error");
  const otpError = document.getElementById("otp-error");
  if (mobileScreen) mobileScreen.style.display = "block";
  if (otpScreen) otpScreen.style.display = "none";
  if (loginError) loginError.style.display = "none";
  if (otpError) otpError.style.display = "none";
  setTimeout(() => {
    const phoneInput = document.getElementById("phone-input");
    if (phoneInput) phoneInput.focus();
  }, 100);
}

async function handlePhoneLogin() {
  const phoneInput = document.getElementById("phone-input");
  const phone = phoneInput ? phoneInput.value.trim() : "";
  const errorText = document.getElementById("login-error");
  const phoneRegex = /^[0-9]{10}$/;

  if (!phoneRegex.test(phone)) {
    if (errorText) {
      errorText.style.display = "block";
      errorText.textContent = "Invalid phone number. Must be 10 digits.";
    }
    return;
  }
  if (errorText) errorText.style.display = "none";

  try {
    const activeBaseUrl = getActiveBaseUrl();
    const response = await fetch(`${API_URL}/api/send-otp`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(activeBaseUrl ? { "X-UPYOG-Base-Url": activeBaseUrl } : {})
      },
      body: JSON.stringify({
        mobile: phone,
        base_url: activeBaseUrl || undefined
      })
    });
    const data = await response.json();
    let errorMsg = null;
    if (data.error) {
      if (typeof data.error === 'object') {
        errorMsg = data.error.message || data.error.description || JSON.stringify(data.error);
      } else {
        errorMsg = data.error;
      }
    } else if (data.Errors && data.Errors.length > 0) {
      errorMsg = data.Errors[0].message || data.Errors[0].code;
    }

    if (errorMsg) {
      if (errorText) {
        errorText.style.display = "block";
        errorText.textContent = (typeof errorMsg === 'string' && errorMsg.length < 70 && !errorMsg.includes('{') && !errorMsg.includes('Exception'))
          ? errorMsg
          : "Failed to send OTP. Please verify your 10-digit mobile number and try again.";
      }
      return;
    }

    localStorage.setItem("upyog_redis_phone", phone);
    const mobileScreen = document.getElementById("login-mobile-screen");
    const otpScreen = document.getElementById("login-otp-screen");
    if (mobileScreen) mobileScreen.style.display = "none";
    if (otpScreen) otpScreen.style.display = "block";
    setTimeout(() => {
      const otpInput = document.getElementById("otp-input");
      if (otpInput) otpInput.focus();
    }, 100);
  } catch (err) {
    if (errorText) {
      errorText.style.display = "block";
      errorText.textContent = "Error sending OTP. Please try again.";
    }
  }
}

async function handleOtpVerify() {
  const otpInput = document.getElementById("otp-input");
  const otp = otpInput ? otpInput.value.trim() : "";
  const errorText = document.getElementById("otp-error");
  const phone = localStorage.getItem("upyog_redis_phone");

  if (otp.length !== 6 || !/^\d+$/.test(otp)) {
    if (errorText) {
      errorText.style.display = "block";
      errorText.textContent = "OTP must be 6 digits.";
    }
    return;
  }
  if (errorText) errorText.style.display = "none";

  try {
    const activeBaseUrl = getActiveBaseUrl();
    const response = await fetch(`${API_URL}/api/verify-otp`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        ...(activeBaseUrl ? { "X-UPYOG-Base-Url": activeBaseUrl } : {})
      },
      body: JSON.stringify({
        mobile: phone,
        otp: otp,
        base_url: activeBaseUrl || undefined
      })
    });
    if (!response.ok) {
      const errData = await response.json().catch(() => ({}));
      let errMsg = "Invalid OTP. Please check the 6-digit code and try again.";
      if (errData && errData.error && typeof errData.error === 'string' && errData.error.length < 70 && !errData.error.includes('{') && !errData.error.includes('Exception')) {
        errMsg = errData.error;
      }
      if (errorText) {
        errorText.style.display = "block";
        errorText.textContent = errMsg;
      }
      return;
    }
    const data = await response.json();

    localStorage.setItem("upyog_auth_token", data.access_token);
    localStorage.setItem("upyog_redis_phone", phone);
    localStorage.setItem("upyog_user_info", JSON.stringify(data.user_info));

    const sessionId = `user_${phone}_${Math.random().toString(36).substring(2, 9)}`;
    sessionStorage.setItem("upyog_active_session", sessionId);
    const modal = document.getElementById("phone-login-modal");
    if (modal) modal.style.display = "none";
    activateUser(sessionId, false);
  } catch (err) {
    if (errorText) {
      errorText.style.display = "block";
      errorText.textContent = "Error verifying OTP. Please try again.";
    }
  }
}

function bindFileAttachment() {
  const attachBtn = document.getElementById('attach-btn');
  const globalFileInput = document.getElementById('global-file-input');

  if (attachBtn && !attachBtn.dataset.bound) {
    attachBtn.dataset.bound = "true";
    attachBtn.addEventListener('click', () => {
      if (globalFileInput) globalFileInput.click();
    });
  }

  if (globalFileInput && !globalFileInput.dataset.bound) {
    globalFileInput.dataset.bound = "true";
    globalFileInput.addEventListener('change', (e) => {
      const file = e.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = function (evt) {
          const base64Data = evt.target.result;
          sendQuery('', file.name, base64Data, "Uploaded document: " + file.name);
        };
        reader.readAsDataURL(file);
        globalFileInput.value = "";
      }
    });
  }
}

function activateGuest(sessionId) {
  conversationSessionId = sessionId;
  console.log("Guest session active:", conversationSessionId);

  bindFileAttachment();

  const textInput = document.getElementById('text-input');
  const sendBtn = document.getElementById('send-text-btn');
  const sessionToggleBtn = document.getElementById('session-toggle-btn');
  const attachBtn = document.getElementById('attach-btn');

  if (textInput) {
    textInput.disabled = false;
    textInput.placeholder = "Ask anything about UPYOG services...";
  }
  if (sendBtn) sendBtn.disabled = false;
  if (sessionToggleBtn) sessionToggleBtn.disabled = false;
  if (attachBtn) attachBtn.disabled = false;

  const header = document.getElementById("main-header");
  if (header) {
    header.style.display = isLocal ? "flex" : "none";
  }
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  if (loginBtn) loginBtn.style.display = isLocal ? "inline-block" : "none";
  if (logoutBtn) logoutBtn.style.display = "none";
}

function activateUser(sessionId, sendGreeting = false) {
  conversationSessionId = sessionId;
  console.log("Memory active for session:", conversationSessionId);

  bindFileAttachment();

  const textInput = document.getElementById('text-input');
  const sendBtn = document.getElementById('send-text-btn');
  const sessionToggleBtn = document.getElementById('session-toggle-btn');
  const attachBtn = document.getElementById('attach-btn');

  if (textInput) {
    textInput.disabled = false;
    textInput.placeholder = "Ask anything about UPYOG services...";
  }
  if (sendBtn) sendBtn.disabled = false;
  if (sessionToggleBtn) sessionToggleBtn.disabled = false;
  if (attachBtn) attachBtn.disabled = false;

  const header = document.getElementById("main-header");
  if (header) {
    header.style.display = isLocal ? "flex" : "none";
  }
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  if (loginBtn) loginBtn.style.display = "none";
  if (logoutBtn) logoutBtn.style.display = isLocal ? "inline-block" : "none";

  if (sendGreeting) {
    sendQuery("hello");
  }
}

function logoutSession() {
  localStorage.removeItem("upyog_redis_phone");
  localStorage.removeItem("upyog_auth_token");
  localStorage.removeItem("upyog_user_info");
  sessionStorage.removeItem("upyog_active_session");
  window.location.reload();
}

// ============== STATE ==============
let currentState = (typeof States !== 'undefined') ? States.IDLE : "IDLE";
let sessionActive = false;
let currentLang = 'auto';
let currentFetch = null;

let turnCount = 0;
let lastDetectedLang = 'en';

let API_URL = window.location.origin;
if (window.location.pathname.includes('/upyog-voice-bot')) {
  API_URL += '/upyog-voice-bot';
} else if (window.location.pathname.includes('/upyog-voice')) {
  API_URL += '/upyog-voice';
}

let NIAUTT_REQUEST_INFO = {};

function getActiveBaseUrl() {
  try {
    const params = new URLSearchParams(window.location.search);
    const customBaseUrl = params.get("base_url") || params.get("baseUrl");
    if (customBaseUrl) {
      sessionStorage.setItem("upyog_base_url", customBaseUrl);
      return customBaseUrl;
    }
    const envParam = (params.get("env") || "").toLowerCase().trim();
    if (envParam === "sandbox") {
      sessionStorage.setItem("upyog_base_url", "https://upyog-sandbox.niua.org");
      return "https://upyog-sandbox.niua.org";
    }
    if (envParam === "production" || envParam === "prod") {
      sessionStorage.setItem("upyog_base_url", "https://upyog.niua.org");
      return "https://upyog.niua.org";
    }
    if (envParam === "niuatt" || envParam === "test") {
      sessionStorage.setItem("upyog_base_url", "https://niuatt.niua.in");
      return "https://niuatt.niua.in";
    }
    if (envParam === "local" || envParam === "localhost") {
      sessionStorage.setItem("upyog_base_url", "http://localhost:8080");
      return "http://localhost:8080";
    }
  } catch (e) {
    // Ignore URL parameter parsing errors
  }

  let b = sessionStorage.getItem("upyog_base_url") || sessionStorage.getItem("upyog_parent_origin");
  if (!b) {
    const origin = window.location.origin;
    if (origin.includes("niuatt.niua.in")) b = "https://niuatt.niua.in";
    else if (origin.includes("upyog-sandbox.niua.org")) b = "https://upyog-sandbox.niua.org";
    else if (origin.includes("upyog.niua.org")) b = "https://upyog.niua.org";
    else if (document.referrer) {
      if (document.referrer.includes("niuatt.niua.in")) b = "https://niuatt.niua.in";
      else if (document.referrer.includes("upyog-sandbox.niua.org")) b = "https://upyog-sandbox.niua.org";
      else if (document.referrer.includes("upyog.niua.org")) b = "https://upyog.niua.org";
    }
  }
  return b || "";
}

function handleNiuattMessage(event) {
  console.log('[UPYOG POST_MESSAGE] Received window postMessage', { data: event.data, origin: event.origin });

  // Store parent origin / base_url dynamically if from a valid web origin
  if (event.origin && (event.origin.startsWith("http://") || event.origin.startsWith("https://"))) {
    sessionStorage.setItem("upyog_parent_origin", event.origin);
  }
  const detectedUrl = event.data?.baseUrl || event.data?.RequestInfo?.baseUrl || event.data?.base_url || event.data?.RequestInfo?.base_url;
  if (detectedUrl) {
    sessionStorage.setItem("upyog_base_url", detectedUrl);
  }

  const payload = event.data?.RequestInfo || event.data?.requestInfo || event.data;
  if (payload && (payload.authToken || payload.userInfo || event.data?.type === "INIT_DATA")) {
    NIAUTT_REQUEST_INFO = event.data?.RequestInfo || payload;
    console.log('[UPYOG AUTH] Stored RequestInfo from parent page', NIAUTT_REQUEST_INFO);

    const token = payload.authToken || payload.token || event.data?.authToken;
    const user = payload.userInfo || payload.user || event.data?.userInfo || {};
    const mobile = user.mobileNumber || user.userName || event.data?.mobile || event.data?.phone;
    let cleanMobile = String(mobile || '').replace(/\D/g, '');
    if (cleanMobile.length > 10) cleanMobile = cleanMobile.slice(-10);

    if (token) {
      localStorage.setItem("upyog_auth_token", token);
      if (cleanMobile && cleanMobile.length === 10) {
        localStorage.setItem("upyog_redis_phone", cleanMobile);
      }
      if (user && Object.keys(user).length > 0) {
        localStorage.setItem("upyog_user_info", JSON.stringify(user));
      }

      closeLoginModal();

      let sessionId = sessionStorage.getItem("upyog_active_session");
      const phoneKey = cleanMobile || "user";
      if (!sessionId || !sessionId.includes(phoneKey)) {
        sessionId = `user_${phoneKey}_${Math.random().toString(36).substring(2, 9)}`;
        sessionStorage.setItem("upyog_active_session", sessionId);
      }
      activateUser(sessionId, false);
    }
  }

  if (event.data?.type === "TOGGLE_MIC" || event.data?.action === "TOGGLE_MIC" || (event.data?.type === "KEY_EVENT" && (event.data?.key === 'd' || event.data?.key === 'D'))) {
    console.log('[UPYOG POST_MESSAGE] Triggering session toggle from postMessage command');
    if (typeof handleSessionToggle === 'function') {
      handleSessionToggle();
    }
  }
}
window.addEventListener("message", handleNiuattMessage);

const getRequestInfo = () => {
  const info = { ...NIAUTT_REQUEST_INFO, msgId: `${Date.now()}|en_IN` };
  const activeBaseUrl = getActiveBaseUrl();
  if (activeBaseUrl) {
    info.baseUrl = activeBaseUrl;
  }
  const savedToken = localStorage.getItem("upyog_auth_token");
  if (savedToken) {
    info.authToken = savedToken;
  } else {
    const savedPhone = localStorage.getItem("upyog_redis_phone");
    if (savedPhone && savedPhone.length === 10 && /^\d+$/.test(savedPhone)) {
      info.authToken = savedPhone;
    }
  }
  return info;
};

// ============== STATE MACHINE ==============
function setState(newState) {
  console.log('[UPYOG STATE] Transitioning state: ' + currentState + ' -> ' + newState);
  currentState = newState;
  const statusDot = document.getElementById('status-dot');
  const statusText = document.getElementById('status-text');
  const sendBtn = document.getElementById('send-text-btn');

  if (!statusDot || !statusText) return;

  if (newState === States.SPEAKING) {
    statusDot.className = 'status-dot speaking';
    statusDot.innerHTML = (typeof ICONS !== 'undefined' && ICONS.speakingWave) ? ICONS.speakingWave : '';
    if (sendBtn) {
      sendBtn.disabled = false;
      sendBtn.innerHTML = (typeof ICONS !== 'undefined' && ICONS.stop) ? ICONS.stop : '';
      sendBtn.title = 'Stop Voice & End Session';
      sendBtn.classList.add('stop-mode');
    }
    if (typeof updateSessionBtnIcon === 'function') updateSessionBtnIcon(sessionActive);
  } else if (newState === States.PROCESSING) {
    statusDot.className = 'status-dot processing';
    statusDot.innerHTML = '';
    if (sendBtn) {
      sendBtn.disabled = false;
      sendBtn.innerHTML = (typeof ICONS !== 'undefined' && ICONS.stop) ? ICONS.stop : '';
      sendBtn.title = 'Stop Voice & End Session';
      sendBtn.classList.add('stop-mode');
    }
    if (typeof updateSessionBtnIcon === 'function') updateSessionBtnIcon(sessionActive);
  } else if (newState === States.LISTENING) {
    statusDot.className = 'status-dot listening';
    statusDot.innerHTML = '';
    if (sendBtn) {
      sendBtn.disabled = false;
      sendBtn.innerHTML = (typeof ICONS !== 'undefined' && ICONS.send) ? ICONS.send : '';
      sendBtn.title = 'Send message';
      sendBtn.classList.remove('stop-mode');
    }
    if (typeof updateSessionBtnIcon === 'function') updateSessionBtnIcon(sessionActive);
    if (typeof startSessionInactivityTimer === 'function') startSessionInactivityTimer();
  } else {
    statusDot.className = 'status-dot ' + String(newState).toLowerCase();
    statusDot.innerHTML = '';
    if (sendBtn) {
      sendBtn.disabled = false;
      sendBtn.innerHTML = (typeof ICONS !== 'undefined' && ICONS.send) ? ICONS.send : '';
      sendBtn.title = 'Send message';
      sendBtn.classList.remove('stop-mode');
    }
    if (typeof updateSessionBtnIcon === 'function') updateSessionBtnIcon(sessionActive);
    if (typeof clearSessionInactivityTimer === 'function') clearSessionInactivityTimer();
    if (typeof clearInterimDisplay === 'function') clearInterimDisplay();
  }

  statusText.innerText = (typeof STATE_LABELS !== 'undefined' && STATE_LABELS[newState]) ? STATE_LABELS[newState] : newState;
}

function toggleSettingsPanel() {
  const settingsPanel = document.getElementById('settings-panel');
  if (settingsPanel) settingsPanel.classList.toggle('show');
}

function updateBargeSensitivity(e) {
  if (typeof bargeInThreshold !== 'undefined') {
    bargeInThreshold = parseFloat(e.target.value);
  }
  const sensitivityValue = document.getElementById('sensitivity-value');
  if (sensitivityValue) sensitivityValue.innerText = parseFloat(e.target.value).toFixed(2);
}

function syncLangPreference(e) {
  currentLang = e.target.value;
  document.querySelectorAll('.lang-btn').forEach(btn => {
    btn.classList.toggle('active', btn.dataset.lang === currentLang);
  });
}

function showTextInputBar(field) {
  const input = document.getElementById('text-input');
  if (input) {
    input.placeholder = (typeof FIELD_PLACEHOLDERS !== 'undefined' && FIELD_PLACEHOLDERS[field]) ? FIELD_PLACEHOLDERS[field] : ((typeof FIELD_PLACEHOLDERS !== 'undefined') ? FIELD_PLACEHOLDERS.default : 'Type your answer...');
    setTimeout(() => input.focus(), 500);
  }
}

function removeTextInputBar() {
  const input = document.getElementById('text-input');
  if (input) input.placeholder = 'Ask anything about UPYOG services...';
}

// ============== THINKING INDICATOR ==============
function showThinkingIndicator() {
  console.log('[UPYOG UI] Displaying Thinking indicator bubble');
  removeThinkingIndicator();

  const chatContainer = document.getElementById('chat-container');
  if (!chatContainer) return;

  const wrapper = document.createElement('div');
  wrapper.className = 'message-wrapper bot thinking-wrapper';
  wrapper.id = 'thinking-indicator';

  const bubble = document.createElement('div');
  bubble.className = 'message bot thinking-bubble';
  bubble.innerHTML = `
      <div class="typing-dots">
          <span></span>
          <span></span>
          <span></span>
      </div>
      <span class="thinking-text">Thinking...</span>
  `;

  wrapper.appendChild(bubble);
  chatContainer.appendChild(wrapper);
  chatContainer.scrollTop = chatContainer.scrollHeight;
}

function removeThinkingIndicator() {
  const indicator = document.getElementById('thinking-indicator');
  if (indicator) {
    console.log('[UPYOG UI] Removing Thinking indicator bubble');
    indicator.remove();
  }
}

function disablePreviousInteractiveElements() {
  const chatContainer = document.getElementById('chat-container');
  if (!chatContainer) return;
  const wrappers = chatContainer.querySelectorAll('.message-wrapper.bot, .message-wrapper');
  wrappers.forEach(w => {
    w.querySelectorAll('button, input, select').forEach(el => {
      el.disabled = true;
      el.style.pointerEvents = 'none';
    });
    w.querySelectorAll('.choice-pill-container, .choice-dropdown-wrapper, .choice-hint, .number-hint, .date-input-container, .file-input-container, .ui-table-actions').forEach(el => {
      el.classList.add('disabled-history');
      el.style.display = 'none';
    });
  });
}

function addUserMessage(text) {
  disablePreviousInteractiveElements();
  const hero = document.getElementById('hero-welcome');
  if (hero) hero.classList.add('hidden');

  const chatContainer = document.getElementById('chat-container');
  if (!chatContainer) return;

  const rowDiv = document.createElement('div');
  rowDiv.className = 'message-row user-row';
  const msgDiv = document.createElement('div');
  msgDiv.className = 'message user';
  msgDiv.innerText = text;
  const avatarDiv = document.createElement('div');
  avatarDiv.className = 'user-avatar';
  avatarDiv.innerHTML = (typeof ICONS !== 'undefined' && ICONS.avatar) ? ICONS.avatar : '';

  rowDiv.appendChild(msgDiv);
  rowDiv.appendChild(avatarDiv);
  chatContainer.appendChild(rowDiv);
  showThinkingIndicator();
  chatContainer.scrollTop = chatContainer.scrollHeight;
}

function formatMarkdown(text) {
  if (!text) return '';
  let str = String(text);

  // 1. Join orphaned heading hashes with next line
  str = str.replace(/(?:^|\n)#{1,6}\s*\n+\s*([^\n#]+)/g, '\n### $1');

  // 2. Code blocks
  str = str.replace(/```([\s\S]*?)```/g, '<pre class="code-block"><code>$1</code></pre>');
  // 3. Inline code
  str = str.replace(/`([^`]+)`/g, '<code class="inline-code">$1</code>');

  // 4. Headings
  str = str.replace(/(?:^|\n)#{4,6}\s*(.*?)(?=\n|$)/g, '<h4>$1</h4>');
  str = str.replace(/(?:^|\n)###\s*(.*?)(?=\n|$)/g, '<h3>$1</h3>');
  str = str.replace(/(?:^|\n)##\s*(.*?)(?=\n|$)/g, '<h3>$1</h3>');
  str = str.replace(/(?:^|\n)#\s*(.*?)(?=\n|$)/g, '<h3>$1</h3>');

  // 5. Remove leftover hashes
  str = str.replace(/(?:^|\n)#{1,6}\s*(?=\n|$)/g, '');

  // 6. Bold & Italic
  str = str.replace(/\*\*\*(.*?)\*\*\*/g, '<strong><em>$1</em></strong>');
  str = str.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>');
  str = str.replace(/__(.*?)__/g, '<strong>$1</strong>');
  str = str.replace(/\*(.*?)\*/g, '<em>$1</em>');
  str = str.replace(/_([^_]+)_/g, '<em>$1</em>');

  // 7. Links
  str = str.replace(/\[([^\]]+)\]\((https?:\/\/[^\s\)]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer">$1</a>');

  // 8. List items
  str = str.replace(/(?:^|\n)\s*[-*+]\s+(.*?)(?=\n|$)/g, '<div class="list-item bullet">• $1</div>');
  str = str.replace(/(?:^|\n)\s*(\d+\.)\s+(.*?)(?=\n|$)/g, '<div class="list-item num"><strong>$1</strong> $2</div>');

  // 9. Line breaks
  str = str.replace(/\n\n+/g, '<br/><br/>');
  str = str.replace(/\n/g, '<br/>');

  // 10. Clean up redundant line breaks
  str = str.replace(/(<br\/>)+\s*(<h[1-6]>)/g, '$2');
  str = str.replace(/(<\/h[1-6]>)\s*(<br\/>)+/g, '$1');
  str = str.replace(/(<br\/>)+\s*(<div class="list-item)/g, '$2');
  str = str.replace(/(<\/div>)\s*(<br\/>)+(?=<div class="list-item)/g, '$1');

  return str.trim();
}

function addBotMessage(text, mode, inputType, options, field, minDate) {
  removeThinkingIndicator();
  disablePreviousInteractiveElements();
  const hero = document.getElementById('hero-welcome');
  if (hero) hero.classList.add('hidden');
  if (mode !== 'grievance_collecting') removeTextInputBar();

  const chatContainer = document.getElementById('chat-container');
  if (!chatContainer) return;

  const wrapper = document.createElement('div');
  wrapper.className = 'message-wrapper bot';
  const bubble = document.createElement('div');
  bubble.className = 'message bot';
  if (mode === 'grievance_collecting') bubble.classList.add('grievance-collecting');
  bubble.innerHTML = formatMarkdown(text);
  wrapper.appendChild(bubble);

  if (inputType === 'choice' && options && options.length > 0) {
    if (options.length > 6) {
      const selectWrapper = document.createElement('div');
      selectWrapper.className = 'choice-dropdown-wrapper';
      const select = document.createElement('select');
      select.className = 'choice-select';
      const defaultOpt = document.createElement('option');
      defaultOpt.value = '';
      defaultOpt.textContent = '-- Select an option --';
      defaultOpt.disabled = true;
      defaultOpt.selected = true;
      select.appendChild(defaultOpt);
      options.forEach(opt => {
        const o = document.createElement('option');
        o.value = opt;
        o.textContent = opt;
        select.appendChild(o);
      });
      const confirmBtn = document.createElement('button');
      confirmBtn.textContent = 'Confirm';
      confirmBtn.className = 'choice-confirm-btn';
      confirmBtn.onclick = function handleConfirmSelection() {
        if (!select.value) return;
        select.disabled = true;
        confirmBtn.disabled = true;
        sendQuery(select.value);
      };
      selectWrapper.appendChild(select);
      selectWrapper.appendChild(confirmBtn);
      wrapper.appendChild(selectWrapper);
    } else {
      const btnContainer = document.createElement('div');
      btnContainer.className = 'choice-pill-container';
      options.forEach(opt => {
        const btn = document.createElement('button');
        btn.textContent = opt;
        btn.className = 'choice-pill-btn';
        btn.onclick = function handleChoiceBtnClick() {
          btnContainer.querySelectorAll('button').forEach(b => { b.disabled = true; });
          btn.classList.add('selected');
          sendQuery(opt);
        };
        btnContainer.appendChild(btn);
      });
      wrapper.appendChild(btnContainer);
    }
    const hint = document.createElement('div');
    hint.className = 'choice-hint';
    hint.textContent = (typeof HINT_MESSAGES !== 'undefined' && HINT_MESSAGES.speakChoice) ? HINT_MESSAGES.speakChoice : 'You can also speak your choice';
    wrapper.appendChild(hint);
  }

  if (inputType === 'checkbox' && options && options.length > 0) {
    const checkContainer = document.createElement('div');
    checkContainer.className = 'choice-dropdown-wrapper';
    checkContainer.style.display = 'flex';
    checkContainer.style.flexDirection = 'column';
    checkContainer.style.gap = '8px';
    checkContainer.style.alignItems = 'flex-start';
    checkContainer.style.background = 'rgba(255, 255, 255, 0.7)';
    checkContainer.style.padding = '12px';
    checkContainer.style.borderRadius = '16px';
    checkContainer.style.marginTop = '8px';

    options.forEach(opt => {
      const label = document.createElement('label');
      label.style.display = 'flex';
      label.style.alignItems = 'center';
      label.style.gap = '8px';
      label.style.cursor = 'pointer';

      const cb = document.createElement('input');
      cb.type = 'checkbox';
      cb.value = opt;

      label.appendChild(cb);
      label.appendChild(document.createTextNode(opt));
      checkContainer.appendChild(label);
    });

    const confirmBtn = document.createElement('button');
    confirmBtn.textContent = 'Submit';
    confirmBtn.className = 'choice-confirm-btn';
    confirmBtn.style.marginTop = '10px';
    confirmBtn.style.alignSelf = 'center';

    confirmBtn.onclick = function () {
      const selected = Array.from(checkContainer.querySelectorAll('input:checked')).map(cb => cb.value);
      if (selected.length === 0) return;
      checkContainer.querySelectorAll('input').forEach(cb => cb.disabled = true);
      confirmBtn.disabled = true;
      sendQuery(selected.join(', '));
    };

    checkContainer.appendChild(confirmBtn);
    wrapper.appendChild(checkContainer);
  }

  if (inputType === 'number') {
    const hint = document.createElement('div');
    hint.className = 'number-hint';
    hint.textContent = (typeof HINT_MESSAGES !== 'undefined' && HINT_MESSAGES.speakDigits) ? HINT_MESSAGES.speakDigits : 'Speak digits clearly, or type below';
    wrapper.appendChild(hint);
    showTextInputBar(field);
  }

  if (inputType === 'date') {
    const dateContainer = document.createElement('div');
    dateContainer.className = 'date-input-container';
    dateContainer.style.marginTop = '10px';
    const dateInput = document.createElement('input');
    dateInput.type = 'date';
    dateInput.className = 'native-date-input';
    dateInput.style.padding = '8px';
    dateInput.style.borderRadius = '8px';
    dateInput.style.border = '1px solid #ccc';
    dateInput.style.fontSize = '16px';
    dateInput.style.marginRight = '10px';

    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    const tYear = tomorrow.getFullYear();
    const tMonth = String(tomorrow.getMonth() + 1).padStart(2, '0');
    const tDay = String(tomorrow.getDate()).padStart(2, '0');
    const tomorrowStr = `${tYear}-${tMonth}-${tDay}`;

    if (minDate && minDate > tomorrowStr) {
      dateInput.min = minDate;
      dateInput.value = minDate;
    } else {
      dateInput.min = tomorrowStr;
      dateInput.value = tomorrowStr;
    }

    const confirmBtn = document.createElement('button');
    confirmBtn.textContent = 'Confirm';
    confirmBtn.className = 'choice-confirm-btn';
    confirmBtn.onclick = function () {
      if (!dateInput.value) return;
      dateInput.disabled = true;
      confirmBtn.disabled = true;
      sendQuery(dateInput.value);
    };

    dateContainer.appendChild(dateInput);
    dateContainer.appendChild(confirmBtn);
    wrapper.appendChild(dateContainer);
  }

  if (inputType === 'file') {
    const fileContainer = document.createElement('div');
    fileContainer.className = 'file-input-container';
    fileContainer.style.marginTop = '10px';

    const fileInput = document.createElement('input');
    fileInput.type = 'file';
    fileInput.accept = 'image/*,application/pdf';
    fileInput.className = 'native-file-input';
    fileInput.style.display = 'none';

    const uploadBtn = document.createElement('button');
    uploadBtn.textContent = 'Choose File';
    uploadBtn.className = 'choice-confirm-btn';
    uploadBtn.style.marginRight = '10px';

    const fileLabel = document.createElement('span');
    fileLabel.textContent = 'No file chosen';
    fileLabel.style.fontSize = '14px';
    fileLabel.style.color = '#666';
    fileLabel.style.marginRight = '10px';

    uploadBtn.onclick = function () {
      fileInput.click();
    };

    let selectedFile = null;
    fileInput.onchange = function (e) {
      const file = e.target.files[0];
      if (file) {
        selectedFile = file;
        fileLabel.textContent = file.name;
        confirmBtn.disabled = false;
      }
    };

    const confirmBtn = document.createElement('button');
    confirmBtn.textContent = 'Upload & Confirm';
    confirmBtn.className = 'choice-confirm-btn';
    confirmBtn.disabled = true;

    confirmBtn.onclick = function () {
      if (!selectedFile) return;
      fileInput.disabled = true;
      uploadBtn.disabled = true;
      confirmBtn.disabled = true;

      const reader = new FileReader();
      reader.onload = function (evt) {
        const base64Data = evt.target.result;
        sendQuery('', selectedFile.name, base64Data);
      };
      reader.readAsDataURL(selectedFile);
    };

    fileContainer.appendChild(fileInput);
    fileContainer.appendChild(uploadBtn);
    fileContainer.appendChild(fileLabel);
    fileContainer.appendChild(confirmBtn);
    wrapper.appendChild(fileContainer);
  }

  if (inputType === 'slot_table' && options && options.length > 0) {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const todayStr = `${year}-${month}-${day}`;

    const validSlots = options.filter(opt => opt && opt.date && String(opt.date).trim() > todayStr);
    if (validSlots.length > 0) {
      const tableContainer = document.createElement('div');
      tableContainer.className = 'ui-table-wrapper';
      const table = document.createElement('table');
      table.className = 'ui-table';
      table.innerHTML = `
        <thead>
          <tr>
            <th>Select</th>
            <th>Ad Type</th>
            <th>Face Area</th>
            <th>Night Light</th>
            <th>Booking Date</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
        </tbody>
      `;
      const tbody = table.querySelector('tbody');
      validSlots.forEach(opt => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td><input type="checkbox" value='${JSON.stringify(opt)}' class="slot-checkbox"></td>
          <td>${opt.type || ''}</td>
          <td>${opt.area || ''}</td>
          <td>${opt.light || ''}</td>
          <td>${opt.date || ''}</td>
          <td><span class="status-badge available">${opt.status || 'Available'}</span></td>
        `;
        tbody.appendChild(tr);
      });
      const btnContainer = document.createElement('div');
      btnContainer.className = 'ui-table-actions';
      const addBtn = document.createElement('button');
      addBtn.className = 'ui-button primary';
      addBtn.textContent = 'Add to Cart';
      addBtn.onclick = () => {
        const selected = Array.from(table.querySelectorAll('.slot-checkbox:checked')).map(cb => JSON.parse(cb.value));
        if (selected.length === 0) return;
        table.querySelectorAll('input').forEach(cb => cb.disabled = true);
        addBtn.disabled = true;
        sendQuery(JSON.stringify(selected), null, null, `Selected ${selected.length} slot(s)`);
      };
      tableContainer.appendChild(table);
      btnContainer.appendChild(addBtn);
      tableContainer.appendChild(btnContainer);
      wrapper.appendChild(tableContainer);
    }
  }

  if ((inputType === 'booking_history' || inputType === 'complaint_history') && options && options.length > 0) {
    const listContainer = document.createElement('div');
    listContainer.className = 'ui-card-list';
    options.forEach(opt => {
      const card = document.createElement('div');
      card.className = 'ui-card';
      const isComplaint = (inputType === 'complaint_history');
      const idLabel = isComplaint ? "Complaint No." : "Booking No.";
      const idVal = opt.serviceRequestId || opt.bookingNo || 'N/A';
      const typeLabel = isComplaint ? "Complaint Type" : "Applicant Name";
      const typeVal = opt.serviceCode || (opt.applicantDetail && opt.applicantDetail.applicantName) || opt.name || 'N/A';
      const dateLabel = isComplaint ? "Date Registered" : "Booking Date";
      const dateVal = opt.filed_on || opt.bookingDate || 'N/A';
      const statusVal = opt.applicationStatus || opt.status || 'N/A';

      card.innerHTML = `
        <div class="ui-card-header">
          <strong>${idLabel}</strong>
          <span>${idVal}</span>
        </div>
        <div class="ui-card-body">
          <div class="ui-card-row">
            <span class="label">${typeLabel}</span>
            <span class="value">${typeVal}</span>
          </div>
          <div class="ui-card-row">
            <span class="label">${dateLabel}</span>
            <span class="value">${dateVal}</span>
          </div>
          <div class="ui-card-row">
            <span class="label">Status</span>
            <span class="value">${statusVal}</span>
          </div>
        </div>
      `;
      listContainer.appendChild(card);
    });
    wrapper.appendChild(listContainer);
  }

  if (inputType === 'applicant_form' && options) {
    const formContainer = document.createElement('div');
    formContainer.className = 'ui-applicant-form';
    formContainer.innerHTML = `
      <div class="ui-cart-summary">
        <h3>Cart Details</h3>
        <div class="cart-total">Total Booking Amount: <span>${options.cartAmount || '0'} INR</span></div>
      </div>
      <div class="ui-form-body">
        <h3>Applicant Personal Details</h3>
        <div class="ui-form-group">
          <label>Applicant Name <span class="required">*</span></label>
          <input type="text" id="app-name" placeholder="Enter full name">
        </div>
        <div class="ui-form-group">
          <label>Mobile Number <span class="required">*</span></label>
          <input type="text" id="app-mobile" placeholder="+91" value="${localStorage.getItem('upyog_redis_phone') || ''}">
        </div>
        <div class="ui-form-group">
          <label>Email ID <span class="required">*</span></label>
          <input type="email" id="app-email" placeholder="example@gmail.com">
        </div>
        <button class="ui-button primary w-100" id="app-submit">Next</button>
      </div>
    `;
    wrapper.appendChild(formContainer);
    setTimeout(() => {
      const submitBtn = formContainer.querySelector('#app-submit');
      if (submitBtn) {
        submitBtn.onclick = () => {
          const name = formContainer.querySelector('#app-name').value;
          const mobile = formContainer.querySelector('#app-mobile').value;
          const email = formContainer.querySelector('#app-email').value;
          if (!name || !mobile || !email) {
            alert('Please fill all required fields');
            return;
          }
          formContainer.querySelectorAll('input').forEach(i => i.disabled = true);
          submitBtn.disabled = true;
          sendQuery(JSON.stringify({ name, mobile, email }), null, null, "Submitted applicant details");
        };
      }
    }, 100);
  }

  if (inputType === 'text') showTextInputBar(field);
  chatContainer.appendChild(wrapper);
  chatContainer.scrollTop = chatContainer.scrollHeight;
}

// ============== SEND QUERY ==============
async function sendQuery(transcript, file_name = null, file_data = null, display_text = null) {
  const activeSessionId = ensureSessionId();
  if ((!String(transcript || '').trim() && !file_name) || !activeSessionId) return;
  if (typeof destroyRecognition === 'function') destroyRecognition();
  setState(States.PROCESSING);
  if (typeof clearInterimDisplay === 'function') clearInterimDisplay();
  if (currentFetch) { currentFetch.abort(); currentFetch = null; }
  if (file_name) {
    addUserMessage(`[Uploaded Document: ${file_name}]`);
  } else {
    addUserMessage(display_text || transcript);
  }
  currentFetch = new AbortController();

  try {
    const activeBaseUrl = getActiveBaseUrl();
    const response = await fetch(`${API_URL}/chat`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        ...(activeBaseUrl ? { 'X-UPYOG-Base-Url': activeBaseUrl } : {})
      },
      body: JSON.stringify({
        RequestInfo: getRequestInfo(),
        query: transcript || '',
        session_id: activeSessionId,
        auth_token: localStorage.getItem("upyog_auth_token"),
        file_name: file_name,
        file_data: file_data,
        base_url: activeBaseUrl || undefined
      }),
      signal: currentFetch.signal
    });

    const data = await response.json();
    currentFetch = null;

    const inputArea = document.querySelector('.input-area');
    if (inputArea) {
      const isGrievance = data.mode === 'grievance' || data.mode === 'grievance_collecting' || data.mode === 'grievance_offered';
      inputArea.classList.toggle('input-area--grievance', isGrievance);
    }

    if (data.error) {
      let errContent = data.response;
      if (!errContent) {
        const errLower = String(data.error).toLowerCase();
        if (errLower.includes('token') || errLower.includes('rate_limit') || errLower.includes('429')) {
          errContent = "The AI assistant has temporarily reached its token limit. Please wait a moment and try again with a shorter message.";
        } else if (errLower.includes('401') || errLower.includes('unauthorized') || errLower.includes('session') || errLower.includes('auth')) {
          errContent = "Your login session has expired. Please log in again using the Login button to continue.";
        } else {
          errContent = "I am currently unable to process your request. Please try again in a few moments.";
        }
      }
      addBotMessage(errContent, 'error', null, null, null);
      if (data.audio && typeof playAudio === 'function') {
        playAudio(data.audio);
      } else {
        setState(States.LISTENING);
        if (typeof onTurnComplete === 'function') onTurnComplete();
      }
      return;
    }

    const detectedLang = data.lang || 'en';
    lastDetectedLang = detectedLang;
    if (typeof recognition !== 'undefined' && recognition) recognition.lang = 'en-IN';

    if (data.messages && Array.isArray(data.messages) && data.messages.length > 1) {
      data.messages.forEach((msg, idx) => {
        const isLast = (idx === data.messages.length - 1);
        addBotMessage(
          msg,
          data.mode,
          isLast ? data.input_type : 'text',
          isLast ? data.options : [],
          data.field,
          data.min_date
        );
      });
    } else {
      addBotMessage(data.response, data.mode, data.input_type, data.options, data.field, data.min_date);
    }

    if (data.audio && typeof playAudio === 'function') {
      playAudio(data.audio);
    } else {
      setState(States.LISTENING);
      if (typeof onTurnComplete === 'function') onTurnComplete();
    }

  } catch (error) {
    if (error.name !== 'AbortError') {
      addBotMessage("I am unable to reach the server at the moment. Please check your internet connection and try again.", null, null, null, null);
    }
    setState(States.LISTENING);
    currentFetch = null;
    if (typeof onTurnComplete === 'function') onTurnComplete();
  }
}

function handleTextSubmit() {
  if (currentState === States.SPEAKING || currentState === States.PROCESSING) return;
  const textInput = document.getElementById('text-input');
  if (!textInput) return;
  const text = textInput.value.trim();
  if (!text) return;
  sendQuery(text);
  textInput.value = '';
}

function handleSendBtnClick() {
  if (currentState === States.SPEAKING || currentState === States.PROCESSING) {
    if (typeof handleStopVoiceClick === 'function') handleStopVoiceClick();
  } else {
    handleTextSubmit();
  }
}

function handleKeydown(e) {
  if ((e.ctrlKey || e.metaKey) && (e.key === 'd' || e.key === 'D' || e.code === 'KeyD')) {
    e.preventDefault();
    e.stopPropagation();
    if (typeof handleSessionToggle === 'function') handleSessionToggle();
  }
}
window.addEventListener('keydown', handleKeydown, true);
document.addEventListener('keydown', handleKeydown, true);

function autoFocusChatbot() {
  try {
    window.focus();
    const input = document.getElementById('text-input');
    if (input && document.activeElement !== input) {
      input.focus();
    }
  } catch (e) { }
}

window.addEventListener('load', autoFocusChatbot);
document.addEventListener('DOMContentLoaded', autoFocusChatbot);
window.addEventListener('mouseenter', autoFocusChatbot);
document.addEventListener('mousemove', autoFocusChatbot, { once: true });

function saveChatHistory() {
  const chatContainer = document.getElementById("chat-container");
  if (chatContainer) {
    localStorage.setItem("upyogChatHistory", chatContainer.innerHTML);
  }
}

// ============== INITIALIZATION RUNNER ==============
function initializeChatApp() {
  const urlParams = new URLSearchParams(window.location.search);
  const urlToken = urlParams.get("token") || urlParams.get("authToken") || urlParams.get("auth_token");
  const urlPhone = urlParams.get("phone") || urlParams.get("mobile") || urlParams.get("mobileNumber");
  if (urlToken && urlPhone && urlPhone.length === 10 && /^\d+$/.test(urlPhone)) {
    localStorage.setItem("upyog_auth_token", urlToken);
    localStorage.setItem("upyog_redis_phone", urlPhone);
  }

  const savedPhone = localStorage.getItem("upyog_redis_phone");
  const savedToken = localStorage.getItem("upyog_auth_token");
  const loginBtn = document.getElementById("login-btn");
  const logoutBtn = document.getElementById("logout-btn");
  const header = document.getElementById("main-header");
  if (header) {
    header.style.display = isLocal ? "flex" : "none";
  }

  closeLoginModal();

  if (savedPhone && savedToken && savedPhone.length === 10 && /^\d+$/.test(savedPhone)) {
    if (loginBtn) loginBtn.style.display = "none";
    if (logoutBtn) logoutBtn.style.display = isLocal ? "inline-block" : "none";
    let sessionId = sessionStorage.getItem("upyog_active_session");
    if (!sessionId || !sessionId.includes(savedPhone)) {
      sessionId = `user_${savedPhone}_${Math.random().toString(36).substring(2, 9)}`;
      sessionStorage.setItem("upyog_active_session", sessionId);
    }
    activateUser(sessionId, false);
  } else {
    if (loginBtn) loginBtn.style.display = isLocal ? "inline-block" : "none";
    if (logoutBtn) logoutBtn.style.display = "none";
    let sessionId = sessionStorage.getItem("upyog_active_session");
    if (!sessionId || !sessionId.startsWith("guest_")) {
      sessionId = `guest_${Math.random().toString(36).substring(2, 9)}`;
      sessionStorage.setItem("upyog_active_session", sessionId);
    }
    activateGuest(sessionId);
  }

  // Language buttons
  document.querySelectorAll('.lang-btn').forEach(btn => {
    btn.addEventListener('click', function handleLangBtnClick() {
      document.querySelectorAll('.lang-btn').forEach(b => b.classList.remove('active'));
      btn.classList.add('active');
      currentLang = btn.dataset.lang;
    });
  });

  const settingsToggle = document.getElementById('settings-toggle');
  if (settingsToggle) settingsToggle.addEventListener('click', toggleSettingsPanel);

  const bargeSensitivity = document.getElementById('barge-sensitivity');
  if (bargeSensitivity) bargeSensitivity.addEventListener('input', updateBargeSensitivity);

  const langPreference = document.getElementById('lang-preference');
  if (langPreference) langPreference.addEventListener('change', syncLangPreference);

  const sendTextBtn = document.getElementById('send-text-btn');
  const textInput = document.getElementById('text-input');

  if (sendTextBtn && !sendTextBtn.dataset.bound) {
    sendTextBtn.dataset.bound = "true";
    sendTextBtn.addEventListener('click', handleSendBtnClick);
  }
  if (textInput && !textInput.dataset.bound) {
    textInput.dataset.bound = "true";
    textInput.addEventListener('keypress', (e) => { if (e.key === 'Enter') handleTextSubmit(); });
  }
}

// Execute when DOM is ready
onDOMReady(initializeChatApp);
