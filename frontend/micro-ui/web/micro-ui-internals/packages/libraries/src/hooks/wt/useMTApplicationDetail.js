import { MTSearch } from "../../services/molecules/WT/MTSearch";
import { useQuery } from "react-query";

// For Mobile Toilet Application Details
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
