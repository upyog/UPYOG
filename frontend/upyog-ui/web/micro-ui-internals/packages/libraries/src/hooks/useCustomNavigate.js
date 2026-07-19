import { useNavigate, useInRouterContext } from "react-router-dom";

/**
 * Custom navigation hook that wraps react-router-dom's useNavigate
 * This centralizes navigation logic for easier upgrades
 * 
 * Usage:
 * const navigate = Digit.Hooks.useCustomNavigate();
 * navigate("/path");
 * navigate("/path", { state: { data } });
 * navigate(-1); // go back
 */
const useCustomNavigate = () => {
  const isInRouter = useInRouterContext();

  // Only use react-router navigation if Router exists
  const navigate = isInRouter ? useNavigate() : null;

  return (to, options = {}) => {
    try {
      if (navigate) {
        navigate(to, options);
        return;
      }
      if (typeof to === "string") {
        // Fallback if router context is unavailable (state cannot be preserved).
        if (options?.state) {
          try {
            sessionStorage.setItem(
              "__digit_nav_state__",
              JSON.stringify(options.state)
            );
          } catch (_) {
            /* ignore quota / clone errors */
          }
        }
        window.location.href = to;
      }
    } catch (error) {
      console.error("Navigation error:", error);
      // Hard navigation drops router state — stash it so acknowledgement can recover.
      if (typeof to === "string") {
        if (options?.state) {
          try {
            sessionStorage.setItem(
              "__digit_nav_state__",
              JSON.stringify(options.state)
            );
          } catch (_) {
            /* ignore */
          }
        }
        window.location.href = to;
      }
    }
  };
};

export default useCustomNavigate;