import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

/*
 * This file defines the `ADSServices` object, which provides methods for interacting 
 * with the Advertisement Display System (ADS). It includes functions for:
 * - Creating ADS resources
 * - Searching ADS resources
 * - Searching ADS slots
 */
export const ADSServices= {
  
  create: (details, tenantId) =>
    Request({
      url: Urls.ads.create,
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
      url: Urls.ads.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),

    slot_search: (details, tenantId) =>
      Request({
        url: Urls.ads.slot_search,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
    update: (details, tenantId) =>
    Request({
      url: Urls.ads.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
  }),
  estimateCreate:(details, tenantId) =>
    Request({
      url: Urls.ads.estimateCreate,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),  
};
