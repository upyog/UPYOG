import { ASSETSearch } from "../../services/molecules/ASSET/Search";
import { useQuery } from "react-query";

const useAssetApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
    
  
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
    ["APPLICATION_SEARCH", "ASSET_SEARCH", applicationNo, userType, args],
    () => ASSETSearch.applicationDetails(t, tenantId, applicationNo, userType, args),
    { select: defaultSelect, ...config }
 
  );
};

export default useAssetApplicationDetail;
