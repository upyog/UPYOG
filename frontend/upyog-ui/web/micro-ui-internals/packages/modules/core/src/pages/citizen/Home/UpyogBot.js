// React core and useState hook for managing chatbot open/close state
import React, { useState, useRef } from "react";
// Chatbot launcher logo image
import logo from "./logo.png";
// CSS keyframe animations and inline style objects for the chatbot UI
import { animations, styles } from "./UpyogBotStyles";

/**
 * Upyog Component
 * Initially, the chatbot popup remains hidden (`isOpen = false`) and only the launcher button is displayed.
 * Clicking the launcher button triggers `toggleChatbot()`, which updates state and opens the chatbot popup.
 * Once opened:
 *     A popup container is rendered.
 *     The chatbot UI header is displayed with logo and close button.
 *     An iframe loads the external UPYOG voice assistant.
 *  Clicking the close button triggers `handleChatbotClose()`, collapsing the popup and showing the launcher button again.
 *  CSS animations are injected dynamically for smooth UI transitions.
 */

function UpyogBot() {
  /* Controls whether the chatbot popup is visible */
  const [isOpen, setIsOpen] = useState(false);

  /*
   * Ref to access the iframe DOM element directly.
   * Required to call postMessage on the iframe's contentWindow.
   */
  const iframeRef = useRef(null);

  /*
   * Called automatically by the browser when the iframe finishes loading.
   * Reads the currently logged-in user's session from niautt's UserService
   * and sends it into the iframe via postMessage.
   * The chatbot (index.html) listens for this message and stores the RequestInfo
   * to attach it to every /chat API request — following the standard niautt API contract.
   */
  const handleIframeLoad = () => {
    const user = Digit.UserService.getUser();
    iframeRef.current?.contentWindow?.postMessage(
      {
        /* type is used by index.html to identify this message */
        type: "INIT_DATA",
        RequestInfo: {
          apiId: "Rainmaker",          /* standard niautt API identifier */
          authToken: user?.access_token || "",   /* logged-in user's auth token */
          userInfo: user?.info || {},            /* user details (id, name, roles, tenantId etc.) */
          plainAccessRequest: {},                /* required by niautt API contract */
        },
      },
      "*" /* target origin — restrict to specific domain in production if needed */
    );
  };

  /* Toggles the chatbot open/closed on launcher button click */
  const toggleChatbot = () => {
    setIsOpen(!isOpen);
  };

  /* Closes the chatbot popup when the close button is clicked */
  const handleChatbotClose = () => {
    setIsOpen(false);
  };

  return (
    <>
      {/* Inject CSS keyframe animations required by the chatbot UI */}
      <style>{animations}</style>

      {/* Launcher button — visible only when the chatbot is closed */}
      {!isOpen && (
        <button
          className="chat-launcher"
          style={styles.launcherButton}
          onClick={toggleChatbot}
        >
          {/* Logo acts as the visual trigger for opening the chatbot */}
          <img
            src={logo}
            alt="UPYOGITA"
            style={styles.launcherImage}
          />
        </button>
      )}

      {/* Chatbot popup — visible only when isOpen is true */}
      {isOpen && (
        <div
          className="chat-popup"
          style={styles.popup}
        >
          {/* Popup header: logo on the left, close button on the right */}
          <div style={styles.header}>
            <div style={styles.logoWrapper}>
              <img
                src={logo}
                alt="UPYOGITA"
                style={styles.logoImage}
              />
            </div>

            {/* Close button — collapses the popup back to the launcher */}
            <button
              className="close-btn"
              onClick={handleChatbotClose}
              style={styles.closeButton}
            >
              {/* SVG cross icon (18×18 viewport mapped to 20px render size) */}
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.5"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            </button>
          </div>

          {/*
           * Embedded UPYOG voice assistant.
           * ref    → gives access to iframe DOM so postMessage can be called on it.
           * src    → voice bot URL from UpyogBotService (/upyog-voice-bot).
           * onLoad → triggers handleIframeLoad once the chatbot page is fully loaded,
           *          which sends the user's RequestInfo into the iframe via postMessage.
           * allow  → grants microphone permission for speech-to-text.
           */}
          <iframe
            ref={iframeRef}
            src={Digit.UpyogBotService.url()}
            onLoad={handleIframeLoad}
            title="UPYOG Chatbot"
            style={styles.iframe}
            allow="microphone"
          />
        </div>
      )}
    </>
  );
}

export default UpyogBot;