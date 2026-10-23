// assets/avatar.js — Avatar Video Controller
// Syncs avatar emotion videos (idle / listening / thinking / talking)
// with the voice-bot state machine. Videos live in assets/avatar/*.mp4.

(function () {
  'use strict';

  // ── Video file map ─────────────────────────────────────────────────────────
  // Filenames must match what's in /assets/avatar/
  const AVATAR_VIDEOS = {
    idle:      'assets/avatar/idle.mp4',
    listening: 'assets/avatar/listening.mp4',
    thinking:  'assets/avatar/thinking.mp4',
    talking:   'assets/avatar/talking.mp4',
  };

  // Map each States value to an avatar emotion key
  const STATE_TO_EMOTION = {
    IDLE:       'idle',
    LISTENING:  'listening',
    PROCESSING: 'thinking',
    SPEAKING:   'talking',
  };

  // ── Internal state ─────────────────────────────────────────────────────────
  let currentEmotion = null;
  let transitionLocked = false;

  // ── DOM refs (set after DOMContentLoaded) ──────────────────────────────────
  let avatarWrapper   = null;
  let videoEls        = {};   // { idle, listening, thinking, talking }
  let labelEl         = null;

  // Label text for each emotion
  const EMOTION_LABELS = {
    idle:      'Ready',
    listening: 'Listening…',
    thinking:  'Thinking…',
    talking:   'Speaking…',
  };

  // ── Video source resolution (Blob URL from Base64 or direct asset path) ───
  function base64ToBlobUrl(base64Data, mimeType = 'video/mp4') {
    try {
      const byteChars = atob(base64Data);
      const sliceSize = 8192;
      const byteArrays = [];
      for (let offset = 0; offset < byteChars.length; offset += sliceSize) {
        const slice = byteChars.slice(offset, offset + sliceSize);
        const byteNumbers = new Uint8Array(slice.length);
        for (let i = 0; i < slice.length; i++) {
          byteNumbers[i] = slice.charCodeAt(i);
        }
        byteArrays.push(byteNumbers);
      }
      const blob = new Blob(byteArrays, { type: mimeType });
      return URL.createObjectURL(blob);
    } catch (e) {
      console.warn('[Avatar] Failed to create blob URL from base64:', e);
      return null;
    }
  }

  function resolveVideoSrc(emotion) {
    if (window.AVATAR_VIDEO_BASE64 && window.AVATAR_VIDEO_BASE64[emotion]) {
      const blobUrl = base64ToBlobUrl(window.AVATAR_VIDEO_BASE64[emotion]);
      if (blobUrl) return blobUrl;
    }
    return AVATAR_VIDEOS[emotion] || '';
  }

  // ── Build the avatar DOM ───────────────────────────────────────────────────
  function buildAvatarDOM() {
    // Outer wrapper — sits between header and #chat-container
    avatarWrapper = document.createElement('div');
    avatarWrapper.id = 'avatar-panel';

    // Video stage — clips the video into a circle
    const stage = document.createElement('div');
    stage.className = 'avatar-stage';

    // Create one <video> per emotion, stacked on top of each other
    Object.entries(AVATAR_VIDEOS).forEach(([emotion, fallbackSrc]) => {
      const vid = document.createElement('video');
      vid.src         = resolveVideoSrc(emotion);
      vid.loop        = true;
      vid.muted       = true;
      vid.playsInline = true;
      vid.preload     = 'auto';
      vid.className   = 'avatar-video';
      vid.dataset.emotion = emotion;
      vid.setAttribute('muted', '');
      vid.setAttribute('playsinline', '');

      // Start invisible
      vid.style.opacity = '0';
      vid.style.zIndex  = '0';

      vid.addEventListener('error', (e) => {
        console.warn(`[Avatar] Video error for emotion '${emotion}':`, e);
      });

      // Load the video silently; handle play errors gracefully
      vid.load();
      stage.appendChild(vid);
      videoEls[emotion] = vid;
    });

    // Glow ring behind the video
    const glow = document.createElement('div');
    glow.className = 'avatar-glow';
    avatarWrapper.appendChild(glow);
    avatarWrapper.appendChild(stage);

    // Status label below the avatar
    labelEl = document.createElement('div');
    labelEl.className = 'avatar-label';
    labelEl.textContent = 'Ready';
    avatarWrapper.appendChild(labelEl);

    // Insert before #chat-container
    const chatContainer = document.getElementById('chat-container');
    if (chatContainer && chatContainer.parentNode) {
      chatContainer.parentNode.insertBefore(avatarWrapper, chatContainer);
    } else {
      document.body.appendChild(avatarWrapper);
    }

    // Start with idle
    switchEmotion('idle', true);
  }

  // ── Switch the visible video ───────────────────────────────────────────────
  function switchEmotion(emotion, instant) {
    if (emotion === currentEmotion) return;
    if (!videoEls[emotion]) return;

    const incoming = videoEls[emotion];
    const outgoing = currentEmotion ? videoEls[currentEmotion] : null;
    currentEmotion = emotion;

    // Update label
    if (labelEl) {
      labelEl.textContent = EMOTION_LABELS[emotion] || '';
      labelEl.dataset.emotion = emotion;
    }

    // Update glow class
    if (avatarWrapper) {
      avatarWrapper.dataset.emotion = emotion;
    }

    if (instant) {
      // Immediate swap (first load)
      if (outgoing) { outgoing.pause(); outgoing.style.opacity = '0'; outgoing.style.zIndex = '0'; }
      incoming.style.opacity = '1';
      incoming.style.zIndex  = '1';
      playVideo(incoming);
    } else {
      // Cross-fade: bring new video in, then fade old one out
      incoming.style.zIndex = '1';
      playVideo(incoming);

      // Fade in new
      requestAnimationFrame(() => {
        incoming.style.opacity = '1';
        if (outgoing) {
          outgoing.style.opacity = '0';
          // After transition, pause outgoing to save resources
          setTimeout(() => {
            if (currentEmotion !== (outgoing.dataset.emotion)) {
              outgoing.pause();
              outgoing.style.zIndex = '0';
            }
          }, 350); // matches CSS transition duration
        }
      });
    }
  }

  // ── Safe play helper (handles autoplay restrictions) ──────────────────────
  function playVideo(vid) {
    if (!vid) return;
    const p = vid.play();
    if (p && typeof p.catch === 'function') {
      p.catch(() => {
        // Autoplay blocked — video stays paused on first interaction,
        // then retries on first user gesture via document click.
      });
    }
  }

  // ── Re-enable autoplay after first user gesture ───────────────────────────
  function onFirstGesture() {
    document.removeEventListener('click', onFirstGesture, true);
    document.removeEventListener('touchstart', onFirstGesture, true);
    document.removeEventListener('keydown', onFirstGesture, true);
    // Replay whichever video is currently visible
    if (currentEmotion && videoEls[currentEmotion]) {
      playVideo(videoEls[currentEmotion]);
    }
  }

  // ── Helper to check if microphone is currently active ─────────────────────
  function isMicActive() {
    if (typeof window.isMicActive === 'function') {
      return window.isMicActive();
    }
    const sessionBtn = document.getElementById('session-toggle-btn');
    if (sessionBtn && sessionBtn.classList.contains('active')) return true;
    if (typeof sessionActive !== 'undefined') return Boolean(sessionActive);
    return false;
  }

  // ── Patch setState ─────────────────────────────────────────────────────────
  // We wrap the global setState so avatar always stays in sync.
  function patchSetState() {
    if (typeof window.setState !== 'function') return false;

    const originalSetState = window.setState;
    window.setState = function (newState) {
      originalSetState.call(this, newState);
      const stateKey = typeof newState === 'string' ? newState : String(newState);
      let emotion  = STATE_TO_EMOTION[stateKey] || 'idle';

      // If state is LISTENING but microphone is OFF -> avatar must be idle
      if (emotion === 'listening' && !isMicActive()) {
        emotion = 'idle';
      }

      switchEmotion(emotion, false);
    };
    return true;
  }

  // ── Mic status watcher & watchdog ───────────────────────────────────────────
  function bindMicWatcher() {
    const sessionBtn = document.getElementById('session-toggle-btn');
    if (sessionBtn) {
      const observer = new MutationObserver(() => {
        if (!isMicActive() && currentEmotion === 'listening') {
          switchEmotion('idle', false);
        }
      });
      observer.observe(sessionBtn, { attributes: true, attributeFilter: ['class'] });

      sessionBtn.addEventListener('click', () => {
        setTimeout(() => {
          if (!isMicActive() && currentEmotion === 'listening') {
            switchEmotion('idle', false);
          }
        }, 50);
      });
    }

    const stopBtn = document.getElementById('stop-voice-btn');
    if (stopBtn) {
      stopBtn.addEventListener('click', () => {
        switchEmotion('idle', false);
      });
    }

    // Watchdog: ensures avatar immediately drops to idle if mic turns off
    setInterval(() => {
      if (currentEmotion === 'listening' && !isMicActive()) {
        switchEmotion('idle', false);
      }
    }, 500);
  }

  // ── Initialisation ─────────────────────────────────────────────────────────
  function init() {
    buildAvatarDOM();

    // Patch setState — chat.js must already be loaded
    if (!patchSetState()) {
      // Retry once after a short delay in case scripts loaded out-of-order
      setTimeout(patchSetState, 200);
    }

    bindMicWatcher();

    // Handle autoplay policy
    document.addEventListener('click',      onFirstGesture, true);
    document.addEventListener('touchstart', onFirstGesture, { capture: true, passive: true });
    document.addEventListener('keydown',    onFirstGesture, true);
  }

  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }
})();
