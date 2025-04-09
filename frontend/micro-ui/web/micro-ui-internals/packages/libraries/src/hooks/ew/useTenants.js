// Importing React and its hooks for state and lifecycle management
import React, { useEffect, useState } from "react";

// Custom hook to fetch and manage tenant information
const useTenants = () => {
  // Retrieve tenant information from session storage
  const tenantInfo = Digit.SessionStorage.get("EW_TENANTS");

  // Initialize state with tenant information if available, otherwise set it to null
  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  // Return the tenant information
  return tenants;
};

export default useTenants; // Exporting the custom hook for use in other components
