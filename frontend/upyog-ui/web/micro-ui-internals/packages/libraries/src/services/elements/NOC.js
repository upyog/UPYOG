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
      params: { tenantId, ...filters },
    }),
  create: (data, tenantId) => {
    const ts = new Date().getTime();
    const customRequestInfo = {
      apiId: "Mihy",
      ver: ".01",
      action: "",
      did: "1",
      key: "",
      msgId: `${ts}|${Digit.StoreData.getCurrentLanguage()}`,
      authToken: Digit.UserService.getUser()?.access_token || null,
      userInfo: Digit.UserService.getUser()?.info,
      requesterId: ""
    };
    Object.defineProperty(data, "RequestInfo", {
      get: () => customRequestInfo,
      set: (val) => {
        Object.assign(customRequestInfo, val);
      },
      configurable: true,
      enumerable: true
    });
    return Request({
      url: Urls.firenoc.create,
      useCache: false,
      method: "POST",
      data: data,
      params: { tenantId },
      auth: true,
      userService: true,
    });
  },
  update: (data, tenantId) => {
    const ts = new Date().getTime();
    const customRequestInfo = {
      apiId: "Rainmaker",
      ver: ".01",
      action: data?.FireNOCs?.[0]?.fireNOCDetails?.action || "APPLY",
      did: "1",
      key: "",
      msgId: `${ts}|${Digit.StoreData.getCurrentLanguage()}`,
      authToken: Digit.UserService.getUser()?.access_token || null,
      userInfo: Digit.UserService.getUser()?.info
    };
    Object.defineProperty(data, "RequestInfo", {
      get: () => customRequestInfo,
      set: (val) => {
        Object.assign(customRequestInfo, val);
      },
      configurable: true,
      enumerable: true
    });
    return Request({
      url: Urls.firenoc.update,
      useCache: false,
      method: "POST",
      data: data,
      params: { tenantId },
      auth: true,
      userService: true,
    });
  },
  search: (tenantId, filters = {}) =>
    Request({
      url: Urls.firenoc.search,
      useCache: false,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
      userService: true,
    }),
}