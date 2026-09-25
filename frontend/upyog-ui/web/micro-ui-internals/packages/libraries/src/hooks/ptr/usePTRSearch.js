import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "../../common/queryClientTemplate";

const usePTRSearch = ({ tenantId, filters, auth, searchedFrom = "" }, config = {}) => {
  const client = useQueryClient();

  const args = tenantId ? { tenantId, filters, auth } : { filters, auth };

  const defaultSelect = (data) => {
    if (data.PetRegistrationApplications.length > 0) data.PetRegistrationApplications[0].owners = data.PetRegistrationApplications[0].owners || [];
    return data;
  };

  const { isLoading, error, data, isSuccess } = queryTemplate({ queryKey: ["ptrSearchList", tenantId, filters, auth, config], queryFn: () => Digit.PTRService.search(args), select: defaultSelect, config });

  return { isLoading, error, data, isSuccess, revalidate: () => client.invalidateQueries({ queryKey: ["ptrSearchList", tenantId, filters, auth] }) };
};

export default usePTRSearch;
