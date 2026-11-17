import { queryTemplate } from "../../common/queryTemplate";
import { OBPSService } from "../../services/elements/OBPS";

const useOCEdcrSearch = (
  tenantId,
  filters,
  config = {},
  ocEdcrNumber
) => {
  const enabled = config?.enabled;

  return queryTemplate({
    queryKey: [
      "OBPS_OC_EDCR_SEARCH",
      tenantId,
      JSON.stringify(filters),
      ocEdcrNumber?.edcrNumber,
    ],
    queryFn: async () => {
      if (!enabled) return null;

      const bpaApprovalResponse =
        await OBPSService.BPASearch(tenantId, { ...filters });

      const baseData = bpaApprovalResponse?.BPA?.[0] || {};
      const newTenantId = baseData?.tenantId || tenantId;

      const edcrDetails =
        await OBPSService.scrutinyDetails(newTenantId, {
          edcrNumber: baseData?.edcrNumber,
        });

      const bpaResponse =
        await OBPSService.BPASearch(newTenantId, {
          edcrNumber: ocEdcrNumber?.edcrNumber,
        });

      const comparisionReport =
        await OBPSService.comparisionReport(newTenantId, {
          ocdcrNumber: ocEdcrNumber?.edcrNumber,
          edcrNumber: baseData?.edcrNumber,
        });

      return {
        bpaApprovalResponse: bpaApprovalResponse?.BPA,
        edcrDetails: edcrDetails?.edcrDetail,
        bpaResponse: bpaResponse?.BPA,
        comparisionReport: comparisionReport?.comparisonDetail,
      };
    },
    config: { enabled, ...config },
  });
};

export default useOCEdcrSearch;