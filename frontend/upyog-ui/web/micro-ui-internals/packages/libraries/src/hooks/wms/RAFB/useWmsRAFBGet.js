import { useMutation, useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";
const useWmsRAFBGet = (id,type, config = {}) => {
 const getPreviousBill=()=>{    console.log("bankList sttepr single data lists")
  return useQuery(["WMS_RAFB_GET_PREVIOUS_BILL", id], () => WMSService.RunningAccountFinalBill.getPreviousBill(), config);
};
const CAList = ()=>{
    return useQuery(["WMS_RAFB_GET_SINGLE_LIST", id], () => WMSService.RunningAccountFinalBill.getSinglePreviousBill(id), config);// fake data
};
// const CAUpdate = ()=>{
//   return useMutation((data) => WMSService.ContractorAgreement.updateFake(data,id), config);

// }

switch(type){
  case "getPriviousBill":
    return getPreviousBill();
  case "getPriviousSingleList":
    return CAList();
  // case "CAUpdate":
  //   return CAUpdate();
}
};
export default useWmsRAFBGet;
