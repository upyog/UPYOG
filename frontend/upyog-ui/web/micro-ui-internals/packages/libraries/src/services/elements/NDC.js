import { Request } from "../atoms/Utils/Request";
import Urls from "../atoms/urls";

export const NDCService = {
  NDCcreate: ({ tenantId, filters, details }) => {
    return Request({
      url: Urls.ndc.create,
      data: details,
      useCache: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
      userService: true,
    });
  },
  NDCsearch: ({ tenantId, filters }) =>
    Request({
      url: Urls.ndc.search,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
  NDCUpdate: ({ tenantId, filters, details }) =>
    Request({
      url: Urls.ndc.update,
      data: details,
      useCache: true,
      userService: true,
      method: "PUT",
      params: { tenantId, ...filters },
      auth: true,
    }),
  NDCCalculator: ({ tenantId, filters, details }) =>
    Request({
      url: Urls.ndc.billingCalculate,
      data: details,
      useCache: true,
      userService: true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: true,
    }),
};
