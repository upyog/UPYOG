import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

/**
 * This file contains service methods for interacting with the PGR_AI API.
 * - create: Sends a POST request to create a new PGR_AI record.
 * - search: Sends a POST request to search for PGR_AI records based on tenant ID and filters.
 * - update: Sends a POST request to update an existing PGR_AI record.
 */
export const PGRAIService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.pgrAi.PGR_Create_AI,
      data: details,
      useCache: true,
      method: "POST",
      params: { tenantId },
      auth: true,
      userService: true,
    }),
    search: (tenantId, filters = {}) => {
      return Request({
        url: Urls.pgrAi.PGR_Search_AI,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId: tenantId, ...filters },
      });
    },
  update: (details) =>
    Request({
      url: Urls.pgrAi.PGR_Update_AI,
      data: details,
      useCache: true,
      auth: true,
      method: "POST",
      params: { tenantId: details.tenantId },
      userService: true,
    }),
  employeeSearch: (tenantId, roles, isActive) => {
    return Request({
      url: Urls.EmployeeSearch,
      params: { tenantId, roles, isActive },
      auth: true,
    });
  },

};
