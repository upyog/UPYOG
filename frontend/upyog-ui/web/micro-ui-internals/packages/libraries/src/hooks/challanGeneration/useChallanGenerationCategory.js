/**
 * Fetches challan generation categories from MDMS using React Query.
 *
 * - Calls MdmsService.getPaymentRules with tenantId and filter.
 * - Extracts BusinessService data from the response.
 * - Removes duplicate categories based on code prefix.
 * - Adds an i18n key for each category for localization.
 *
 * @param {string} tenantId - Tenant identifier
 * @param {Object} filter - MDMS query filters
 * @param {Object} config - Optional React Query config
 *
 * @returns {Object} { Categories, data }
 */

import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

const useChallanGenerationCategory = (tenantId, filter, config = {}) => {
  return queryTemplate({
    queryKey: ["ChallanGeneration_CATEGORY_SERVICE", tenantId, filter],

    queryFn: () => MdmsService.getPaymentRules(tenantId, filter),

    select: (data) => {
      let Categories = [];

      data?.MdmsRes?.BillingService?.BusinessService?.forEach((ob) => {
        const exists = Categories.some(
          (el) => el?.code.split(".")[0] === ob.code.split(".")[0]
        );

        if (!exists) {
          Categories.push({
            ...ob,
            i18nkey: `BILLINGSERVICE_BUSINESSSERVICE_${ob.code
              .split(".")[0]
              .toUpperCase()}`,
          });
        }
      });

      return { Categories, data };
    },

    config,
  });
};

export default useChallanGenerationCategory;