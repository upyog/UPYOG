import { queryTemplate } from "../../common/queryTemplate";
import { useQueryClient } from "@tanstack/react-query";

export const useComplaintsList = (tenantId, filters = {}) => {
  const client = useQueryClient();

  const queryKey = [
    "PGR_COMPLAINT_LIST",
    tenantId,
    JSON.stringify(filters),
  ];

  const query = queryTemplate({
    queryKey,
    queryFn: () =>
      Digit.PGRService.search(tenantId, filters),
  });

  return {
    ...query,
    revalidate: () =>
      client.invalidateQueries({ queryKey }),
  };
};

export const useComplaintsListByMobile = (
  tenantId,
  mobileNumber
) => {
  return useComplaintsList(tenantId, { mobileNumber });
};