
import { EWSearch } from "../../services/molecules/EW/Search";
import { useQuery } from "react-query";

const useEwApplicationDetail = (t, tenantId, requestId, config = {}, userType, args) => {
    
  
  const defaultSelect = (data) => {
    console.log("datatatat",data);
    
     let applicationDetails = data.applicationDetails.map((obj) => {
    
      return obj;
    });
    

    return {
      applicationData : data,
      applicationDetails
    }
  };

  return useQuery(
    ["APP_SEARCH", "EW_SEARCH", requestId, userType, args],
    () => EWSearch.applicationDetails(t, tenantId, requestId, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useEwApplicationDetail;
