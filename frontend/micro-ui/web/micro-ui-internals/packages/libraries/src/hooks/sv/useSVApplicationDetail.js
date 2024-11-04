 /* 
 * hook to structure the data of applicationDetails page for a single application
 * Uses the SVSearch page where the data is strtured and returned here
 */

import { useQuery } from "react-query";
import { SVSearch } from "../../services/molecules/SV/Search";

const useSVApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  
  const defaultSelect = (data) => {
     let applicationDetails = data.applicationDetails.map((obj) => {
      return obj;
    });
    
    return {
      applicationData : data,
      applicationDetails
    }
  };

  return useQuery(
    ["APPLICATION_SEARCH", "SV_SEARCH", applicationNumber, userType, args],
    () => SVSearch.applicationDetails(t, tenantId, applicationNumber, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useSVApplicationDetail;
