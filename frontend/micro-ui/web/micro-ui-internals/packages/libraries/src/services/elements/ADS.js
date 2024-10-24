import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

 
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

    slot_search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.ads.slot_search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
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
  })
};
