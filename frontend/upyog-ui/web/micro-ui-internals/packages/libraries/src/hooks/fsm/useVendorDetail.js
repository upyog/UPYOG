import { queryTemplate } from "../../common/queryTemplate";
import { FSMService } from "../../services/elements/FSM";

const useVendorDetail = (filters = {}, config = {}) => {
  const tenantId = Digit.ULBService.getCurrentTenantId();
  const { uuid } = Digit.UserService.getUser().info;

  const finalFilters = {
    ...filters,
    ownerIds: uuid,
  };

  const queryKey = [
    "FSM_VENDOR_DETAIL",
    tenantId,
    JSON.stringify(finalFilters),
  ];

  const queryFn = () =>
    FSMService.vendorSearch(tenantId, finalFilters);

  return queryTemplate({
    queryKey,
    queryFn,
    config,
  });
};

export default useVendorDetail;