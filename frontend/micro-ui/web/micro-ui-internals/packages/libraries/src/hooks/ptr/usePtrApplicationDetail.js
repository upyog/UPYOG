import { PTRSearch } from "../../services/molecules/PTR/Search";
import { useQuery } from "react-query";

const usePtrApplicationDetail = (t, tenantId, applicationNumber, config = {}, userType, args) => {
  
  const defaultSelect = (data) => {
    // console.log("####",data)
     let applicationDetails = data.applicationDetails.map((obj) => {
    
      return obj;
    });
    

    return {
      applicationData : data,
      applicationDetails
    }
  };

  return useQuery(
    ["APPLICATION_SEARCH", "PT_SEARCH", applicationNumber, userType, args],
    () => PTRSearch.applicationDetails(t, tenantId, applicationNumber, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default usePtrApplicationDetail;
