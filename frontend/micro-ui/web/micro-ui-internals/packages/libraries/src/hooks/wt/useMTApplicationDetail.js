import { MTSearch } from "../../services/molecules/WT/MTSearch";
import { useQuery } from "react-query";

/*hook for fetching Mobile Toilet (MT) application details.  
Calls `MTSearch.applicationDetails` and structures the response.*/
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
