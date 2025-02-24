
import { useQuery } from "react-query";
import { SVService } from "../services/elements/SV";

const svApplications = async (tenantId, filters) => {
  return (await SVService.search({ tenantId, filters })).SVDetail;
};

const refObj = (tenantId, filters) => {
  let consumerCodes = filters?.consumerCodes;

  return {
   
    street: {
      searchFn: () => svApplications(null, { ...filters, applicationNo: consumerCodes }),
      key: "applicationNo",
      label: "SV_APPLICATION_NO",
    },
  };
};

export const useApplicationsForBusinessServiceSearch = ({ tenantId, businessService, filters }, config = {}) => {
  let _key = businessService?.toLowerCase().split(".")[0];
  
  if (window.location.href.includes("sv-services")) {
    _key = "street"
  } 

  /* key from application ie being used as consumer code in bill */
  const { searchFn, key, label } = refObj(tenantId, filters)[_key];
  const applications = useQuery(["applicationsForBillDetails", { tenantId, businessService, filters, searchFn }], searchFn, {
    ...config,
  });

  return { ...applications, key, label };
};
