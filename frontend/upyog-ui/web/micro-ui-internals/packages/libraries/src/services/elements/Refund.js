import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const RefundService = {
  create: (details, params = {}) =>
    Request({
      url: Urls.refund.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: params,
      auth: true,
    }),
  update: (details, params = {}) =>
    Request({
      url: Urls.refund.update,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: params,
      auth: true,
    }),
  search: (criteria = {}, params = {}) =>
    Request({
      url: Urls.refund.search,
      data: {
        moduleName: "CHB",
        businessService: "CHB.REFUND",
        ...criteria,
      },
      useCache: false,
      setTimeParam: false,
      method: "POST",
      auth: true,
      userService: true,
      params: params,
    }),
};

