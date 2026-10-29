import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const useEmpvendorCommonSearch = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient();
  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if (data.VendorDetails.length > 0) data.VendorDetails[0] = data.VendorDetails[0] || [];
    return data;
  };


  const { isLoading, error, data, isSuccess } = queryTemplate({ queryKey: [tenantId, filters, auth, config], queryFn: () => Digit.VendorService.vendorcommonSearch(args), select: defaultSelect, config });
  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries({ queryKey: [tenantId, filters, auth] }) };
};

export default useEmpvendorCommonSearch;
