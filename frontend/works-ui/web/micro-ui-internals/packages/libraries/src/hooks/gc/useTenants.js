import React, { useEffect, useState } from "react";

const useTenants = () => {
  const tenantInfo = Digit.SessionStorage.get("GC_TENANTS");


  return tenantInfo ? tenantInfo : null;
};

export default useTenants;