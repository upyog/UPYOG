import React, { useEffect, useState } from "react";

const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("CHB_TENANTS");
  console.log("tenenennenenenen",tenantInfo)

  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenants;
