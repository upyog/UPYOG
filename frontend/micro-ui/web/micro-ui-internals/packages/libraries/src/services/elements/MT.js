import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

// MTService: This service provides methods to create, update, and search for MT-related data.

export const MTService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.mt.create,
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
        url: Urls.mt.update,
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
        url: Urls.mt.search,
        useCache: false,
        method: "POST",
        auth: auth === false ? auth : true,
        userService: auth === false ? auth : true,
        params: { tenantId, ...filters },
      }),    
};




