import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

 
export const CHBServices= {
  
  create: (details, tenantId) =>
    Request({
      url: Urls.chb.create,
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
      url: Urls.chb.search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),

    slot_search: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.chb.slot_search,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),
    estimate: ({ tenantId, filters, auth }) =>
    Request({
      url: Urls.chb.estimate,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    }),
    update: (details, tenantId) =>
    Request({
      url: Urls.chb.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
  })
};




