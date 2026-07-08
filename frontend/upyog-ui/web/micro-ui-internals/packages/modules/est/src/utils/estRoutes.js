/** EST route helpers — derive paths from useModuleBasePath() instead of hardcoding /upyog-ui/... */

export const getEmployeeHomeFromModulePath = (modulePath = "") =>
  modulePath.replace(/\/est\/?$/, "") || "/upyog-ui/employee";

export const getCitizenHomeFromModulePath = (modulePath = "") =>
  modulePath.replace(/\/est\/?$/, "") || "/upyog-ui/citizen";

export const getCitizenPaymentPath = (estateNo) =>
  `/upyog-ui/citizen/payment/my-bills/est-services/${estateNo}`;
