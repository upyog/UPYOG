import React, { useEffect, useState } from "react";

/* Getting the data from sessionStorage which is setted in the module.js of pet module */
const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("PTR_TENANTS");
  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenants;
