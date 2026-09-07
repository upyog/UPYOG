import { Request } from "../atoms/Utils/Request"
import Urls from "../atoms/urls";

export const NOCService = {
  NOCsearch: ({ tenantId, filters }) =>
    Request({
      url: Urls.noc.nocSearch,
      useCache: false,
      method: "POST",
      auth: true,
      userService: true,
      setTimeParam: false,
      params: { tenantId, ...filters },
    }),
  create: (data, tenantId) =>
    Request({
      url: Urls.firenoc.create,
      useCache: false,
      method: "POST",
      data: data,
      params: { tenantId },
      auth: true,
      userService: true,
    }),
  update: (data, tenantId) =>
    Request({
      url: Urls.firenoc.update,
      useCache: false,
      method: "POST",
      data: data,
      params: { tenantId },
      auth: true,
      userService: true,
    }),
  search: (tenantId, filters = {}) =>
    Request({
      url: Urls.firenoc.search,
      useCache: false,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
      userService: true,
      setTimeParam: false,
    }),
}