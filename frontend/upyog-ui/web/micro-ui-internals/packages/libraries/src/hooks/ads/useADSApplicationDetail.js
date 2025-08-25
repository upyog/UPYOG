import { ADSSearch } from "../../services/molecules/ADS/Search";
import { useQuery } from "react-query";

const useADSApplicationDetail = (t, tenantId, bookingNo, config = {}, userType, args) => {
    
  
  const defaultSelect = (data) => {    
     let applicationDetails = data.applicationDetails || {};

    return {
      applicationData : data,
      applicationDetails
    }
  };

  return useQuery(
    [bookingNo, userType, args],
    () => ADSSearch.applicationDetails(t, tenantId, bookingNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useADSApplicationDetail;