import React, { useState } from "react";

const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("WMS_TENANTS");

  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenants;
