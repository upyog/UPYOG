import { queryTemplate } from "../../common/queryTemplate";
import HrmsService from "../../services/elements/HRMS";

export const useHRMSSearch = (
  searchparams,
  tenantId,
  filters,
  isupdated,
  config = {}
) => {
  const queryKey = [
    "HRMS_SEARCH",
    tenantId,
    JSON.stringify(searchparams),
    JSON.stringify(filters),
    isupdated,
  ];

  const queryFn = () =>
    HrmsService.search(tenantId, filters, searchparams);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useHRMSSearch;