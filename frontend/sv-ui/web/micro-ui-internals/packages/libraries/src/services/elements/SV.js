import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";
export const SVService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.sv.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),

  search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.sv.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),

  update: (details) =>
    Request({
      url: Urls.sv.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
  }),

  deleteDraft:({ tenantId, filters, auth }) =>
    Request({
      url: Urls.sv.deleteDraft,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),


  /**
 * Function: demandCreate
 *
 * This function is responsible for creating a new demand by making a POST request
 * to the `demandCreate` endpoint defined in `Urls.sv.demandCreate`.
 *
 * Parameters:
 * - `details` (Object): The request payload containing the demand details to be sent to the backend.
 *
 * Request Configuration:
 * - `url`: Uses the predefined URL from `Urls.sv.demandCreate` for the API endpoint.
 * - `data`: The payload containing the demand details.
 * - `useCache: false`: Ensures the request is not cached.
 * - `setTimeParam: false`: Disables automatic timestamping of the request.
 * - `userService: true`: Indicates that the request is associated with a user service.
 * - `method: "POST"`: Specifies the HTTP method as POST to create a new demand.
 * - `params: {}`: No additional query parameters are included.
 * - `auth: true`: Ensures that authentication is required for this request.
 *
 * @param {Object} details - The request payload containing demand details.
 * @returns {Promise} A promise resolving to the response data from the API.
 */

  demandCreate: (details) =>
    Request({
      url: Urls.sv.demandCreate,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
};




