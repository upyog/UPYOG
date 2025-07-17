import { DateRange, Details } from "@upyog/digit-ui-react-components";
import Urls from "../atoms/urls";
import { Request } from "../atoms/Utils/Request";

export const PreApprovedPlanService = {
  search: (data={}) => {
    console.log("Urls.preApproved.search", data)
    return Request({
      url: Urls.preApproved.search,
      params: {...data },
      auth: true,
      userService: true,
      method: "POST",
      //data: data,
      useCache: false,
    });
  },
  estimate: (data, params) => {
    console.log("dataaEst", data)
    if (data && data.CalulationCriteria) {
      
      return Request({
        url: Urls.preApproved.estimate,
        data: data,
        params: { ...params },
        method: "POST",
        userService: true,
        useCache: false,
        auth: true,
      });
    } else {
      return Request({
        url: Urls.preApproved.estimate,
        data: params,
        params: { ...params }, 
        method: "POST",
        userService: true,
        useCache: false,
        auth: true,
      });
    }
  } 
};