import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

// EST Service for managing EST-related API calls
// This service provides methods to create, search, and update EST assets and applications.


// Create EST Asset API Call
// This method creates a new EST asset in the system.
export const ESTService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.est.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),

    // Create allotment
    // This method creates a new allotment in the EST system.

  allotmentcreate: (details, tenantId) =>
    Request({
      url: Urls.est.allotment,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
      reqTimestamp: true,
    }),

  allotmentUpdate: (details, tenantId) =>
    Request({
      url: Urls.est.allotmentUpdate,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
      reqTimestamp: true,
    }),

  // Search EST Assets
  // This method searches for EST assets based on provided filters.

  search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.est.search,
      useCache: false,
      data: filters,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId },
    }),

// Update EST Asset API Call
// This method updates an existing EST asset in the system.

  update: (details, tenantId) =>
    Request({
      url: Urls.est.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),

// Asset Search API Call
// This method searches for EST assets based on provided filters.

  assetSearch: ({ tenantId, filters }) =>
  Request({
    url: Urls.est.search,
    useCache: false,
    method: "POST",
    auth: true,
    userService: true,
    params: { tenantId },
    data: filters,
  }),


// Application Search API Call
// This method searches for EST applications based on provided filters.

  applicationSearch: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.est.applicationSearch,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),
// Allotment Search API Call
// This method searches for EST allotments based on provided filters.

    allotmentSearch: ({ tenantId, filters }) => {
      const criteria = filters?.AllotmentSearchCriteria || filters || {};
      return Request({
        url: Urls.est.allotmentSearch,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId },
        data: {
          AllotmentSearchCriteria: criteria,
        },
      });
    },


};