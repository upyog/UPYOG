/**
 * useIsMobile.js
 *
 * Responsive breakpoint hook. Prefer this over Digit.Utils.browser.isMobile()
 * when the UI must re-render on window resize (that helper is often a one-shot
 * snapshot at mount).
 *
 * @param {number} [breakpoint=768] - Max width (px) treated as mobile.
 * @returns {boolean} True when window.innerWidth <= breakpoint.
 *
 * @example
 *   const isMobile = useIsMobile();
 *   const isTablet = useIsMobile(1024);
 */

import { useEffect, useState } from "react";

/**
 * Tracks whether the viewport is at or below `breakpoint`.
 * Initializes from window.innerWidth (false during SSR when window is undefined)
 * and listens for resize.
 *
 * @param {number} [breakpoint=768]
 * @returns {boolean}
 */
const useIsMobile = (breakpoint = 768) => {
  const [isMobile, setIsMobile] = useState(() =>
    typeof window !== "undefined" ? window.innerWidth <= breakpoint : false
  );

  useEffect(() => {
    const handleResize = () => setIsMobile(window.innerWidth <= breakpoint);
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, [breakpoint]);

  return isMobile;
};

export default useIsMobile;
