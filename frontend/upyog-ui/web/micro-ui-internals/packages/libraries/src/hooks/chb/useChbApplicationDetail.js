import { CHBSearch } from "../../services/molecules/CHB/Search";
import { useQuery } from "react-query";

const useChbApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
    
  
  const defaultSelect = (data) => {
    
     let applicationDetails = data.applicationDetails || {};
    

    return {
      applicationData : data,
      applicationDetails
    }
  };

  return useQuery(
    [applicationNo, userType, args],
    () => CHBSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useChbApplicationDetail;
