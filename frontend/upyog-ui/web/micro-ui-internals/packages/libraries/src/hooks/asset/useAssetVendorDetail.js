import { ASSETSearch } from "../../services/molecules/ASSET/Search";
import { useQuery } from "react-query";

// Custom hook to fetch asset vendor details and application information
const useAssetVendorDetail = (t, tenantId, applicationNo, config = {}, userType, args) => {
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
    ["APPLICATION_ASSET_VENDOR_UP", "ASSET_ASSET_VENDOR_UP", applicationNo, userType, args],
    () => ASSETSearch.applicationDetails(t, tenantId, applicationNo, userType, combinedData, args),
    { select: defaultSelect, ...config }
  );
};

export default useAssetVendorDetail;
