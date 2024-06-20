import WMSService from "../../../services/elements/WMS";
import { useMutation } from "react-query";

const useWmsRAFBUpdate = (tenantId, uid,type = true) => {

  if (type) {
    console.log("useWmsRAFBUpdate tenantId ",tenantId,uid)
    return useMutation((data) => WMSService.RunningAccountFinalBill.editPreviousBill(data,uid,tenantId));
  } else {
    alert("false useWms RAFB Update")
    // return useMutation((data) => WMSService.update(data, tenantId));
  }
};

export default useWmsRAFBUpdate;
