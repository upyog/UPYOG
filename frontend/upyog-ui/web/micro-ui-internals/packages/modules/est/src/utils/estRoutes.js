/** EST route helpers — derive paths from useModuleBasePath() instead of hardcoding /upyog-ui/... */

export const getEstModulePath = (modulePath = "/upyog-ui/employee") => {
  const role = modulePath.includes("citizen") ? "citizen" : "employee";
  return `/upyog-ui/${role}/est`;
};

export const getCreateAssetPath = (modulePath = "/upyog-ui/employee") =>
  `${getEstModulePath(modulePath)}/create-asset/newRegistration`;

export const getEmployeeHomeFromModulePath = (modulePath = "") =>
  modulePath.replace(/\/est\/?$/, "") || "/upyog-ui/employee";

export const getCitizenHomeFromModulePath = (modulePath = "") =>
  modulePath.replace(/\/est\/?$/, "") || "/upyog-ui/citizen";

export const getCitizenPaymentPath = (estateNo) =>
  `/upyog-ui/citizen/payment/my-bills/est-services/${estateNo}`;

export const getEmployeePaymentCollectPath = (estateNo) =>
  `/upyog-ui/employee/payment/collect/est-services/${estateNo}`;
