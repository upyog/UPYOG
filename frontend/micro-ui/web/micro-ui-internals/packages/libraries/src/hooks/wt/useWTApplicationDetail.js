import { WTSearch } from "../../services/molecules/WT/Search";
import { useQuery } from "react-query";

const useWTApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
    
  
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
    ["APPLICATION_SEARCH", "WT_SEARCH", applicationNo, userType, args],
    () => WTSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useWTApplicationDetail;
