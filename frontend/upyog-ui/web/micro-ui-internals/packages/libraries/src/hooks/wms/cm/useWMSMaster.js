// import { MdmsService } from "../../services/elements/MDMS";
import WMSService from "../../../services/elements/WMS";

import { useQuery,useMutation } from "react-query";

const useWMSMaster = ( tenantId, type,endppoints, moduleCode, config = {}) => {
  console.log("tenantId, type ",{tenantId, type})
  // Bank Branch IFSC Code
  const bankBranchIFSCCode  = () => {
    return useQuery(["WMS_BANK_BRANCH", tenantId], () => WMSService.ContractorMaster.getMasterData(tenantId, moduleCode, type), config);
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

  // Vendor Sub Type
  const vendorSubType  = () => {
    return useQuery(["WMS_SUB_TYPE", tenantId], () => WMSService.ContractorMaster.getMasterSubTypeData(tenantId), config);
  };
  const vendorSubTypeGetSingleRecord  = () => {
    return useQuery(["WMS_SUB_TYPE_SINGLE_RECORD", tenantId], () => WMSService.ContractorMaster.getSingleResordsMasterSubTypeData(tenantId), config);
  };
  const vendorSubTypeUpdateRecord = () => {
    return useMutation((data) => WMSService.ContractorMaster.updateMasterSubTypeData(data), config);
  };
  const vendorSubTypeCreateRecord = () => {
    return useMutation((data) => WMSService.ContractorMaster.createMasterSubTypeData(data), config);
  };
  
//   const useHrmsEmployeeReasons = () => {
//     return useQuery(["HRMS_EMP_REASON", tenantId], () => MdmsService.getHrmsEmployeeReason(tenantId, moduleCode, type), config);
//   };

// Vendor Type
const useVendorTypeList = () => {
    return useQuery(["WMS_MD_VENDER_TYPE_LIST", tenantId], () => WMSService.ContractorMaster.getMasterTypeData(tenantId), config);
  };
  const useVendorTypeAdd = () => {
    return useMutation((data) => WMSService.ContractorMaster.createMasterTypeData(data), config);
  };
  const useVendorTypeUpdate = () => {
    return useMutation((data) => WMSService.ContractorMaster.updateMasterTypeData(data), config);
  };
   const useVendorTypeSingleRecord = () => {
    return useQuery(["WMS_MD_VENDER_TYPE_SINGLE_RECORD",tenantId], () => WMSService.ContractorMaster.getSingleResordsMasterTypeData(tenantId));
  };


  // Vendor CLass
const useVendorClassList = () => {
  return useQuery(["WMS_MD_VENDER_CLASS_LIST", tenantId], () => WMSService.ContractorMaster.getMasterClassData(tenantId), config);
};
const useVendorClassAdd = () => {
  return useMutation((data) => WMSService.ContractorMaster.createMasterClassData(data), config);
};
const useVendorClassUpdate = () => {
  return useMutation((data) => WMSService.ContractorMaster.updateMasterClassData(data), config);
};
 const useVendorClassSingleRecord = () => {
  // alert("view si")
  return useQuery(["WMS_MD_VENDER_CLASS_SINGLE_RECORD",tenantId], () => WMSService.ContractorMaster.getSingleResordsMasterClassData(tenantId));
};
  
//Account Head
const useAccountHeadList = () => {
  return useQuery(["WMS_MD_ACCOUNT_HEAD_LIST", tenantId], () => WMSService.ContractorMaster.getAccountHeadData(tenantId), config);
};
const useAccountHeadAdd = () => {
  return useMutation((data) => WMSService.ContractorMaster.createAccountHeadData(data), config);
};
const useAccountHeadUpdate = () => {
  return useMutation((data) => WMSService.ContractorMaster.updateAccountHeadData(data), config);
};
 const useAccountHeadSingleRecord = () => {
  return useQuery(["WMS_MD_ACCOUNT_HEAD_SINGLE_RECORD",tenantId], () => WMSService.ContractorMaster.getSingleResordsAccountHeadData(tenantId));
};

//Function Head
const useFunctionAppList = () => {
  return useQuery(["WMS_MD_FUNCTION_APP_LIST", tenantId], () => WMSService.ContractorMaster.getFunctionAppData(tenantId), config);
};
const useFunctionAppAdd = () => {
  return useMutation((data) => WMSService.ContractorMaster.createFunctionAppData(data), config);
};
const useFunctionAppUpdate = () => {
  return useMutation((data) => WMSService.ContractorMaster.updateFunctionAppData(data), config);
};
 const useFunctionAppSingleRecord = () => {
  return useQuery(["WMS_MD_FUNCTION_APP_SINGLE_RECORD",tenantId], () => WMSService.ContractorMaster.getSingleResordsFunctionAppData(tenantId));
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

    case "WMS_SUB_TYPE_VIEW":
      return vendorSubType();
    case "WMS_SUB_TYPE_SINGLE_RECORD":
      return vendorSubTypeGetSingleRecord();
    case "WMS_SUB_TYPE_UPDATE":
      return vendorSubTypeUpdateRecord();
    case "WMS_SUB_TYPE_CREATE":
      return vendorSubTypeCreateRecord();

    case "WMS_V_TYPE_LIST":
        return useVendorTypeList();
    case "WMS_V_TYPE_ADD":
        return useVendorTypeAdd();
    case "WMS_V_TYPE_UPDATE":
        return useVendorTypeUpdate()
    case "WMS_V_TYPE_SINGLE_RECORD":
    return useVendorTypeSingleRecord();


    case "WMS_V_CLASS_LIST":
      return useVendorClassList();
  case "WMS_V_CLASS_ADD":
      return useVendorClassAdd();
  case "WMS_V_CLASS_UPDATE":
      return useVendorClassUpdate()
  case "WMS_V_CLASS_SINGLE_RECORD":
  return useVendorClassSingleRecord();

  
  case "WMS_ACCOUNT_HEAD_LIST":
      return useAccountHeadList();
  case "WMS_ACCOUNT_HEAD_ADD":
      return useAccountHeadAdd();
  case "WMS_ACCOUNT_HEAD_UPDATE":
      return useAccountHeadUpdate()
  case "WMS_ACCOUNT_HEAD_SINGLE_RECORD":
  return useAccountHeadSingleRecord();
  

  case "WMS_FUNCTION_APP_LIST":
      return useFunctionAppList();
  case "WMS_FUNCTION_APP_ADD":
      return useFunctionAppAdd();
  case "WMS_FUNCTION_APP_UPDATE":
      return useFunctionAppUpdate()
  case "WMS_FUNCTION_APP_SINGLE_RECORD":
  return useFunctionAppSingleRecord();
  }
};

export default useWMSMaster;
