import React, { useEffect, useState } from "react";

/**
 * Retrieves challan generation tenant data from session storage.
 *
 * - Reads "ChallanGeneration_TENANTS" from Digit.SessionStorage.
 * - Initializes state with stored tenant info.
 *
 * @returns {Object|null} tenants - Stored tenant data or null
 */

const usechallangenerationTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("ChallanGeneration_TENANTS");
  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);
  return tenants;
};

export default usechallangenerationTenants;
