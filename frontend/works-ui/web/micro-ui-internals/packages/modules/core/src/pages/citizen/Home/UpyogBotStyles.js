/**
 * SpeechBot UI Configuration
 * --------------------------
 * This file contains reusable animation definitions and inline styling
 * configurations for the SpeechBot component.
 *
 * Includes:
 * - CSS animations for launcher floating and popup opening effects
 * - Hover interactions for launcher and close button
 * - Styling for chatbot launcher button, popup container,
 *   header, logo, close button, and iframe
 * - Fixed positioning and responsive UI appearance
 */
export const animations = `
  @keyframes float {
    0% {
      transform: translateY(0px);
    }
    50% {
      transform: translateY(-10px);
    }
    100% {
      transform: translateY(0px);
    }
  }

  @keyframes popupOpen {
    from {
      opacity: 0;
      transform: translateY(40px) scale(0.92);
    }
    to {
      opacity: 1;
      transform: translateY(0px) scale(1);
    }
  }

  .chat-launcher {
    animation: float 3s ease-in-out infinite;
    transition: transform 0.3s ease;
  }

  .chat-launcher:hover {
    transform: scale(1.08);
  }

  .chat-popup {
    animation: popupOpen 0.35s ease;
  }

  .close-btn:hover {
    transform: scale(1.08);
    background: rgba(255,255,255,0.95) !important;
    box-shadow: 0 8px 20px rgba(0,0,0,0.12);
  }
`;

export const styles = {
  launcherButton: {
    position: "fixed",
    bottom: "35px",
    right: "20px",
    width: "160px",
    height: "150px",
    border: "none",
    background: "transparent",
    cursor: "pointer",
    zIndex: 9999,
    padding: 0,
  },

  launcherImage: {
    width: "100%",
    height: "100%",
    objectFit: "contain",
    filter:
      "drop-shadow(0px 10px 25px rgba(255, 153, 0, 0.35))",
  },

  popup: {
    position: "fixed",
    bottom: "20px",
    right: "20px",
    width: "430px",
    height: "700px",
    background: "rgba(255,255,255,0.95)",
    borderRadius: "30px",
    overflow: "hidden",
    display: "flex",
    flexDirection: "column",
    zIndex: 9999,
    boxShadow:
      "0 25px 60px rgba(0,0,0,0.18)",
    backdropFilter: "blur(14px)",
    border: "1px solid rgba(255,255,255,0.5)",
  },

  header: {
    background:
      "linear-gradient(135deg, #FFF7ED 0%, #FCE7F3 45%, #F3E8FF 100%)",
    padding: "18px 22px",
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    borderBottom: "1px solid rgba(0,0,0,0.05)",
  },

  logoWrapper: {
    width: "50px",
    height: "50px",
    borderRadius: "50%",
    overflow: "hidden",
    background: "white",
    boxShadow:
      "0 4px 15px rgba(0,0,0,0.08)",
  },

  logoImage: {
    width: "100%",
    height: "100%",
    objectFit: "cover",
  },

  closeButton: {
    width: "44px",
    height: "44px",
    borderRadius: "14px",
    border: "none",
    background: "rgba(255,255,255,0.75)",
    backdropFilter: "blur(12px)",
    color: "#334155",
    cursor: "pointer",
    transition: "all 0.3s ease",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    boxShadow:
      "0 6px 18px rgba(0,0,0,0.08)",
  },

  iframe: {
    width: "100%",
    height: "100%",
    border: "none",
    flex: 1,
    background: "white",
  },
};