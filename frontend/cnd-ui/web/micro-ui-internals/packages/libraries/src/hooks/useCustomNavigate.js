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
      } else if (typeof to === "string") {
        // Fallback if router context is unavailable
        window.location.href = to;
      }
    } catch (error) {
      console.error("Navigation error:", error);
      // Fallback for navigation failures
      if (typeof to === "string") {
        window.location.href = to;
      }
    }
  };
};

export default useCustomNavigate;
