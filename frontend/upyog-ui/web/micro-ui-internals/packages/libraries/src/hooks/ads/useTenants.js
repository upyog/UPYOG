import React, { useEffect, useState } from "react";
/**
 * Custom hook to manage and retrieve tenant information from session storage.
 * Returns the current tenants state.
 */

const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("ADS_TENANTS");

  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenants;
