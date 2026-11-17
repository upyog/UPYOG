import React from "react";
import { queryTemplate } from "../../common/queryTemplate";
import { MdmsService } from "../../services/elements/MDMS";

/**
 * Provides a query to fetch challan form configuration from MDMS.
 *
 * - Calls MdmsService.getDataByCriteria for "CreateFieldsConfig" under mCollect module.
 * - Uses tenantId in query key for caching and refetching.
 * - Transforms response to return only CreateFieldsConfig using `select`.
 *
 * @param {string} tenantId - Tenant identifier
 * @param {Object} config - Optional React Query config
 *
 * @returns {Object} Query result with form configuration data
 */

const useChallanGenerationFormConfig = {
  getFormConfig: (tenantId, config = {}) =>
    queryTemplate({
      queryKey: [tenantId, "FORM_CONFIG"],

      queryFn: () =>
        MdmsService.getDataByCriteria(
          tenantId,
          {
            details: {
              tenantId,
              moduleDetails: [
                {
                  moduleName: "mCollect",
                  masterDetails: [{ name: "CreateFieldsConfig" }],
                },
              ],
            },
          },
          "mCollect"
        ),

      select: (d) => d?.mCollect?.CreateFieldsConfig,

      config,
    }),
};

export default useChallanGenerationFormConfig;