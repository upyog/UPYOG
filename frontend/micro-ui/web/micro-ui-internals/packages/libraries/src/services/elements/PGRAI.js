import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const PGRAIService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.PGR_Create_AI,
      data: details,
      useCache: true,
      method: "POST",
      params: { tenantId },
      auth: true,
      userService: true,
    }),
    search: (tenantId, filters = {}) => {
      return Request({
        url: Urls.PGR_Search_AI,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
        params: { tenantId: tenantId, ...filters },
      });
    },
  update: (details) =>
    Request({
      url: Urls.pgr_update,
      data: details,
      useCache: true,
      auth: true,
      method: "POST",
      params: { tenantId: details.tenantId },
      userService: true,
    }),

};
