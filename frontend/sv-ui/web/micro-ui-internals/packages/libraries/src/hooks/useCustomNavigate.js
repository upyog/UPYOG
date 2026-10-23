import { useNavigate } from "react-router-dom";

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
  const navigate = useNavigate();
  
  return (to, options = {}) => {
    try {
      navigate(to, options);
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
