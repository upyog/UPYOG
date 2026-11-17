import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

/**
 * Searches for resources based on the provided data.
 * 
 * @param {Object} data - The search parameters containing tenant ID, filters, and other options.
 * @returns {Promise} - A promise that resolves with the search results.
 */
export const CMServices = {
  search: (data) =>
    Request({
      url: Urls.cm.search,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      data: data,
    })
};