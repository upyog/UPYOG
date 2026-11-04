import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const GIS = {
  searchPT: ( details) =>
    Request({
      url: Urls.gis.gis_dx_PT,
      data: details,
      useCache: false,
      setTimeParam: false,
      userService: true,
      method: "POST",
      params: {},
      auth: true,
    }),
    searchAsset: ( details) =>
      Request({
        url: Urls.gis.gis_dx_ASSET,
        data: details,
        useCache: false,
        setTimeParam: false,
        userService: true,
        method: "POST",
        params: {},
        auth: true,
      }),
};

      
