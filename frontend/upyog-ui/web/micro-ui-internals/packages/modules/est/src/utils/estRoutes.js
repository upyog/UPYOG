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

/** Application / allotment summary page (path segment is allotmentNo or estateNo). */
export const getApplicationDetailsPath = (modulePath = "", applicationNo = "") =>
  `${getEstModulePath(modulePath)}/application-details/${encodeURIComponent(applicationNo)}`;

export const getCitizenPaymentPath = (allotmentNo) =>
  `/upyog-ui/citizen/payment/my-bills/est-services/${allotmentNo}`;

export const getEmployeePaymentCollectPath = (allotmentNo) =>
  `/upyog-ui/employee/payment/collect/est-services/${allotmentNo}`;
