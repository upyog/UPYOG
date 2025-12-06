/** 
 * CND Payment Configuration
 * It uses `react-query` for data fetching and caching, and dynamically sets the search function based on the business service.
 * The returned object includes the fetched applications and metadata like `key` and `label` for bill generation reference.
 * 
*/
import { useQuery } from "react-query";
import { CNDService } from "../services/elements/CND";

const cndApplications = async (tenantId, filters) => {
  return (await CNDService.search({ tenantId, filters })).cndApplicationDetail;
};


const referenceObject = (tenantId, filters) => {
  let consumerCodes = filters?.consumerCodes;

  return {
    cnd: {
      searchFn: () => cndApplications(null, { ...filters, applicationNumber: consumerCodes }),
      key: "applicationNumber",
      label: "CND_APPLICATION_NUMBER",
    },
  };
};

export const useApplicationsForBusinessServiceSearch = ({ tenantId, businessService, filters }, config = {}) => {
  let _key = businessService?.toLowerCase().split(".")[0];
  if (businessService==="cnd-service") {
    _key = "cnd"
  } 
  
  /* key from application ie being used as consumer code in bill */
  const { searchFn, key, label } = referenceObject(tenantId, filters)[_key];
  const applications = useQuery(["applicationsForBillDetails", { tenantId, businessService, filters, searchFn }], searchFn, {
    ...config,
  });

  return { ...applications, key, label };
};
