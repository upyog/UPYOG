import { ASSETSearch } from "../../services/molecules/ASSET/Search";
import { useQuery } from "react-query";

// Custom hook to search and retrieve asset application details by application number
const useAssetApplicationSearch = (t, tenantId, applicationNo, config = {}, userType, args) => {
  const stateTenantId = Digit.ULBService.getStateId();
  const defaultSelect = (data) => {
    let applicationDetails = data.applicationDetails;
    return {
      applicationData: data,
      applicationDetails,
    };
  };
  let combinedData = [];

  return useQuery(
    ["APPLICATION_SEARCH_UP", "ASSET_SEARCH_UP", applicationNo, userType, args],
    () => ASSETSearch.applicationDetails(t, tenantId, applicationNo, userType, combinedData, args),
    { select: defaultSelect, ...config }
  );
};

export default useAssetApplicationSearch;
