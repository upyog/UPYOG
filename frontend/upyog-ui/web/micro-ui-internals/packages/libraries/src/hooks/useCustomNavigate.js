import { useNavigate, useInRouterContext } from "react-router-dom";

/**
 * Custom navigation hook that wraps react-router-dom's useNavigate.
 * Centralizes navigation logic so call sites don't have to guard for the
 * (rare) case where the app renders outside a Router context.
 *
 * Usage:
 * const navigate = Digit.Hooks.useCustomNavigate();
 * navigate("/path");
 * navigate("/path", { state: { data } });
 * navigate(-1); // go back
 */

// Session key used to hand router `state` across a full-page (hard) navigation.
// react-router `state` lives in the History API and is LOST on window.location
// changes, so we stash it here and let the destination page read + clear it.
// Currently recovered by ESTAllotmentAcknowledgement.js (readAckState).
const NAV_STATE_KEY = "__upyog_nav_state__";

// Persist router state before a hard navigation so the next page can restore it.
// Wrapped in try/catch: state may be non-serializable (circular refs) or exceed
// sessionStorage quota — in either case we just skip stashing rather than throw.
const stashNavState = (state) => {
  if (!state) return;
  try {
    sessionStorage.setItem(NAV_STATE_KEY, JSON.stringify(state));
  } catch (_) {
    /* ignore quota / clone errors */
  }
};

// Fallback navigation when the SPA router can't be used. Only string paths are
// supported here (numeric deltas like navigate(-1) require the router, so they
// are intentionally ignored in this path).
//
const hardNavigate = (to, options) => {
  if (typeof to !== "string") return;
  stashNavState(options?.state);
  window.location.href = to;
};

const useCustomNavigate = () => {
  const isInRouter = useInRouterContext();

  // Only grab react-router's navigate when a Router is actually present;
  // calling useNavigate outside a Router throws.
  const navigate = isInRouter ? useNavigate() : null;

  return (to, options = {}) => {
    try {
      if (navigate) {
        // Normal path: SPA navigation keeps history/router state intact.
        navigate(to, options);
        // Return so we don't fall through to the hard-navigation fallback,
        // which would trigger a second, full-page navigation.
        return;
      }
      // No Router in context (e.g. component mounted standalone): fall back to
      // a hard navigation, preserving state via sessionStorage.
      hardNavigate(to, options);
    } catch (error) {
      console.error("Navigation error:", error);
      // If SPA navigation itself threw, still get the user to the destination
      // with a hard navigation (router state is dropped, hence the stash).
      hardNavigate(to, options);
    }
  };
};

export default useCustomNavigate;
