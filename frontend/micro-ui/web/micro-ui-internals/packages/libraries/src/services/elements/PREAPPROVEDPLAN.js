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
    estimate: (data, tenantId) =>
    Request({
      url: Urls.preApproved.estimate,
      data: data,
      //multipartData: data,
      //useCache: false,
      //setTimeParam: false,
      userService: true,
      method: "POST",
      //params: { tenantId },
      auth: true,
      //multipartFormData: true
    })
};