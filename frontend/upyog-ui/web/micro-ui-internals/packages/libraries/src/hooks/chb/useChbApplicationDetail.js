import { CHBSearch } from "../../services/molecules/CHB/Search";
import { useQuery } from "react-query";

const useChbApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
    
  
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
    ["APPLICATION_SEARCH", "CHB_SEARCH", applicationNo, userType, args],
    () => CHBSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useChbApplicationDetail;
