import WMSService from "../../../services/elements/WMS";
import { useMutation } from "react-query";

const useWmsRAFBCreate = (tenantId, type = true) => {
  if (type) {
    
    return useMutation((data) => WMSService.RunningAccountFinalBill.createPreviousBill(data, tenantId));
  } else {
    alert("false useWmsRAFBCreate")
    // return useMutation((data) => WMSService.update(data, tenantId));
  }
};

export default useWmsRAFBCreate;
