import React, { useEffect, useState } from "react";

const useTenantsPTR = () => {
  const tenantInfo = Digit.SessionStorage.get("PTR_TENANTS");

  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenantsPTR;
