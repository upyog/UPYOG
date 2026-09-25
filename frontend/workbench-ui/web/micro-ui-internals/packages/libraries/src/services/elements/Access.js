import { ServiceRequest } from "../atoms/Utils/Request";
import Urls from "../atoms/urls";

const AccessControlService = {
  getAccessControl: (roles = []) =>
  ServiceRequest({
      url: Urls.access_control,
      method: "POST",
      auth: true,
      useCache: true,
      userService: true,
      data: {
        roleCodes:  [
        "LOC_ADMIN",
        "MDMS_ADMIN",
    ],     // static role codes for now, will be dynamic based on the logged in user
        tenantId: Digit.ULBService.getStateId(),
        actionMaster: "actions-test",
        enabled: true,
      },
      reqTimestamp: true,
    }),
};
export default AccessControlService;
