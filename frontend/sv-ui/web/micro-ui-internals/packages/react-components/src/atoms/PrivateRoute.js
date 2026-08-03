import React from "react";
import { Navigate, useLocation } from "react-router-dom";

/**
 * PrivateRoute component for React Router v6
 * Protects routes that require authentication
 * 
 * Usage:
 * <Route path="profile" element={<PrivateRoute><ProfileComponent /></PrivateRoute>} />
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - Component to render if authenticated
 * @returns {React.ReactNode}
 */
export const PrivateRoute = ({ children }) => {
  const user = Digit.UserService.getUser();
  const userType = Digit.UserService.getType();
  const location = useLocation();
  
  /**
   * Get login redirection link based on user type
   * @returns {string} Login page URL
   */
  const getLoginRedirectionLink = () => {
    if (userType === "employee") {
      return "/upyog-ui/employee/user/language-selection";
    } else {
      return "/upyog-ui/citizen/login";
    }
  };

  // Check if user is authenticated
  if (!user || !user.access_token) {
    // Not logged in - redirect to login page with return URL
    return (
      <Navigate 
        to={getLoginRedirectionLink()} 
        state={{ from: location.pathname + location.search }} 
        replace 
      />
    );
  }

  // User is authenticated - render protected content
  return children;
};