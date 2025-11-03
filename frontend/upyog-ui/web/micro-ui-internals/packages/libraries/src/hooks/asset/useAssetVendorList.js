import { useQuery, useQueryClient } from "react-query";

const useAssetVendorList = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if (data.Vendors.length > 0) data.Vendors[0].vendorNumber = data.Vendors[0].vendorNumber || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = useQuery(
    ["assetVendorList", tenantId, filters, auth, config],
    () => Digit.ASSETService.assetVendorList(args),
    {
      select: defaultSelect,
      ...config,
    }
  );

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries(["assetVendorList", tenantId, filters, auth]) };
};

export default useAssetVendorList;
