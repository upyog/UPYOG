import React, { useEffect, useState } from "react";

/**
 * useTenants Hook
 * 
 * This custom hook is responsible for fetching and managing tenant information for the CHB (Community Hall Booking) module.
 * 
 * Logic:
 * - Retrieves tenant information from session storage using `Digit.SessionStorage.get("CHB_TENANTS")`.
 * - Initializes the `tenants` state with the fetched data or `null` if no data is available.
 * - Returns the `tenants` state for use in components.
 * 
 * Returns:
 * - The tenant information stored in session storage or `null` if not available.
 */
const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("CHB_TENANTS");

  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenants;
