import { useQuery, useQueryClient } from "react-query";

const useEmpvendorCommonSearch = ({tenantId,filters, auth, searchedFrom=""},config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if(data.VendorDetails.length > 0) data.VendorDetails[0] = data.VendorDetails[0] || [];
    return data;
  };

  console.log("useempvendorCommonSearch hook", tenantId, filters, auth, config);
  const { isLoading, error, data, isSuccess } = useQuery([tenantId, filters, auth, config], () => Digit.VendorService.vendorcommonSearch(args), {
    select: defaultSelect,
    ...config,
  });
  return {isLoading,error, data, isSuccess, revalidate: () => client.invalidateQueries([tenantId, filters, auth])};


};

export default useEmpvendorCommonSearch;