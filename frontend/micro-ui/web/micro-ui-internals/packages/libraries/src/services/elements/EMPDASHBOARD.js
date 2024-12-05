import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";


export const EmployeeDashboardService = {
  search: ({ tenantId, moduleName, auth }) =>
    Request({
      url: Urls.employeeDashboardSearch,
      useCache: false,
      method: "POST",
      auth: auth === false ? auth : true,
      userService: auth === false ? auth : true,
      data: { tenantId, moduleName }
    }),
    
};
