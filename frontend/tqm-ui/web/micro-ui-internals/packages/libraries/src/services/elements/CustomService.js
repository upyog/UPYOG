import { Request } from "../atoms/Utils/Request";

export const CustomService = {
  getResponse: ({ url, params, body, plainAccessRequest, useCache = true, userService = true, setTimeParam = true, userDownload = false, method = "POST" }) =>
    Request({
      url: url,
      data: body,
      useCache,
      userService,
      method: method,
      auth: true,
      params: params,
      plainAccessRequest: plainAccessRequest,
      userDownload: userDownload,
      setTimeParam,
    }),
};
