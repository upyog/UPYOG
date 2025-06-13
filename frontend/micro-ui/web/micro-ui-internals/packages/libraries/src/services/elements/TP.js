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
    })  
};




