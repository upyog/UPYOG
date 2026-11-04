import { Request } from "../atoms/Utils/Request";
import Urls from "../atoms/urls";

export const GCServices = {
  create: (details, tenantId) =>
    Request({
      url: Urls.gc.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
  search: async ({ tenantId, filters, auth, data }) => {
    return await Request({
      url: Urls.gc.search,
      data: data || {},
      useCache: false,
      setTimeParam: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      params: { tenantId, ...filters },
    });
  },
  update: (details, tenantId) =>
    Request({
      url: Urls.gc.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: { tenantId },
      auth: true,
    }),
};