import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

export const useBPAREGgetbill = ({ tenantId, businessService, ...filters }, config = {}) => {
  const client = useQueryClient();

  const _tenantId =
    tenantId || Digit.UserService.getUser()?.info?.tenantId;

  const queryKey = [
    "OBPS_BPA_REG_BILL",
    _tenantId,
    businessService,
    JSON.stringify(filters),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () =>
      Digit.OBPSService.BPAREGGetBill(_tenantId, {
        businessService,
        ...filters,
      }),
    config: { retry: false, ...config },
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};