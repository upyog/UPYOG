import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const TPService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.tp.create,
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
        url: Urls.tp.search,
        useCache: false,
        method: "POST",
        auth: auth === false ? auth : true,
        userService: auth === false ? auth : true,
        params: { tenantId, ...filters },
      }),
       update: (details, tenantId) =>
      Request({
        url: Urls.tp.update,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
    })  
};




