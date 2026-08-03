import { queryTemplate } from "../../common/queryTemplate";

const useLicenseDetails = (tenantId, filters, config = {}) => {
  return queryTemplate({
    queryKey: [
      "OBPS_LICENSE_DETAIL",
      tenantId,
      JSON.stringify(filters),
    ],
    queryFn: () =>
      Digit.OBPSService.LicenseDetails(tenantId, filters),
    config,
  });
};

export default useLicenseDetails;