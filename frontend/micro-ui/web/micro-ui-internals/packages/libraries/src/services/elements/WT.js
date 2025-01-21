import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";


 
export const WTService = {
  create: (details, tenantId) =>
    Request({
      url: Urls.wt.create,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),    
};




