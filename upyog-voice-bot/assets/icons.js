// All SVG icons used in the UI.
// Use fill="currentColor" so icons inherit the parent element's CSS color.
// speakingWave is not an SVG — it's a div with animated bars (see styles.css .speaking-wave).
const ICONS = {
    // Star shown in the orange logo box in the header
    logo: `<svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
        <polygon points="12,2 15.09,8.26 22,9.27 17,14.14 18.18,21.02 12,17.77 5.82,21.02 7,14.14 2,9.27 8.91,8.26"/>
    </svg>`,

    // Microphone — used in the voice session button and speech preview pill
    mic: `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 14a3.5 3.5 0 0 0 3.5-3.5V5a3.5 3.5 0 1 0-7 0v5.5A3.5 3.5 0 0 0 12 14z"/>
        <path d="M18 10.5a1 1 0 1 1 2 0 8 8 0 0 1-7 7.93V21h2.5a1 1 0 1 1 0 2h-7a1 1 0 1 1 0-2H11v-2.57a8 8 0 0 1-7-7.93 1 1 0 1 1 2 0 6 6 0 0 0 12 0z"/>
    </svg>`,

    // Paper-plane send arrow — used in the text send button
    send: `<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor">
        <path d="M2 21l21-9L2 3v7l15 2-15 2v7z"/>
    </svg>`,

    // Animated bars shown in the status dot while the bot is speaking
    speakingWave: `<div class="speaking-wave"><span></span><span></span><span></span><span></span><span></span></div>`,

    // Gear icon — used in the settings toggle button
    settings: `<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.39a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z"/>
        <circle cx="12" cy="12" r="3"/>
    </svg>`,

    // Person silhouette — shown next to user messages in the chat
    avatar: `<svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
        <circle cx="12" cy="8" r="4"/>
        <path d="M4 20c0-4 3.6-7 8-7s8 3 8 7"/>
    </svg>`
};

// All rendering below is synchronous — this script is loaded at the end of <body>
// so every DOM element already exists by the time these lines execute.

// Inject animated background divs (ambient blobs, grain overlay)
document.getElementById('bg-layers').innerHTML =
    BG_LAYERS.map(cls => `<div class="${cls}"></div>`).join('');

// Render EN / HI / Auto language toggle buttons in the header
document.getElementById('lang-toggle').innerHTML =
    LANG_OPTIONS.map(opt =>
        `<button class="lang-btn${opt.active ? ' active' : ''}" data-lang="${opt.value}">${opt.label}</button>`
    ).join('');

// Populate the language preference dropdown in the settings panel
document.getElementById('lang-preference').innerHTML =
    LANG_PREFERENCES.map(opt =>
        `<option value="${opt.value}">${opt.label}</option>`
    ).join('');

// Inject SVG icons into their placeholder elements
document.getElementById('speech-preview-icon').innerHTML = ICONS.mic;
document.getElementById('logo-icon').innerHTML = ICONS.logo;
document.getElementById('settings-toggle').innerHTML = ICONS.settings;
document.getElementById('session-toggle-btn').innerHTML = ICONS.mic;
document.getElementById('send-text-btn').innerHTML = ICONS.send;
