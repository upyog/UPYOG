import { ASSETSearch } from "../../services/molecules/ASSET/Search";
import { useQuery } from "react-query";

const useAssetApplicationDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
  const stateTenantId = Digit.ULBService.getStateId();
  const defaultSelect = (data) => {
     let applicationDetails = data.applicationDetails.map((obj) => {
      return obj;
    });    

    return {
      applicationData : data,
      applicationDetails
    }
  };

  
    const { data: cityResponseObject} =  Digit.Hooks.useCustomMDMSV2(tenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
      select: (data) => {
        
        const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
        return formattedData;
      },
    });
 
    const {data: stateResponseObject} =  Digit.Hooks.useCustomMDMSV2(stateTenantId, "ASSET", [{ name: "AssetParentCategoryFields" }], {
      select: (data) => {
        const formattedData = data?.["ASSET"]?.["AssetParentCategoryFields"];
        return formattedData;
      },
    });
  
    let combinedData;

    // if city level master is not available then fetch  from state-level
    if (cityResponseObject) {
      combinedData = cityResponseObject;
    } else if (stateResponseObject) {
      combinedData = stateResponseObject;
    } else {
      combinedData = [];
    }

  //   const processDepreciation = async(assetId) => {
     
   
  // }

  return useQuery(
    ["APPLICATION_SEARCH", "ASSET_SEARCH", applicationNo, userType, combinedData,  args],
    () => ASSETSearch.applicationDetails(t, tenantId, applicationNo, userType, combinedData,  args),
    { select: defaultSelect, ...config }
 
  );
};

export default useAssetApplicationDetail;
