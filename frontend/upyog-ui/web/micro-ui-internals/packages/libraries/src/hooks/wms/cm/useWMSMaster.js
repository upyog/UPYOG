// import { MdmsService } from "../../services/elements/MDMS";
import WMSService from "../../../services/elements/WMS";

import { useQuery,useMutation } from "react-query";

const useWMSMaster = ( tenantId, type,endppoints, moduleCode, config = {}) => {
  // Bank Branch IFSC Code
  const bankBranchIFSCCode  = () => {
    return useQuery(["WMS_BANK_BRANCH", tenantId], () => WMSService.ContractorMaster.getMasterData(endppoints,tenantId, moduleCode, type), config);
  };
  const bankGetSingleRecord  = () => {
    return useQuery(["WMS_BANK_BRANCH_SINGLE_RECORD", tenantId], () => WMSService.ContractorMaster.getSingleResordsMasterData(tenantId), config);
  };
  const bankUpdateRecord = () => {
    return useMutation((data) => WMSService.ContractorMaster.updateMasterData(data), config);
  };
  const bankCreateRecord = () => {
    return useMutation((data) => WMSService.ContractorMaster.createMasterData(data), config);
  };
  
//   const useHrmsEmployeeReasons = () => {
//     return useQuery(["HRMS_EMP_REASON", tenantId], () => MdmsService.getHrmsEmployeeReason(tenantId, moduleCode, type), config);
//   };

// Vendor Type
const useVendorTypeList = () => {
    return useQuery(["WMS_MD_VENDER_TYPE_LIST", tenantId], () => WMSService.ContractorMaster.getMasterDataFakeAPI(tenantId), config);
  };
  const useVendorTypeAdd = () => {
    return useMutation((data) => WMSService.ContractorMaster.createMasterDataFakeAPI(data,tenantId), config);
  };
  const useVendorTypeUpdate = () => {
    return useMutation((data) => WMSService.ContractorMaster.updateMasterDataFakeAPI(data,tenantId), config);
  };
   const useVendorTypeSingleRecord = () => {
    return useQuery(["WMS_MD_VENDER_TYPE_SINGLE_RECORD",tenantId], () => WMSService.ContractorMaster.getMasterSingleDataFakeAPI(tenantId));
  };

  

  switch (type) {
    case "WMS_BANK_BRANCH_TYPE":
      return bankBranchIFSCCode();
    case "WMS_BANK_SINGLE_RECORD":
      return bankGetSingleRecord();
    case "WMS_BANK_UPDATE":
      return bankUpdateRecord();
      case "WMS_BANK_CREATE":
      return bankCreateRecord();
      
    // case "DeactivationReason":
    //   return useHrmsEmployeeReasons();
    case "WMS_V_TYPE_LIST":
        return useVendorTypeList();
    case "WMS_V_TYPE_ADD":
        return useVendorTypeAdd();
    case "WMS_V_TYPE_UPDATE":
        return useVendorTypeUpdate()
    case "WMS_V_TYPE_SINGLE_RECORD":
    return useVendorTypeSingleRecord();
  }
};
export default useWMSMaster;
