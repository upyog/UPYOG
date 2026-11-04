import { useEffect } from 'react';

/**
 * Custom hook to handle back button navigation
 * 
 * @param {Object} config - Configuration object
 * @param {string} config.redirectPath - Path to redirect to when back button is clicked
 * @param {boolean} [config.enableConfirmation=false] - Whether to show confirmation dialog
 * @param {string} [config.confirmationMessage='Are you sure you want to leave this page?'] - Custom confirmation message
 */
export const useCustomBackNavigation = ({
  redirectPath,
  enableConfirmation = false,
  confirmationMessage = 'Are you sure you want to leave this page?'
}) => {
  const navigate = Digit.Hooks.useCustomNavigate();

  useEffect(() => {
    // Add a new entry to browser's history stack
    window.history.pushState(null, '', window.location.pathname);

    const handleBackButton = (event) => {
      // Prevent default back navigation
      event.preventDefault();

      if (enableConfirmation) {
        // Show confirmation dialog if enabled
        const shouldRedirect = window.confirm(confirmationMessage);
        if (shouldRedirect) {
          navigate(redirectPath);
        } else {
          // If user cancels, push a new state to prevent back navigation
          window.history.pushState(null, '', window.location.pathname);
        }
      } else {
        // Directly redirect without confirmation
        navigate(redirectPath);
      }
    };

    // Add popstate event listener
    window.addEventListener('popstate', handleBackButton);

    // Cleanup function to remove event listener
    return () => {
      window.removeEventListener('popstate', handleBackButton);
    }; 
  }, [navigate, redirectPath, enableConfirmation, confirmationMessage]);
};