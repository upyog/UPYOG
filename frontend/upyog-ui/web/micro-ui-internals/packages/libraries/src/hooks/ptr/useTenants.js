import React, { useEffect, useState } from "react";

const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("PTR_TENANTS");
<<<<<<< HEAD
  console.log("tenenennenenenen",tenantInfo)
=======

>>>>>>> master-LTS

  const [tenants, setTenants] = useState(tenantInfo ? tenantInfo : null);

  return tenants;
};

export default useTenants;
