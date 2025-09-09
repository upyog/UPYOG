import { MTSearch } from "../../services/molecules/WT/MTSearch";
import { useQuery } from "react-query";

/*Custom React hook for fetching Mobile Toilet (MT) application details.
  - Calls `MTSearch.applicationDetails` to retrieve application details based on provided parameters.
  - Uses `react-query`'s `useQuery` to manage the fetching, caching, and updating of the data.
  - Transforms the response structure before returning it..*/
const useMTApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
    
  
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
    [ applicationNo, userType, args],
    () => MTSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useMTApplicationDetail;
