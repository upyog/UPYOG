// import { useQuery, useMutation } from "react-query";

// import WMSService from "../../../services/elements/WMS";

// export const useWmsCMGet = (tenantId, config = {}) => {
//   return useMutation((data) => WMSService.ContractorMaster.get(tenantId));
// };

// export default useWmsCMGet;


import { useMutation, useQuery, useQueryClient } from "react-query";
import WMSService from "../../../services/elements/WMS";
const useWmsCAGet = (tenantId,type, config = {}) => {
  console.log("bankList sttepr single data useWMSCAGet tenantId,type ",{tenantId,type})
  // return useQuery(["WMS_CA_GET_LIST", tenantId], () => WMSService?.ContractorAgreement?.getSingleFake(tenantId), config);
//   debugger
const CAAllData=()=>{    console.log("bankList sttepr single data lists")
  // return useQuery(["WMS_CA_GET_ALL_DATA", tenantId], () => WMSService.ContractorAgreement.getList(), config);
  return useQuery(["WMS_CA_GET_ALL_DATA", tenantId], () => WMSService.ContractorAgreement.getListFake(), config);
};
const CAList = ()=>{
    console.log("bankList sttepr single data single")
    // return useQuery(["WMS_CA_GET_List", tenantId], () => WMSService.ContractorAgreement.getSingle(tenantId), config);
    return useQuery(["WMS_CA_GET_LIST", tenantId], () => WMSService.ContractorAgreement.getSingleFake(tenantId), config);// fake data
};
const CAUpdate = ()=>{
  console.log("bankList sttepr single data update")
  return useMutation((data) => WMSService.ContractorAgreement.updateFake(data,tenantId), config);

}

switch(type){
  case "AllData":
    return CAAllData();
  case "CASingleList":
    return CAList();
  case "CAUpdate":
    return CAUpdate();
}
};
export default useWmsCAGet;
