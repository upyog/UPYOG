import React, { useEffect, useState } from "react";

/**
 * Custom hook for managing tenant information in E-Waste module.
 * Retrieves tenant data from session storage and provides access through state.
 * Used for maintaining consistent tenant context across the application.
 *
 * @returns {Object|null} Current tenant information or null if not available
 *
 * @example
 * const tenants = useTenants();
 * if (tenants) {
 *   const currentTenant = tenants.find(tenant => tenant.code === 'pb.amritsar');
 * }
 */
const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("EW_TENANTS");
  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);
  return tenants;
};

export default useTenants;