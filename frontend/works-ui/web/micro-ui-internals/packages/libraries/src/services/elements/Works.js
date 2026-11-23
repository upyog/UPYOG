import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const WorksService = {
  createActionPlan: (details, tenantId) =>
    Request({
      url: Urls.works.createActionPlan,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: tenantId ? { tenantId } : {},
      auth: true,
    }),

  updateActionPlan: (details, tenantId) =>
    Request({
      url: Urls.works.updateActionPlan,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: tenantId ? { tenantId } : {},
      auth: true,
    }),

  searchActionPlan: ({ tenantId, filters = {}, auth = true }) =>
    Request({
      url: Urls.works.searchActionPlan,
      data: {},
      useCache: false,
      setTimeParam: false,
      userService: auth === false ? false : true,
      method: "POST",
      params: { tenantId, ...filters },
      auth: auth === false ? false : true,
    }),
};

