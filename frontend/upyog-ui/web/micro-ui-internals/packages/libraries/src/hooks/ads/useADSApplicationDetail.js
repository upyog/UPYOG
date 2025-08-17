import { ADSSearch } from "../../services/molecules/ADS/Search";
import { useQuery } from "react-query";

//Custom hook to fetch and manage ADS application details
const useADSApplicationDetail = (t, tenantId, bookingNo, config = {}, userType, args) => {
    
  
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
    ["APPLICATION_SEARCH", "ADS_SEARCH", bookingNo, userType, args],
    () => ADSSearch.applicationDetails(t, tenantId, bookingNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useADSApplicationDetail;
