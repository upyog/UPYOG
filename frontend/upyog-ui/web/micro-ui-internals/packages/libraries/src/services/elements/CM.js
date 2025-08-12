import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

/**
 * Searches for resources based on the provided tenant ID and filters.
 * 
 * @param  - The parameters for the search.
 * @param tenantId - The ID of the tenant to search within.
 * @param filters - The filters to apply to the search.
 * @param auth - Indicates whether authentication is required.
 * @returns {Promise} - A promise that resolves with the search results.
 */
export const CMServices = {
  search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.cm.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    })
};