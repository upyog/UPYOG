/**
 * Fetches MDMS data for challan generation based on type.
 *
 * - Supports fetching:
 *   • BusinessService (billing services)
 *   • applicationStatus
 * - Calls respective MdmsService APIs using React Query.
 * - Uses type to determine which query to execute.
 *
 * @param {string} tenantId - Tenant identifier
 * @param {string} moduleCode - MDMS module name
 * @param {string} type - Data type (e.g., "BusinessService", "applicationStatus")
 * @param {Object} filter - MDMS query filters
 * @param {Object} config - Optional React Query config
 *
 * @returns {Object|null} Query result or null if type is unsupported
 */

import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useChallanGenerationMDMS = (
  tenantId,
  moduleCode,
  type,
  filter,
  config = {}
) => {
  const isBusiness = type === "BusinessService";

  return queryTemplate({
    queryKey: [
      isBusiness
        ? "CHALLANGENERATION_BILLING_SERVICE"
        : "CHALLANGENERATION_APPLICATION_STATUS",
      tenantId,
      moduleCode,
      filter,
    ],

    queryFn: () =>
      isBusiness
        ? MdmsService.getChallanGenerationBillingService(
            tenantId,
            moduleCode,
            type,
            filter
          )
        : MdmsService.getChallanGenerationApplcationStatus(
            tenantId,
            moduleCode,
            type,
            filter
          ),

    enabled: !!type,
    config,
  });
};

export default useChallanGenerationMDMS;
