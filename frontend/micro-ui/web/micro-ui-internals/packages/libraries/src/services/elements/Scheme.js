import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

const SchemeService = {
    get: (data) =>{
      return Request({
        data: data,
        url: Urls.common.get,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      })
    },
    getSchemes:(data)=>{
      return Request({
        data: data,
        url: Urls.schemes.getSchemes,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      })
    },
    getUserDetails:(data)=>{
      return Request({
        data: data,
        url: Urls.users.getUserDetails,
        useCache: false,
        method: "POST",
        auth: true,
        userService: true,
      })
    }
};

export default SchemeService;
